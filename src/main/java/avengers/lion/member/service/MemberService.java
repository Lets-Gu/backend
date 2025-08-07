package avengers.lion.member.service;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.member.Member;
import avengers.lion.member.dto.MyProfileResponse;
import avengers.lion.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /*
    마이페이지 내 프로필 조회
     */
    public MyProfileResponse getMyProfile(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new BusinessException(ExceptionType.MEMBER_NOT_FOUND));
        return MyProfileResponse.of(member);
    }
}
