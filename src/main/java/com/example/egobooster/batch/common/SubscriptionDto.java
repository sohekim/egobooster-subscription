package com.example.egobooster.batch.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class SubscriptionDto {

  private Long id;
  private String email;
  private LocalDateTime updateDate;

  @JsonCreator
  public SubscriptionDto(
      @JsonProperty("id") Long id,
      @JsonProperty("email") String email,
      @JsonProperty("updateDate") LocalDateTime updateDate) {
    this.id = id;
    this.email = email;
    this.updateDate = updateDate;
  }

  public String getEmail() {
    return email;
  }

}
