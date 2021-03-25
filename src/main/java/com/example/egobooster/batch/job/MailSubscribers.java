package com.example.egobooster.batch.job;

import com.example.egobooster.batch.service.MailService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MailSubscribers {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  private List<String> emailList;
  private String egoBooster;

  private final MailService mailService;

  @Bean
  public Job mailSubscriberJob() {
    return jobBuilderFactory.get("mailSubscribers").start((init()))
        .next(getTodayEgoBooster())
        .next(addEmails())
        .next(sendEmails())
        .build();
  }

  @Bean
  public Step init() {
    return stepBuilderFactory.get("init").tasklet(((contribution, chunkContext) -> {
      log.info(">>>>>> init");
      emailList = new ArrayList<>();
      return RepeatStatus.FINISHED;
    }
    )).build();
  }

  @Bean
  public Step getTodayEgoBooster() {
    return stepBuilderFactory.get("findEgoBooster").tasklet(((contribution, chunkContext) -> {
      log.info(">>>>>> getting today's ego booster");
      egoBooster = mailService.findTodayEgoBooster();
      return RepeatStatus.FINISHED;
    }
    )).build();
  }

  @Bean
  public Step addEmails() {
    return stepBuilderFactory.get("addEmails").tasklet(((contribution, chunkContext) -> {
      log.info(">>>>>> adding emails");
      emailList = mailService.findEmails(emailList);
      for (String email : emailList) {
        log.info(email);
      }
      return RepeatStatus.FINISHED;
    }
    )).build();
  }

  @Bean
  public Step sendEmails() {
    return stepBuilderFactory.get("sendEmails").tasklet(((contribution, chunkContext) -> {
      log.info(">>>>>> sending emails");
      mailService.mailSend(egoBooster, emailList);
      return RepeatStatus.FINISHED;
    }
    )).build();
  }

}
