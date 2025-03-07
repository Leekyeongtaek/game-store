package com.mrlee.game_store.service;

import com.mrlee.game_store.domain.*;
import com.mrlee.game_store.dto.request.GameSaveForm;
import com.mrlee.game_store.dto.request.GameDiscountSearchCondition;
import com.mrlee.game_store.dto.request.GameUpdateForm;
import com.mrlee.game_store.dto.response.*;
import com.mrlee.game_store.repository.*;
import com.mrlee.game_store.dto.response.GamePromotionResponse;
import com.mrlee.game_store.repository.query.GameQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final GameQueryRepository gameQueryRepository;
    private final PublisherRepository publisherRepository;
    private final GenreRepository genreRepository;
    private final PlatformRepository platformRepository;
    private final LanguageRepository languageRepository;

    public Long saveGame(GameSaveForm form) {
        Game game = form.toEntity();

        List<Genre> genres = genreRepository.findAllById(form.getGameGenreIds());
        genres.forEach(genre -> game.addGameGenre(new GameGenre(genre)));

        List<Platform> platforms = platformRepository.findAllById(form.getGamePlatformIds());
        platforms.forEach(platform -> game.addGamePlatform(new GamePlatform(platform)));

        List<Language> languages = languageRepository.findAll();

        List<Language> gameAudioLanguages = languages.stream()
                .filter(language -> form.getGameAudioLanguageIds().contains(language.getId()))
                .toList();

        gameAudioLanguages.forEach(language -> game.addGameAudioLanguage(new GameAudioLanguage(language)));

        List<Language> gameScreenLanguages = languages.stream()
                .filter(language -> form.getGameScreenLanguageIds().contains(language.getId()))
                .toList();

        gameScreenLanguages.forEach(language -> game.addGameScreenLanguage(new GameScreenLanguage(language)));

        return gameRepository.save(game).getId();
    }

    public void updateGame(Long gameId, GameUpdateForm form) {
        Game game = gameRepository.findByIdJoinFetchGameDiscount(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        game.update(form);
        handleGameDiscountUpdate(game, form);
        handleGameGenreUpdate(game, form);
        handleGamePlatformUpdate(game, form);
        handleGameAudioLanguageUpdate(game, form);
        handleGameScreenLanguageUpdate(game, form);
    }

    private void handleGameDiscountUpdate(Game game, GameUpdateForm form) {
        game.applyGameDiscount(form.getIsDiscounted(), form.getGameDiscount());
    }

    private void handleGameGenreUpdate(Game game, GameUpdateForm form) {
        List<Genre> newGenres = genreRepository.findAllById(form.getGameGenreIds());

        List<GameGenre> existingGameGenres = game.getGameGenres();

        Set<Long> existingGenreIds = existingGameGenres.stream()
                .map(existing -> existing.getGenre().getId())
                .collect(Collectors.toSet());

        List<GameGenre> newGameGenres = newGenres.stream()
                .filter(genre -> !existingGenreIds.contains(genre.getId()))
                .map(GameGenre::new)
                .toList();

        Set<Long> newGenreIds = newGenres.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        List<GameGenre> removeGameGenres = existingGameGenres.stream()
                .filter(existing -> !newGenreIds.contains(existing.getGenre().getId()))
                .toList();

        newGameGenres.forEach(game::addGameGenre);

        removeGameGenres.forEach(game::removeGameGenre);
    }

    private void handleGamePlatformUpdate(Game game, GameUpdateForm form) {
        List<Platform> newPlatforms = platformRepository.findAllById(form.getGamePlatformIds());
        List<GamePlatform> existingGamePlatforms = game.getGamePlatforms();

        Set<Long> existingPlatformIds = existingGamePlatforms.stream()
                .map(existing -> existing.getPlatform().getId())
                .collect(Collectors.toSet());

        List<GamePlatform> newGamePlatforms = newPlatforms.stream()
                .filter(platform -> !existingPlatformIds.contains(platform.getId()))
                .map(GamePlatform::new)
                .toList();

        Set<Long> newPlatformIds = newPlatforms.stream()
                .map(Platform::getId)
                .collect(Collectors.toSet());

        List<GamePlatform> removeGamePlatforms = existingGamePlatforms.stream()
                .filter(existing -> !newPlatformIds.contains(existing.getPlatform().getId()))
                .toList();

        newGamePlatforms.forEach(game::addGamePlatform);
        removeGamePlatforms.forEach(game::removeGamePlatform);
    }

    private void handleGameAudioLanguageUpdate(Game game, GameUpdateForm form) {
        List<Language> newAudioLanguages = languageRepository.findAllById(form.getGameAudioLanguageIds());
        List<GameAudioLanguage> existingGameAudioLanguages = game.getGameAudioLanguages();

        Set<Long> existingAudioLanguageIds = existingGameAudioLanguages.stream()
                .map(existing -> existing.getLanguage().getId())
                .collect(Collectors.toSet());

        List<GameAudioLanguage> newGameAudioLanguages = newAudioLanguages.stream()
                .filter(language -> !existingAudioLanguageIds.contains(language.getId()))
                .map(GameAudioLanguage::new)
                .toList();

        Set<Long> newAudioLanguageIds = newAudioLanguages.stream()
                .map(Language::getId)
                .collect(Collectors.toSet());

        List<GameAudioLanguage> removeAudioLanguages = existingGameAudioLanguages.stream()
                .filter(existing -> !newAudioLanguageIds.contains(existing.getLanguage().getId()))
                .toList();

        newGameAudioLanguages.forEach(game::addGameAudioLanguage);
        removeAudioLanguages.forEach(game::removeGameAudioLanguage);
    }

    private void handleGameScreenLanguageUpdate(Game game, GameUpdateForm form) {
        List<Language> newScreenLanguages = languageRepository.findAllById(form.getGameScreenLanguageIds());
        List<GameScreenLanguage> existingGameScreenLanguages = game.getGameScreenLanguages();

        Set<Long> existingScreenLanguageIds = existingGameScreenLanguages.stream()
                .map(existing -> existing.getLanguage().getId())
                .collect(Collectors.toSet());

        List<GameScreenLanguage> newGameScreenLanguages = newScreenLanguages.stream()
                .filter(language -> !existingScreenLanguageIds.contains(language.getId()))
                .map(GameScreenLanguage::new)
                .toList();

        Set<Long> newScreenLanguageIds = newScreenLanguages.stream()
                .map(Language::getId)
                .collect(Collectors.toSet());

        List<GameScreenLanguage> removeScreenLanguages = existingGameScreenLanguages.stream()
                .filter(existing -> !newScreenLanguageIds.contains(existing.getLanguage().getId()))
                .toList();

        newGameScreenLanguages.forEach(game::addGameScreenLanguage);
        removeScreenLanguages.forEach(game::removeGameScreenLanguage);
    }

    private Game findGameWithGameDiscount(Long gameId) {
        return gameRepository.findByIdJoinFetchGameDiscount(gameId).orElseThrow(() ->
                new IllegalArgumentException("Game not found: " + gameId));
    }

    public GameResponse getGameResponse(Long gameId) {
        return new GameResponse(findGameWithGameDiscount(gameId));
    }

    public GameUpdateForm getGameUpdateForm(Long gameId) {
        return new GameUpdateForm(findGameWithGameDiscount(gameId));
    }

    public Page<GameListItem> getGames(Pageable pageable) {
        return gameQueryRepository.findGameList(pageable);
    }

    public PageImpl<GamePromotionResponse> searchPromotionGame(Pageable pageable, GameDiscountSearchCondition dto) {
        PageImpl<GamePromotionResponse> result = gameQueryRepository.searchPromotionGame(pageable, dto);
        causeThreadSleep(result.getContent().size());
        return result;
    }

    private void causeThreadSleep(int contentSize) {
        if (contentSize == 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public GameDetailsResponse getGameDetails(Long gameId) {
        return gameQueryRepository.getGameDetails(gameId);
    }

    //-------------------- ModelAttribute --------------------//
    public List<PublisherResponse> getPublishers() {
        List<PublisherResponse> publisherList = publisherRepository.findAll().stream()
                .map(PublisherResponse::new)
                .toList();

        return publisherList.stream()
                .sorted(Comparator.comparing(PublisherResponse::getName))
                .toList();
    }

    public List<LanguageResponse> getLanguages() {
        List<Language> languages = languageRepository.findAll();
        languages.sort(Comparator.comparing(Language::getName));
        return languages.stream().map(LanguageResponse::new).toList();
    }

    public List<GenreResponse> getGenres() {
        List<Genre> genres = genreRepository.findAll();
        List<Genre> sortedGenres = genres.stream()
                .sorted(Comparator.comparing(Genre::getName))
                .toList();
        return sortedGenres.stream().map(GenreResponse::new).toList();
    }

    public List<PlatformResponse> getPlatforms() {
        List<Platform> platforms = platformRepository.findAll();
        return platforms.stream().map(PlatformResponse::new).toList();
    }
}
