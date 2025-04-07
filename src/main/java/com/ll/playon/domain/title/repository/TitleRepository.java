package com.ll.playon.domain.title.repository;

import com.ll.playon.domain.title.entity.Title;
import com.ll.playon.domain.title.entity.enums.ConditionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TitleRepository extends JpaRepository<Title, Long> {
    List<Title> findByConditionTypeOrderByConditionValueAsc(ConditionType conditionType);
}
