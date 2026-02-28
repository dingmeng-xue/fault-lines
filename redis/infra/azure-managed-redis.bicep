// Azure Managed Redis (Redis Enterprise) Bicep Template
// Based on REST API: Microsoft.Cache/redisEnterprise@2025-07-01
// Simplified with minimal parameters for ease of use

@description('Name of the Azure Managed Redis (Redis Enterprise) cluster')
param clusterName string

@description('Location for the Azure Managed Redis cluster')
param location string = resourceGroup().location

@description('SKU name for Azure Managed Redis')
@allowed([
  'Balanced_B0'
  'Balanced_B1'
  'Balanced_B3'
  'Balanced_B5'
  'Balanced_B10'
  'Balanced_B20'
  'MemoryOptimized_M10'
  'MemoryOptimized_M20'
  'MemoryOptimized_M50'
  'MemoryOptimized_M100'
  'ComputeOptimized_X3'
  'ComputeOptimized_X5'
  'ComputeOptimized_X10'
  'ComputeOptimized_X20'
])
param skuName string = 'Balanced_B1'

@description('Tags to apply to the resource')
param tags object = {}

// Azure Managed Redis (Redis Enterprise) cluster resource
resource redisEnterpriseCluster 'Microsoft.Cache/redisEnterprise@2025-07-01' = {
  name: clusterName
  location: location
  tags: tags
  sku: {
    name: skuName
  }
  properties: {
    minimumTlsVersion: '1.2'
    publicNetworkAccess: 'Enabled'
    highAvailability: 'Enabled'
  }
}

// Outputs for application configuration
@description('Hostname of the Azure Managed Redis cluster')
output hostname string = redisEnterpriseCluster.properties.hostName

@description('Resource ID of the Redis Enterprise cluster')
output resourceId string = redisEnterpriseCluster.id

@description('Provisioning state of the cluster')
output provisioningState string = redisEnterpriseCluster.properties.provisioningState

@description('Resource state of the cluster')
output resourceState string = redisEnterpriseCluster.properties.resourceState

@description('Redundancy mode of the cluster')
output redundancyMode string = redisEnterpriseCluster.properties.redundancyMode
