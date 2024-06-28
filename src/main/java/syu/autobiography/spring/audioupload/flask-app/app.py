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

def get_gpt_response(transcript):
    api_key = os.getenv("OPENAI_API_KEY")
    GPTHeaders = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {api_key}"
    }
    GPTUrl = "https://api.openai.com/v1/chat/completions"
    GPTData = {
        "model": "gpt-3.5-turbo",
        "max_tokens": 1000,
        "n": 1,
        "stop": None,
        "temperature": 0.8,
        "messages": [
            {
                "role": "system",
                "content": "너는 자서전 글을 쓰는 전문가야."
            },
            {
                "role": "user",
                "content": f"해당 주제에 관한 자서전 초안을 써줘. '{transcript}'에 대한 글을 써주되 각주를 많이 사용하여서 글에 대한 지식이 많이 없는 사람들도 이해하기 쉽게 작성해줘."
            }
        ],
    }

    GPTResponse = requests.post(GPTUrl, headers=GPTHeaders, data=json.dumps(GPTData))

    if GPTResponse.status_code == 200:
        GPTResult = GPTResponse.json()
        return GPTResult["choices"][0]["message"]["content"].strip()
    else:
        return f"Error {GPTResponse.status_code}: {GPTResponse.text}"

@app.route('/upload', methods=['POST'])
def upload_audio():
    if 'file' not in request.files:
        return jsonify({"error": "No audio file provided"}), 400

    audio_file = request.files['file']

    try:
        audio = AudioSegment.from_file(audio_file)
        audio = audio.set_frame_rate(16000)
        audio = audio.set_channels(1)
        audio = audio.set_sample_width(2)

        wav_io = BytesIO()
        audio.export(wav_io, format="wav")
        wav_io.seek(0)
    except Exception as e:
        return jsonify({"error": f"Audio file conversion error: {str(e)}"}), 500

    recognizer = sr.Recognizer()

    with sr.AudioFile(wav_io) as source:
        audio_data = recognizer.record(source)

    recognized_text = recognize_speech(audio_data)
    if recognized_text:
        guideline = get_gpt_response(recognized_text)
        return jsonify({"transcript": recognized_text, "guideline": guideline})
    else:
        return jsonify({"error": "Speech recognition failed"}), 500

@app.route('/regenerate', methods=['POST'])
def regenerate():
    data = request.get_json()
    transcript = data.get('transcript')

    guideline = get_gpt_response(transcript)

    response = {
        'transcript': transcript,
        'guideline': guideline
    }
    return jsonify(response)

if __name__ == '__main__':
    app.run(debug=True)
