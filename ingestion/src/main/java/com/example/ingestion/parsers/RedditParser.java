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
                        postData.optLong("created_utc", 0) * 1000,
                        postData.getString("title"),
                        postData.optString("url", ""),
                        postData.optInt("ups", 0),
                        postData.optString("subreddit", "unknown")
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
            logger.debug("Raw Reddit comment response: {}", jsonResponse);

            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                logger.error("Received empty or null JSON response for Reddit comments.");
                return comments; // Handle empty response gracefully
            }

            // Reddit comments API returns an array; check for it
            JSONArray jsonArray = new JSONArray(jsonResponse);
            if (jsonArray.length() < 2) {
                logger.error("Unexpected Reddit comment response structure: {}", jsonResponse);
                return comments; // Likely an API issue
            }

            // The second object in the array contains the comment data
            JSONObject commentListing = jsonArray.getJSONObject(1);
            //JSONObject json = new JSONObject(jsonResponse);
            JSONArray children = commentListing.getJSONObject("data").getJSONArray("children");

            for (int i = 0; i < children.length(); i++) {
                JSONObject commentData = children.getJSONObject(i).getJSONObject("data");

                String parentId = commentData.getString("parent_id");
                String postId = parentId.startsWith("t3_") ? parentId.substring(3) : null;  // Extract post ID if parent is a post

                RedditComment redditComment = new RedditComment(
                        commentData.getString("id"),
                        postId, // Store postId instead of raw parentId
                        commentData.optString("body", "[deleted]"), // Handle deleted comments
                        commentData.optString("subreddit", "unknown"),
                        commentData.optLong("created_utc", 0) * 1000,
                        commentData.optInt("ups", 0) // Fixed "upvotes" -> "ups"
                );
                comments.add(redditComment);
            }
        } catch (Exception e) {
            logger.error("Error parsing Reddit comments", e);
        }
        return comments;
    }
}