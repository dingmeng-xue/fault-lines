# Azure Functions Python Sample - Survey Application

This sample demonstrates a Python Azure Functions application that displays a survey form and processes submissions. It also showcases the migration process from Python 3.10 to Python 3.13.

## Application Overview

This Azure Functions app provides two HTTP-triggered endpoints:

- **`/api/show_survey`**: Displays an interactive HTML survey form
- **`/api/check_response`**: Processes form submissions and displays results

The survey collects:
- Full Name (required)
- Gender (radio buttons)
- Hobbies (checkboxes)

## Prerequisites

- Azure CLI installed and authenticated (`az login`)
- An Azure subscription
- A resource group

## Quick Start

### Step 1: Deploy Azure Function App Infrastructure

Follow the deployment guide in [infra/DEPLOYMENT.md](infra/DEPLOYMENT.md) to create the Azure Function App infrastructure using Bicep.

```bash
# Create resource group
az group create --name rg-myfunctions --location eastus

# Deploy infrastructure
az deployment group create \
  --resource-group rg-myfunctions \
  --template-file functions/python/infra/function-app.bicep \
  --parameters resourceName=mysurveyapp
```

This creates a Function App named `func-mysurveyapp` running Python 3.10.

### Step 2: Deploy the Application Code

Deploy the function code from the [src/function_app.py](src/function_app.py) file to your Azure Function App:

```bash
# Navigate to the src directory
cd functions/python/src

# Deploy using Azure Functions Core Tools (https://learn.microsoft.com/azure/azure-functions/functions-run-local#install-the-azure-functions-core-tools)
func azure functionapp publish func-mysurveyapp

# OR using VS Code Azure Functions extension:
# Right-click on the Function App in Azure Extension and select "Deploy to Function App"
```

**Alternative: Manual deployment via CLI**
```bash
# Create a zip of the src directory
cd functions/python/src
zip -r ../function-app.zip .

# Deploy the zip
az functionapp deployment source config-zip \
  --resource-group rg-myfunctions \
  --name func-mysurveyapp \
  --src ../function-app.zip
```

### Step 3: Verify the Application

After deployment, test the application:

1. **Access the survey form:**
   ```
   https://func-mysurveyapp.azurewebsites.net/api/show_survey
   ```
   
   You should see a styled form with:
   - Full Name input field
   - Gender radio buttons (Male, Female, Don't want to share)
   - Hobbies checkboxes (Coding, Reading, Hiking, Gaming)
   - Submit Survey button

2. **Test form submission:**
   - Fill in the form with your information
   - Click **Submit Survey** button
   - Verify that you see a success page with:
     - Green heading: "Submission Received!"
     - Your submitted data displayed (Name, Gender, Hobbies)
     - A "Back to Form" button

3. **Test validation:**
   - Try submitting without entering a name
   - You should see a red error message: "Submission Failed"

## ⚠️ Migrating to Python 3.13

### 📋 Quick Summary

**Current State:** Code uses `cgi` and `distutils` modules → ❌ **Incompatible with Python 3.12+**

**Required Actions:**
1. ✏️ Update code to use `urllib.parse` instead of `cgi` and `packaging.version` instead of `distutils.version`
2. 🚀 Deploy updated code  
3. ⚙️ Upgrade runtime to Python 3.13
4. ✅ Verify application works

**Time Required:** ~15 minutes | **Risk Level:** Low (if steps followed in order)

---

### 🔴 Breaking Changes from Python 3.10 to 3.13

This section documents all breaking changes between Python 3.10 and Python 3.13 relevant to [src/function_app.py](src/function_app.py).

**Summary:**
- ❌ `distutils` module removed in **Python 3.12**
- ❌ `cgi` module removed in **Python 3.13**
- ✅ Both must be addressed to migrate to Python 3.13

#### Breaking Change #1: Removal of `cgi` Module

**Status:** ❌ **BREAKS THIS APPLICATION**

**What Changed:**
- The `cgi` module was deprecated in Python 3.11 ([PEP 594](https://peps.python.org/pep-0594/))
- The `cgi` module was completely removed in Python 3.13
- `cgi.FieldStorage` class is no longer available

**Code Impact in This Application:**
```python
# Lines 2-3 and 15-19 in src/function_app.py
import cgi          # ❌ Module doesn't exist in Python 3.13
import io

form = cgi.FieldStorage(
    fp=io.BytesIO(body_bytes),
    headers={'content-type': content_type},
    environ={'REQUEST_METHOD': 'POST'}
)
```

**Error You'll See:**
```
ModuleNotFoundError: No module named 'cgi'
```

**Why It Was Removed:**
- Known security vulnerabilities
- Outdated design incompatible with modern web standards
- Better alternatives available (`urllib.parse`, modern frameworks)
- Part of Python's "dead batteries" removal initiative

**Migration Required:** ✅ YES

---

#### Breaking Change #2: Removal of `distutils` Module

**Status:** ❌ **BREAKS THIS APPLICATION**

**What Changed:**
- The `distutils` module was deprecated in Python 3.10 ([PEP 632](https://peps.python.org/pep-0632/))
- The `distutils` module was completely removed in Python 3.12
- `distutils.version.LooseVersion` class is no longer available

**Code Impact in This Application:**
```python
# Lines 4 and 29-36 in src/function_app.py
from distutils.version import LooseVersion   # ❌ Module doesn't exist in Python 3.12+

# Verify version using distutils.version
try:
    current_version = LooseVersion(version)
    min_version = LooseVersion('1.0.0')
    version_valid = current_version >= min_version
    version_status = "✓ Valid" if version_valid else "✗ Invalid (< 1.0.0)"
except Exception as e:
    version_valid = False
    version_status = f"✗ Error: {str(e)}"
```

**Error You'll See:**
```
ModuleNotFoundError: No module named 'distutils'
```

**Why It Was Removed:**
- Replaced by `setuptools` and `packaging` libraries
- Outdated design from Python 2 era
- Not compatible with modern Python packaging standards
- Part of Python's standard library cleanup

**Migration Required:** ✅ YES

---

#### Other Python 3.13 Changes (No Impact on This Code)

These breaking changes do **NOT** affect this application:

**Removed Modules (PEP 594):**  
`aifc`, `audioop`, `chunk`, `imghdr`, `mailcap`, `nis`, `nntplib`, `ossaudiodev`, `pipes`, `sndhdr`, `sunau`, `telnetlib`, `uu`, `xdrlib`  
→ **Impact:** None - not used

**`typing` Module Changes:**  
Removed `typing.io` and `typing.re`  
→ **Impact:** None - minimal type hints used

**`locale.getdefaultlocale()` Removal:**  
Removed (deprecated since Python 3.11)  
→ **Impact:** None - not used

**`unittest` Method Removals:**  
Removed camelCase test methods  
→ **Impact:** None - no tests

---

### ⚠️ Important Warning

**🚨 DO NOT simply change the Python runtime version to 3.13 (or 3.12) without updating your code first!**

If you skip the code update, your function app will:
- Fail to start completely
- Return **503 Service Unavailable** on all endpoints
- Show `ModuleNotFoundError: No module named 'cgi'` or `ModuleNotFoundError: No module named 'distutils'` in logs

---

### Migration Steps

Follow these steps **in order** to successfully migrate to Python 3.13:

#### Step 1: Update function_app.py for Python 3.13 Compatibility

The `cgi` and `distutils` modules are no longer available in Python 3.12+. Update [src/function_app.py](src/function_app.py) to use `urllib.parse` and `packaging` instead.

**🔧 Required Code Changes:**

**Change 1: Update imports (lines 1-4)**

```python
# ❌ REMOVE these imports:
import cgi
import io
from distutils.version import LooseVersion

# ✅ ADD these imports:
from urllib.parse import parse_qs
from packaging.version import Version
```

**Note:** The `packaging` library is included in Azure Functions by default, so no additional installation is required.

**Change 2: Replace form parsing logic in `check_response` function (lines ~12-24)**

```python
# ❌ OLD CODE (Python 3.10 - incompatible with 3.13):
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
```

```python
# ✅ NEW CODE (Python 3.13 compatible):
body_bytes = req.get_body()
body_str = body_bytes.decode('utf-8')

# Parse form data using urllib.parse
form_data = parse_qs(body_str)

# parse_qs returns dict with lists as values
name = form_data.get('name', [''])[0].strip()
gender = form_data.get('gender', ['Not Specified'])[0]
hobbies = form_data.get('hobby', [])
hobbies_str = ", ".join(hobbies) if hobbies else "None selected"
```

**Change 3: Replace version verification logic in `check_response` function (lines ~27-36)**

```python
# ❌ OLD CODE (Python 3.10 - incompatible with 3.12+):
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
```

```python
# ✅ NEW CODE (Python 3.12+ compatible):
version = form_data.get('version', ['0.0.0'])[0].strip()

# Verify version using packaging.version
try:
    current_version = Version(version)
    min_version = Version('1.0.0')
    version_valid = current_version >= min_version
    version_status = "✓ Valid" if version_valid else "✗ Invalid (< 1.0.0)"
except Exception as e:
    version_valid = False
    version_status = f"✗ Error: {str(e)}"
```

**📝 Complete Updated `check_response` Function:**

```python
@app.route(route="check_response", auth_level=func.AuthLevel.ANONYMOUS)
def check_response(req: func.HttpRequest) -> func.HttpResponse:
    logging.info('Processing form submission.')

    try:
        body_bytes = req.get_body()
        body_str = body_bytes.decode('utf-8')
        
        # Parse form data using urllib.parse
        form_data = parse_qs(body_str)
        
        # parse_qs returns a dict with lists as values
        name = form_data.get('name', [''])[0].strip()
        gender = form_data.get('gender', ['Not Specified'])[0]
        hobbies = form_data.get('hobby', [])
        hobbies_str = ", ".join(hobbies) if hobbies else "None selected"
        version = form_data.get('version', ['0.0.0'])[0].strip()
        
        # Verify version using packaging.version
        try:
            current_version = Version(version)
            min_version = Version('1.0.0')
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

        # Return the response page
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
```

**🔍 Key Differences:**

| Aspect | `cgi.FieldStorage` | `urllib.parse.parse_qs` |
|--------|-------------------|-------------------------|
| Return Type | Object with methods | Dictionary |
| Single Value | `form.getvalue('key')` | `form_data.get('key', [''])[0]` |
| Multiple Values | `form.getlist('key')` | `form_data.get('key', [])` (already list)  |
| Input Format | Binary with headers | URL-encoded string |
| Decoding | Automatic | Manual: `body_bytes.decode('utf-8')` |
| Lists | Built-in support | All values are lists by default |

| Aspect | `distutils.version.LooseVersion` | `packaging.version.Version` |
|--------|----------------------------------|----------------------------|
| Module | `distutils.version` (removed) | `packaging.version` |
| Class | `LooseVersion` | `Version` |
| Parsing | Lenient, accepts any string | Strict, follows PEP 440 |
| Comparison | Basic | Full semantic versioning support |

#### Step 2: Deploy the Updated Code and Verify

Deploy the modified code to your Function App:

```bash
cd functions/python/src
func azure functionapp publish func-mysurveyapp
```

**⚠️ Important:** At this point, your app still runs on Python 3.10 but with Python 3.12+ compatible code (works on 3.12 and 3.13).

**Verify deployment before upgrading runtime:**

```bash
# Test the survey form endpoint
curl https://func-mysurveyapp.azurewebsites.net/api/show_survey

# Test form submission
curl -X POST https://func-mysurveyapp.azurewebsites.net/api/check_response \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=TestUser&gender=male&hobby=coding&hobby=reading"
```

**Expected Results:**
- ✅ Survey form loads successfully (HTML returned)
- ✅ Form submission returns success page with submitted data
- ✅ No errors in function logs

**If you see errors:**
1. Check logs: `az functionapp log tail --name func-mysurveyapp --resource-group rg-myfunctions`
2. Verify code changes were applied correctly
3. **Do NOT proceed to Step 3** until code works on Python 3.10

#### Step 3: Update Python Runtime Version to 3.12 or 3.13

Now that your code is compatible, update the Function App's Python version to 3.12 or 3.13:

**For Python 3.13:**
```bash
az functionapp config set \
  --name func-mysurveyapp \
  --resource-group rg-myfunctions \
  --python-version "3.13"
```

**For Python 3.12:**
```bash
az functionapp config set \
  --name func-mysurveyapp \
  --resource-group rg-myfunctions \
  --python-version "3.12"
```

The Function App will restart automatically with the new Python version.

#### Step 4: Verify the Migration

Test the application again to ensure everything works:

1. Visit the survey form:
   ```
   https://func-mysurveyapp.azurewebsites.net/api/show_survey
   ```

2. Fill out and submit the form

3. Verify that submission processing works correctly

4. Check the Function App logs for any errors:
   ```bash
   az functionapp log tail \
     --name func-mysurveyapp \
     --resource-group rg-myfunctions
   ```

5. Verify the Python version in Azure Portal:
   - Navigate to your Function App
   - Go to Configuration → General Settings
   - Confirm Python Version shows "3.13" or "3.12"

### What Happens If You Skip Step 1?

❌ **If you update to Python 3.12 or 3.13 without modifying the code:**

**Immediate Failures:**
- ❌ Function app fails to start
- ❌ All HTTP endpoints return **503 Service Unavailable**
- ❌ Application Insights shows initialization failures

**Errors in Logs:**
```
Result: Failure
Exception: ModuleNotFoundError: No module named 'cgi'
Stack:
  File "/home/site/wwwroot/function_app.py", line 2, in <module>
    import cgi
ModuleNotFoundError: No module named 'cgi'
```

Or if you're using Python 3.12+:
```
Result: Failure
Exception: ModuleNotFoundError: No module named 'distutils'
Stack:
  File "/home/site/wwwroot/function_app.py", line 4, in <module>
    from distutils.version import LooseVersion
ModuleNotFoundError: No module named 'distutils'
```

**How to Check Logs:**
```bash
# Stream live logs
az functionapp log tail \
  --name func-mysurveyapp \
  --resource-group rg-myfunctions

# Or in Azure Portal:
# Function App → Monitoring → Log stream
```

**Recovery Steps:**
1. Roll back to Python 3.10:
   ```bash
   az functionapp config set \
     --name func-mysurveyapp \
     --resource-group rg-myfunctions \
     --python-version "3.10"
   ```
2. Update your code following Step 1
3. Deploy updated code (Step 2)
4. Then upgrade to Python 3.12 or 3.13 again (Step 3)

---

### Why This Migration is Necessary

**Reason for `cgi` Module Removal:**

The Python Software Foundation removed the `cgi` module as part of [PEP 594: "Removing dead batteries from the standard library"](https://peps.python.org/pep-0594/).

**Security Issues:**
- `cgi.FieldStorage` had known security vulnerabilities
- Complex parsing logic prone to edge-case failures
- Not maintained with modern security standards

**Better Alternatives:**
- `urllib.parse` - Built-in, actively maintained, more secure
- Modern web frameworks (FastAPI, Django, Flask) - Handle parsing automatically
- Dedicated libraries for complex scenarios

**Reason for `distutils` Module Removal:**

The Python Software Foundation removed the `distutils` module as part of [PEP 632: "Deprecate distutils module"](https://peps.python.org/pep-0632/).

**Why Removed:**
- Legacy packaging infrastructure from Python 2 era
- Replaced by `setuptools` and modern packaging tools
- Not compatible with PEP 517/518 build standards
- Maintenance burden on core Python developers

**Better Alternatives:**
- `packaging.version` - Modern, PEP 440 compliant version parsing
- `setuptools` - For package building and distribution
- `build` - Modern Python package builder

---

### Benefits of Upgrading to Python 3.13

Beyond compatibility, Python 3.13 offers significant improvements:

**Performance:**
- ✨ ~15% faster than Python 3.10 on average
- ✨ New experimental JIT compiler for additional speed gains
- ✨ Improved memory efficiency

**Developer Experience:**
- 📝 Better error messages with more context
- 🐛 Enhanced debugging capabilities
- 🔧 Improved REPL (interactive shell)

**Security:**
- 🔒 Latest security patches and fixes
- 🔒 Removal of legacy code with known vulnerabilities
- 🔒 Modern cryptographic defaults

**New Features:**
- 🆕 Improved type hints and typing system
- 🆕 Better async/await support
- 🆕 Enhanced standard library modules

**Support Lifecycle:**
- ⏰ **Python 3.10 end-of-life:** October 2026 (9 months away)
- ⏰ **Python 3.13 supported until:** October 2029
- ⚠️ Critical to stay on supported versions for security updates

---

### Migration Checklist

Track your migration progress:

- [ ] **Backup**: Commit current code to git
- [ ] **Code Update**: Modify `function_app.py`
- [ ] Update imports & remove cgi/io/distutils
  - [ ] Remove `import cgi`, `import io`, and `from distutils.version import LooseVersion`
  - [ ] Add `from urllib.parse import parse_qs` and `from packaging.version import Version`
  - [ ] Update `check_response` function form parsing logic
  - [ ] Update `check_response` function version verification logic
- [ ] **Local Testing** (optional): Test locally with Python 3.13
- [ ] **Deploy Code**: Push to Azure Function App
- [ ] **Verify on 3.10**: Test all endpoints work correctly
- [ ] **Upgrade Runtime**: Change Python version to 3.13
- [ ] **Verify on 3.13**: Test all endpoints still work
- [ ] **Monitor Logs**: Check for any runtime errors
- [ ] **Document**: Update any environment-specific notes

---

### Troubleshooting

**Problem: Form data not parsing correctly after update**

**Symptoms:**
- `name`, `gender`, or `hobbies` showing as empty
- Server error 500

**Solution:**
```python
# Make sure you're accessing parse_qs results correctly
form_data = parse_qs(body_str)

# ✅ CORRECT: parse_qs returns dict with lists as values
name = form_data.get('name', [''])[0]

# ❌ WRONG: Treating as simple dict
name = form_data.get('name', '')  # Returns list ['John'], not 'John'
```

---

**Problem: Unicode/encoding errors**

**Symptoms:**
- `UnicodeDecodeError` when processing form data
- Special characters not displaying correctly

**Solution:**
```python
# Ensure proper UTF-8 decoding
body_str = body_bytes.decode('utf-8')

# For robust handling of malformed input:
body_str = body_bytes.decode('utf-8', errors='replace')
```

---

**Problem: Version parsing errors**

**Symptoms:**
- `InvalidVersion` or version comparison errors
- Version validation not working correctly

**Root Cause:**
The `packaging.version.Version` class is stricter than `distutils.version.LooseVersion` and requires PEP 440 compliant version strings.

**Solution:**
```python
from packaging.version import Version, InvalidVersion

# ✅ CORRECT: Handle invalid version strings gracefully
try:
    current_version = Version(version)
    min_version = Version('1.0.0')
    version_valid = current_version >= min_version
    version_status = "✓ Valid" if version_valid else "✗ Invalid (< 1.0.0)"
except InvalidVersion as e:
    version_valid = False
    version_status = f"✗ Invalid version format: {str(e)}"
```

**Valid PEP 440 versions:** `1.0.0`, `2.1.3`, `1.0.0a1`, `1.0.0rc1`, `1.0.0.post1`  
**Invalid versions:** `v1.0.0` (prefix), `1.0` (missing patch), `latest` (non-numeric)

---

**Problem: Deployment succeeds but function returns 500 error**

**Debug Steps:**

1. **Check real-time logs:**
   ```bash
   az functionapp log tail --name func-mysurveyapp --resource-group rg-myfunctions
   ```

2. **Check Application Insights:**
   - Azure Portal → Function App → Application Insights → Failures
   - Look for exceptions and stack traces

3. **Enable detailed logging:**
   ```python
   # Add at top of function_app.py
   logging.basicConfig(level=logging.DEBUG)
   ```

4. **Test locally:**
   ```bash
   cd functions/python/src
   func start
   # Then test: curl http://localhost:7071/api/check_response ...
   ```

---

### Testing After Migration

**Comprehensive Test Cases:**

1. **Test form display:**
   ```bash
   curl https://func-mysurveyapp.azurewebsites.net/api/show_survey
   # Should return HTML form
   ```

2. **Test valid submission:**
   ```bash
   curl -X POST https://func-mysurveyapp.azurewebsites.net/api/check_response \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "name=John+Doe&gender=male&hobby=coding&hobby=reading"
   # Should return success page with "John Doe"
   ```

3. **Test empty name (validation):**
   ```bash
   curl -X POST https://func-mysurveyapp.azurewebsites.net/api/check_response \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "gender=female&hobby=hiking"
   # Should return error page: "Submission Failed"
   ```

4. **Test special characters (UTF-8):**
   ```bash
   curl -X POST https://func-mysurveyapp.azurewebsites.net/api/check_response \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "name=José+García&gender=male&hobby=coding"
   # Should handle accented characters correctly
   ```

5. **Test version validation:**
   ```bash
   curl -X POST https://func-mysurveyapp.azurewebsites.net/api/check_response \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "name=Test+User&gender=male&hobby=coding&version=1.0.1"
   # Should show "Version: 1.0.1 (✓ Valid)"
   ```

6. **Verify Python version:**
   ```bash
   az functionapp config show \
     --name func-mysurveyapp \
     --resource-group rg-myfunctions \
     --query "pythonVersion" -o tsv
   # Should output: 3.13 or 3.12
   ```

---

## Additional Resources

- [Azure Functions Python Developer Guide](https://learn.microsoft.com/azure/azure-functions/functions-reference-python)
- [Python 3.13 What's New](https://docs.python.org/3/whatsnew/3.13.html)
- [Python 3.12 What's New](https://docs.python.org/3/whatsnew/3.12.html)
- [PEP 594 - Removing Dead Batteries (cgi removal)](https://peps.python.org/pep-0594/)
- [PEP 632 - Deprecate distutils module](https://peps.python.org/pep-0632/)
- [packaging library - Modern Python version parsing](https://packaging.pypa.io/)
