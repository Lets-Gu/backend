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
        // 장소 데이터를 전부 가져오기
        List<Place> places = placeRepository.findAll();
        for(Place place : places) {
            GeocodeInfo info = placeService.geocode(place.getName());
            BigDecimal lat = BigDecimal.valueOf(info.lat());
            BigDecimal lng = BigDecimal.valueOf(info.lng());
            place.setGeocodingResult(lat, lng);
            placeRepository.save(place);
        }
    }
}
