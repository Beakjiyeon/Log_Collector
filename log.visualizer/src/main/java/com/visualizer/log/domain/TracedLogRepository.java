package com.visualizer.log.domain;

import com.visualizer.log.domain.TracedLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TracedLogRepository extends JpaRepository<TracedLog, Long> {
}