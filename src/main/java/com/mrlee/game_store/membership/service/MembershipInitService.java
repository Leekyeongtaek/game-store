package com.mrlee.game_store.membership.service;

import com.mrlee.game_store.membership.domain.Member;
import com.mrlee.game_store.membership.domain.MemberSubscription;
import com.mrlee.game_store.membership.domain.Membership;
import com.mrlee.game_store.membership.repository.*;
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
    private final MemberSubscriptionRepository memberSubscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;

    @PostConstruct
    public void initService() {
        deleteAllMemberMembership();
        initializeFreeMemberships();
        deleteAllPayment();
        deleteAllRefund();
    }

    private void deleteAllRefund() {
        refundRepository.deleteAll();
    }

    private void deleteAllPayment() {
        paymentRepository.deleteAll();
    }

    private void deleteAllMemberMembership() {
        memberSubscriptionRepository.deleteAll();
    }

    private void initializeFreeMemberships() {
        List<Member> members = memberRepository.findAll();
        Membership freeMembership = membershipRepository.findAll().stream()
                .findAny().filter(e -> e.getName().equals(Membership.MembershipType.FREE))
                .get();

        for (Member member : members) {
            MemberSubscription memberSubscription = MemberSubscription.createMemberSubscription(member.getId(), freeMembership);
            memberSubscriptionRepository.save(memberSubscription);
        }
    }
}
