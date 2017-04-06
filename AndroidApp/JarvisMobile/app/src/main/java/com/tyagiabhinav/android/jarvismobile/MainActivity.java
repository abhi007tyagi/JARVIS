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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, MqttCallback {

    private TextView speechText;
    private static final int SPEECH_INPUT = 27;
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextToSpeech tts;
    private TextView jarvisResponse;
    private MqttClient client;

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

        FloatingActionButton stop = (FloatingActionButton) findViewById(R.id.stopSpeaking);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSpeaking(view);
            }
        });

        speechText = (TextView) findViewById(R.id.speechToText);
        jarvisResponse = (TextView) findViewById(R.id.jarvisResponse);

        tts = new TextToSpeech(this, this);

        try {
            client = new MqttClient("tcp://192.168.43.86:1883", "AndroidThingSub", new MemoryPersistence());
            client.setCallback(this);
            client.connect();

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize Speech Recognition
     */
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


    /**
     * Get speech to text result
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
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

    /**
     * Initialize Text to speech
     *
     * @param status
     */
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


    /**
     * Process speech text, send it to Brain and receive response. Then do task based on response.
     *
     * @param speech
     */
    public void processSpeech(final String speech) {
        Log.d(TAG, "processSpeech");
        String url = "https://37b69e6b.ngrok.io/jarvis";

        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        String payload = "{\"query\":\"" + speech + "\"}";
        try {
            JSONObject requestPayload = new JSONObject(payload);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, requestPayload, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String resp = response.getString("response");
                        jarvisResponse.setText(resp);
                        String type = response.getString("type");
                        Log.d(TAG, "onResponse: " + resp + " -- " + type);
                        switch (type) {
                            // Command type should be handled using MQTT. Lamp is directly controlled. Rover is controlled via Jarvis Things Module
                            case "cmd":
                                if (client != null) {

                                    switch (resp) {
                                        case "LAMP ON":
                                            client.publish("topic/lamp", new MqttMessage("1".getBytes("UTF-8")));
                                            break;
                                        case "LAMP OFF":
                                            client.publish("topic/lamp", new MqttMessage("0".getBytes("UTF-8")));
                                            break;
                                        case "FORWARD":
                                            client.publish("topic/rover", new MqttMessage("FORWARD".getBytes("UTF-8")));
                                            break;
                                        case "BACKWARD":
                                            client.publish("topic/rover", new MqttMessage("BACKWARD".getBytes("UTF-8")));
                                            break;
                                        case "LEFT":
                                            client.publish("topic/rover", new MqttMessage("LEFT".getBytes("UTF-8")));
                                            break;
                                        case "RIGHT":
                                            client.publish("topic/rover", new MqttMessage("RIGHT".getBytes("UTF-8")));
                                            break;
                                        case "STOP":
                                            client.publish("topic/rover", new MqttMessage("STOP".getBytes("UTF-8")));
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                break;
                            // Rest of the responses are played via speaker
                            case "mth":
                            case "wel":
                                speak(resp);
                                break;
                            case "err":
                            default:
                                speak("Something went wrong. Please try again later!");
                                break;
                        }
                        if (type.equalsIgnoreCase("err")) {
                            speak("Something went wrong. Please try again later!");
                        } else {
                            speak(resp);
                        }
                        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
                    } catch (JSONException | MqttException | IOException e) {
                        e.printStackTrace();
                        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
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
                    speak("Something went wrong. Please try again later!");
                    ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
                }
            });
            Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "connectionLost....");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d(TAG, "message received --> " + payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, "deliveryComplete....");
    }
}
