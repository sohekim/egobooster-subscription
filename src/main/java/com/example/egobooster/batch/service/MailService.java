package com.example.egobooster.batch.service;

import com.example.egobooster.batch.common.BoosterDto;
import com.example.egobooster.batch.common.SubscriptionDto;
import java.io.File;
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

  @Value("${key}")
  private String key;

  private Integer boosterNum;

  public List<String> findEmails() {
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

    boosterNum = findBoosterNum();

    String booster = "";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity request = new HttpEntity(headers);
    //retry up to 5 times
    for (int i = 0; i < 5; i++) {
      String boosterURL = url + "/api/v1/boosters/" + boosterNum;
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
        booster = response.getBody().getBooster();
        boosterNum++;
        break;
      } else {
        System.out.println("Booster Request Failed");
        System.out.println(response.getStatusCode());
      }
    }
    return booster;
  }

  public void setBatchNum() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("key", key);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity request = new HttpEntity(headers);
    String boosterURL = url + "/api/v1/batch/set?num=" + boosterNum;
    System.out.println(boosterURL);
    ResponseEntity<Object> response = restTemplate.exchange(
        boosterURL,
        HttpMethod.PUT,
        request,
        Object.class,
        1
    );
    if (response.getStatusCode() == HttpStatus.OK) {
      System.out.println("increment Request Successful.");
    } else {
      System.out.println("Booster Request Failed");
    }
  }


  public Integer findBoosterNum() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("key", key);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity request = new HttpEntity(headers);
    String boosterURL = url + "/api/v1/batch";
    ResponseEntity<Integer> response = restTemplate.exchange(
        boosterURL,
        HttpMethod.GET,
        request,
        Integer.class,
        1
    );
    if (response.getStatusCode() == HttpStatus.OK) {
      System.out.println("Booster Num Found Successful.");
      System.out.println(response.getBody());
      return response.getBody();
    } else {
      System.out.println("Booster Num Found Failed");
      System.out.println(response.getStatusCode());
      return 1;
    }
  }

  public String getHTMLText(String egoBooster) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        "    <div class=\"container\" style=\"max-width: 280px;min-height: 300px;display: flex;\n"
            + "    justify-content: center;\n"
            + "    margin-left: auto;\n"
            + "    margin-right: auto;\n"
            + "    text-align: center;\n"
            + "    border: 2px solid #f2711d;\n"
            + "    \">\n"
            + "        <div class=\"textBox\">\n"
            + "            <div class=\"textBox-preview\">\n"
            + "                <img name =\"booster\" src='cid:morningImage' alt=\"\" style=\"width: 200px; margin-top: 7%;\" />\n"
            + "                <div class=\"textBox-info\" style=\" padding: 10px; background-color: #f2711d; color: white; margin-top: 5%;\">\n"
            + "                    <h2>");
    stringBuilder.append(egoBooster);
    stringBuilder.append("</h2>\n"
        + "                </div>\n"
        + "            </div>\n"
        + "            <div>\n"
        + "                <div><img src='cid:greenImage' alt=\"\" style=\"width: 20px; margin-top: 10px;\" /></div>\n"
        + "                <img src='cid:soheeImage' alt=\"\" style=\"width: 200px; margin-top: 2%; margin-bottom: 5%;\" />\n"
        + "            </div>\n"
        + "        </div>\n"
        + "    </div>");
    return stringBuilder.toString();
  }

  public void mailSend(String egoBooster, List<String> emailList)
      throws MessagingException {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
    helper.setText(getHTMLText(egoBooster), true);
    helper.addInline("morningImage",
        new File("src/main/java/com/example/egobooster/batch/images/morning.png"));
    helper.addInline("greenImage",
        new File("src/main/java/com/example/egobooster/batch/images/green.png"));
    helper.addInline("soheeImage",
        new File("src/main/java/com/example/egobooster/batch/images/sohee.png"));
    helper.setSubject("Daily Ego Booster");
    helper.setFrom(myAddress);
    for (String toAddress : emailList) {
      log.info(toAddress);
      helper.setTo(toAddress);
      mailSender.send(mimeMessage);
    }
  }

}
