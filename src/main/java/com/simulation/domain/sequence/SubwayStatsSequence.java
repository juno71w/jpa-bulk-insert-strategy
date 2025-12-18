package com.simulation.domain.sequence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;


@Getter
@NoArgsConstructor
@Table(name = "subway_stats_sequence")
@Entity
public class SubwayStatsSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "order_seq",
            sequenceName = "order_seq",
            allocationSize = 500
    )
    private Long id;

    private String stationName;

    private int boardingCount;    // 승차 인원

    private int exitingCount;   // 하차 인원

    private LocalDateTime time;

    public SubwayStatsSequence(String stationName, LocalDateTime time) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        this.stationName = stationName;
        this.boardingCount = random.nextInt(1000);
        this.exitingCount = random.nextInt(1000);
        this.time = time;
    }
}