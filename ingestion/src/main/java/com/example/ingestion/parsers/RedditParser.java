package com.example.ingestion.parsers;

import com.example.ingestion.models.RedditPost;
import com.example.ingestion.models.RedditComment;
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

    public List<RedditPost> parseRedditPosts(String jsonResponse) {
        List<RedditPost> posts = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(jsonResponse);
            JSONArray children = json.getJSONObject("data").getJSONArray("children");

            for (int i = 0; i < children.length(); i++) {
                JSONObject postData = children.getJSONObject(i).getJSONObject("data");
                RedditPost redditPost = new RedditPost(
                        postData.getString("id"),
                        postData.optString("selftext", ""),
                        postData.getLong("created_utc") * 1000,
                        postData.getString("title"),
                        postData.optString("url", ""),
                        postData.optInt("ups", 0),
                        postData.getString("subreddit")
                );
                posts.add(redditPost);
            }
        } catch (Exception e) {
            logger.error("Error parsing Reddit posts", e);
        }
        return posts;
    }

    public List<RedditComment> parseRedditComments(String jsonResponse) {
        List<RedditComment> comments = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(jsonResponse);
            JSONArray children = json.getJSONObject("data").getJSONArray("children");

            for (int i = 0; i < children.length(); i++) {
                JSONObject commentData = children.getJSONObject(i).getJSONObject("data");
                RedditComment redditComment = new RedditComment(
                        commentData.getString("id"),
                        commentData.getString("body"),
                        commentData.getLong("created_utc") * 1000,
                        commentData.getString("parent_id"),
                        commentData.getString("subreddit")
                );
                comments.add(redditComment);
            }
        } catch (Exception e) {
            logger.error("Error parsing Reddit comments", e);
        }
        return comments;
    }
}