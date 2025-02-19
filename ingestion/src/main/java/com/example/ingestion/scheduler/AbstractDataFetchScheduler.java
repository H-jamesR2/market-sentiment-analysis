package com.example.ingestion.scheduler;

import com.example.ingestion.fetcher.DataFetcher;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public abstract class AbstractDataFetchScheduler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDataFetchScheduler.class);
    private final List<DataFetcher> fetchers;

    protected AbstractDataFetchScheduler(List<DataFetcher> fetchers) {
        this.fetchers = fetchers;
    }

    @Scheduled(fixedRateString = "${fetch.scheduler.interval:120000}") // Default 60s interval
    public void scheduleFetch() {
        logger.info("Scheduled fetch triggered for: " + this.getClass().getSimpleName());
        fetchers.forEach(DataFetcher::fetchData);
    }
}
