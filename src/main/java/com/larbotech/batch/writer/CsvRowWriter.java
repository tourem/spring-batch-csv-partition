package com.larbotech.batch.writer;

import com.larbotech.batch.model.CsvRow;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

public class CsvRowWriter implements ItemWriter<CsvRow> {

  private static final Logger log = LoggerFactory.getLogger(CsvRowWriter.class);

  private JobExecution jobExecutionContext;

  private String threadName;
  private String fileName;
  private String[] header;
  private String head;
  private ICsvMapWriter csvWriter;

  public CsvRowWriter(String threadName, String fileName, JobExecution jobExecutionContext) throws IOException {
    this.threadName = threadName;
    this.fileName = fileName;
    this.csvWriter = new CsvMapWriter(new FileWriter(fileName), CsvPreference.STANDARD_PREFERENCE);
    this.jobExecutionContext = jobExecutionContext;
    header = (String[])jobExecutionContext.getExecutionContext().get(threadName);
    log.info(threadName+"------------------------------------------"+header);
    this.csvWriter.writeHeader(header);

  }

  @Override
  public void write(List<? extends CsvRow> list) throws IOException {
    if (header == null) {
      header = (String[])jobExecutionContext.getExecutionContext().get(threadName);
      log.info(threadName+"------------------------------------------"+header);
      this.csvWriter.writeHeader(header);
    }
    for (CsvRow csvRow:list){
      csvWriter.write(csvRow.getMapRow(), header);
    }

  }

  @BeforeStep
  public void beforeStep(StepExecution stepExecution) {
    jobExecutionContext = stepExecution.getJobExecution();
  }

  @AfterStep
  public void afterStep(StepExecution stepExecution) throws IOException{
    csvWriter.flush();
    csvWriter.close();
  }
}
