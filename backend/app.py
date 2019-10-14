from flask import Flask, jsonify, request
from flask_cors import *
import os

app = Flask(__name__)
CORS(app, supports_credentials=True)


@app.route('/data', methods=['GET'])
def data():
    lines = []
    sites = set()
    file = open('subway.txt', 'r')
    for item in file:
        ll = item.strip().split()
        if ll[0].find('*') != -1:
            lines.append(ll[1])
        else:
            sites.add(ll[0])
            sites.add(ll[1])
    return jsonify({
        'lines': lines,
        'sites': list(sites)
    })


GET_ALL_CMD = 'java subway -map subway.txt -a %s -o out.txt'
GET_PATH_CMD = 'java subway -map subway.txt -b %s %s -o out.txt'


@app.route('/query', methods=['POST'])
def query():
    json_data = request.get_json()
    all = json_data['isAll']
    if all:
        os.system(GET_ALL_CMD % json_data['query'])
    else:
        os.system(GET_PATH_CMD % (json_data['query'][0], json_data['query'][1]))
    file = open('out.txt', 'r')
    res = []
    for item in file:
        res.append(item.strip())
    return jsonify({
        'res': res
    })


if __name__ == '__main__':
    app.run()
