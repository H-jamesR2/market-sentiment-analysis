package com.example.ingestion.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDataFetchScheduler implements FetchScheduler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDataFetchScheduler.class);

    @Scheduled(fixedRateString = "${fetch.scheduler.interval:60000}") // 60s interval
    public void scheduleFetch() {
        logger.info("Scheduled fetch triggered for: " + this.getClass().getSimpleName());
        fetchData();
    }
}
