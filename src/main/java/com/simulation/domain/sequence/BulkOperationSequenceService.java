package com.simulation.domain.sequence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BulkOperationSequenceService {

    private final SubwayStatsSequenceRepository repository;

    @Transactional
    public void bulkInsert(List<SubwayStatsSequence> statsList) {
        repository.saveAll(statsList);
    }
}