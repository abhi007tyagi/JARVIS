package com.tyagiabhinav.androidthings.jarvis;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener, MqttCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SPEECH_INPUT = 27;
    public static final String BTN_PIN = "BCM17"; //physical pin #11 for speech initiation switch
    public static final String ROVER_1_PIN = "BCM21"; //physical pin #40 for motor control
    public static final String ROVER_2_PIN = "BCM20"; //physical pin #38 for motor control
    public static final String ROVER_3_PIN = "BCM24"; //physical pin #18 for motor control

    private boolean isListening = false; // to stop bouncing of physical switch

    private TextToSpeech tts;

    // for on board mic ON-OFF
    private Gpio mBtnGpio;

    // for motor control of rover
    private Gpio roverPin1;
    private Gpio roverPin2;
    private Gpio roverPin3;

    private MqttClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        tts = new TextToSpeech(this, this);
        try {
            client = new MqttClient("tcp://192.168.43.86:1883", "AndroidThingSub", new MemoryPersistence());
            client.setCallback(this);
            client.connect();

            client.subscribe("topic/rover");


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
            // Create GPIO connection for Rover Pin 1.
            roverPin1 = service.openGpio(ROVER_1_PIN);
            // Configure as an output.
            roverPin1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            // Create GPIO connection for Motor A Pin 2.
            roverPin2 = service.openGpio(ROVER_2_PIN);
            // Configure as an output.
            roverPin2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            // Create GPIO connection for Motor B Pin 1.
            roverPin3 = service.openGpio(ROVER_3_PIN);
            // Configure as an output.
            roverPin3.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);

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
                } else {
                    Log.d(TAG, "onActivityResult: Result Code:" + resultCode + " || Data: " + data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
                    speak("There was some issue while listening. Please try again!");
                }
                break;
            }
            default: {
                Log.d(TAG, "onActivityResult: Default!!" + resultCode);
            }
            break;
        }
    }


    /**
     * Initialize Text to speech
     *
     * @param status
     */
    @Override
    public void onInit(int status) {
        Log.d(TAG, "onInit: " + status);
        speak("Status is " + status);
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
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "connectionLost....");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d(TAG, "message received --> " + payload);
        // Check if any Rover control message is received from Jarvis Mobile via MQTT
        switch (payload) {
            case "FORWARD":
                roverCommand("FWD");
                break;
            case "BACKWARD":
                roverCommand("BWD");
                break;
            case "LEFT":
                roverCommand("LFT");
                break;
            case "RIGHT":
                roverCommand("RGT");
                break;
            case "STOP":
                roverCommand("STP");
                break;
            default:
                break;
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, "deliveryComplete....");
    }

    // Register an event callback.
    private GpioCallback mMotorCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            Log.i(TAG, "GPIO callback ------------");

            if (!isListening) {
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


    /**
     * Process speech text. Send it to Jarvis Brain and perform task based on response.
     *
     * @param speech
     */
    private void processSpeech(final String speech) {
        Log.d(TAG, "processSpeech");
        String url = "https://37b69e6b.ngrok.io/jarvis";

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
                        switch (type) {
                            case "cmd":
                                if (client != null) {

                                    switch (resp) {
                                        // LAMP is controlled via MQTT
                                        case "LAMP ON":
                                            client.publish("topic/lamp", new MqttMessage("1".getBytes("UTF-8")));
                                            break;
                                        case "LAMP OFF":
                                            client.publish("topic/lamp", new MqttMessage("0".getBytes("UTF-8")));
                                            break;
                                        // Rover is controlled via RF serial communication
                                        case "FORWARD":
                                            roverCommand("FWD");
                                            break;
                                        case "BACKWARD":
                                            roverCommand("BWD");
                                            break;
                                        case "LEFT":
                                            roverCommand("LFT");
                                            break;
                                        case "RIGHT":
                                            roverCommand("RGT");
                                            break;
                                        case "STOP":
                                            roverCommand("STP");
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                break;
                            // Rest of the types are played on speaker
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
                    } catch (JSONException | MqttException | IOException e) {
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
                    speak("Something went wrong. Please try again later!");
                }
            });
            Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void roverCommand(String command) throws IOException {
        switch (command) {
            case "FWD":
                Log.d(TAG, "Rover Forward");
                roverPin1.setValue(true);
                roverPin2.setValue(true);
                roverPin3.setValue(false);
                stopDemoRover();
                break;
            case "BWD":
                Log.d(TAG, "Rover Backward");
                roverPin1.setValue(false);
                roverPin2.setValue(false);
                roverPin3.setValue(false);
                stopDemoRover();
                break;
            case "LFT":
                Log.d(TAG, "Rover Left");
                roverPin1.setValue(true);
                roverPin2.setValue(false);
                roverPin3.setValue(false);
                stopDemoRover();
                break;
            case "RGT":
                Log.d(TAG, "Rover Right");
                roverPin1.setValue(false);
                roverPin2.setValue(true);
                roverPin3.setValue(false);
                stopDemoRover();
                break;
            case "STP":
                Log.d(TAG, "Rover Stop");
                roverPin1.setValue(false);
                roverPin2.setValue(false);
                roverPin3.setValue(true);
                break;
            default:
                Log.d(TAG, "Message not supported!");
                roverPin1.setValue(false);
                roverPin2.setValue(false);
                roverPin3.setValue(true);
                break;
        }
    }

    // Stop rover after 5 sec. For Demo purpose as we don't want the rover to continue moving.
    private void stopDemoRover() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                try {
                    roverCommand("STP");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 5000);
    }


}
