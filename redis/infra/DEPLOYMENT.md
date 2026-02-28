# Azure Managed Redis (Redis Enterprise) - Bicep Deployment Guide

## Overview

This Bicep template provisions an Azure Managed Redis (Redis Enterprise) cluster with the following features:

- **Latest Redis Version** - Automatically managed by the service
- **High Availability** - Data replication enabled by default
- **Availability Zones** - Support for zone-redundant deployments
- **Customer-Managed Encryption** - Optional Key Vault integration
- **Flexible SKU Options** - Balanced, Memory Optimized, Compute Optimized, Flash Optimized, and Enterprise tiers
- **TLS Security** - TLS 1.2 minimum, always encrypted

## Files

- `azure-managed-redis.bicep` - Main Bicep template
- `azure-managed-redis.parameters.json` - Sample parameters file

## Quick Start

### 1. Deploy with Azure CLI

```bash
# Create resource group
az group create --name rg-redis --location westus2

# Deploy the template
az deployment group create \
  --resource-group rg-redis \
  --template-file azure-managed-redis.bicep \
  --parameters azure-managed-redis.parameters.json
```

### 2. Deploy with PowerShell

```powershell
# Create resource group
New-AzResourceGroup -Name rg-redis -Location westus2

# Deploy the template
New-AzResourceGroupDeployment `
  -ResourceGroupName rg-redis `
  -TemplateFile azure-managed-redis.bicep `
  -TemplateParameterFile azure-managed-redis.parameters.json
```

### 3. Inline Parameters

```bash
az deployment group create \
  --resource-group rg-redis \
  --template-file azure-managed-redis.bicep \
  --parameters clusterName=my-redis-cluster
```

## Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `clusterName` | string | **Required** | Name of the Redis Enterprise cluster (1-60 chars, alphanumeric with hyphens) |
| `location` | string | Resource Group location | Azure region to deploy to |
| `skuName` | string | `Balanced_B1` | SKU name (capacity is embedded in the name, e.g., B1, B5, M10): `Balanced_B0`-`B20`, `MemoryOptimized_M10`-`M100`, `ComputeOptimized_X3`-`X20` |
| `tags` | object | `{}` | Tags to apply to the resource |

**Note**: Advanced settings (zones, TLS version, encryption, high availability) use secure defaults: TLS 1.2, public access enabled, high availability enabled.

## Outputs

| Output | Description |
|--------|-------------|
| `hostname` | Full hostname of the Redis Enterprise cluster |
| `resourceId` | Resource ID of the Redis Enterprise cluster |
| `provisioningState` | Current provisioning state (Succeeded, Creating, Updating, etc.) |
| `resourceState` | Current resource state (Running, Creating, Updating, etc.) |
| `redundancyMode` | Redundancy mode (None, LR=Local Redundancy, ZR=Zone Redundant) |

## Connection Configuration

### Getting Connection Information

After deployment, retrieve the hostname from outputs:

```bash
# Get the hostname
az deployment group show \
  --resource-group rg-redis \
  --name <deployment-name> \
  --query properties.outputs.hostname.value -o tsv
```

### For .NET Applications

Azure Managed Redis (Redis Enterprise) requires database-specific connections. Each cluster can have multiple databases:

```csharp
// Connection string format for Redis Enterprise
var connectionString = $"{hostname}:10000,ssl=True,abortConnect=False";
var connection = ConnectionMultiplexer.Connect(connectionString);
```

**Note**: Access key authentication requires creating a database within the cluster. Use Azure CLI or Portal to create databases after cluster deployment.

## SKU Selection Guide

The SKU name includes the capacity (e.g., `Balanced_B5` is Balanced tier with capacity 5). Choose based on your workload needs:

| SKU Family | Best For | Use Cases | Example SKUs |\n|------------|----------|-----------|-------------|\n| **Balanced** | General purpose | Most applications, mixed workloads | `Balanced_B0`, `B1`, `B5`, `B10` |\n| **Memory Optimized** | Memory-intensive workloads | Large data sets, caching large objects | `MemoryOptimized_M10`, `M20`, `M50` |\n| **Compute Optimized** | High throughput | Many concurrent connections, compute-heavy ops | `ComputeOptimized_X3`, `X5`, `X10` |

## High Availability

- **High Availability** is enabled by default
- Replicates data for redundancy and protection against data loss
- Provides local redundancy within a region
- For advanced zone redundancy options, modify the Bicep template to add the `zones` property

## Security Best Practices

1. **TLS 1.2 Enabled**: Enforced by default in this template
2. **Disable Public Access**: Modify the Bicep template to set `publicNetworkAccess='Disabled'` and use Private Endpoints
3. **Apply Network Security Groups**: Restrict traffic to your application subnets
4. **Use Managed Identities**: For database authentication after cluster creation
5. **Customer-Managed Encryption**: For advanced scenarios, extend the template with Key Vault integration

## Migration from Azure Cache for Redis

If migrating from the old service, see [../README.md](../README.md) for detailed migration steps.

### Key Differences

| Aspect | Azure Cache for Redis | Azure Managed Redis (Redis Enterprise) |
|--------|----------------------|-----------------------------------------|
| Resource Type | `Microsoft.Cache/Redis` | `Microsoft.Cache/redisEnterprise` |
| SKU Structure | `name` + `family` + `capacity` | `name` (e.g., `Balanced_B1`) + `capacity` |
| Availability Zones | `zonalAllocationPolicy` property | `zones` array property |
| Authentication | Access keys + optional Entra ID | Database-level access keys |
| High Availability | Optional zone redundancy | `highAvailability` property for replication |

## Troubleshooting

### Connection Issues

1. **Database Required**: Redis Enterprise clusters require creating a database before connecting (not included in this template)
2. Verify firewall rules allow traffic to the Redis Enterprise cluster
3. Check that TLS is enabled in your client library
4. Ensure your client library supports Redis Enterprise
5. Verify the cluster provisioning state is `Succeeded` and resource state is `Running`

### Deployment Issues

1. Ensure the cluster name follows the pattern: 1-60 characters, alphanumeric with hyphens (no leading/trailing/consecutive hyphens)
2. Verify the selected region supports Azure Managed Redis (Redis Enterprise)
3. Choose appropriate SKU name - capacity is embedded in the SKU name (e.g., `Balanced_B5` not `Balanced_B` with capacity 5)
4. Verify your subscription has sufficient quota for the selected SKU

## Additional Resources

- [Azure Cache for Redis Enterprise Documentation](https://learn.microsoft.com/azure/azure-cache-for-redis/cache-overview-enterprise)
- [Redis Enterprise REST API Reference](https://learn.microsoft.com/rest/api/redis/redisenterprisecache/redis-enterprise/create?view=rest-redis-redisenterprisecache-2025-07-01)
- [Redis Enterprise Pricing](https://azure.microsoft.com/pricing/details/cache/)
- [Migration Guide](https://learn.microsoft.com/azure/redis/migrate/migrate-overview)
