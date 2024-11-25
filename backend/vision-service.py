import functions_framework
from openai import OpenAI
import os
import base64
from flask import jsonify, request

# Initialize OpenAI client
client = OpenAI(api_key=os.environ['OPENAI_API_KEY'])

# Max image size for Base64 (5 MB)
MAX_IMAGE_SIZE = 5 * 1024 * 1024

PROMPT = """
    You are an expert in processing image data for extracting transactional information. The input you receive will be one of three specific types of images:

    1. Product Price Labels:
       - These are physical labels found on products, such as barcode stickers or printed price tags.
       - Extract:
         - Product name or description (if available).
         - Price (normal price and discounted price, if applicable).

    2. Receipts:
       - These are printed or digital receipts from purchases.
       - Extract:
         - A list of items with:
           - Name
           - Quantity
           - Price per unit
           - Total price
         - Tax
         - Discounts
         - Total amount.

    3. Screenshots of Purchases:
       - These are digital screenshots, such as transaction notifications or order confirmations on mobile devices.
       - Extract:
         - Merchant name
         - Transaction amount
         - Date and time
         - Transaction type (e.g., debit, credit, refund).

    Guidelines:
    - For images that do not belong to one of these three categories, return:
      {"error": "Unsupported image type"}

    - If you find a valid image type but cannot extract any useful information (e.g., no items or prices on a receipt), return:
      {"error": "No labels or prices found in the image", "items": []}

    - Always return the result as a JSON object. Do not include any other text or commentary.

    Your task is to analyze the provided image and extract the relevant information into JSON format based on these rules.
"""

@functions_framework.http
def vision_service(request):
    # Handle preflight requests for CORS
    if request.method == "OPTIONS":
        headers = {
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'POST',
            'Access-Control-Allow-Headers': 'Content-Type,Authorization',
            'Access-Control-Max-Age': '3600'
        }
        return '', 204, headers

    # Ensure the request method is POST
    if request.method != 'POST':
        return jsonify({'error': 'Only POST method is supported'}), 405

    # Parse the request JSON
    request_json = request.get_json(silent=True)
    if not request_json or 'image_data' not in request_json:
        return jsonify({'error': 'Missing required field: image_data'}), 400

    # Extract the Base64-encoded image data
    base64_image = request_json['image_data']

    # Validate Base64 input
    if not is_valid_base64(base64_image):
        return jsonify({'error': 'Invalid Base64-encoded image data'}), 400

    # Ensure the image size is within limits
    if len(base64_image) > MAX_IMAGE_SIZE:
        return jsonify({'error': 'Image size exceeds maximum limit of 5 MB'}), 400

    try:
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
            max_tokens=1000
        )
        result_text = response.choices[0].message.content

        # Validate and parse JSON response
        parsed_response = validate_and_parse_json(result_text)
        if "error" in parsed_response:
            return jsonify(parsed_response), 400

        return jsonify(parsed_response), 200

    except Exception as e:
        return jsonify({'error': str(e)}), 500

# Helper: Validate Base64 input
def is_valid_base64(base64_string):
    try:
        base64.b64decode(base64_string)
        return True
    except Exception:
        return False

# Helper: Validate and parse JSON output from AI
def validate_and_parse_json(response_text):
    import json
    try:
        parsed_json = json.loads(response_text)
        # Ensure JSON has the expected structure
        if "items" in parsed_json or "error" in parsed_json:
            return parsed_json
        return {"error": "Unexpected JSON format"}
    except json.JSONDecodeError:
        return {"error": "Failed to parse JSON from AI response"}
