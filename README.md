J.A.R.V.I.S.
================

Just A Rather Very Intelligent System<br>

This project demonstrates the use of different technologies and their integration to build an intelligent system which will interact with a human and support in their day to day tasks. It is inspired from the AI bot, "JARVIS" from the movie, "Iron Man".<br>

[![J.R.V.I.S.](https://i9.ytimg.com/vi/xPKLcJJxpJM/1.jpg?sqp=CLj6iccF&rs=AOn4CLCK0mxJ-rGqvDGqqUzRrUEnh9pxKw&time=1491238350130)](https://youtu.be/xPKLcJJxpJM)

Currently, the project version 0.2, has 3 main platforms:<br>
1. JARVIS BRAIN<br>
2. JARVIS THINGS<br>
3. JARVIS MOBILE<br><br>

JARVIS BRAIN
--------------
NLTK and Scikit based, NLP engine written in python to classify the input speech (in the text) and process it depending on the classification. Currently, the trainer trains the engine to classify for 3 patterns:<br>
a. welcome greetings<br>
b. basic mathematics expression solving (+,-,*,/)<br>
c. commands to do tasks<br><br>

If the input speech result is not found out from the classified module, then it is processed for online web search using "duckduckgo" web search API.<br>

This engine is accessible via python based service based on Flask.<br><br>


JARVIS THINGS
---------------
Android Things (running on RPi3) based interface and controlling unit. It takes speech input from the humans and sends the text version of it to JARVIS BRAIN for processing. Then based on the response from the Brain, it performs tasks. It can also have its inputs via sensors and can triggers tasks directly.<br>
USB mic connected takes the input speech and via Google Voice Search (added manually via old apk), converts it into text. This text is sent to Brain using Volley library. The JSON response is parsed and further processed to check what the Things have to do. If it's a command type, the respective task is performed like moving a rover, turning on the lamp, etc. Or else if it's a simple reply, using inbuilt Text-To-Speech, it is spoken out via USB speaker connected to RPi3.<br>
MQTT protocol is used for assisting JARVIS MOBILE to control the Rover and the Lamp. 

Rover control: Rover is based on Arduino UNO board and communicates with JARVIS THINGS via RF module. Based on the command received from Brain, different commands are transmitted to Rover to make it move forward, backwards, left, right and stop. For the demo, all commands execute for 5 seconds only.

Lamp control: Lamp is based on ESP8266 wifi module. It is programmed to control the relay which then controls the AC appliance i.e. the LAMP. MQTT protocol is used for communication between the Thing and the Lamp.<br><br>

JARVIS MOBILE
---------------
The Android application provides another user interface to communicate with the Brain and perform the tasks. Similar to JARVIS THINGS, it uses, in-built Speech-To-Text and Text-To-Speech libraries. The replies are read out aloud on the mobile device itself. However, to perform other tasks like moving the rover and other, it is dependent on JARVIS THINGS to perform those tasks on its behalf. MQTT is used to send instructions to the Things and the tasks are performed.<br><br>  


