import nltk
from nltk.corpus import stopwords


def get_greeting(text):
    # tokenize and remove stop words
    tokenized = nltk.word_tokenize(text)

    stop_words = set(stopwords.words("english"))
    # filtered_text = [w for w in tokenized if not w in stop_words]
    # print("fitered text -> ", filtered_text)

    #  tag the filtered words
    tags = nltk.pos_tag(tokenized)
    print("fitered tags -> ", tags)

