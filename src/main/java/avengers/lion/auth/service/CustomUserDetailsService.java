package avengers.lion.auth.service;

import avengers.lion.auth.domain.CustomUserDetails;
import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.member.domain.Member;
import avengers.lion.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ExceptionType.MEMBER_NOT_FOUND));
        return new CustomUserDetails(member);
    }
}