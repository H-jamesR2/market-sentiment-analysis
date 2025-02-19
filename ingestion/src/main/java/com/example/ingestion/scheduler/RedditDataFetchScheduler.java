package com.example.ingestion.scheduler;

import com.example.ingestion.fetcher.DataFetcher;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RedditDataFetchScheduler extends AbstractDataFetchScheduler {
    public RedditDataFetchScheduler(List<DataFetcher> fetchers) {
        super(fetchers);
    }
}
