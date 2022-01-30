package com.collector.log.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TracedLogRepository extends JpaRepository<TracedLog, Long> {
}