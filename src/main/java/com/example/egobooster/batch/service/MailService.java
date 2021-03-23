package com.example.egobooster.batch.service;

import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String myAddress;

  public List<String> findEmails(List<String> list) {
    List<String> emails = jdbcTemplate
        .query("select email from subscription", rs -> {
          while (rs.next()) {
            String email = rs.getString("email");
            list.add(email);
          }
          return list;
        });
    return emails;
  }

  public String findTodayEgoBooster() {
    // TODO
    // query 대신 api 호출?
    // 찾을때까지 호출
    String egoBooster = jdbcTemplate
        .query("select * from booster WHERE id = 1", rs -> {
          String booster = "";
          while (rs.next()) {
            booster = rs.getString("text");
          }
          return booster;
        });
    return egoBooster;
  }

  public String getHTMLText(String egoBooster) {

    //    StringBuilder stringBuilder = new StringBuilder();
//    stringBuilder.append("<h2>&#10024; Good Morning Sunshine &#10024;</h2>");
//    stringBuilder.append("<div class=\"card\" style = \"box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);"
//        + "  transition: 0.3s;"
//        + "  width: 60%;\">");
//    stringBuilder.append("<div class=\"container\" style = \"padding: 2px 16px;\">");
//    stringBuilder.append("<h3>" + egoBooster + "</h3>");
//    stringBuilder.append("</div> </div>");
//    stringBuilder.append("<p>Powered by Sohee's Love &#129505;&#128156;&#128154;</p>");
//    stringBuilder.append(
//        "<p>If you want to <u> unsubscribe</u>, you have to manually send us an email with 10 good convincing and well formed reasons why you must unsubscribe. </p>");

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



//    for (String toAddress : emailList) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
    String htmlMsg = "<h3>" + egoBooster + "</h3>";
    helper.setText(getHTMLText(egoBooster), true); // Use this or above line.
    helper.setTo("kim66s@mtholyoke.edu");
    helper.setSubject("Your Daily Dose of Ego Booster");
    helper.setFrom(myAddress);
    mailSender.send(mimeMessage);

//    }

  }

}
