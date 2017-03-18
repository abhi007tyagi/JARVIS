package com.tyagiabhinav.androidthings.jarvis;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abhinavtyagi on 18/03/17.
 */

public class NetworkUtil {

    public static final String TAG = NetworkUtil.class.getSimpleName();

    public static void processSpeech(final Context context, String speech) {
        Log.d(TAG, "processSpeech");
        String url = "https://api.foursquare.com/v2/venues/explore/?ll=28.607770,77.371988&section=food&venuePhotos=1&limit=50&sortByDistance=1&query=chinese&oauth_token=GB1ZZLQPMFMGXERZPLGFEBRP1UNWWAH3QQ5QHIDXIUIPJ45E&v=20161229";

        String payload = "{\"query\":\"" + speech + "\"}";
        try {
            JSONObject requestPayload = new JSONObject(payload);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Response->" + response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                onConnectionFailed(error.toString());
                    if (error != null && error.getMessage() != null) {
                        Log.i(TAG, error.getMessage());
                    } else {
                        Log.i(TAG, "Unknown error from server!!");
                    }
                }
            });
            Volley.newRequestQueue(context).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
