J.A.R.V.I.S.
================

Just A Rather Very Intelligent System<br>

[![J.A.R.V.I.S.](https://i.ytimg.com/vi/3pHFdfW_OTQ/1.jpg)](https://youtu.be/3pHFdfW_OTQ)


This project demonstrates the use of different technologies and their integration to build an intelligent system which will interact with a human and support in their day to day tasks. It is inspired from the AI bot, "JARVIS" from the movie, "Iron Man".<br>

Currently, the project version 0.30, has 5 main modules:<br>
1. JARVIS BRAIN<br>
2. JARVIS THINGS<br>
3. JARVIS MOBILE<br>
4. JARVIS AMAZON ALEXA<br>
5. JARVIS WEB<br><br>

JARVIS BRAIN
--------------
NLTK and Scikit based, NLP engine written in python to classify the input speech (in the text) and process it depending on the classification. Currently, the trainer trains the engine to classify for 3 patterns:<br>
a. welcome greetings<br>
b. basic mathematics expression solving (+,-,*,/, square, squareroot, cube, cuberoot)<br>
c. commands to do tasks<br>

If the input speech result is not found out from the classified module, then it is processed for online web search using "duckduckgo" web search API.<br>

This engine is accessible via python based service build using Flask framework.<br><br>


JARVIS THINGS
---------------
Android Things (running on RPi3) based interface and controlling unit. It takes speech input from the humans and sends the text version of it to JARVIS BRAIN for processing. Then based on the response from the Brain, it performs tasks. It can also have its inputs via sensors and can triggers tasks directly.<br>
USB mic connected takes the input speech and via Google Voice Search (added manually via old apk), converts it into text. This text is sent to Brain using Volley library. The JSON response is parsed and further processed to check what the Things have to do. If it's a command type, the respective task is performed like moving a rover, turning on the lamp, etc. Or else if it's a simple reply, using inbuilt Text-To-Speech, it is spoken out via USB speaker connected to RPi3.<br>
MQTT protocol is used for assisting JARVIS MOBILE to control the Rover and the Lamp. 

Rover control: Rover is based on Arduino UNO board and communicates with JARVIS THINGS via RF module. Based on the command received from Brain, different commands are transmitted to Rover to make it move forward, backwards, left, right and stop. For the demo, all commands execute for 5 seconds only.

Lamp control: Lamp is based on ESP8266 wifi module. It is programmed to control the relay which then controls the AC appliance i.e. the LAMP. MQTT protocol is used for communication between the Thing and the Lamp.

Location Temperature: Added in ver 0.25, speaking out "Temperature of New Delhi" will fetch the temperature for New Delhi using Open Weather Map API. If not found it will go for regular web search as fail safe option.<br><br>

JARVIS MOBILE
---------------
The Android application provides another user interface to communicate with the Brain and perform the tasks. Similar to JARVIS THINGS, it uses, in-built Speech-To-Text and Text-To-Speech libraries. The replies are read out aloud on the mobile device itself. However, to perform other tasks like moving the rover and other, it is dependent on JARVIS THINGS to perform those tasks on its behalf. MQTT is used to send instructions to the Things and the tasks are performed.<br><br> 

JARVIS AMAZON ALEXA
---------------------
Basic Amazon Alexa skill which is integrated with JARVIS BRAIN via HTTP protocol. Jarvis skill can be invoked on Amazon Alexa supported device like Amazon Echo using the invoke term as "jarvis". The device will use inbuilt Voice to Text and Text to Speech features and communicate with the JARVIS BRAIN. During testing, it was found that Voice to Text on Alexa was not that accurate as compared to Google's and this resulted in error responses from BRAIN. The skill is not published for world yet.<br><br> 

JARVIS WEB
------------
Similar to JARVIS MOBILE, JARVIS WEB gives another interface to interact via web platform. It uses 'webkitSpeechRecognition' for Voice to Text conversion and 'SpeechSynthesisUtterance' for Text to Speech. AJAX in simplest form is used to communicate with JARVIS BRAIN. JARVIS WEB can do the greetings, maths calculations, fetching temperature for the location or do a web search. In future, it would be integrated with JARVIS THINGS and control other hardwares using MQTT.<br><br>

HOW TO TRAIN & RUN SERVER
---------------------------
1. Fork and clone the project to your repository.
2. Make sure all the pre-requisites are set.
3. Train the AI engine by running "trainer.py"
4. Activate JARVIS BRAIN by running "server.py". <br><br>


CONTRIBUTION
--------------
I am just a regular coder just like you. You are free to contribute to the project. Help me with implementing new features, do code review, submit bugs, resolve bugs. Let's build the J.A.R.V.I.S. together :)<br>
Just Raise an issue for the work you want to contribute. Add some details. Once I have a look at it and approve it, you can send your PR.

