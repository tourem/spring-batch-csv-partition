package com.larbotech.batch.writer;

import com.larbotech.batch.model.CsvRow;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

public class CsvRowLogWriter implements ItemWriter<CsvRow> {

  private static final Logger log = LoggerFactory.getLogger(CsvRowLogWriter.class);

  @Override
  public void write(List<? extends CsvRow> list) throws IOException {

    for (CsvRow csvRow : list) {
      log.info("log writer csv" +csvRow);
    }

  }
}
