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

###  Breaking Changes from Python 3.10 to 3.13

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


**Migration Required:** ✅ YES

---

#### Breaking Change #2: Removal of `distutils` Module

**Status:** ❌ **BREAKS THIS APPLICATION**

**What Changed:**
- The `distutils` module was deprecated in Python 3.10 ([PEP 632](https://peps.python.org/pep-0632/))
- The `distutils` module was completely removed in Python 3.12
- `distutils.version.LooseVersion` class is no longer available


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

## Additional Resources

- [Azure Functions Python Developer Guide](https://learn.microsoft.com/azure/azure-functions/functions-reference-python)
- [Python 3.13 What's New](https://docs.python.org/3/whatsnew/3.13.html)
- [Python 3.12 What's New](https://docs.python.org/3/whatsnew/3.12.html)
- [PEP 594 - Removing Dead Batteries (cgi removal)](https://peps.python.org/pep-0594/)
- [PEP 632 - Deprecate distutils module](https://peps.python.org/pep-0632/)
- [packaging library - Modern Python version parsing](https://packaging.pypa.io/)
