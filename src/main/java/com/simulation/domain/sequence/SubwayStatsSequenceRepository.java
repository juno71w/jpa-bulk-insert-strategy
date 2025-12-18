package com.simulation.domain.sequence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubwayStatsSequenceRepository extends JpaRepository<SubwayStatsSequence, Long> {
}