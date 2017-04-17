import nltk
from nltk.tokenize import word_tokenize
import random
from nltk.classify.scikitlearn import SklearnClassifier
import pickle

from sklearn.naive_bayes import MultinomialNB, BernoulliNB
from sklearn.linear_model import LogisticRegression, SGDClassifier
from sklearn.svm import SVC, LinearSVC, NuSVC

print("Reading training corpus...")

welcome = open("raw/welcome.txt", "r").read()
command = open("raw/commands.txt", "r").read()
maths = open("raw/maths.txt", "r").read()
random_things = open("raw/random.txt", "r").read()

print("Building documents and words...")

all_words = []
documents = []

#  j is adject, r is adverb, and v is verb
allowed_word_types = ["J", "R", "V", "N", "CD"]

for wel in welcome.split("\n"):
    documents.append((wel, "wel"))
    words = word_tokenize(wel)
    pos = nltk.pos_tag(words)
    for w in pos:
        if w[1][0] in allowed_word_types:
            all_words.append(w[0].lower())

for cmd in command.split("\n"):
    documents.append((cmd, "cmd"))
    words = word_tokenize(cmd)
    pos = nltk.pos_tag(words)
    for w in pos:
        if w[1][0] in allowed_word_types:
            all_words.append(w[0].lower())

for mth in maths.split("\n"):
    documents.append((mth, "mth"))
    words = word_tokenize(mth)
    pos = nltk.pos_tag(words)
    for w in pos:
        if w[1][0] in allowed_word_types:
            all_words.append(w[0].lower())

# for rand in random_things.split("\n"):
#     documents.append((rand, "rand"))
#     words = word_tokenize(rand)
#     pos = nltk.pos_tag(words)
#     for w in pos:
#         if w[1][0] in allowed_word_types:
#             all_words.append(w[0].lower())

print("Saving documents...")

save_documents = open("pickled/documents.pickle", "wb")
pickle.dump(documents, save_documents)
save_documents.close()

all_words = nltk.FreqDist(all_words)

word_features = list(all_words.keys())
print("Saving words...")

save_word_features = open("pickled/word_features.pickle", "wb")
pickle.dump(word_features, save_word_features)
save_word_features.close()


def find_features(document):
    words = word_tokenize(document)
    features = {}
    for w in word_features:
        features[w] = (w in words)

    return features


featuresets = [(find_features(rev), category) for (rev, category) in documents]
random.shuffle(featuresets)
random.shuffle(featuresets)
random.shuffle(featuresets)
random.shuffle(featuresets)

save_featureset = open("pickled/featuresets.pickle", "wb")
pickle.dump(featuresets, save_featureset)
save_featureset.close()
# print(featuresets)

print("Feature-set created of length ->", len(featuresets))

training_set = featuresets[:180]
testing_set = featuresets[180:]

print("Starting training different algorithms...")

orig_classifier = nltk.NaiveBayesClassifier.train(training_set)
print("Original_classifier Accuracy ->", (nltk.classify.accuracy(orig_classifier, testing_set)) * 100)
# orig_classifier.show_most_informative_features()

save_classifier = open("pickled/originalnaivebayes.pickle", "wb")
pickle.dump(orig_classifier, save_classifier)
save_classifier.close()


## Different classifiers are used at the moment. During code optimization, only the required classifier/s will be used.
MNB_classifier = SklearnClassifier(MultinomialNB())
MNB_classifier.train(training_set)
print("MNB_classifier Accuracy ->", (nltk.classify.accuracy(MNB_classifier, testing_set)) * 100)

save_classifier = open("pickled/MNB_classifier.pickle", "wb")
pickle.dump(MNB_classifier, save_classifier)
save_classifier.close()

BernoulliNB_classifier = SklearnClassifier(BernoulliNB())
BernoulliNB_classifier.train(training_set)
print("BernoulliNB_classifier Accuracy ->", (nltk.classify.accuracy(BernoulliNB_classifier, testing_set)) * 100)

save_classifier = open("pickled/BernoulliNB_classifier.pickle", "wb")
pickle.dump(BernoulliNB_classifier, save_classifier)
save_classifier.close()

LogisticRegression_classifier = SklearnClassifier(LogisticRegression())
LogisticRegression_classifier.train(training_set)
print("LogisticRegression_classifier Accuracy ->",
      (nltk.classify.accuracy(LogisticRegression_classifier, testing_set)) * 100)

save_classifier = open("pickled/LogisticRegression_classifier.pickle", "wb")
pickle.dump(LogisticRegression_classifier, save_classifier)
save_classifier.close()

SGDClassifier_classifier = SklearnClassifier(SGDClassifier())
SGDClassifier_classifier.train(training_set)
print("SGDClassifier_classifier Accuracy ->", (nltk.classify.accuracy(SGDClassifier_classifier, testing_set)) * 100)

save_classifier = open("pickled/SGDC_classifier.pickle", "wb")
pickle.dump(SGDClassifier_classifier, save_classifier)
save_classifier.close()

# SVC_classifier = SklearnClassifier(SVC())
# SVC_classifier.train(training_set)
# print("SVC_classifier Accuracy ->", (nltk.classify.accuracy(SVC_classifier, testing_set))*100)

LinearSVC_classifier = SklearnClassifier(LinearSVC())
LinearSVC_classifier.train(training_set)
print("LinearSVC_classifier Accuracy ->", (nltk.classify.accuracy(LinearSVC_classifier, testing_set)) * 100)

save_classifier = open("pickled/LinearSVC_classifier.pickle", "wb")
pickle.dump(LinearSVC_classifier, save_classifier)
save_classifier.close()

NuSVC_classifier = SklearnClassifier(NuSVC())
NuSVC_classifier.train(training_set)
print("NuSVC_classifier Accuracy ->", (nltk.classify.accuracy(NuSVC_classifier, testing_set)) * 100)

save_classifier = open("pickled/NuSVC_classifier.pickle", "wb")
pickle.dump(NuSVC_classifier, save_classifier)
save_classifier.close()
#
# # custom_classifier = CustomClassifier(SGDClassifier, LinearSVC_classifier, LogisticRegression_classifier,
# #                                      BernoulliNB_classifier, MNB_classifier)
# # print("Custom_classifier Accuracy ->", (nltk.classify.accuracy(custom_classifier, testing_set)) * 100)
