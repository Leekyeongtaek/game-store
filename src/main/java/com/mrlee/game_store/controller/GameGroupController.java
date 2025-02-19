package com.mrlee.game_store.controller;

import com.mrlee.game_store.dto.request.GameGroupSaveForm;
import com.mrlee.game_store.dto.request.GameGroupUpdateForm;
import com.mrlee.game_store.dto.response.GameGroupResponse;
import com.mrlee.game_store.service.GameGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@RequestMapping("/game-groups")
@Controller
public class GameGroupController {

    private final GameGroupService gameGroupService;

    @GetMapping("/")
    public String gameGroups(Pageable pageable, Model model) {
        model.addAttribute("gameGroups", gameGroupService.getGameGroupListWithPaging(pageable));
        return "game-group/game-groups";
    }

    @GetMapping("/add")
    public String addGameGroupForm(Model model) {
        model.addAttribute("gameGroup", new GameGroupResponse());
        return "game-group/game-group-add";
    }

    @GetMapping("/{gameGroupId}")
    public String gameGroup(@PathVariable("gameGroupId") Long gameGroupId, Model model) {
        model.addAttribute("gameGroup", gameGroupService.getGameGroup(gameGroupId));
        return "game-group/game-group";
    }

    @PostMapping("/add")
    public String addGameGroup(RedirectAttributes redirectAttributes, @ModelAttribute GameGroupSaveForm form) {
        redirectAttributes.addAttribute("gameGroupId", gameGroupService.saveGameGroup(form));
        redirectAttributes.addAttribute("status", true);
        return "redirect:/game-groups/{gameGroupId}";
    }

    @GetMapping("/{gameGroupId}/edit")
    public String editGameGroupForm(@PathVariable("gameGroupId") Long gameGroupId, Model model) {
        model.addAttribute("gameGroup", gameGroupService.getGameGroup(gameGroupId));
        return "game-group/game-group-edit";
    }

    @PostMapping("/{gameGroupId}/edit")
    public String edit(@PathVariable("gameGroupId") Long gameGroupId, @ModelAttribute GameGroupUpdateForm form) {
        gameGroupService.updateGameGroup(gameGroupId, form);
        return "redirect:/game-groups/{gameGroupId}";
    }
}
