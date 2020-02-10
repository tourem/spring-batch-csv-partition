package com.larbotech.batch.reader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CsvRowReaderTest {

  @Value("classpath*:data/*.csv")
  private Resource[] csvs;

  private CsvRowReader csvRowReader;

  @BeforeEach
   void init() throws IOException {

    if (csvRowReader == null) {
      Resource csv = new ClassPathResource("data/sample-data.csv");
      csvRowReader = new CsvRowReader("partion#1", csv.getFile().getAbsolutePath(),
          new JobExecution(1L));
    }
  }


  @DisplayName("csv reader")
  @Test
  void read() throws Exception {
    assertThat(csvRowReader.read()).isNotNull();
  }
}
