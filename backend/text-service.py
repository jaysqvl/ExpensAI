import functions_framework
from openai import OpenAI
import os
from flask import jsonify, request

# Initialize OpenAI client
client = OpenAI(api_key=os.environ['OPENAI_API_KEY'])

MAX_SUMMARY_TOKENS = 200
MAX_INSIGHT_TOKENS = 200

# Configurable prompts for each endpoint
SUMMARY_PROMPT = """
    Summarize the following transaction data. Your summary should include:
    1. The total number of transactions.
    2. The total amount spent across all transactions.
    3. The category with the highest spending.
    4. Any notable trends (e.g., a significant increase in spending in certain categories).
    5. Your final recommendations for me.

    Format your response as JSON:
    {
        "total_transactions": <number>,
        "total_spent": <amount>,
        "highest_spending_category": "<category>",
        "trends": "<description>",
        "recommendations": [
            "<recommendation 1>",
            "<recommendation 2>",
            ...
        ]
    }

    Transaction Data:
    <Insert transaction data here>
"""

INSIGHT_PROMPT = """
    Analyze the following transaction data and generate actionable insights for the user. Your insights should include:
    1. Spending patterns (e.g., consistent, increasing, or decreasing).
    2. Unusual spending behavior (e.g., spikes in specific categories or large one-time expenses).
    3. Recommendations to optimize spending or save money.

    Format your response as JSON:
    {
        "spending_pattern": "<description>",
        "unusual_behavior": "<description>",
        "recommendations": [
            "<recommendation 1>",
            "<recommendation 2>"
        ]
    }

    Transaction Data:
    <Insert transaction data here>
"""

@functions_framework.http
def text_service(request):
    # Handle preflight requests for CORS
    if request.method == "OPTIONS":
        return cors_preflight_response()

    # Ensure the request method is POST
    if request.method != 'POST':
        return jsonify({'error': 'Only POST method is supported'}), 405

    # Parse request JSON
    request_json = request.get_json(silent=True)
    if not request_json or 'endpoint' not in request_json or 'input' not in request_json:
        return jsonify({'error': 'Missing required fields: endpoint or input'}), 400

    # Extract endpoint and input text
    endpoint = request_json['endpoint']
    input_text = request_json['input']

    # Validate input length
    if len(input_text) > 10000:  # Example max input length
        return jsonify({'error': 'Input text exceeds maximum allowed length (10,000 characters)'}), 400

    # Dispatch to the correct handler based on the endpoint
    if endpoint == 'summary':
        return handle_summary(input_text)
    elif endpoint == 'insight':
        return handle_insight(input_text)
    else:
        return jsonify({'error': 'Invalid endpoint specified'}), 400

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
            max_tokens=MAX_SUMMARY_TOKENS
        )
        result_text = response.choices[0].text.strip()
        return jsonify({'output': result_text}), 200
    except Exception as e:
        return jsonify({'error': f"Failed to generate summary: {str(e)}"}), 500

# Helper function for generating insights from text
def handle_insight(input_text):
    try:
        response = client.completions.create(
            model="gpt-4o-mini",
            prompt=f"{INSIGHT_PROMPT} {input_text}",
            max_tokens=MAX_INSIGHT_TOKENS
        )
        result_text = response.choices[0].text.strip()
        return jsonify({'output': result_text}), 200
    except Exception as e:
        return jsonify({'error': f"Failed to generate insights: {str(e)}"}), 500
