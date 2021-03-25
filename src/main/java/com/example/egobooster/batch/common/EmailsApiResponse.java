package com.example.egobooster.batch.common;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmailsApiResponse {

  List<SubscriptionDto> emails;

}
