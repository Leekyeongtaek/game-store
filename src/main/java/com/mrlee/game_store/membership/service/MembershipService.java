package com.mrlee.game_store.membership.service;

import com.mrlee.game_store.membership.domain.Membership;
import com.mrlee.game_store.membership.domain.Membership.MembershipType;
import com.mrlee.game_store.membership.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MembershipService {

    //JPA 엔티티는 비즈니스 로직을 포함하기보다는, 데이터(상태) 관리 및 간단한 상태 체크 메서드 정도만 가지는 것이 운영 환경에서 더 적절한 방식이다.
    //엔티티 내에서 다른 엔티티를 참조하는 코드는 LazyInitializationException 발생 가능성, 엔티티는 데이터 저장 및 조회가 주 역할이다.
    //결론: 엔티티는 비즈니스 로직이 아니라, "상태 관리"와 관련된 로직만 가지는 것이 좋다.
    //SRP: 하나의 메서드는 하나의 기능만을 담당해야한다

    private final MembershipRepository membershipRepository;

    public Membership getMembershipById(Long membershipId) {
        return membershipRepository.findById(membershipId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버십을 찾을 수 없습니다: " + membershipId));
    }

    public Membership getFreeMembership() {
        return membershipRepository.findByName(MembershipType.FREE)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 멤버십을 찾을 수 없습니다: " + MembershipType.FREE));
    }
}
