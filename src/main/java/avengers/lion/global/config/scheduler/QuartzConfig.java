package avengers.lion.global.config.scheduler;

import avengers.lion.mission.service.MissionScheduler;
import avengers.lion.place.service.PlaceScheduler;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail missionGenerateJobDetail(){
        return JobBuilder.newJob(MissionScheduler.class)
                .withIdentity("myJob")   //  이 Job의 고유한 이름
                .storeDurably() // 트리거 없이 JobDetail 유지
                .build();
    }

    /*
    언제 실행될지 정하는 스케줄
     */
    @Bean
    public Trigger jobTrigger(JobDetail missionGenerateJobDetail){
        Date startTime = Date.from(
                LocalDateTime.now().plusDays(21)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
        return TriggerBuilder.newTrigger()
                .forJob(missionGenerateJobDetail)  // 어떤 JobDetail을 실행할지
                .withIdentity("myTrigger")  // 트리거 고유 ID
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInHours(24*14)  // 14일 마다
                                .repeatForever()   // 무한 반복
                )
                .startAt(startTime) // 앱 실행 시 즉시 시작
                .build();
    }

    @Bean
    public JobDetail googleApiJobDetail(){
        return JobBuilder.newJob(PlaceScheduler.class)
                .withIdentity("place")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger googleApiJobTrigger(JobDetail googleApiJobDetail){
        Date startTime = Date.from(
                LocalDateTime.now().plusDays(21)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
        return TriggerBuilder.newTrigger()
                .forJob(googleApiJobDetail)
                .withIdentity("placeTrigger")
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInHours(24*20)
                                .repeatForever()
                )
                .startAt(startTime)
                .build();
    }

}
