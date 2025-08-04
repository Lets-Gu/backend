package avengers.lion.mission.service;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MissionScheduler implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String jobType = jobDataMap.getString("jobType");

        if("mission".equals(jobType)){

        }else if("place".equals(jobType)){

        } else {

        }
    }

    /*
    place에 있는 장소 이름을 이용하여 구글 api 호출하기
     */
    public void getPlaceWithGoogleGeocodingAPI(){

    }

    /*
    장소 데이터를 카테고리별로 5개 추출하여, gpt에 요청보내기
     */
}
