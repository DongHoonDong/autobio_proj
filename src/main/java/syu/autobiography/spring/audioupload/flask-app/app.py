from flask import Flask, request, jsonify, render_template
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
    당신은 노년층을 위한 상세하고 친절한 자서전 가이드라인을 제공하는 전문가입니다.
    다음 원칙을 반드시 따라주세요:
    1. 사용자의 녹음 내용을 바탕으로 작성
    2. 쉽고 명확한 언어 사용
    3. 적당한 길이의 문장과 단락 구성
    4. 큰 글씨체 권장 (14pt 이상)
    5. 중요 정보는 굵은 글씨로 강조
    6. 단계별 상세 지침 제공
    7. 각 단계마다 구체적인 예시 포함
    8. 존댓말 사용
    9. 필요시 관련 경험이나 일화 추가
    """

    user_content = f"""
    다음 내용을 바탕으로 노년층을 위한 상세한 자서전 가이드라인을 작성해주세요:

    '{content}'

    가이드라인 작성 시 주의사항:
    1. 전체 과정을 7-10개의 주요 단계로 나누어 설명해주세요.
    2. 각 단계는 다음 구조로 구성해주세요:
       a. 단계 제목 (굵은 글씨)
       b. 2-3문장으로 단계 개요 설명
       c. 3-5개의 세부 지침 (번호 매기기)
       d. 구체적인 예시나 관련 일화 (1-2개)
       e. 해당 단계를 완료했을 때의 이점 설명
       f. 격려의 말
    3. 어려운 용어는 피하고, 필요시 괄호 안에 쉬운 설명을 덧붙여주세요.
    4. 각 단계 사이에 짧은 휴식이나 준비 팁을 추가해주세요.
    5. 중간중간 독자의 경험을 떠올리게 하는 질문을 넣어주세요.
    6. 마지막에 전체 과정에 대한 긍정적인 메시지와 자서전 작성의 가치를 강조해주세요.
    7. 필요하다면 부록으로 간단한 연습문제나 체크리스트를 추가해주세요.
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
    4. 필요시 부제목 추가
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
    print("generate_final_draft called")  # 로깅 추가
    content = request.json['content']
    guideline = get_gpt_response(content)
    title = generate_title_with_gpt(guideline)
    return jsonify({'guideline': guideline, 'title': title})

if __name__ == '__main__':
    app.run(debug=True)