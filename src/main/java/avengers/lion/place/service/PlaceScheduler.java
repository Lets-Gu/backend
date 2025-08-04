package avengers.lion.place.service;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.place.domain.Place;
import avengers.lion.place.dto.GeocodeInfo;
import avengers.lion.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.math.BigDecimal;
import java.util.List;

/*
DB에서 장소를 전부 꺼내서 좌표 설정
 */
@RequiredArgsConstructor
@Slf4j
public class PlaceScheduler implements Job {

    private final PlaceRepository placeRepository;
    private final PlaceService placeService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("장소 선택 스케줄러 시작");
        // 장소 데이터를 전부 가져오기
        List<Place> places = placeRepository.findAll();

        //
        for(Place place : places){
            try{
                GeocodeInfo info = placeService.geocode(place.getName());
                String address = info.formattedAddress();
                BigDecimal lat = BigDecimal.valueOf(info.lat());
                BigDecimal lng = BigDecimal.valueOf(info.lng());
                place.setGeocodingResult(address, lat, lng);
                placeRepository.save(place);
            } catch (Exception e){
                log.info("장소 저장 과정에서 에러 발생");
                throw new BusinessException(ExceptionType.PLACE_FORMAT_ERROR);
            }
        }
    }
}
