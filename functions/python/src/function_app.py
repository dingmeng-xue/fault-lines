import logging
import cgi
import io
from distutils.version import LooseVersion
import azure.functions as func

app = func.FunctionApp()

@app.route(route="check_response", auth_level=func.AuthLevel.ANONYMOUS)
def check_response(req: func.HttpRequest) -> func.HttpResponse:
    logging.info('Processing form submission.')

    try:
        body_bytes = req.get_body()
        content_type = req.headers.get('Content-Type')

        form = cgi.FieldStorage(
            fp=io.BytesIO(body_bytes),
            headers={'content-type': content_type},
            environ={'REQUEST_METHOD': 'POST'}
        )

        name = form.getvalue('name', '').strip()
        gender = form.getvalue('gender', 'Not Specified')
        hobbies = form.getlist('hobby') 
        hobbies_str = ", ".join(hobbies) if hobbies else "None selected"
        version = form.getvalue('version', '0.0.0').strip()
        
        # Verify version using distutils.version
        try:
            current_version = LooseVersion(version)
            min_version = LooseVersion('1.0.0')
            version_valid = current_version >= min_version
            version_status = "✓ Valid" if version_valid else "✗ Invalid (< 1.0.0)"
        except Exception as e:
            version_valid = False
            version_status = f"✗ Error: {str(e)}"

        if not name:
            status_title = "Submission Failed"
            status_color = "red"
            message = "The 'Name' field is required. Please go back and fill it out."
        else:
            status_title = "Submission Received!"
            status_color = "green"
            message = f"Thank you, {name}. Your data has been recorded."

        # 3. Return the response page
        response_html = f"""
        <html>
        <body style="font-family: sans-serif; padding: 20px;">
            <h1 style="color: {status_color};">{status_title}</h1>
            <p>{message}</p>
            <hr>
            <p><strong>Name:</strong> {name if name else "<i>Missing</i>"}</p>
            <p><strong>Gender:</strong> {gender}</p>
            <p><strong>Hobbies:</strong> {hobbies_str}</p>
            <p><strong>Version:</strong> {version} ({version_status})</p>
            <br>
            <button onclick="history.back()">Back to Form</button>
        </body>
        </html>
        """

        return func.HttpResponse(response_html, mimetype="text/html")

    except Exception as e:
        logging.exception("Error parsing form data.")
        return func.HttpResponse(f"Error: {str(e)}", status_code=500)

@app.route(route="show_survey", auth_level=func.AuthLevel.ANONYMOUS)
def show_survey(req: func.HttpRequest) -> func.HttpResponse:
    logging.info('Python HTTP trigger function processed a request.')

    try:
        # Define the HTML form with CSS for better layout
        html_content = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Survey Form</title>
            <style>
                body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; line-height: 1.6; padding: 20px; max-width: 500px; margin: auto; }
                .form-group { margin-bottom: 15px; }
                label { font-weight: bold; display: block; margin-bottom: 5px; }
                input[type="text"] { width: 100%; padding: 8px; box-sizing: border-box; }
                .radio-group, .checkbox-group { display: flex; flex-direction: column; gap: 5px; }
                .inline-label { font-weight: normal; display: inline; margin-left: 5px; }
                button { background-color: #0078d4; color: white; border: none; padding: 10px 15px; cursor: pointer; border-radius: 4px; }
                button:hover { background-color: #005a9e; }
            </style>
        </head>
        <body>
            <h1>User Survey</h1>
            <form action="/api/check_response" method="POST">
                
                <div class="form-group">
                    <label for="name">Full Name:</label>
                    <input type="text" id="name" name="name" placeholder="Enter your name" required>
                </div>

                <div class="form-group">
                    <label>Gender:</label>
                    <div class="radio-group">
                        <div><input type="radio" id="male" name="gender" value="male"><label class="inline-label" for="male">Male</label></div>
                        <div><input type="radio" id="female" name="gender" value="female"><label class="inline-label" for="female">Female</label></div>
                        <div><input type="radio" id="private" name="gender" value="not_shared" checked><label class="inline-label" for="private">Don't want to share</label></div>
                    </div>
                </div>

                <div class="form-group">
                    <label>Hobbies (Select all that apply):</label>
                    <div class="checkbox-group">
                        <div><input type="checkbox" id="coding" name="hobby" value="coding"><label class="inline-label" for="coding">Coding</label></div>
                        <div><input type="checkbox" id="reading" name="hobby" value="reading"><label class="inline-label" for="reading">Reading</label></div>
                        <div><input type="checkbox" id="hiking" name="hobby" value="hiking"><label class="inline-label" for="hiking">Hiking</label></div>
                        <div><input type="checkbox" id="gaming" name="hobby" value="gaming"><label class="inline-label" for="gaming">Gaming</label></div>
                    </div>
                </div>

                <input type="hidden" name="version" value="1.0.1">

                <button type="submit">Submit Survey</button>
            </form>
        </body>
        </html>
        """

        return func.HttpResponse(
            html_content,
            status_code=200,
            mimetype="text/html"
        )

    except Exception as e:
        logging.exception("Runtime error rendering the form.")
        return func.HttpResponse("Internal Server Error", status_code=500)