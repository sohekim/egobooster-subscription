package com.example.egobooster.batch.service;

import com.example.egobooster.batch.common.BoosterDto;
import com.example.egobooster.batch.common.SubscriptionDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender mailSender;
  private final RestTemplate restTemplate;

  @Value("${spring.mail.username}")
  private String myAddress;

  @Value("${url}")
  private String url;

  public List<String> findEmails(List<String> list) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity request = new HttpEntity(headers);
    String subscriptionURL = url + "/api/v1/subscriptions";
    ResponseEntity<List<SubscriptionDto>> response = restTemplate.exchange(
        subscriptionURL,
        HttpMethod.GET,
        request,
        new ParameterizedTypeReference<>() {
        },
        1
    );

    if (response.getStatusCode() == HttpStatus.OK) {
      System.out.println("Email Request Successful.");
      System.out.println(response.getBody());
      return response.getBody().stream().map(dto -> dto.getEmail()).collect(Collectors.toList());
    } else {
      System.out.println("Email Request Failed");
      System.out.println(response.getStatusCode());
      return new ArrayList<>();
    }

  }

  public String findTodayEgoBooster() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity request = new HttpEntity(headers);
    String boosterURL = url + "/api/v1/boosters/random";
    ResponseEntity<BoosterDto> response = restTemplate.exchange(
        boosterURL,
        HttpMethod.GET,
        request,
        BoosterDto.class,
        1
    );
    if (response.getStatusCode() == HttpStatus.OK) {
      System.out.println("Booster Request Successful.");
      System.out.println(response.getBody().getBooster());
      return response.getBody().getBooster();
    } else {
      System.out.println("Booster Request Failed");
      System.out.println(response.getStatusCode());
      return "";
    }
  }

  public String getHTMLText(String egoBooster) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        "<div class=\"courses-container\"><div class=\"course\" style=\"border-radius: 10px;box-shadow: 0 10px 10px rgba(0, 0, 0, 0.2);display: flex;max-width: 100%;margin: 20px;overflow: hidden;width: 60%;\"><div class=\"course-preview\" style =\"background-color: #FF5733;color: #fff;padding: 30px;max-width: 250px;\"><h5 style = \"opacity: 0.6;margin: 0;letter-spacing: 1px;text-transform: uppercase;\">&#10024; Good Morning Sunshine &#10024;</h5><h2 style = \"letter-spacing: 1px;margin: 10px 0;\">Ego Booster<a href=\"#\" style = \"color: #fff;display: inline-block;font-size: 10px;opacity: 0.6;margin-top: 30px;text-decoration: none;\">Ego-Booster.com &#8250;</a></div><div class=\"course-info\" style = \"padding: 30px;position: relative;width: 100%;\"><h2>\"");
    stringBuilder.append(egoBooster);
    stringBuilder.append("\"</h2></div></div></div>");
    stringBuilder.append("<p>Powered by Sohee's Love &#129505;&#128156;&#128154;</p>");
    stringBuilder.append(
        "<p>If you want to <u> unsubscribe</u>, you have to manually send us an email with 10 good convincing and well formed reasons why you must unsubscribe. </p>");
    return stringBuilder.toString();
  }

  public void mailSend(String egoBooster, List<String> emailList) throws MessagingException {

    for (String toAddress : emailList) {
      log.info(toAddress);
    }
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
    String htmlMsg = "<h3>" + egoBooster + "</h3>";
    helper.setText(getHTMLText(egoBooster), true); // Use this or above line.
    helper.setTo("kim66s@mtholyoke.edu");
    helper.setSubject("Your Daily Dose of Ego Booster");
    helper.setFrom(myAddress);
    mailSender.send(mimeMessage);
  }

}
