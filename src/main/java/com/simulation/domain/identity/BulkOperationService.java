package com.simulation.domain.identity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BulkOperationService {

    private final SubwayStatsRepository repository;

    @Transactional
    public void bulkInsert(List<SubwayStatsIdentity> statsList) {
        repository.saveAll(statsList);
    }
}