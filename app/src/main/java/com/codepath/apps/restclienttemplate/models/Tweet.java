package com.codepath.apps.restclienttemplate.models;

import android.provider.ContactsContract;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity=User.class, parentColumns="id", childColumns="userId"))
//Given a json object which is a tweet in json, we want to turn it to java
public class Tweet {
    @PrimaryKey
    @ColumnInfo
    public long id;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public long userId;

    @Ignore
    public User user;

    //empty constructor needed by the Parceler library
    public Tweet() {}
    //With this, we have built the tweet as part of the fields in the JSON objects
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet =  new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        //This takes in the "user Json Object" and returns a java "user" model, fromJson method has been defined in user.java
        User user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.user = user;
        tweet.userId = user.id;
        tweet.id = jsonObject.getLong("id");
        return tweet;

    }
    //From our timeline activity we are getting a jsonarray, we need to parse that json array and get back a list of tweets.
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        //for loop: for each element(tweet) in the jsonArray, we are calling the fromJson method that we craeted above in order to add it to our list of tweet object

        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }


}
