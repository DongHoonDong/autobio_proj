from flask import Flask, request, jsonify, render_template, session
import os
import speech_recognition as sr
import requests
import json
from pydub import AudioSegment
from io import BytesIO
from dotenv import load_dotenv
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

load_dotenv()

@app.route('/')
def index():
    return render_template('fileupload.html')

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

    system_content = """
    당신은 노년층의 녹음된 내용을 바탕으로 상세하고 감동적인 자서전 초안을 작성하는 전문가입니다.
    다음 원칙을 반드시 따라주세요:
    1. 사용자의 녹음 내용을 충실히 반영하고 상상력을 동원해 더 풍부한 내용으로 확장
    2. 쉽고 명확한 언어 사용
    3. 시간 순서나 주제별로 내용을 상세히 구성
    4. 개인의 감정과 경험을 생생하고 구체적으로 표현
    5. 존댓말 사용 (단, 화자의 직접 인용은 예외)
    6. 맥락을 제공하는 설명과 역사적, 사회적 배경 정보 추가
    7. 화자의 어조와 개성을 살리는 문체 사용
    8. 중요한 사건이나 전환점을 자세히 묘사하고 강조
    9. 화자의 내면 세계와 성장 과정을 심도 있게 탐구
    10. 주변 인물들과의 관계와 그들이 화자에게 미친 영향 상세히 기술
    """

    user_content = f"""
    다음 녹음 내용을 바탕으로 노년층을 위한 상세한 자서전 초안을 작성해주세요:
    '{content}'

    초안 작성 시 주의사항:
    1. 전체 내용을 7-10개의 주요 장으로 나누어 구성해주세요. 각 장은 최소 500단어 이상으로 작성해주세요.
    2. 각 장은 다음 구조로 구성해주세요:
       a. 장 제목 (굵은 글씨)
       b. 4-5문단의 상세한 본문 내용
       c. 구체적인 일화나 에피소드 최소 2개 이상 포함
       d. 해당 시기나 사건에 대한 화자의 감정, 생각, 내적 변화를 깊이 있게 표현
       e. 당시의 사회적, 역사적 배경에 대한 간략한 설명
    3. 어려운 용어는 피하고, 필요시 괄호 안에 쉬운 설명을 덧붙여주세요.
    4. 각 장 사이에 자연스러운 전환을 위한 문장을 추가하고, 전체적인 이야기의 흐름을 유지해주세요.
    5. 화자의 성격, 가치관, 인생 철학이 구체적인 사례와 함께 드러나도록 작성해주세요.
    6. 마지막에 화자의 인생을 요약하고 미래에 대한 희망이나 후손들에게 전하고 싶은 메시지를 포함해주세요.
    7. 초안 작성 후, 다음 사항들을 포함하여 글을 더 보완할 수 있는 방법을 제시해주세요:
       a. 더 깊이 탐구할 수 있는 주제나 사건 제안
       b. 추가 인터뷰가 필요한 부분 지적
       c. 역사적 맥락이나 사회적 배경에 대한 추가 조사가 필요한 부분 제안
       d. 감정적 깊이를 더할 수 있는 부분 제안
       e. 독자의 흥미를 더 끌 수 있는 방안 제시
    """

    GPTData = {
        "model": "gpt-3.5-turbo",
        "max_tokens": 4096,
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

def generate_title_with_gpt(draft):
    api_key = os.getenv("OPENAI_API_KEY")
    GPTHeaders = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {api_key}"
    }
    GPTUrl = "https://api.openai.com/v1/chat/completions"

    system_content = """
    당신은 자서전의 내용을 바탕으로 적절한 제목을 생성하는 전문가입니다.
    다음 원칙을 따라 제목을 생성해주세요:
    1. 자서전의 핵심 내용을 반영
    2. 간결하면서도 의미 있는 제목
    3. 독자의 흥미를 끌 수 있는 제목
    """

    user_content = f"""
    다음 자서전 내용을 바탕으로 적절한 제목을 생성해주세요:

    '{draft[:500]}...'
    """

    GPTData = {
        "model": "gpt-3.5-turbo",
        "max_tokens": 100,
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

    guideline = get_gpt_response(content)
    title = generate_title_with_gpt(guideline)

    return jsonify({'guideline': guideline, 'title': title})

if __name__ == '__main__':
    app.run(debug=True)