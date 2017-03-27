/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tyagiabhinav.androidthings.jarvis;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity  implements TextToSpeech.OnInitListener, MqttCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SPEECH_INPUT = 27;
    public static final String BTN_PIN = "BCM17"; //physical pin #11 for speech initiation switch
    public static final String MOTOR_A_1_PIN = "BCM21"; //physical pin #40 for motor control
    public static final String MOTOR_A_2_PIN = "BCM20"; //physical pin #38 for motor control
    public static final String MOTOR_B_1_PIN = "BCM24"; //physical pin #18 for motor control
    public static final String MOTOR_B_2_PIN = "BCM23"; //physical pin #16 for motor control

    private boolean isListening = false; // to stop bouncing of physical switch

    private TextToSpeech tts;

    // for on board mic ON-OFF
    private Gpio mBtnGpio;

    // for motor control of rover
    private Gpio motorAPin1;
    private Gpio motorAPin2;
    private Gpio motorBPin1;
    private Gpio motorBPin2;

    private MqttClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        tts = new TextToSpeech(this, this);
        try {
            client = new MqttClient("tcp://192.168.1.7:1883", "AndroidThingSub", new MemoryPersistence());
            client.setCallback(this);
            client.connect();

            String topic = "topic/rover";
            client.subscribe(topic);

            client.publish("topic/lamp", new MqttMessage("LAMP ON".getBytes()));

        } catch (MqttException e) {
            e.printStackTrace();
        }

        PeripheralManagerService service = new PeripheralManagerService();
        try {
            // Create GPIO connection for PUSH btn
            mBtnGpio = service.openGpio(BTN_PIN);
            // Configure as an input.
            mBtnGpio.setDirection(Gpio.DIRECTION_IN);
            // Enable edge trigger events for both rising and falling edges.
            mBtnGpio.setEdgeTriggerType(Gpio.EDGE_RISING);
            // Register an event callback.
            mBtnGpio.registerGpioCallback(mMotorCallback);


            // for rover motor control
            // Create GPIO connection for Motor A Pin 1.
            motorAPin1 = service.openGpio(MOTOR_A_1_PIN);
            // Configure as an output.
            motorAPin1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            // Create GPIO connection for Motor A Pin 2.
            motorAPin2 = service.openGpio(MOTOR_A_2_PIN);
            // Configure as an output.
            motorAPin2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            // Create GPIO connection for Motor B Pin 1.
            motorBPin1 = service.openGpio(MOTOR_B_1_PIN);
            // Configure as an output.
            motorBPin1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            // Create GPIO connection for Motor B Pin 2.
            motorBPin2 = service.openGpio(MOTOR_B_2_PIN);
            // Configure as an output.
            motorBPin2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        // Close the resource
        if (mBtnGpio != null) {
            mBtnGpio.unregisterGpioCallback(mMotorCallback);
            try {
                mBtnGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        isListening = false;
        switch (requestCode) {
            case SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String responseMessage = result.get(0);
                    Log.d(TAG, "onActivityResult: " + responseMessage);
//                    speak(responseMessage);
                    processSpeech(responseMessage);
                }else{
                    Log.d(TAG, "onActivityResult: Result Code:"+resultCode+ " || Data: "+ data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
                    speak("There was some issue while listening. Please try again!");
                }
                break;
            }
            default: {
                Log.d(TAG, "onActivityResult: Default!!"+ resultCode);
            }
            break;
        }
    }

    // Register an event callback.
    private GpioCallback mMotorCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            Log.i(TAG, "GPIO callback ------------");

            if(!isListening) {
                listen();
            }
            // Return true to keep callback active.
            return true;
        }
    };

    private void listen() {
        Log.d(TAG, "listen: Listening!!");
        isListening = true;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            Log.d(TAG, "Listening... ");
            startActivityForResult(intent, SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Log.d(TAG, "Sorry! Device does not support speech input");
        }
    }

    private void speak(String text) {
        Log.d(TAG, "speaking... " + text);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onInit(int status) {
        Log.d(TAG, "onInit: "+status);
        speak("Status is "+status);
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    public void processSpeech(final String speech) {
        Log.d(TAG, "processSpeech");
        String url = "https://8713cb8f.ngrok.io/jarvis";

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
                        switch (type){
                            case "cmd":
                                if(client != null){

                                    switch (resp){
                                        case "LAMP ON":
                                            client.publish("topic/lamp", new MqttMessage("ON".getBytes("UTF-8")));
                                            break;
                                        case "LAMP OFF":
                                            client.publish("topic/lamp", new MqttMessage("OFF".getBytes("UTF-8")));
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                break;
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
                    } catch (JSONException | MqttException | UnsupportedEncodingException e) {
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

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "connectionLost....");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d(TAG, "message received --> "+payload);
        switch (payload) {
            case "FWD":
                Log.d(TAG, "Rover Forward");
                motorAPin1.setValue(true);
                motorAPin2.setValue(false);
                motorBPin1.setValue(false);
                motorBPin2.setValue(true);
                break;
            case "BWD":
                Log.d(TAG, "Rover Backward");
                motorAPin1.setValue(false);
                motorAPin2.setValue(true);
                motorBPin1.setValue(true);
                motorBPin2.setValue(false);
                break;
            case "LFT":
                Log.d(TAG, "Rover Left");
                motorAPin1.setValue(true);
                motorAPin2.setValue(false);
                motorBPin1.setValue(false);
                motorBPin2.setValue(false);
                break;
            case "RGT":
                Log.d(TAG, "Rover Right");
                motorAPin1.setValue(false);
                motorAPin2.setValue(false);
                motorBPin1.setValue(false);
                motorBPin2.setValue(true);
                break;
            case "STP":
                Log.d(TAG, "Rover Stop");
                motorAPin1.setValue(false);
                motorAPin2.setValue(false);
                motorBPin1.setValue(false);
                motorBPin2.setValue(false);
                break;
            default:
                Log.d(TAG, "Message not supported!");
                motorAPin1.setValue(false);
                motorAPin2.setValue(false);
                motorBPin1.setValue(false);
                motorBPin2.setValue(false);
                break;
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, "deliveryComplete....");
    }
}
