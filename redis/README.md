# Azure Cache for Redis to Azure Managed Redis Migration

## Overview

Azure Managed Redis is a native Azure first-party service with advanced Redis Enterprise features, continuous regional expansion, simplified billing, and better integration with Azure services.

## Migration Steps (All Tiers)

### 1. **Pre-Migration Assessment**
   - Check current cache size and configuration in Azure Portal
   - For Enterprise: Note scale-out factor (e.g., "2 x 4GB")
   - Verify [regional availability](https://azure.microsoft.com/explore/global-infrastructure/products-by-region/)
   - Test Redis 7.4 compatibility with critical commands/features
   - Assess network requirements (VNET, private endpoints, firewall rules)
   - Verify required features are supported in Managed Redis
   - Identify maintenance window or plan dual-write for zero downtime

### 2. **Choose Performance Tier**
   - **Memory Optimized** - Memory-intensive workloads
   - **Balanced** - Mix of memory and performance
   - **Compute Optimized** - Compute-intensive workloads

### 3. **Provision Azure Managed Redis**
   - Create new instance with appropriate size and tier
   - Enable high availability (zone redundant by default)
   - Configure networking and security settings

### 4. **Migrate Data** (Choose One)

   **A. Dual-Write Strategy** ✅ *Recommended for production*
   - Write to both old and new caches simultaneously
   - Switch reads to new cache after sync
   - Zero downtime, no data loss
   - ⚠️ Requires running two caches temporarily

   **B. Export/Import via RDB File**
   - Export from source cache to Azure Storage
   - Import RDB file to new Managed Redis
   - ⚠️ Data written during export may be lost

   **C. Programmatic Migration**
   - Use tools: redis-copy, RIOT, or RIOT-X
   - Full control over migration process

   **D. Create New & Repopulate**
   - Simplest approach
   - ⚠️ Requires downtime or cache warming

### 5. **Update Application Configuration**

   | Setting | Old Value | New Value |
   |---------|-----------|-----------|
   | **Hostname** (Enterprise) | `.redisenterprise.cache.azure.net` | `.redis.azure.net` |
   | **Hostname** (Basic/Standard/Premium) | `.redis.cache.azure.net` | `<region>.redis.azure.net` |
   | **TLS Port** | 6380 | 10000 |
   | **Non-TLS Port** | 6379 | ❌ Not supported |
   | **Redis Version** | 4.x/6.x | 7.4 |
   | **Authentication** | Access keys | Access keys or Microsoft Entra ID (recommended) |

### 6. **Verify & Clean Up**
   - Test application thoroughly with new cache
   - Monitor performance and connectivity
   - Delete old cache instance once verified

## Support Resources
- [Azure Managed Redis Documentation](https://learn.microsoft.com/azure/redis/)
- [Migration Guide](https://learn.microsoft.com/azure/redis/migrate/migrate-overview)
- [API Spec of Azure Managed Redis](https://learn.microsoft.com/rest/api/redis/redisenterprisecache/redis-enterprise/create)