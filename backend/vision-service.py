import functions_framework
from openai import OpenAI
import os
import base64
from flask import jsonify, request
import traceback

# Initialize OpenAI client
client = OpenAI(api_key=os.environ['OPENAI_API_KEY'])

# Max image size for Base64 (5 MB)
MAX_IMAGE_SIZE = 5 * 1024 * 1024

PROMPT = """
You are an expert in processing receipt images. Analyze the image and extract the transaction details.

You must respond with ONLY a valid JSON object. Here are examples of valid responses:

Example 1 (Full details with multiple items):
{
    "merchant_name": "Walmart Superstore",
    "items": [
        {
            "name": "Milk 2%",
            "quantity": 1,
            "price_per_unit": 4.99,
            "total_price": 4.99
        },
        {
            "name": "Bread",
            "quantity": 2,
            "price_per_unit": 2.50,
            "total_price": 5.00
        },
        {
            "name": "Eggs Large",
            "quantity": 1,
            "price_per_unit": 3.99,
            "total_price": 3.99
        }
    ],
    "tax": 1.50,
    "total_amount": 15.48
}

Example 2 (Single item with tax):
{
    "merchant_name": "Starbucks",
    "items": [
        {
            "name": "Grande Latte",
            "quantity": 1,
            "price_per_unit": 4.50,
            "total_price": 4.50
        }
    ],
    "tax": 0.49,
    "total_amount": 4.99
}

Example 3 (Minimal details - only required fields):
{
    "merchant_name": "7-Eleven",
    "total_amount": 2.99
}

Example 4 (No items but has tax):
{
    "merchant_name": "Gas Station",
    "tax": 1.20,
    "total_amount": 25.99
}

Critical Requirements:
1. Response must be a single JSON object
2. No text before or after the JSON
3. Required fields:
   - merchant_name (string)
   - total_amount (number)
4. Optional fields:
   - items (array of items)
   - tax (number)
5. All numbers must be numeric values (not strings)
6. No currency symbols in any values
7. merchant_name must be the store name exactly as it appears on the receipt

Common mistakes to avoid:
- Don't add comments or explanations
- Don't use string values for numbers
- Don't include $ or other currency symbols
- Don't add extra fields
- Don't format numbers with commas (use 1000.00, not 1,000.00)

Parse the receipt image and respond with ONLY the JSON object following these exact requirements.
"""

@functions_framework.http
def vision_service(request):
    logs = ["Vision service received request"]
    
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
        return jsonify({
            'error': 'Only POST method is supported',
            'details': f'Received {request.method} request',
            'logs': logs
        }), 405

    # Parse the request JSON
    request_json = request.get_json(silent=True)
    logs.append(f"Received request JSON: {request_json.keys() if request_json else None}")
    
    if not request_json or 'image_data' not in request_json:
        logs.append("Missing image_data in request")
        return jsonify({
            'error': 'Missing required field: image_data',
            'details': 'Request must include image_data field with base64 encoded image',
            'logs': logs
        }), 400

    # Extract the Base64-encoded image data
    base64_image = request_json['image_data']
    logs.append(f"Received base64 image of length: {len(base64_image)}")

    # Validate Base64 input
    if not is_valid_base64(base64_image):
        logs.append("Invalid base64 data")
        return jsonify({
            'error': 'Invalid Base64-encoded image data',
            'details': 'The provided string is not valid base64 encoded data',
            'logs': logs
        }), 400

    # Ensure the image size is within limits
    if len(base64_image) > MAX_IMAGE_SIZE:
        image_size_mb = len(base64_image) / (1024 * 1024)
        logs.append(f"Image too large: {image_size_mb:.2f}MB")
        return jsonify({
            'error': 'Image size exceeds maximum limit',
            'details': f'Image size: {image_size_mb:.2f}MB (max: 5MB)',
            'logs': logs
        }), 400

    try:
        logs.append("Sending request to OpenAI")
        # Send the Base64 image data and the prompt to OpenAI's API
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {
                    "role": "user",
                    "content": [
                        {"type": "text", "text": PROMPT},
                        {
                            "type": "image_url",
                            "image_url": {
                                "url": f"data:image/jpeg;base64,{base64_image}"
                            },
                        }
                    ],
                }
            ],
            # For testing purposes this is a large window
            max_tokens=2000
        )
        result_text = response.choices[0].message.content

        # Validate and parse JSON response
        parsed_response = validate_and_parse_json(result_text)
        if "error" in parsed_response:
            logs.append(f"Error in response: {parsed_response['error']}")
            return jsonify({'output': parsed_response}), 400

        return jsonify({'output': result_text}), 200

    except Exception as e:
        logs.append(f"Error: {str(e)}")
        logs.append(f"Error type: {type(e).__name__}")
        logs.append(f"Stack trace: {traceback.format_exc()}")
        return jsonify({'output': {'error': str(e), 'logs': logs}}), 500

# Helper: Validate Base64 input
def is_valid_base64(base64_str):
    try:
        # Check if string is properly padded
        if len(base64_str) % 4:
            return False
            
        # Try to decode the string
        base64.b64decode(base64_str)
        return True
    except Exception:
        return False

# Helper: Validate and parse JSON output from AI
def validate_and_parse_json(response_text):
    import json
    try:
        parsed_json = json.loads(response_text)
        # Only check for required fields
        required_fields = ["merchant_name", "total_amount"]
        if all(field in parsed_json for field in required_fields):
            return parsed_json
        return {"error": f"Missing required fields. Expected: {required_fields}"}
    except json.JSONDecodeError:
        return {"error": "Failed to parse JSON from AI response"}
