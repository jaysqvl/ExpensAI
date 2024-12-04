import functions_framework
from openai import OpenAI
import os
from flask import jsonify, request
import traceback

# Initialize OpenAI client
client = OpenAI(api_key=os.environ['OPENAI_API_KEY'])

MAX_SUMMARY_TOKENS = 250
MAX_INSIGHT_TOKENS = 250

# Modified prompt for summary endpoint
SUMMARY_PROMPT = """
You are a financial advisor analyzing transaction data. Create a clear, concise summary of the following transactions.
Focus on:
1. Total spending and number of transactions
2. Main spending categories and their totals
3. Notable patterns or trends
4. Actionable recommendations for saving money

Keep your response natural and conversational, but professional.
"""

INSIGHT_PROMPT = """
Analyze the following transaction data and generate actionable insights. Consider:
1. Spending patterns and trends
2. Unusual expenses or behaviors
3. Specific recommendations for optimization

Keep your response natural and conversational, but professional.
"""

@functions_framework.http
def text_service(request):
    logs = ["Text service received request"]
    
    # Handle preflight requests for CORS
    if request.method == "OPTIONS":
        logs.append("Handling OPTIONS request")
        headers = {
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'POST',
            'Access-Control-Allow-Headers': 'Content-Type,Authorization',
            'Access-Control-Max-Age': '3600'
        }
        return '', 204, headers

    # Ensure the request method is POST
    if request.method != 'POST':
        logs.append(f"Invalid method: {request.method}")
        return jsonify({'error': 'Only POST method is supported', 'logs': logs}), 405

    # Parse request JSON
    request_json = request.get_json(silent=True)
    logs.append(f"Received request JSON: {request_json.keys() if request_json else None}")
    
    if not request_json or 'endpoint' not in request_json or 'input' not in request_json:
        logs.append("Missing required fields")
        return jsonify({'error': 'Missing required fields: endpoint or input', 'logs': logs}), 400

    endpoint = request_json['endpoint']
    input_text = request_json['input']
    
    try:
        logs.append(f"Processing {endpoint} request")
        if endpoint == 'summary':
            response = client.chat.completions.create(
                model="gpt-4o-mini",
                messages=[
                    {
                        "role": "system", 
                        "content": SUMMARY_PROMPT
                    },
                    {
                        "role": "user", 
                        "content": input_text
                    }
                ],
                max_tokens=MAX_SUMMARY_TOKENS
            )
            result = response.choices[0].message.content.strip()
        elif endpoint == 'insight':
            response = client.chat.completions.create(
                model="gpt-4o-mini",
                messages=[
                    {
                        "role": "system", 
                        "content": INSIGHT_PROMPT
                    },
                    {
                        "role": "user", 
                        "content": input_text
                    }
                ],
                max_tokens=MAX_INSIGHT_TOKENS
            )
            result = response.choices[0].message.content.strip()
        else:
            logs.append(f"Invalid endpoint: {endpoint}")
            return jsonify({'error': 'Invalid endpoint specified', 'logs': logs}), 400

        logs.append("Successfully processed request")
        return jsonify({
            'output': result,
            'logs': logs
        }), 200

    except Exception as e:
        logs.append(f"Error: {str(e)}")
        logs.append(f"Stack trace: {traceback.format_exc()}")
        return jsonify({
            'error': str(e),
            'logs': logs
        }), 500
