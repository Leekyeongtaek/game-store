package com.mrlee.game_store.domain;

import com.mrlee.game_store.common.AuditingDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "game_group")
@Entity
public class GameGroup extends AuditingDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_group_id")
    private Long id;
    private String name;

    public GameGroup(String name) {
        this.name = name;
    }

    public void update(String name) {
        this.name = name;
    }
}
