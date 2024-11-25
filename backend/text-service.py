import functions_framework
from openai import OpenAI
import os
from flask import jsonify, request

# Initialize OpenAI client
client = OpenAI(api_key=os.environ['OPENAI_API_KEY'])

# Configurable prompts for each endpoint
SUMMARY_PROMPT = "Summarize the following text:"
INSIGHT_PROMPT = "Provide insights on the following text:"

@functions_framework.http
def text_service(request):
    # Handle preflight requests for CORS
    if request.method == "OPTIONS":
        return cors_preflight_response()

    # Ensure the request method is POST
    if request.method != 'POST':
        return 'Only POST method is supported', 405

    # Parse request JSON
    request_json = request.get_json(silent=True)
    if not request_json or 'endpoint' not in request_json or 'input' not in request_json:
        return jsonify({'error': 'Missing required fields: endpoint or input'}), 400

    # Extract endpoint and input text
    endpoint = request_json['endpoint']
    input_text = request_json['input']

    # Dispatch to the correct handler based on the endpoint
    if endpoint == 'summary':
        response_data = handle_summary(input_text)
    elif endpoint == 'insight':
        response_data = handle_insight(input_text)
    else:
        response_data = jsonify({'error': 'Invalid endpoint specified'}), 400

    # Add CORS headers to the response
    response_data.headers.add('Access-Control-Allow-Origin', '*')
    return response_data

# Helper function for handling CORS preflight requests
def cors_preflight_response():
    headers = {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'POST',
        'Access-Control-Allow-Headers': 'Content-Type,Authorization',
        'Access-Control-Max-Age': '3600'
    }
    return '', 204, headers

# Helper function for summarizing text
def handle_summary(input_text):
    try:
        response = client.completions.create(
            model="gpt-4o-mini",
            prompt=f"{SUMMARY_PROMPT} {input_text}",
            max_tokens=50
        )
        result_text = response.choices[0].text.strip()
        return jsonify({'output': result_text}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

# Helper function for generating insights from text
def handle_insight(input_text):
    try:
        response = client.completions.create(
            model="gpt-4o-mini",
            prompt=f"{INSIGHT_PROMPT} {input_text}",
            max_tokens=100
        )
        result_text = response.choices[0].text.strip()
        return jsonify({'output': result_text}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500