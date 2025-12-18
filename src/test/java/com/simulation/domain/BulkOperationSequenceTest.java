package com.simulation.domain;

import com.simulation.config.IntegrationTest;
import com.simulation.domain.sequence.BulkOperationSequenceService;
import com.simulation.domain.sequence.SubwayDataSequenceProvider;
import com.simulation.domain.sequence.SubwayStatsSequence;
import com.simulation.domain.sequence.SubwayStatsSequenceRepository;
import com.simulation.util.TestLogUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class BulkOperationSequenceTest {

    @Autowired
    private BulkOperationSequenceService bulkOperationService;

    @Autowired
    private SubwayStatsSequenceRepository repository;

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();
        repository.deleteAllInBatch();
        assertThat(repository.count()).isZero();
        TestLogUtil.CleanEnd();
    }

    @DisplayName("BulkInsert")
    @Test
    void testBulkInsert() {
        // when
        List<SubwayStatsSequence> data = SubwayDataSequenceProvider.createData(10);
        bulkOperationService.bulkInsert(data);

        // then
        TestLogUtil.assertStart();
        assertThat(repository.count()).isEqualTo(1440);
        TestLogUtil.assertEnd();
    }
}