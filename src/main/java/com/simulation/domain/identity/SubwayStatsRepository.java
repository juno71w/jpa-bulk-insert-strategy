package com.simulation.domain.identity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubwayStatsRepository extends JpaRepository<SubwayStatsIdentity, Long> {
}