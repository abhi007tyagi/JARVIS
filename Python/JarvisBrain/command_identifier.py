import nltk


def execute_lamp_cmd(tags):
    print("lamp cmd -> ", tags)
    for word in tags:
        if word[0] in ["on", "ON", "On", "dark", "not visible", "not bright"]:
            print("LAMP ON")
            return "LAMP ON"
        elif word[0] in ["off", "OFF", "Off", "bright"]:
            print("LAMP OFF")
            return "LAMP OFF"
    return ""


def execute_car_cmd(tags):
    print("car cmd -> ", tags)
    for word in tags:
        if word[0] in ["forward", "up", "ON", "on"]:
            print("FORWARD")
            return "FORWARD"
        elif word[0] in ["backward", "backwards", "down"]:
            print("BACKWARD")
            return "BACKWARD"
        elif word[0] in ["left"]:
            print("LEFT")
            return "LEFT"
        elif word[0] in ["right"]:
            print("RIGHT")
            return "RIGHT"
        elif word[0] in ["stop", "off", "OFF", "halt"]:
            print("STOP")
            return "STOP"
    return ""


def execute_song_cmd(tags):
    print("song cmd -> ", tags)
    for word in tags:
        if word[0] in ["play"]:
            print("PLAY")
            return "PLAY"
        elif word[0] in ["up", "high", "higher", "increase"]:
            print("VOLUME UP")
            return "VOLUME UP"
        elif word[0] in ["down", "low", "lower", "decrease"]:
            print("VOLUME DOWN")
            return "VOLUME DOWN"
        elif word[0] in ["stop", "off", "OFF", "halt"]:
            print("STOP PLAYING")
            return "STOP PLAYING"
    return ""


def extract_cmd(tags):
    print("executing cmd ->", tags)
    for word in tags:
        if word[0] in ["dark", "lamp", "light", "lights", "bright"]:
            return execute_lamp_cmd(tags)
        elif word[0] in ["robot", "car", "bot", "move", "rover"]:
            return execute_car_cmd(tags)
        elif word[0] in ["play", "song", "listen", "listening", "music", "volume"]:
            return execute_song_cmd(tags)


def get_command(text):
    # tokenize and remove stop words
    tokenized = nltk.word_tokenize(text)

    # stop_words = set(stopwords.words("english"))
    # filtered_text = [w for w in tokenized if not w in stop_words]
    # print("fitered text -> ", filtered_text)

    #  tag the filtered words
    tags = nltk.pos_tag(tokenized)
    print("filtered tags -> ", tags)

    return extract_cmd(tags)
