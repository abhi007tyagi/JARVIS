from datetime import timedelta
from flask import Flask, make_response, request, current_app, jsonify
from functools import update_wrapper
import requests
import json
import classifier

app = Flask(__name__)


def crossdomain(origin=None, methods=None, headers=None,
                max_age=21600, attach_to_all=True,
                automatic_options=True):
    if methods is not None:
        methods = ', '.join(sorted(x.upper() for x in methods))
    if headers is not None and not isinstance(headers, str):
        headers = ', '.join(x.upper() for x in headers)
    if not isinstance(origin, str):
        origin = ', '.join(origin)
    if isinstance(max_age, timedelta):
        max_age = max_age.total_seconds()

    def get_methods():
        if methods is not None:
            return methods

        options_resp = current_app.make_default_options_response()
        return options_resp.headers['allow']

    def decorator(f):
        def wrapped_function(*args, **kwargs):
            if automatic_options and request.method == 'OPTIONS':
                resp = current_app.make_default_options_response()
            else:
                resp = make_response(f(*args, **kwargs))
            if not attach_to_all and request.method != 'OPTIONS':
                return resp

            h = resp.headers

            h['Access-Control-Allow-Origin'] = origin
            h['Access-Control-Allow-Methods'] = get_methods()
            h['Access-Control-Max-Age'] = str(max_age)
            if headers is not None:
                h['Access-Control-Allow-Headers'] = headers
            return resp

        f.provide_automatic_options = False
        return update_wrapper(wrapped_function, f)

    return decorator


@app.route('/')
def hello_jarvis():
    return 'JARVIS is up and running'


@app.route('/jarvis', methods=['GET', 'POST', 'OPTIONS'])
@crossdomain(origin="*", headers=['Content-Type', 'Accept'])
def jarvis():
    try:
        input_request = request.get_json(silent=True)
        print("request->" + str(input_request))
        text = input_request['query']
    except:
        text = request.args.get('query')

    result = ""
    typ = ""
    try:
        result, typ = classifier.classify(text)
    except Exception as e:
        print("error -> ", e.args)
        result = e.args
        typ = "err"

    server_response = jsonify(
        response=result,
        type=typ
    )

    print("Server Response --> ", server_response)
    return server_response


if __name__ == '__main__':
    app.run()
