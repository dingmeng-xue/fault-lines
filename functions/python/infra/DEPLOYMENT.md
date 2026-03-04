# Azure Function App Deployment Guide

This guide explains how to deploy the Python 3.10 Function App using Azure CLI.

## Prerequisites

- Azure CLI installed and authenticated (`az login`)
- An Azure subscription
- A resource group (or create one as shown below)

## Deployment Steps

### 1. Create a Resource Group (if needed)

```bash
az group create --name rg-myfunctions --location eastus
```

### 2. Deploy the Bicep Template

```bash
az deployment group create \
  --resource-group <your-resource-group-name> \
  --template-file functions/python/infra/function-app.bicep \
  --parameters resourceName=<your-resource-name>
```

**Example:**
```bash
az deployment group create \
  --resource-group rg-myfunctions \
  --template-file functions/python/infra/function-app.bicep \
  --parameters resourceName=myapp
```

### 3. Resources Created

The deployment will create the following resources:

- **Function App**: `func-<resourceName>`
- **App Service Plan**: `asp-<resourceName>` (Consumption plan, Linux)
- **Storage Account**: `st<resourceName><uniquestring>` (Standard_LRS)
- **Application Insights**: `appi-<resourceName>`

### 4. Deployment Outputs

After successful deployment, you'll receive:
- `functionAppName`: The name of the deployed Function App
- `functionAppId`: The resource ID of the Function App
- `functionAppHostName`: The hostname to access the Function App

## Configuration

The Function App is configured with:
- **Python Version**: 3.10
- **Runtime**: Python on Linux
- **Functions Extension Version**: ~4
- **Security**: HTTPS only, TLS 1.2 minimum, FTP disabled

## Example Usage

```bash
# Deploy to East US
az group create --name rg-myfunctions --location eastus

az deployment group create \
  --resource-group rg-myfunctions \
  --template-file functions/python/infra/function-app.bicep \
  --parameters resourceName=myapp

# Verify deployment
az functionapp show --name func-myapp --resource-group rg-myfunctions
```
