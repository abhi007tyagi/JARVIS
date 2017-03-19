import jarvis as j
import math_expression_calculator as mth
import command_identifier as cmd


def classify(text):
    context = j.get_context(text)
    print(context[0], context[1])
    if context[0] == 'mth' and context[1] >= 0.6:
        return mth.get_math_evaluation(text)
    elif context[0] == 'wel' and context[1] >= 0.6:
        return "Hello! I am Jarvis."
    elif context[0] == 'cmd' and context[1] >= 0.6:
        cmd.get_command(text)
        return "Ok! I will do it."
