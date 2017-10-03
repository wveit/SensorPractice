package com.example.androidu.sensorpractice.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by bill on 9/28/17.
 */

public final class JSON {
    public static String parseYelpAuthInfo(String authStr) throws JSONException {
        final String YELP_API_ACCESS_TOKEN = "access_token";
        final String YELP_API_EXPIRATION   = "expires_in";
        final String YELP_API_TOKEN_TYPE   = "token_type";

        JSONObject authInfo = new JSONObject(authStr);

        String access_token = authInfo.getString(YELP_API_ACCESS_TOKEN);

        return access_token;
    }

    public static String[] parseYelpSearchInfo(String searchStr) throws JSONException {
        final String YELP_API_SEARCH_BUSINESSES = "businesses";
        final String YELP_API_SEARCH_TOTAL      = "total";
        final String YELP_API_SEARCH_REGION     = "region";

        final String YELP_API_SEARCH_BUSINESS_NAME = "name";

        JSONObject searchObj = new JSONObject(searchStr);
        JSONArray businesses = searchObj.getJSONArray(YELP_API_SEARCH_BUSINESSES);

        int size = searchObj.getInt(YELP_API_SEARCH_TOTAL);
        if(size > 3) size = 3;
        String[] titles = new String[size];

        for(int i=0; i<size; i++) {
            JSONObject b = businesses.getJSONObject(i);
            titles[i] = b.getString(YELP_API_SEARCH_BUSINESS_NAME);
        }

        return titles;
    }
}
