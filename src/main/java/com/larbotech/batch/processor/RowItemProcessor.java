package com.larbotech.batch.processor;

import com.larbotech.batch.model.CsvRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class RowItemProcessor implements ItemProcessor<CsvRow, CsvRow> {

  private static final Logger log = LoggerFactory.getLogger(RowItemProcessor.class);

  @Override
  public CsvRow process(final CsvRow csvRow) throws Exception {
    if (csvRow.getMapRow() != null) {
      log.info(" ====================Process Row ==================================");
      log.info("Converting (" + csvRow.getRow() + ")");
    }

    return csvRow;
  }

}
