package com.example.egobooster.batch.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class BoosterDto {

  private Long id;
  private String text;
  private LocalDateTime updateDate;

  @JsonCreator
  public BoosterDto(
      @JsonProperty("id") Long id,
      @JsonProperty("text") String text,
      @JsonProperty("updateDate") LocalDateTime updateDate) {
    this.id = id;
    this.text = text;
    this.updateDate = updateDate;
  }

  public String getBooster () {
    return text;
  }

}
