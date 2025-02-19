package com.mrlee.game_store.controller;

import com.mrlee.game_store.domain.GameType;
import com.mrlee.game_store.dto.request.GameSaveForm;
import com.mrlee.game_store.dto.request.GameDiscountSearchCondition;
import com.mrlee.game_store.dto.request.GameUpdateForm;
import com.mrlee.game_store.dto.response.*;
import com.mrlee.game_store.dto.response.GamePromotionResponse;
import com.mrlee.game_store.service.GameGroupService;
import com.mrlee.game_store.service.GameService;
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
    private final GameGroupService gameGroupService;

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
    public String searchPromotionGame(@PageableDefault(size = 18) Pageable pageable, @ModelAttribute GameDiscountSearchCondition condition, Model model) {
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
        List<GameGroupResponse> gameGroupList = gameGroupService.getGameGroupList();
        return gameGroupList.stream()
                .sorted(Comparator.comparing(GameGroupResponse::getName))
                .toList();
    }

    @ModelAttribute("publishers")
    public List<PublisherResponse> publishers() {
        return gameService.getPublishers();
    }

    @ModelAttribute("languages")
    public List<LanguageResponse> languages() {
        return gameService.getLanguages();
    }

    @ModelAttribute("platforms")
    public List<PlatformResponse> platforms() {
        return gameService.getPlatforms();
    }

    @ModelAttribute("genres")
    public List<GenreResponse> genres() {
        return gameService.getGenres();
    }

    @ModelAttribute("gameTypes")
    public Map<String, String> gameTypes() {
        Map<String, String> gameTypeMap = new LinkedHashMap<>();
        for (GameType gameType : GameType.values()) {
            gameTypeMap.put(gameType.name(), gameType.getKoreanName());
        }
        return gameTypeMap;
    }

    @ModelAttribute("webBasePrices")
    public Map<String, String> webBasePrices() {
        Map<String, String> webBasePrices = new LinkedHashMap<>();
        webBasePrices.put("0-4999", "4,999원 이하");
        webBasePrices.put("5000-9999", "5,000원 - 9,999원");
        webBasePrices.put("10000-19999", "10,000원 - 19,999원");
        webBasePrices.put("20000-49999", "20,000원 - 49,999원");
        webBasePrices.put("50000-999999", "50,000원 이상");
        return webBasePrices;
    }
}
