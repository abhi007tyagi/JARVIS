import jarvis as j
import nltk
import math_expression_calculator as math_cal
import re
import inflect
import text2num
import classifier

# print(j.get_context("hello"))
# print(j.get_context("hi"))
# print(j.get_context("hey! can you move up the ladder?"))
# print(j.get_context("hey, how are you today?"))
# print(j.get_context("how's today's weather"))
# print(j.get_context("hey, add three to 4"))
# print(j.get_context("what is three multiplied to 4"))
print(classifier.classify("its dark in here"))



# print(math_cal.get_math_evaluation("what is three multiplied to 4"))
# print("#############################  12")
# print(math_cal.get_math_evaluation("can you answer what 1898 subtracted from 100 is?"))
# print("#############################  -1798")
# print(math_cal.get_math_evaluation("what is the value for 10 divided by 2?"))
# print("#############################  5")
# print(math_cal.get_math_evaluation("multiply 7 with 5"))
# print("#############################  35")
# print(math_cal.get_math_evaluation("multiply 10 by 3"))
# print("#############################  30")
# print(math_cal.get_math_evaluation("add 5 to 2"))
# print("#############################  7")
# print(math_cal.get_math_evaluation("subtract 2 from 9"))
# print("#############################  7")
# print(math_cal.get_math_evaluation("what is 5 minus 3"))
# print("#############################  2")
# print(math_cal.get_math_evaluation("divide 14 by 2"))
# print("#############################  7")
# print(math_cal.get_math_evaluation("answer to 10 / 5"))
# print("#############################  2")
# print(math_cal.get_math_evaluation("what is the result for 7 multiplied by 6"))
# print("#############################  42")
# print(math_cal.get_math_evaluation("add two thousand three hundred seventy six to 100"))
# print("#############################  2476")
# print(math_cal.get_math_evaluation("what is three hundred seventy five multiplied by 100"))
# print("#############################  37500")
# print(math_cal.get_math_evaluation("eleven million seventy five multiplied by 100"))
# print("#############################  110007500")
# print(math_cal.get_math_evaluation("what do we get when 100 is added to 23?"))
# print("#############################  123")
# print(math_cal.get_math_evaluation("multiply 1 to 2 minus 6 plus 10 multiplied by 2 divided by 4"))
# print("#############################  1")
# print(math_cal.get_math_evaluation("what is 10 multiplied by 6 plus 2 minus 6 plus 10 multiplied by 2 divided by 4"))
# print("#############################  61")
# print(math_cal.get_math_evaluation("calculate forty two thousand one hundred seventy five divided by 100"))
# print("#############################  421.75")
# print(math_cal.get_math_evaluation("evaluate 10 / 2 - 6 + 7"))
# print("#############################  6")
# print(math_cal.get_math_evaluation("what is seven multiplied by 6"))
# print("############################# 42")
# print(math_cal.get_math_evaluation("what is eight + six"))
# print("############################# 14")
# print(math_cal.get_math_evaluation("what is eight minus six"))
# print("############################# 2")
# print(math_cal.get_math_evaluation("what is eight plus six"))
# print("############################# 14")
# print(math_cal.get_math_evaluation("what is eleven million X 10"))
# print("#############################  11000000")
# print(math_cal.get_math_evaluation("what is 7 x 8"))
# print("#############################  56")
# print(math_cal.get_math_evaluation("subtract 5 from 100"))
# print("#############################  95")
# print(math_cal.get_math_evaluation("add 5 to 100"))
# print("#############################  105")
# print(math_cal.get_math_evaluation("multiply 5 with 100"))
# print("#############################  500")
# print(math_cal.get_math_evaluation("divide 100 by 5"))
# print("#############################  20")
# print(math_cal.get_math_evaluation("5 subtracted from 100"))
# print("############################# 95")
# print(math_cal.get_math_evaluation("5 plus 10"))
# print("#############################  15")
# print(math_cal.get_math_evaluation("5 minus 10"))
# print("#############################  -5")
# print(math_cal.get_math_evaluation("5 multiplied by 100"))
# print("#############################  500")
# print(math_cal.get_math_evaluation("5 divided by 10"))
# print("#############################  0.5")
# print(math_cal.get_math_evaluation("calculate 5 subtracted from 10"))
# print("#############################  5")
# print(math_cal.get_math_evaluation("what is twelve billion divided by 1000 x 2 minus 100000")) # FAILED CASE
# print("#############################")



# text = "multiply 1 to 2 minus 6 plus 10 multiplied by 2 divided by 4"
# text = "1/2+3-4="
# exp = "1*2-6+10*2/4"
# p = inflect.engine()
# print(p.number_to_words(99))
#
# print(text2num.text2num("eight"))
#
# tokenized = nltk.word_tokenize(text)
# tags = nltk.pos_tag(tokenized)
# print(tags)
# print(str(eval(exp)))

# text = "asix3b"
#
# print(re.search("[0-9][\*+-/xX][0-9]", text))