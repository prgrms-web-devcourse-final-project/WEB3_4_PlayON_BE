package com.ll.playon.domain.game.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    @Builder.Default
    private List<SteamGame> games = new ArrayList<>();

    public SteamGenre(String name) {
        this.name = name;
    }
}
