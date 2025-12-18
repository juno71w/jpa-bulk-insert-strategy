package com.simulation.domain.identity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BulkOperationImprovedService {

    private final JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;  // 실제 EntityManager 사용

    private final SubwayStatsRepository repository;

    @Transactional
    public void bulkInsert(List<SubwayStatsIdentity> statsList) {
        String sql = """
                INSERT INTO subway_stats (station_name, boarding_count, exiting_count, time)
                VALUES (?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        SubwayStatsIdentity stats = statsList.get(i);
                        ps.setString(1, stats.getStationName());
                        ps.setInt(2, stats.getBoardingCount());
                        ps.setInt(3, stats.getExitingCount());
                        ps.setTimestamp(4, Timestamp.valueOf(stats.getTime()));
                    }

                    @Override
                    public int getBatchSize() {
                        return statsList.size();
                    }
                });
    }

}