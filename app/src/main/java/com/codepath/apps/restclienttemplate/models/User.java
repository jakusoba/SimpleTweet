package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    //Add what attributes of the user object we care about. NB: screenname is also called handle.
    public String name;
    public String screenName;
    public String profileImageUrl;

    //"throws JSONException means that whoever is calling this error has to handle it.
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User ();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        return user;




    }
}
