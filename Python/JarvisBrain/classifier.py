import jarvis as j
import math_expression_calculator as mth
import command_identifier as cmd
import welcome_message_identifier as wel
import random_web_search as rand_web


def classify(text):
    context = j.get_context(text)
    print(context[0], context[1])
    result = ""
    if context[0] == 'mth' and context[1] >= 0.55:
        result = mth.get_math_evaluation(text)
        if len(result) > 0:
            return result, "mth"
        else:
            # do random ?
            result = rand_web.get_web_result(text, "mth")
            return result, "mth"
    elif context[0] == 'wel' and context[1] >= 0.55:
        result = wel.get_greeting(text)
        if len(result) > 0:
            return result, "wel"
        else:
            # do random
            result = rand_web.get_web_result(text, "wel")
            return result, "wel"
    elif context[0] == 'cmd' and context[1] >= 0.55:
        result = cmd.get_command(text)
        if len(result) > 0:
            return result, "cmd"
        else:
            # do random
            result = rand_web.get_web_result(text, "cmd")
            return result, "cmd"
    else:  # context[0] == 'rand':
        result = rand_web.get_web_result(text, "rand")
        return result, "rand"
