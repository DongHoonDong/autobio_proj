from flask import Flask, request, jsonify, render_template
import os
import speech_recognition as sr
import requests
import json
from pydub import AudioSegment
from io import BytesIO
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)

@app.route('/')
def index():
    return render_template('fileupload.html')

def recognize_speech(audio_data):
    r = sr.Recognizer()
    try:
        recognized_text = r.recognize_google(audio_data, language="ko-KR")
        return recognized_text
    except sr.UnknownValueError:
        return "Your speech cannot be understood"
    except sr.RequestError as e:
        return f"Request Error; {0}".format(e)

def get_gpt_response(content):
    api_key = os.getenv("OPENAI_API_KEY")
    GPTHeaders = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {api_key}"
    }
    GPTUrl = "https://api.openai.com/v1/chat/completions"

    system_content = "너는 다른 사람들의 이야기를 듣고, 이해하기 쉬운 자서전 가이드라인을 제공해주는 전문가야."
    user_content = f"다음 내용을 바탕으로 하나의 자서전 가이드라인을 작성해줘. 제공된 정보를 바탕으로 누구나 이해하기 쉬운 구조로 가이드라인을 작성해줘: '{content}'"

    GPTData = {
        "model": "gpt-3.5-turbo",
        "max_tokens": 2000,
        "n": 1,
        "stop": None,
        "temperature": 0.8,
        "messages": [
            {
                "role": "system",
                "content": system_content
            },
            {
                "role": "user",
                "content": user_content
            }
        ],
    }

    GPTResponse = requests.post(GPTUrl, headers=GPTHeaders, data=json.dumps(GPTData))

    if GPTResponse.status_code == 200:
        GPTResult = GPTResponse.json()
        return GPTResult["choices"][0]["message"]["content"].strip()
    else:
        return f"Error {GPTResponse.status_code}: {GPTResponse.text}"

@app.route('/generate-final-draft', methods=['POST'])
def generate_final_draft():
    content = request.json.get('content')
    if not content:
        return jsonify({"error": "No content provided"}), 400

    final_draft = get_gpt_response(content)

    return jsonify({"finalDraft": final_draft})

if __name__ == '__main__':
    app.run(debug=True)