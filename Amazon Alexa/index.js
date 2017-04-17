var APP_ID = undefined;
var http = require('http')
var https = require('https');
/**
 * The AlexaSkill prototype and helper functions
 */
var AlexaSkill = require('./AlexaSkill');


/**
 * URL for JARVIS service
 */
var jarvis_url = 'https://37b69e6b.ngrok.io/jarvis';

var cardTitle = 'J.A.R.V.I.S.';


var JarvisSkill = function () {
    AlexaSkill.call(this, APP_ID);
};

// Extend AlexaSkill
JarvisSkill.prototype = Object.create(AlexaSkill.prototype);
JarvisSkill.prototype.constructor = JarvisSkill;

JarvisSkill.prototype.eventHandlers.onSessionStarted = function (sessionStartedRequest, session) {
    console.log("JarvisSkill onSessionStarted requestId: " + sessionStartedRequest.requestId + ", sessionId: " + session.sessionId);
    // any initialization logic goes here
};

JarvisSkill.prototype.eventHandlers.onLaunch = function (launchRequest, session, response) {
    console.log("JarvisSkill onLaunch requestId: " + launchRequest.requestId + ", sessionId: " + session.sessionId);
    var speechOutput = "Hi! I am Jarvis. How may I help you?";
    var repromptText = "Please let me know, how may I help you?";
    response.ask(speechOutput, repromptText);    
};

JarvisSkill.prototype.eventHandlers.onSessionEnded = function (sessionEndedRequest, session) {
    console.log("JarvisSkill onSessionEnded requestId: " + sessionEndedRequest.requestId + ", sessionId: " + session.sessionId);
    // any cleanup logic goes here
};

JarvisSkill.prototype.intentHandlers = {
    "AskQuestionIntent": function (intent, session, response) {
      var questionSlot = intent.slots.question;
      if (questionSlot && questionSlot.value) {
        var reply = '';
        var dataQuery = '?query='+encodeURIComponent(questionSlot.value);
        getRequest(jarvis_url+dataQuery, function(resp){
          reply = resp.response;
          console.log("response-> "+reply);
          var repromptText = "Please let me know, how may I help you?";
          response.askWithCard(reply, repromptText, cardTitle, reply);
        });
      }else {
        var speechText = "An error has occurred!";
        var repromptText = speechText;
        response.askWithCard(repromptText, repromptText, cardTitle, repromptText);
      }
    },
    "AMAZON.StopIntent": function (intent, session, response) {
        var speechOutput = "Do you want to continue with Jarvis Skills? Say yes or no!";
        var repromptText = "Please let me know, do you want to continue with Jarvis Skills? Say yes or no!";
        response.askWithCard(speechOutput, repromptText,cardTitle , speechOutput);
    },
    "AMAZON.CancelIntent": function (intent, session, response) {
        var speechOutput = "Do you want to continue with Jarvis Skills? Say yes or no!";
        var repromptText = "Please let me know, do you want to continue with Jarvis Skills? Say yes or no!";
        response.askWithCard(speechOutput, repromptText,cardTitle , speechOutput);
    }
};

// Create the handler that responds to the Alexa Request.
exports.handler = function (event, context) {
    // Create an instance of the JarvisSkill.
    var jarvisSkill = new JarvisSkill();
    jarvisSkill.execute(event, context);
};

function getRequest(url, eventCallback){

    https.get(url, function(res) {
        var body = '';

        res.on('data', function (chunk) {
            body += chunk;
        });

        res.on('end', function () {
            eventCallback(JSON.parse(body));
        });
    }).on('error', function (e) {
        console.log("Got error: ", e);
        eventCallback(" Facing issue communicating with server! ");
    });

}