package com.ll.playon.domain.game.game.entity;

import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SteamGenre extends BaseTime {
    private String name;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GameGenre> gameGenres = new ArrayList<>();
}
