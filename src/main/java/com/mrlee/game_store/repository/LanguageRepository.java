package com.mrlee.game_store.repository;

import com.mrlee.game_store.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {
}
