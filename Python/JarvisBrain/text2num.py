import re

Small = {
    'zero': 0,
    'one': 1,
    'two': 2,
    'three': 3,
    'four': 4,
    'five': 5,
    'six': 6,
    'seven': 7,
    'eight': 8,
    'nine': 9,
    'ten': 10,
    'eleven': 11,
    'twelve': 12,
    'thirteen': 13,
    'fourteen': 14,
    'fifteen': 15,
    'sixteen': 16,
    'seventeen': 17,
    'eighteen': 18,
    'nineteen': 19,
    'twenty': 20,
    'thirty': 30,
    'forty': 40,
    'fifty': 50,
    'sixty': 60,
    'seventy': 70,
    'eighty': 80,
    'ninety': 90
}

Magnitude = {
    'thousand': 1000,
    'million': 1000000,
    'billion': 1000000000,
    'trillion': 1000000000000,
    'quadrillion': 1000000000000000,
    'quintillion': 1000000000000000000,
    'sextillion': 1000000000000000000000,
    'septillion': 1000000000000000000000000,
    'octillion': 1000000000000000000000000000,
    'nonillion': 1000000000000000000000000000000,
    'decillion': 1000000000000000000000000000000000,
}


class NumberException(Exception):
    def __init__(self, msg):
        Exception.__init__(self, msg)


def text2num(s):
    a = re.split(r"[\s-]+", s)
    n = 0
    g = 0
    for w in a:
        x = Small.get(w, None)
        if x is not None:
            g += x
        elif w == "hundred" and g != 0:
            g *= 100
        else:
            x = Magnitude.get(w, None)
            if x is not None:
                n += g * x
                g = 0
            else:
                raise NumberException("Unknown number: " + w)
    return n + g


if __name__ == "__main__":
    assert 1 == text2num("one")
    assert 12 == text2num("twelve")
    assert 72 == text2num("seventy two")
    assert 300 == text2num("three hundred")
    assert 1200 == text2num("twelve hundred")
    assert 12304 == text2num("twelve thousand three hundred four")
    assert 6000000 == text2num("six million")
    assert 6400005 == text2num("six million four hundred thousand five")
    assert 123456789012 == text2num(
        "one hundred twenty three billion four hundred fifty six million seven hundred eighty nine thousand twelve")
    assert 4000000000000000000000000000000000 == text2num("four decillion")