package com.larbotech.batch;

import com.larbotech.batch.model.CsvRow;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

public class Main {

  public static void main(String[] args) throws Exception{
    readCsvLib();
  }


  private static void usingFileWriter() throws IOException
  {
    String fileContent = "CustomerId,CustomerName,Country,PinCode,Email";

    FileWriter fileWriter = new FileWriter("/Users/mtoure/dev/bnp/data/data.csv");

    fileWriter.write(fileContent);
    for (int i=0;i<50000000; i++){
      fileWriter.write("\n");
      fileWriter.write("10003,Blue,France,330003,ghi@gmail.com");
    }
    fileWriter.close();
  }

  private static void readCsvLib() throws IOException
  {

    ICsvMapReader listReader = new CsvMapReader(new FileReader("/Users/mtoure/dev/bnp/data/data.csv"), CsvPreference.STANDARD_PREFERENCE);
    String[] headers = listReader.getHeader(true);
    CellProcessor[] processors = getProcessors();
    long startTime = System.nanoTime();
    Map<String, Object> fieldsInCurrentRow = listReader.read(headers, processors);
    while (fieldsInCurrentRow != null) {
      fieldsInCurrentRow = listReader.read(headers, processors);
      System.out.print(listReader.getUntokenizedRow());
    }

    long endTime = System.nanoTime();
    long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
    System.out.println("Total elapsed time: " + elapsedTimeInMillis + " ms");


  }

  public static void readMappedByteBuffer() {

    long startTime = System.nanoTime();

    List<String> res = new ArrayList<>();

    try {
      RandomAccessFile aFile = new RandomAccessFile("/Users/mtoure/dev/bnp/data/data.csv", "r");
      FileChannel inChannel = aFile.getChannel();
      MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());

      for (int i = 0; i < buffer.limit(); i++) {
        byte read = buffer.get();
        Byte.toString(read);
       // System.out.print((char)read);

      }
      aFile.close();

    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    long endTime = System.nanoTime();
    long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
    System.out.println(" MappedByteBuffer Total elapsed time: " + elapsedTimeInMillis + " ms");
    res.size();
  }

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
}
