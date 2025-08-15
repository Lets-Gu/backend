package avengers.lion.member.service;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.member.domain.Member;
import avengers.lion.member.dto.MyProfileResponse;
import avengers.lion.member.dto.UpdateNicknameRequest;
import avengers.lion.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /*
    내 포인트 조회
     */
    public Long getMyPoint(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new BusinessException(ExceptionType.MEMBER_NOT_FOUND));
        return member.getPoint();
    }

    @Transactional
    public void updateNickname(Long memberId, UpdateNicknameRequest request){
        String nickname = request.nickname();
        if(memberRepository.existsByNickname(nickname))
            throw new BusinessException(ExceptionType.NICKNAME_ALREADY_EXISTS);
        Member member = memberRepository.findById(memberId)
                        .orElseThrow(()-> new BusinessException(ExceptionType.MEMBER_NOT_FOUND));
        member.updateNickname(nickname);
    }
}
