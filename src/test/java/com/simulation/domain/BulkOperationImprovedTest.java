package com.simulation.domain;

import com.simulation.config.IntegrationTest;
import com.simulation.domain.identity.BulkOperationImprovedService;
import com.simulation.domain.identity.SubwayDataProvider;
import com.simulation.domain.identity.SubwayStatsIdentity;
import com.simulation.domain.identity.SubwayStatsRepository;
import com.simulation.util.TestLogUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class BulkOperationImprovedTest {

    @Autowired
    private BulkOperationImprovedService bulkOperationImprovedService;

    @Autowired
    private SubwayStatsRepository repository;

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();
        repository.deleteAllInBatch();
        assertThat(repository.count()).isZero();
        TestLogUtil.CleanEnd();
    }

    @DisplayName("BulkInsertImproved")
    @Test
    void testBulkInsertImproved() {
        // when
        List<SubwayStatsIdentity> data = SubwayDataProvider.createData(10);
        bulkOperationImprovedService.bulkInsert(data);

        // then
        TestLogUtil.assertStart();
        assertThat(repository.count()).isEqualTo(1440);
        TestLogUtil.assertEnd();
    }

}