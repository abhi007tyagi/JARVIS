import nltk
from nltk.corpus import stopwords
import requests


def get_web_result(text):
    # tokenize and remove stop words
    tokenized = nltk.word_tokenize(text)

    stop_words = set(stopwords.words("english"))
    filtered_text = [w for w in tokenized if not w in stop_words]
    print("fitered text -> ", filtered_text)

    #  tag the filtered words
    tags = nltk.pos_tag(filtered_text)
    print("fitered tags -> ", tags)

    allowed_word_types = ["N"]  # ["J", "R", "V", "N", "CD"]

    query = ""
    for w in tags:
        if w[1][0] in allowed_word_types:
            query += w[0] + " "

    url_endpoint = 'https://www.duckduckgo.com'
    param = {'q': query[:-1], 'format': 'json', 't': 'h', 'ia': 'web'}
    headers = {'Content-Type': 'application/json'}

    resp = requests.get(url_endpoint, params=param, headers=headers)
    print("resp -> ", resp)
    result_json = resp.json()
    print("result json-> ", result_json)
    try:
        result = result_json['RelatedTopics'][0]['Text']
        result = "Here's what I have found about " + query[:-1] + "! " + result
    except:
        result = "Sorry! I couldn't find anything around " + query[:-1]
    print("result -> ", result)
    return result
