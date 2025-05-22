package com.mrlee.game_store.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class MemberRes {
    private String email;
    private String birth;
    private char gender;
    private LocalDateTime createdAt;

    public MemberRes(String email, String birth, char gender, LocalDateTime createdAt) {
        this.email = email;
        this.birth = birth;
        this.gender = gender;
        this.createdAt = createdAt;
    }
}
