import nltk
import text2num as t2n
import re
from nltk.corpus import stopwords


def cuberoot(x):
    try:
        x = float(x)
    except:
        x = float(t2n.text2num(x))
    if 0 <= x:
        return x ** (1. / 3.)
    return -(-x) ** (1. / 3.)


def squareroot(x):
    try:
        x = float(x)
    except:
        x = float(t2n.text2num(x))
    if 0 <= x:
        return x ** (1. / 2.)
    return -(-x) ** (1. / 2.)


def cube(x):
    try:
        return float(x) ** 3
    except:
        return float(t2n.text2num(x)) ** 3


def square(x):
    try:
        return float(x) ** 2
    except:
        return float(t2n.text2num(x)) ** 2


def get_exp(chunked):
    exp = extract_chunks(chunked, ["Chunk00", "Chunk1", "Chunk2", "Chunk3"])
    # print("get_exp ->", exp)
    return exp


def extract_chunks(chunked, tags):
    exp = ""
    digit = ""
    for subtree in chunked.subtrees(filter=lambda t: t.label() in tags):
        for l in subtree.leaves():
            print("l[0] -->>> ", str(l[0]))
            if str(l[0]) not in ["+", "-", "*", "/", "x", "X", "plus", "minus", "multiplied", "divided"]:
                digit += str(l[0]) + " "
            else:
                try:
                    digit = str(t2n.text2num(digit[:-1]))
                    digit += " " + str(l[0])
                    exp += " " + digit
                    digit = ""
                except Exception as e:
                    print("text2num error ->", e.args)
    if len(digit) > 0:
        exp += " " + digit
    return exp


def extract_direct_math_expressions(tags):
    exp = ""
    stack = []
    counter = 0
    isSubtract = False
    isSubtracted = False

    for word in tags:
        skip = False
        if "add" == word[0]:
            stack.append(" + ")
        elif "subtract" == word[0]:
            stack.append(" - ")
            isSubtract = True
        elif "multiply" == word[0]:
            stack.append(" * ")
        elif "divide" == word[0]:
            stack.append(" / ")
        elif "plus" == word[0] or "+" == word[0] or "added" == word[0]:
            exp += " + "
        elif "minus" == word[0] or "-" == word[0]:
            exp += " - "
        elif "multiplied" == word[0] or "*" == word[0] or "x" == word[0] or "X" == word[0]:
            exp += " * "
        elif "divided" == word[0] or "/" == word[0]:
            exp += " / "
        elif "subtracted" == word[0]:
            exp += " - "
            # isSubtracted = True
            return str(eval("abc"))

        if word[1] == "CD" and word[0] not in ["*", "x", "X", "/", "+", "-"]:
            if isSubtract and len(stack) != 2:
                try:
                    stack.append(str(t2n.text2num(str(word[0]))))
                except:
                    stack.append(word[0])
                skip = True
            # elif isSubtracted:

            else:
                try:
                    exp += str(t2n.text2num(str(word[0])))
                except:
                    exp += str(word[0])

        # to check word numbers that are tagged as non 'CD' .... this is the issue with NLTK
        elif word[0] not in ["*", "x", "X", "/", "+", "-"]:
            if isSubtract and len(stack) != 2:
                try:
                    stack.append(str(t2n.text2num(str(word[0]))))
                except:
                    print("")
                skip = True
            else:
                try:
                    exp += str(t2n.text2num(str(word[0])))
                except:
                    print("")

        if counter > 0 and len(stack) > 0 and not skip:
            if isSubtract:
                stack.reverse()
                exp += stack.pop()
                exp += stack.pop()
                isSubtract = False
            else:
                exp += stack.pop()
        if word[0] in ["*", "x", "X", "/", "+", "-", "add", "subtract", "multiply", "divide", "added", "subtracted",
                       "multiplied", "divided"]:
            counter += 1

    print("exp 2 -> ", exp)
    return str(eval(exp))


def check_word_action(exp):
    if "multiplied" in exp:
        exp = exp.replace("multiplied", "*")
        return exp
    if "multiplied" in exp:
        exp = exp.replace("multiplied", "*")
        return exp
    if "multiply" in exp:
        exp = exp.replace("multiply ", "")
        if "with" in exp:
            exp = exp.replace("with", "*")
            return exp
        if "by" in exp:
            exp = exp.replace("by", "*")
            return exp
    if "x" in exp:
        exp = exp.replace("x", "*")
        return exp
    if "X" in exp:
        exp = exp.replace("X", "*")
        return exp
    if "divided" in exp:
        exp = exp.replace("divided", "/")
        return exp
    if "divide" in exp:
        exp = exp.replace("divide", "")
        if "with" in exp:
            exp = exp.replace("with", "/")
            return exp
        if "by" in exp:
            exp = exp.replace("by", "/")
            return exp
    if "plus" in exp:
        exp = exp.replace("plus", "+")
        return exp
    if "add" in exp:
        exp = exp.replace("add ", "")
        if "in" in exp:
            exp = exp.replace("in", "+")
            return exp
        if "to" in exp:
            exp = exp.replace("to", "+")
            return exp
    if "added" in exp:
        exp = exp.replace("added", "+")
        return exp
    if "minus" in exp:
        exp = exp.replace("minus", "-")
        return exp
    if "subtracted" in exp:
        temp = exp.split(" subtracted ")
        exp = temp[1] + " - " + temp[0]
        return exp
    if "subtract" in exp:
        exp = exp.replace("subtract", "")
        if "from" in exp:
            temp = exp.split(" from ")
            exp = temp[1] + " - " + temp[0]
            return exp
    return exp


def format_input(text):
    # regex = r"[0-9\s][*+/xX-][0-9\s]"

    # if re.search(regex, text):
    #     if "-" in text:
    #         text = text.replace("-", " - ")
    #     if "+" in text:
    #         text = text.replace("+", " + ")
    #     if "*" in text:
    #         text = text.replace("*", " * ")
    #     if "/" in text:
    #         text = text.replace("/", " / ")
    #     if "x" in text:
    #         text = text.replace("x", " * ")
    #     if "X" in text:
    #         text = text.replace("X", " * ")
    regex = r"[0-9\s][-][0-9\s]"
    text = re.sub(regex, " - ", text, 0)

    regex = r"[0-9\s][+][0-9\s]"
    text = re.sub(regex, " + ", text, 0)

    regex = r"[0-9\s][/][0-9\s]"
    text = re.sub(regex, " / ", text, 0)

    regex = r"[0-9\s][*][0-9\s]"
    text = re.sub(regex, " * ", text, 0)

    regex = r"[0-9\s][X][0-9\s]"
    text = re.sub(regex, " * ", text, 0)

    regex = r"[0-9\s][x][0-9\s]"
    text = re.sub(regex, " * ", text, 0)

    if "calculate" in text:
        text = text.replace("calculate", "")
    if "answer" in text:
        text = text.replace("answer", "")
    return text


def text_to_num(text):
    tokenized = nltk.word_tokenize(text);
    tags = nltk.pos_tag(tokenized)
    print(tags)
    chunkPattern = r""" Chunk0: {((<NN|CD.?|RB>)<CD.?|VBD.?|VBP.?|VBN.?|NN.?|RB.?|JJ>*)<NN|CD.?>} """
    chunkParser = nltk.RegexpParser(chunkPattern)
    chunkedData = chunkParser.parse(tags)
    print(chunkedData)

    for subtree in chunkedData.subtrees(filter=lambda t: t.label() in "Chunk0"):
        exp = ""
        for l in subtree.leaves():
            exp += str(l[0]) + " "
        exp = exp[:-1]
        print(exp)
        try:
            text = text.replace(exp, str(t2n.text2num(exp)))
        except Exception as e:
            print("error text2num ->", e.args)
        print("text2num -> ", text)
    return text


def process_power(tags):
    size = len(tags)
    i = 0
    for word in tags:
        print("******* WORD *******", word[0])
        # check i < size-1 where tag[i+1] is compared. this resolves index out of bound error
        if i < size - 1 and (word[0] == "square" or word[0] == "Square") and tags[i + 1][1] == "CD":
            sqr = square(tags[i + 1][0])
            tags[i] = (str(sqr), 'CD')
            del tags[i + 1]
            # recalculate size of tuple
            size = len(tags)
        # check i < size-1 where tag[i+1] is compared. this resolves index out of bound error
        elif i < size - 1 and (word[0] == "cube" or word[0] == "Cube") and tags[i + 1][1] == "CD":
            cub = cube(tags[i + 1][0])
            tags[i] = (str(cub), 'CD')
            del tags[i + 1]
            # recalculate size of tuple
            size = len(tags)
        # check i < size-1 where tag[i+1] is compared. this resolves index out of bound error
        elif i < size - 1 and (word[0] == "square" or word[0] == "Square") and tags[i + 1][0] == "root" and tags[i + 2][
            1] == "CD":
            sqrt = squareroot(tags[i + 2][0])
            tags[i] = (str(sqrt), 'CD')
            del tags[i + 1]
            del tags[i + 1]
            # recalculate size of tuple
            size = len(tags)
        # check i < size-1 where tag[i+1] is compared. this resolves index out of bound error
        elif i < size - 1 and (word[0] == "cube" or word[0] == "Cube") and tags[i + 1][0] == "root" and tags[i + 2][
            1] == "CD":
            cubrt = cuberoot(tags[i + 2][0])
            tags[i] = (str(cubrt), 'CD')
            del tags[i + 1]
            del tags[i + 1]
            size = len(tags)
        # check i < size no -1. else action will not be done on number like square or cube
        elif i < size and (
                        word[0] == "square" or word[0] == "Square" or word[0] == "squared" or word[0] == "Squared") and \
                        tags[i - 1][1] == "CD":
            sqr = square(tags[i - 1][0])
            tags[i - 1] = (str(sqr), 'CD')
            del tags[i]
            # recalculate size of tuple
            size = len(tags)
        # check i < size no -1. else action will not be done on number like square or cube
        elif i < size and (word[0] == "cube" or word[0] == "Cube" or word[0] == "cubed" or word[0] == "Cubed") and \
                        tags[i - 1][1] == "CD":
            cub = cube(tags[i - 1][0])
            tags[i - 1] = (str(cub), 'CD')
            del tags[i]
            # recalculate size of tuple
            size = len(tags)
        # increment index counter
        i += 1

    print("Processed TAGS --> ", tags)
    return tags


def get_math_evaluation(text):
    print("text received ->", text)

    # formatting the input text
    text = format_input(text)
    print("formated text -> ", text)
    result = ""

    # convert word numbers to digits
    text = text_to_num(text)

    # calculating simple expression like 10/2+2
    try:
        # removing any spaces
        exp = text.replace(" ", "")
        print("exp 1 -> ", exp)
        result = str(eval(exp))
    except Exception as e:
        print("error 1 -> ", e.args)

    # if result length is zero it means simple calculation failed proceed to second method
    if len(result) == 0:

        # tokenize and remove stop words
        tokenized = nltk.word_tokenize(text)

        stop_words = set(stopwords.words("english"))
        filtered_text = [w for w in tokenized if not w in stop_words]
        print("fitered text -> ", filtered_text)

        #  tag the filtered words
        tags = nltk.pos_tag(filtered_text)
        print("fitered tags -> ", tags)

        # process power of a number
        tags = process_power(tags)

        # calculating direct math expressions like "add 10 to 6 multiplied by 7 divided by 6
        try:
            result = extract_direct_math_expressions(tags)
        except Exception as e:
            print("error 2 -> ", e.args)

        # if result length is zero it means second calculation failed proceed to the third rule based approach
        if len(result) == 0:
            # do the chunking of tags
            chunk_pattern = r"""
                                Chunk00: {((<NN|CD.?|RB|VB>)<CD.?|VBD.?|VBP.?|VBN.?|NN.?|RB.?|JJ.?>*)<NN|CD.?>}
                                Chunk1: {<VB|VBP|RB|VBD|JJ|NNS><CD*><IN|TO><CD*>}
                                Chunk2: {<RB.?|CD*><JJ|VB|VBP|VBN|VBD|NNS|NN|CC><IN|TO><CD*>}
                                Chunk3: {<RB.?|CD*><JJ|VBP|VB|VBN|VBD|NNS|NN|:|CC><CD*>}
                            """
            chunk_parser = nltk.RegexpParser(chunk_pattern)
            chunked_data = chunk_parser.parse(tags)
            print(chunked_data)

            exp = get_exp(chunked_data)
            result = ""
            if len(exp) >= 2:
                try:
                    expression = check_word_action(exp)
                    result = str(eval(expression))
                except Exception as e:
                    print("error 3 ->", e.args)
                    # return str(expression + " = " + str(eval(expression)))
                    # else:
                    #     print("issue")

    return result
