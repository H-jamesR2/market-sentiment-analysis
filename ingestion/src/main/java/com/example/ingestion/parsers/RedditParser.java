package com.example.ingestion.reddit;

import com.example.ingestion.models.RedditPost;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class RedditParser {
    private static final Logger logger = LoggerFactory.getLogger(RedditParser.class);

    public List<RedditPost> parseRedditResponse(String jsonResponse) {
        List<RedditPost> posts = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(jsonResponse);
            JSONArray children = json.getJSONObject("data").getJSONArray("children");

            for (int i = 0; i < children.length(); i++) {
                JSONObject postData = children.getJSONObject(i).getJSONObject("data");

                String id = postData.getString("id");
                String content = postData.optString("selftext", ""); // Some posts have no text
                long timestamp = postData.getLong("created_utc") * 1000; // Convert to milliseconds
                String title = postData.getString("title");
                String url = postData.optString("url", ""); // Some posts may not have a URL
                int upvotes = postData.optInt("ups", 0); // Default to 0 if missing
                String subreddit = postData.getString("subreddit");

                RedditPost redditPost = new RedditPost(id, content, timestamp, title, url, upvotes, subreddit);
                posts.add(redditPost);
            }
        } catch (Exception e) {
            logger.error("Error parsing Reddit API response", e);
        }

        return posts;
    }
}


