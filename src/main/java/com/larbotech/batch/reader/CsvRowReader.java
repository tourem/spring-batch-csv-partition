package com.larbotech.batch.reader;

import com.larbotech.batch.model.CsvRow;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.ItemReader;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;


public class CsvRowReader implements ItemReader<CsvRow> {

  private JobExecution jobExecutionContext;
  private String threadName;
  private String fileName;
  private ICsvMapReader listReader;
  final CellProcessor[] processors;
  private String[] headers;
  private Integer errorCount = 0;
  private Integer nbLine = 0;

  public CsvRowReader(String threadName, String fileName, JobExecution jobExecutionContext) throws IOException {
    this.threadName = threadName;
    this.fileName = fileName;
    this.listReader = new CsvMapReader(new FileReader(fileName), CsvPreference.STANDARD_PREFERENCE);
    this.headers = listReader.getHeader(true);
    this.processors = getProcessors();
    this.jobExecutionContext = jobExecutionContext;
    this.jobExecutionContext.getExecutionContext().put(threadName, headers);
  }

 /* @BeforeStep
  public void beforeStep(StepExecution stepExecution) {
    jobExecutionContext = stepExecution.getJobExecution();
    if (jobExecutionContext.getExecutionContext().get(threadName) == null) {
      jobExecutionContext.getExecutionContext().put(threadName, headers);
    }
  }*/

  @Override
  public CsvRow read()
      throws Exception {

    try {
      Map<String, Object> fieldsInCurrentRow = listReader.read(headers, processors);
      if (fieldsInCurrentRow != null) {
        nbLine++;
        return CsvRow.build(fieldsInCurrentRow, listReader.getUntokenizedRow(), errorCount == 0);
      }
      return null;

    } catch (IOException e) {
      nbLine++;
      errorCount++;
      return CsvRow.build(new HashMap<>(), null, false);
    }
  }

  /**
   * Sets up the processors used for the examples.
   */
  private static CellProcessor[] getProcessors() {
    final String emailRegex = "[a-z0-9\\._]+@[a-z0-9\\.]+";
    StrRegEx.registerMessage(emailRegex, "must be a valid email address");

    return new CellProcessor[]{
        new NotNull(new ParseInt()), // CustomerId
        new NotNull(), // CustomerName
        new NotNull(), // Country
        new Optional(new ParseLong()), // PinCode
        new StrRegEx(emailRegex) // Email
    };
  }


  public JobExecution getJobExecutionContext() {
    return jobExecutionContext;
  }
}
