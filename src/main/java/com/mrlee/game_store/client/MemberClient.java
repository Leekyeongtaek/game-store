package com.mrlee.game_store.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "member-service", url = "http://localhost:9090")
public interface MemberClient {

    @GetMapping("/members/")
    List<MemberRes> getMembers();
}
