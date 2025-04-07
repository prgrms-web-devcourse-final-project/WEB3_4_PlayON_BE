package com.ll.playon.domain.title.service;

import com.ll.playon.domain.title.entity.Title;
import com.ll.playon.domain.title.entity.enums.ConditionType;
import com.ll.playon.domain.title.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TitleService {
    private final TitleRepository titleRepository;

    public List<Title> findByConditionTypeOrderByConditionValueAsc(ConditionType conditionType) {
        return titleRepository.findByConditionTypeOrderByConditionValueAsc(conditionType);
    }
}
