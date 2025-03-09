package com.mrlee.game_store.controller;

import com.mrlee.game_store.dto.request.GameSaveForm;
import com.mrlee.game_store.dto.request.GameDiscountSearchCondition;
import com.mrlee.game_store.dto.request.GameUpdateForm;
import com.mrlee.game_store.dto.response.*;
import com.mrlee.game_store.dto.response.GamePromotionResponse;
import com.mrlee.game_store.service.GameService;
import com.mrlee.game_store.service.RedisCacheManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/games")
@Controller
public class GameController {

    private final GameService gameService;
    private final RedisCacheManager redisCacheManager;

    @GetMapping("/")
    public String games(Pageable pageable, Model model) {
        model.addAttribute("games", gameService.getGames(pageable));
        return "game/games";
    }

    @GetMapping("/add")
    public String addGameForm(Model model) {
        model.addAttribute("game", new GameSaveForm());
        return "game/game-add";
    }

    @PostMapping("/add")
    public String addGame(RedirectAttributes redirectAttributes, @Validated @ModelAttribute("game") GameSaveForm form,
                          BindingResult bindingResult) {

        if (form.getCoverImage() == null || form.getCoverImage().isEmpty()) {
            bindingResult.rejectValue("coverImage", "", "게임 이미지는 필수 입력 사항입니다.");
        }

        if (bindingResult.hasErrors()) {
            return "game/game-add";
        }

        redirectAttributes.addAttribute("gameId", gameService.saveGame(form));
        redirectAttributes.addAttribute("status", true);
        return "redirect:/games/{gameId}";
    }

    @GetMapping("/{gameId}")
    public String game(@PathVariable("gameId") Long gameId, Model model) {
        model.addAttribute("game", gameService.getGameResponse(gameId));
        return "game/game";
    }

    @GetMapping("/{gameId}/edit")
    public String editGameForm(@PathVariable("gameId") Long gameId, Model model) {
        model.addAttribute("game", gameService.getGameUpdateForm(gameId));
        model.addAttribute("gameId", gameId);
        return "game/game-edit";
    }

    @PostMapping("/{gameId}/edit")
    public String editGame(@PathVariable("gameId") Long gameId, @Validated @ModelAttribute("game") GameUpdateForm form,
                           BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("gameId", gameId);
            return "game/game-edit";
        }

        gameService.updateGame(gameId, form);
        return "redirect:/games/{gameId}";
    }

    @GetMapping("/search-promotion")
    public String searchPromotionGame(@PageableDefault(size = 18) Pageable pageable, @ModelAttribute GameDiscountSearchCondition condition,
                                      Model model, HttpServletRequest request) {
        log.info("[RequestUserInfo] IP={}, User-Agent={}", request.getRemoteAddr(), request.getHeader("User-Agent"));
        PageImpl<GamePromotionResponse> games = gameService.searchPromotionGame(pageable, condition);
        model.addAttribute("searchCondition", condition);
        model.addAttribute("games", games);
        return "game/game-promotion";
    }

    @GetMapping("/{gameId}/details")
    public String detailsGame(@PathVariable("gameId") Long gameId, Model model) {
        GameDetailsResponse gameDetails = gameService.getGameDetails(gameId);
        model.addAttribute("game", gameDetails);
        return "game/game-details";
    }

    //------------------------- ModelAttribute -------------------------//
    @ModelAttribute("gameGroups")
    public List<GameGroupResponse> gameGroups() {
        return redisCacheManager.getCacheGameGroups();
    }

    @ModelAttribute("publishers")
    public List<PublisherResponse> publishers() {
        return redisCacheManager.getCachePublishers();
    }

    @ModelAttribute("languages")
    public List<LanguageResponse> languages() {
        return redisCacheManager.getCacheLanguages();
    }

    @ModelAttribute("platforms")
    public List<PlatformResponse> platforms() {
        return redisCacheManager.getCachePlatforms();
    }

    @ModelAttribute("genres")
    public List<GenreResponse> genres() {
        return redisCacheManager.getCacheGenres();
    }

    @ModelAttribute("gameTypes")
    public Map<String, String> gameTypes() {
        return redisCacheManager.getCacheGameTypes();
    }

    @ModelAttribute("webBasePrices")
    public Map<String, String> webBasePrices() {
        return redisCacheManager.getCacheWebBasePrices();
    }

}
