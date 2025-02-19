package com.mrlee.game_store.service;

import com.mrlee.game_store.common.MyUtil;
import com.mrlee.game_store.domain.GameGroup;
import com.mrlee.game_store.dto.request.GameGroupSaveForm;
import com.mrlee.game_store.dto.request.GameGroupUpdateForm;
import com.mrlee.game_store.dto.response.GameGroupResponse;
import com.mrlee.game_store.repository.GameGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class GameGroupService {

    private final GameGroupRepository gameGroupRepository;

    public Long saveGameGroup(GameGroupSaveForm form) {
        return gameGroupRepository.save(new GameGroup(form.getName())).getId();
    }

    public GameGroupResponse getGameGroup(Long productId) {
        GameGroup gameGroup = gameGroupRepository.findById(productId).orElseThrow();
        return new GameGroupResponse(gameGroup);
    }

    public Page<GameGroupResponse> getGameGroupListWithPaging(Pageable pageable) {
        Page<GameGroup> gameGroupPage = gameGroupRepository.findAll(MyUtil.getPageableWithSortByIdDesc(pageable));
        return gameGroupPage.map(GameGroupResponse::new);
    }

    public List<GameGroupResponse> getGameGroupList() {
        List<GameGroup> gameGroups = gameGroupRepository.findAll();
        return gameGroups.stream().map(GameGroupResponse::new).toList();
    }

    public void updateGameGroup(Long gameGroupId, GameGroupUpdateForm form) {
        GameGroup gameGroup = gameGroupRepository.findById(gameGroupId).orElseThrow();
        gameGroup.update(form.getName());
    }
}
