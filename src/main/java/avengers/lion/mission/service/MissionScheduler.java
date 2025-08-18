package avengers.lion.mission.service;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.mission.domain.BatchStatus;
import avengers.lion.mission.domain.Mission;
import avengers.lion.mission.domain.MissionBatches;
import avengers.lion.mission.domain.MissionTemplate;
import avengers.lion.mission.repository.MissionBatchRepository;
import avengers.lion.mission.repository.MissionRepository;
import avengers.lion.mission.repository.MissionTemplateRepository;
import avengers.lion.place.domain.PlaceCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MissionScheduler implements Job {

    private final MissionRepository missionRepository;
    private final MissionBatchRepository missionBatchRepository;
    private final MissionTemplateRepository missionTemplateRepository;

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        // 기존 배치 상태 업데이트
        finishMissionBatch();
        MissionBatches newMissionBatches = startMissionBatch();
        // GPT에 보낼 미션 데이터들
        List<MissionTemplate> missions = new ArrayList<>();
        /*
        각 카테고리별로 조회
         */
        for(PlaceCategory category : PlaceCategory.values()){
            // 현재일 기준으로, 20일 전에 조회된 데이터는 제외
            LocalDateTime now = LocalDate.now().minusDays(20).atStartOfDay();
            List<MissionTemplate> templates = missionTemplateRepository.findRandomByCategory(category, now);
            if (templates.isEmpty()) {
                throw new BusinessException(ExceptionType.PLACE_NOT_FOUND);
            }
            MissionTemplate missionTemplate = templates.get(0);
            // 마지막으로 조회된 날짜, 카운트 개수 설정
            missionTemplate.updateSelectionCount();
            missions.add(missionTemplate);
        }
        missionBatchRepository.save(newMissionBatches);
        for(MissionTemplate missionTemplate : missions){
            Mission mission = Mission.createFromTemplate(missionTemplate, newMissionBatches);
            missionRepository.save(mission);
        }
    }

    /*
    기존 배치 종료
     */
    public void finishMissionBatch(){
        MissionBatches missionBatches =missionBatchRepository.findFirstByBatchStatusOrderByBatchStartDateDesc(BatchStatus.ACTIVE);
        if(missionBatches != null){
            missionBatches.completeBatch();
            missionBatches.getMissions().forEach(Mission::finishMission);
            missionBatchRepository.save(missionBatches);
        }
    }

    /*
    새 미션배치 생성
     */
    public MissionBatches startMissionBatch(){
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(14);
        String batchName = "Batch_"+startDate.toLocalDate().toString();
        return MissionBatches.createNewBatch(batchName, startDate, endDate);
    }
}
