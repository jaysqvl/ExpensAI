import functions_framework
from openai import OpenAI
import os
from flask import jsonify, request

# Initialize OpenAI client
client = OpenAI(api_key=os.environ['OPENAI_API_KEY'])

# TODO: Give it an example of what the json object/structure should look like
PROMPT = "Look at this receipt, itemize all the items into a JSON object and return them to me. No other text and in this format ..."

# We should also probably determine a max token limit and play around with it to see how much it can handle in terms of receipt size

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
        return 'Only POST method is supported', 405

    # Parse the request JSON
    request_json = request.get_json(silent=True)
    if not request_json or 'image_data' not in request_json:
        response = jsonify({'error': 'No image data provided'})
        response.headers.add('Access-Control-Allow-Origin', '*')
        return response, 400

    # Extract the Base64-encoded image data
    base64_image = request_json['image_data']

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
            max_tokens=500
        )
        result_text = response.choices[0].message.content
        response_data = jsonify({'output': result_text})

    except Exception as e:
        response_data = jsonify({'error': str(e)})
        return response_data, 500

    # Add CORS headers to the response
    response_data.headers.add('Access-Control-Allow-Origin', '*')
    return response_data, 200
