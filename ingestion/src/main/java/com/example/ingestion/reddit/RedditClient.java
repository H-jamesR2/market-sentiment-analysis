package main.java.com.example.ingestion.reddit;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

public class RedditClient {
    private static final String BASE_URL = "https://www.reddit.com/r/";
    private static final String USER_AGENT = "Mozilla/5.0"; // Reddit requires a User-Agent

    private final HttpClient httpClient;

    public RedditClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String fetchSubredditPosts(String subreddit) throws IOException, InterruptedException {
        String url = BASE_URL + subreddit + "/new.json?limit=10";
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", USER_AGENT)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(
            request, HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Failed to fetch data: HTTP " + response.statusCode());
        }
    }
}
