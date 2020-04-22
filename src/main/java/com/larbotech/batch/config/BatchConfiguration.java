package com.larbotech.batch.config;

import com.larbotech.batch.JobCompletionNotificationListener;
import com.larbotech.batch.model.CsvRow;
import com.larbotech.batch.partitioner.CustomMultiResourcePartitioner;
import com.larbotech.batch.processor.RowItemProcessor;
import com.larbotech.batch.reader.CsvRowReader;
import com.larbotech.batch.writer.CsvRowLogWriter;
import com.larbotech.batch.writer.CsvRowWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
@ComponentScan("com.larbotech")
public class BatchConfiguration {


  @Value("${batch.outDirectory}")
  private String outDirectory;

  @Value("${step.chunk.size}")
  private int chunkSize;

  @Value("${batch.intDirectory}")
  private String intDirectory;

  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;

  @Bean("reader")
  @StepScope
  public CsvRowReader reader(@Value("#{stepExecutionContext[partitionId]}") String partitionId,
      @Value("#{stepExecutionContext[fileName]}") String fileName,
      @Value("#{stepExecution.jobExecution}") JobExecution jobExecution) throws IOException {
    System.out.println(Thread.currentThread().getName() + "reader1" + fileName);
    return new CsvRowReader(partitionId, fileName, jobExecution);

  }

  @Bean
  @StepScope
  public RowItemProcessor processor(
      @Value("#{stepExecutionContext[partitionId]}") String partitionId,
      @Value("#{stepExecutionContext[fileName]}") String fileName) {
    System.out.println(Thread.currentThread().getName() + "  processor1  " + fileName);
    return new RowItemProcessor();
  }

  @Bean("writer")
  @StepScope
  @DependsOn({"reader"})
  public CsvRowWriter writer(@Value("#{stepExecutionContext[partitionId]}") String partitionId,
      @Value("#{stepExecutionContext[fileNameResult]}") String fileName,
      CsvRowReader csvRowReader) throws IOException {
    System.out.println(Thread.currentThread().getName() + " writer1 " + fileName);
    return new CsvRowWriter(partitionId, fileName, csvRowReader.getJobExecutionContext());
  }


  @Bean
  public CsvRowLogWriter csvRowLogWriter() {
    return new CsvRowLogWriter();
  }

  @Bean
  public ClassifierCompositeItemWriter<CsvRow> classifierCompositeItemWriter(
      CsvRowWriter csvRowWriter,
      CsvRowLogWriter csvRowLogWriter) {
    ClassifierCompositeItemWriter<CsvRow> classifierCompositeItemWriter = new ClassifierCompositeItemWriter<>();

    CompositeItemWriter<CsvRow> csvRowCompositeItemWriter = new CompositeItemWriter<>();
    List<ItemWriter<? super CsvRow>> delegates = new ArrayList<>(2);
    delegates.add(csvRowWriter);
    delegates.add(csvRowLogWriter);
    csvRowCompositeItemWriter.setDelegates(delegates);


    classifierCompositeItemWriter.setClassifier(csvRow -> {
      if (csvRow.getRow() != null && csvRow.getRow().contains("log")) {
        return csvRowCompositeItemWriter;
      }
      return csvRowWriter;
    });

    return classifierCompositeItemWriter;
  }

  @Bean
  public Job importUserJob(JobCompletionNotificationListener listener, Step masterStep) {
    return jobBuilderFactory.get("importUserJob")
        .incrementer(new RunIdIncrementer())
        .listener(listener)
        .flow(masterStep)
        .end()
        .build();
  }

  @Bean
  public Step step1() throws IOException {
    return stepBuilderFactory.get("step1")
        .<CsvRow, CsvRow>chunk(10)
        .reader(reader(null, null, null))
        .processor(processor(null, null))
        .writer(classifierCompositeItemWriter( null, null))
        .build();
  }

  @Bean
  public Step masterStep(Step twoStepFlow) {
    return stepBuilderFactory.get("masterStep")
        .partitioner(twoStepFlow)
        .partitioner("slaveStep", partitioner())
        .gridSize(2)
        .taskExecutor(taskExecutor())
        .build();


  }

  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setMaxPoolSize(2);
    taskExecutor.setCorePoolSize(2);
    taskExecutor.afterPropertiesSet();
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    return taskExecutor;
  }

  @Bean
  public Step twoStepFlow(Flow flow1) {
    return stepBuilderFactory.get("twoStepFlow")
        .flow(flow1)
        .build();

  }

  @Bean
  public Flow buildFlow(Step step1) {
    Flow flow1 = new FlowBuilder<SimpleFlow>("flow1")
        .start(step1)
        .build();
    return flow1;

  }


  @Bean("partitioner")
  @StepScope
  public Partitioner partitioner() {

    CustomMultiResourcePartitioner partitioner = new CustomMultiResourcePartitioner();
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource[] resources = null;
    try {
      resources = resolver.getResources(intDirectory + "/*.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    partitioner.setResources(resources);
    partitioner.partition(10);
    return partitioner;
  }

}
