package com.ll.playon.domain.title.entity;

import com.ll.playon.domain.title.entity.enums.ConditionType;
import com.ll.playon.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Title extends BaseEntity {
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private ConditionType conditionType;

    private int conditionValue;

    @OneToMany(mappedBy = "title", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberTitle> memberTitles = new ArrayList<>();
}
