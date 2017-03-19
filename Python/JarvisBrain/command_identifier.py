import nltk
from nltk.corpus import stopwords


def execute_lamp_cmd(tags):
    print("lamp cmd")
    for word in tags:
        if word[0] in ["on", "ON", "On", "dark", "not visible", "not bright"]:
            print("LAMP ON")
        elif word[0] in ["off", "OFF", "Off", "bright"]:
            print("LAMP OFF")


def execute_car_cmd(tags):
    print("car cmd")
    for word in tags:
        if word[0] in ["forward", "up", "ON", "on"]:
            print("FORWARD")
        elif word[0] in ["backward", "down"]:
            print("BACKWARD")
        elif word[0] in ["left"]:
            print("LEFT")
        elif word[0] in ["right"]:
            print("RIGHT")
        elif word[0] in ["stop", "off", "OFF", "halt"]:
            print("STOP")


def execute_song_cmd(tags):
    print("song cmd")
    for word in tags:
        if word[0] in ["play"]:
            print("PLAY")
        elif word[0] in ["up", "high", "higher"]:
            print("VOLUME UP")
        elif word[0] in ["down", "low", "lower"]:
            print("VOLUME DOWN")
        elif word[0] in ["stop", "off", "OFF", "halt"]:
            print("STOP PLAYING")


def extract_cmd(tags):
    print("executing cmd ->", tags)
    for word in tags:
        if word[0] in ["dark", "lamp", "light", "bright"]:
            execute_lamp_cmd(tags)
        elif word[0] in ["robot", "car", "bot", "move", "rover"]:
            execute_car_cmd(tags)
        elif word[0] in ["play", "song", "listen", "listening", "music", "volume"]:
            execute_song_cmd(tags)


def get_command(text):
    # tokenize and remove stop words
    tokenized = nltk.word_tokenize(text)

    stop_words = set(stopwords.words("english"))
    # filtered_text = [w for w in tokenized if not w in stop_words]
    # print("fitered text -> ", filtered_text)

    #  tag the filtered words
    tags = nltk.pos_tag(tokenized)
    print("fitered tags -> ", tags)

    extract_cmd(tags)
