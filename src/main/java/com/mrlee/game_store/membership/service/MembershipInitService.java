package com.mrlee.game_store.membership.service;

import com.mrlee.game_store.membership.domain.Member;
import com.mrlee.game_store.membership.domain.MemberMembership;
import com.mrlee.game_store.membership.domain.Membership;
import com.mrlee.game_store.membership.repository.MemberMembershipRepository;
import com.mrlee.game_store.membership.repository.MemberRepository;
import com.mrlee.game_store.membership.repository.MembershipRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MembershipInitService {

    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final MemberMembershipRepository memberMembershipRepository;

    @PostConstruct
    public void initService() {
        deleteAllMemberMembership();
        initializeFreeMemberships();
    }

    private void deleteAllMemberMembership() {
        memberMembershipRepository.deleteAll();
    }

    private void initializeFreeMemberships() {
        List<Member> members = memberRepository.findAll();
        Membership freeMembership = membershipRepository.findAll().stream()
                .findAny().filter(e -> e.getName().equals(Membership.MembershipType.FREE))
                .get();

        for (Member member : members) {
            MemberMembership memberMembership = new MemberMembership(member.getId(), freeMembership.getId(), freeMembership.getDurationDays());
            memberMembershipRepository.save(memberMembership);
        }
    }
}
