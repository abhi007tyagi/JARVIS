package com.tyagiabhinav.android.jarvismobile;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView speechText;
    private static final int SPEECH_INPUT = 27;
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextToSpeech tts;
    private TextView jarvisResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.mic);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
            }
        });

        speechText = (TextView) findViewById(R.id.speechToText);
        jarvisResponse = (TextView) findViewById(R.id.jarvisResponse);

        tts = new TextToSpeech(this, this);
    }

    private void init() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something !");
        try {
            startActivityForResult(intent, SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Sorry! Device does not support speech input", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && data != null) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speechText.setText(result.get(0));
                    Log.d(TAG, "onActivityResult: " + result.get(0));
                    processSpeech(result.get(0));
                }
                break;
            }

        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void speak(String text) {
        findViewById(R.id.stopSpeaking).setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void stopSpeaking(View view) {
        tts.stop();
    }


    public void processSpeech(final String speech) {
        Log.d(TAG, "processSpeech");
        String url = "https://65aaf6b7.ngrok.io/jarvis";

        String payload = "{\"query\":\"" + speech + "\"}";
        try {
            JSONObject requestPayload = new JSONObject(payload);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, requestPayload, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String resp = response.getString("response");
                        String type = response.getString("type");
                        Log.d(TAG, "onResponse: " + resp + " -- " + type);
                        if (type.equalsIgnoreCase("err")) {
                            speak("Something went wrong. Please try again later!");
                        } else {
                            speak(resp);
                        }
                        jarvisResponse.setText(resp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null && error.getMessage() != null) {
                        Log.i(TAG, error.getMessage());
                    } else {
                        Log.i(TAG, "Unknown error from server!!");
                    }
                }
            });
            Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
