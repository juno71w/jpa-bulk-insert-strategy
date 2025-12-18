package com.simulation;

import com.simulation.domain.identity.BulkOperationImprovedService;
import com.simulation.domain.identity.BulkOperationService;
import com.simulation.domain.identity.SubwayDataProvider;
import com.simulation.domain.identity.SubwayStatsIdentity;
import com.simulation.domain.sequence.BulkOperationSequenceService;
import com.simulation.domain.sequence.SubwayDataSequenceProvider;
import com.simulation.domain.sequence.SubwayStatsSequence;
import com.simulation.global.config.monitoring.BatchContext;
import com.simulation.global.config.monitoring.BatchContextHolder;
import com.simulation.global.config.monitoring.BatchName;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class SimulationApplication implements CommandLineRunner {

    private final BulkOperationService bulkOperationService;
    private final BulkOperationSequenceService bulkOperationSequenceService;
    private final BulkOperationImprovedService bulkOperationImprovedService;

    public static void main(String[] args) {
		SpringApplication.run(SimulationApplication.class, args);
	}

    @Override
    public void run(String... args) {
        // testSize * 144 개의 데이터를 bulk insert 하는 실험
        // 144개부터 14만4천개의 row를 insert
        int[] testSize = {1, 10, 100, 1_000};

        for (int size : testSize) {
            long regularElapsedTime = bulkInsertWithIdentity(size);
            long batchElapsedTime = bulkInsertWithSequence(size);
            long regularImprovedElapsedTime = bulkInsertWithIdentityImproved(size);

            System.out.println(String.join("", Collections.nCopies(50, "-")));
            System.out.format("%-20s%-5s%-10s%-5s%8sms\n", "Regular inserts", "|", size, "|", regularElapsedTime);
            System.out.format("%-20s%-5s%-10s%-5s%8sms\n", "Batch inserts", "|", size, "|", batchElapsedTime);
            System.out.format("%-20s%-5s%-10s%-5s%8sms\n", "Regular improved inserts", "|", size, "|", regularImprovedElapsedTime);
        }

    }

    private long bulkInsertWithIdentity(int testSize) {
        List<SubwayStatsIdentity> data = SubwayDataProvider.createData(testSize);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Batch 시작 시, 수동으로 BatchContext를 초기화
        BatchContextHolder.initContext(new BatchContext(BatchName.BULK_INSERT));

        bulkOperationService.bulkInsert(data);

        BatchContext batchContext = BatchContextHolder.getContext();
        if (batchContext != null) {
            batchContext.log();
        }

        // 메모리 누수 방지를 위해 반드시 호출 필요
        BatchContextHolder.clear();
        stopWatch.stop();
        return stopWatch.getTotalTimeMillis();
    }

    private long bulkInsertWithSequence(int testSize) {
        List<SubwayStatsSequence> data = SubwayDataSequenceProvider.createData(testSize);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // Batch 시작 시, 수동으로 BatchContext를 초기화
        BatchContextHolder.initContext(new BatchContext(BatchName.BULK_INSERT_SEQUENCE));

        bulkOperationSequenceService.bulkInsert(data);

        BatchContext batchContext = BatchContextHolder.getContext();
        if (batchContext != null) {
            batchContext.log();
        }

        // 메모리 누수 방지를 위해 반드시 호출 필요
        BatchContextHolder.clear();
        stopWatch.stop();
        return stopWatch.getTotalTimeMillis();
    }

    private long bulkInsertWithIdentityImproved(int testSize) {
        List<SubwayStatsIdentity> data = SubwayDataProvider.createData(testSize);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Batch 시작 시, 수동으로 BatchContext를 초기화
        BatchContextHolder.initContext(new BatchContext(BatchName.IMPROVED_BULK_INSERT));

        bulkOperationImprovedService.bulkInsert(data);

        BatchContext batchContext = BatchContextHolder.getContext();
        if (batchContext != null) {
            batchContext.log();
        }

        // 메모리 누수 방지를 위해 반드시 호출 필요
        BatchContextHolder.clear();
        stopWatch.stop();
        return stopWatch.getTotalTimeMillis();
    }
}
