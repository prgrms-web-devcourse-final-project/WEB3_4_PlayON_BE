package com.ll.playon.domain.game.game.repository;

import com.ll.playon.domain.game.game.dto.request.GameSearchCondition;
import com.ll.playon.domain.game.game.entity.SteamGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GameRepositoryCustom {
    Page<SteamGame> searchGames(GameSearchCondition condition, Pageable pageable);
    List<SteamGame> searchByGameName(String keyword);
}