package com.larbotech.batch.model;

import java.util.Map;

public class CsvRow {

  private Map<String, Object> mapRow;
  private String row;
  private boolean write;

  private CsvRow(Map<String, Object> mapRow, String row, boolean write) {
    this.mapRow = mapRow;
    this.row = row;
    this.write = write;
  }

  public static CsvRow build(Map<String, Object> mapRow, String row, boolean write){
    return new CsvRow(mapRow, row, write);
  }

  public Map<String, Object> getMapRow() {
    return mapRow;
  }

  public void setMapRow(Map<String, Object> mapRow) {
    this.mapRow = mapRow;
  }

  public String getRow() {
    return row;
  }

  public void setRow(String row) {
    this.row = row;
  }

  public boolean isWrite() {
    return write;
  }

  public void setWrite(boolean write) {
    this.write = write;
  }
}
