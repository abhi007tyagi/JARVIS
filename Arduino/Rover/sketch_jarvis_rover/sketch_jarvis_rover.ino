const int inputPin1 = 10;
const int inputPin2 = 11;
const int inputPin3 = 12;
const int motorAPin1 =  7; 
const int motorAPin2 =  6;
const int motorBPin1 =  5;
const int motorBPin2 =  4;     

// variables for input pins
int input1State = 0;         
int input2State = 0;
int input3State = 0;

void setup() {
  // initialize the input pins
  pinMode(inputPin1, INPUT);
  pinMode(inputPin2, INPUT);
  pinMode(inputPin3, INPUT);

  // initialize the LED pin as an output:
  pinMode(motorAPin1, OUTPUT);
  pinMode(motorAPin2, OUTPUT);
  pinMode(motorBPin1, OUTPUT);
  pinMode(motorBPin2, OUTPUT);
}

void loop() {
  // read the state of the input pins:
  input1State = digitalRead(inputPin1);
  input2State = digitalRead(inputPin2);
  input3State = digitalRead(inputPin3);

  // check for input command
  // check for FORWARD
  if (input1State == HIGH && input2State == HIGH && input3State == LOW) {
    // move forward
    digitalWrite(motorAPin1, HIGH);
    digitalWrite(motorAPin2, LOW);
    digitalWrite(motorBPin1, LOW);
    digitalWrite(motorBPin2, HIGH);
  } else if (input1State == LOW && input2State == LOW && input3State == LOW) {
    // move backward
    digitalWrite(motorAPin1, LOW);
    digitalWrite(motorAPin2, HIGH);
    digitalWrite(motorBPin1, HIGH);
    digitalWrite(motorBPin2, LOW);
  } else if (input1State == HIGH && input2State == LOW && input3State == LOW) {
    // turn left
    digitalWrite(motorAPin1, HIGH);
    digitalWrite(motorAPin2, LOW);
    digitalWrite(motorBPin1, LOW);
    digitalWrite(motorBPin2, LOW);
  } else if (input1State == LOW && input2State == HIGH && input3State == LOW) {
    // turn right
    digitalWrite(motorAPin1, LOW);
    digitalWrite(motorAPin2, LOW);
    digitalWrite(motorBPin1, LOW);
    digitalWrite(motorBPin2, HIGH);
  } else if (input3State == HIGH) {
    // stop
    digitalWrite(motorAPin1, LOW);
    digitalWrite(motorAPin2, LOW);
    digitalWrite(motorBPin1, LOW);
    digitalWrite(motorBPin2, LOW);
  }
}
