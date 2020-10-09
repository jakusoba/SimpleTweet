package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.github.scribejava.apis.TwitterApi;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

//we need to get an instance of twitter client in the timelineactivity.java in order to call the method gethomeline from the timeline activity

public class TimelineActivity extends AppCompatActivity {
    public static final String TAG = "TimelineActivity";

    //This is an instance variable, so we can use it in multiple methods
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        //TwitterClient client is an instance of twitter client.

        client =TwitterApp.getRestClient(this);
        

        swipeContainer = findViewById(R.id.swipeContainer);
        //Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        //we set an onrefreshlistener on the swipe refreshner layout to get notified when user is swiping i.e a pull to refresh
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data");
                populateHomeTimeline();

            }
        });


        //Find the recycler view

        rvTweets = findViewById(R.id.rvTweets);
        //initialize the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        LinearLayoutManager layoutManager = new  LinearLayoutManager(this);
        
        //Configure and setup recyclerview. a)layout manager
        rvTweets.setLayoutManager(layoutManager);
        //b)The adapter
        rvTweets.setAdapter(adapter);
        //create a new method called populateHomeTimeline

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                loadMoreData();

            }
        };
        
        
        //adds the scroll listener to recyclerView
        rvTweets.addOnScrollListener(scrollListener);
        populateHomeTimeline();

    }

    private void loadMoreData() {
        // 1. Send an API request to retrieve appropriate paginated data
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess for loadMoreData!" + json.toString());
                // 2. Deserialize and construct new model objects from the API response
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                    // 3. Append the new data objects to the existing set of items inside the array of items
                    // 4. Notify the adapter of the new items made with `notifyItemRangeInserted()`
                    adapter.addAll(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure for loadMoreData!", throwable);

            }
        }, tweets.get(tweets.size() -1).id);
        // 2. Deserialize and construct new model objects from the API response
        // 3. Append the new data objects to the existing set of items inside the array of items
        // 4. Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }



    private void populateHomeTimeline() {
        //We are going to finally call the API method using twitter client and this takes in a jsonresponse handler.
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    //clear the elements in the recyclerview and refresh with new data
                    adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                    //now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure!", throwable);
            }
        });
    }
}