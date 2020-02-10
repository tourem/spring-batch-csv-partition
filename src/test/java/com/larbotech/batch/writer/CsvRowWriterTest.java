package com.larbotech.batch.writer;

import static org.assertj.core.api.Assertions.assertThat;

import com.larbotech.batch.model.CsvRow;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.core.JobExecution;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CsvRowWriterTest {

  final String[] header = new String[]{"CustomerId", "CustomerName", "Country", "PinCode", "Email"};

  private CsvRowWriter csvRowWriter;


  @DisplayName("csv writer")
  @Test
  void write(@TempDir Path tempDir) throws Exception {

    //GIVEN
    Path outFile = tempDir.resolve("data.csv");
    if (csvRowWriter == null) {
      JobExecution jobExecution = new JobExecution(1L);
      jobExecution.getExecutionContext().put("partion#1", header);
      csvRowWriter = new CsvRowWriter("partion#1", outFile.toFile().getAbsolutePath(),
          jobExecution);

    }
    Map<String, Object> mapRow = new HashMap<>();
    mapRow.put("CustomerId", 10001);
    mapRow.put("CustomerName", "Lokesh");
    mapRow.put("Country", "Mali");
    mapRow.put("PinCode", "110001");
    mapRow.put("Email", "test@larbotech.ml");
    String row = "10001,Lokesh,Mali,110001,test@larbotech.ml";

    CsvRow csvRow = CsvRow.build(mapRow, row, true);

    List<CsvRow> rows = new ArrayList<>(1);
    rows.add(csvRow);

    //WHEN
    csvRowWriter.write(rows);

    //THEN
    assertThat(outFile.toFile().length()).isPositive();
  }
}