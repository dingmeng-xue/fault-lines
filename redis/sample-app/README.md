# Redis Sample Application

A C# RESTful API application demonstrating Redis caching patterns, performance characteristics, and breaking changes across Redis client library versions.

## 🏛️ Classification

**Type: B - Documented (Manual Test Guide)**

This project builds and runs with documented manual testing instructions, including API endpoints and verification steps.

## 📋 Overview

This sample application is a C# REST API that demonstrates Redis caching behavior with performance metrics. The application provides:

### API Endpoints

#### `GET /v1/items/{item-key}`
Returns item information with caching metrics:
- **Request**: GUID-formatted item key
- **Response** (JSON):
  - `key`: The requested item key (GUID format)
  - `value`: Item value (key + " value")
  - `cacheHit`: Boolean indicating if data was retrieved from cache
  - `timeMs`: Time in milliseconds to retrieve the data
  
**Behavior**:
- **Cache Hit**: Returns cached item immediately
- **Cache Miss**: Generates item in-memory with 1-second latency (simulating database/external API call)
- **Cache Not Configured**: Returns null for value
- **Item Format**: Key=GUID, Value="{GUID} value"

#### `GET /v1/items`
Returns the latest 100 items accessed through the `/v1/items/{item-key}` endpoint.

**Purpose**: Tracks recently accessed items to demonstrate:
- Cache access patterns
- Hit/miss ratios over time
- Performance impact of caching

## 🎯 Purpose

The examples in this directory help identify and understand:
- **API Breaking Changes**: Method signature changes, deprecated APIs
- **Connection Model Changes**: Connection pooling vs connection multiplexing
- **Command Compatibility**: Commands that behave differently across Redis versions
- **Cluster Mode Differences**: Single-node vs cluster topology requirements
- **Data Structure Changes**: Serialization format incompatibilities

## 🔧 Common Breaking Changes Demonstrated

### Redis Client Library Migrations
- **Jedis 3.x → 4.x**: Connection pooling changes, command API updates
- **Lettuce 5.x → 6.x**: Reactive API changes, connection lifecycle
- **StackExchange.Redis v1 → v2**: Async pattern changes, configuration API

### Redis Server Version Upgrades
- **Redis 4.x → 5.x**: Streams introduction, active defragmentation
- **Redis 5.x → 6.x**: ACL implementation, SSL/TLS changes
- **Redis 6.x → 7.x**: Functions, command changes

### Architecture Changes
- **Standalone → Cluster**: Hash tags, multi-key operations restrictions
- **Cluster → Sentinel**: Failover configuration differences
- **On-premises → Cloud (Azure Cache, AWS ElastiCache)**: Connection string formats, SSL requirements

## 📁 Structure

```
redis/sample-app/
├── README.md                    # This file
├── RedisSampleApp.csproj        # C# project file
├── Program.cs                   # Application entry point
├── Controllers/                 # API controllers
│   └── ItemsController.cs       # Items API endpoint
├── Services/                    # Business logic
│   ├── ICacheService.cs         # Cache service interface
│   └── RedisCacheService.cs     # Redis cache implementation
├── Models/                      # Data models
│   └── ItemResponse.cs          # API response model
└── appsettings.json            # Configuration (Redis connection string)
```

## 🚀 Usage

### Prerequisites
- .NET 8 SDK
- Redis server (local or remote) - optional for testing without cache

### Configuration

Update `appsettings.json` with your Redis connection string:

```json
{
  "Redis": {
    "ConnectionString": "localhost:6379",
    "CacheTTLMinutes": 5
  }
}
```

**Configuration Options**:
- `ConnectionString`: Redis server connection string (leave empty to disable caching)
- `CacheTTLMinutes`: Cache expiration time in minutes (default: 5)

**Storage**: The latest 100 items list is stored in Redis as a sorted set, ensuring persistence across application restarts.

### Running the Application

```bash
cd redis/sample-app
dotnet restore
dotnet run
```

The application will start on `http://localhost:5000` with Swagger UI available at the root URL.

### Testing the APIs

**Using Swagger UI**: Navigate to `http://localhost:5000` in your browser for interactive API testing.

**Using cURL**:

#### Test Single Item Retrieval

```bash
# First call (cache miss - 1 second delay)
curl http://localhost:5000/v1/items/550e8400-e29b-41d4-a716-446655440000

# Response:
# {
#   "key": "550e8400-e29b-41d4-a716-446655440000",
#   "value": "550e8400-e29b-41d4-a716-446655440000 value",
#   "cacheHit": false,
#   "timeMs": 1005
# }

# Second call (cache hit - fast)
curl http://localhost:5000/v1/items/550e8400-e29b-41d4-a716-446655440000

# Response:
# {
#   "key": "550e8400-e29b-41d4-a716-446655440000",
#   "value": "550e8400-e29b-41d4-a716-446655440000 value",
#   "cacheHit": true,
#   "timeMs": 5
# }
```

#### Test Recent Items List

```bash
# Get the last 100 accessed items (newest first)
curl http://localhost:5000/v1/items

# Response: Array of recent item keys
# [
#   "550e8400-e29b-41d4-a716-446655440000",
#   "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
#   ...
# ]
```

### Testing Without Redis

To test the application without Redis (cache disabled):

1. Set empty connection string in `appsettings.json`:
   ```json
   "Redis": {
     "ConnectionString": "",
     "CacheTTLMinutes": 5
   }
   ```

2. Run the application - all requests will return `cacheHit: false` and `value: null`

3. Recent items list (`GET /v1/items`) will return an empty array

## 🔍 Example Scenarios

This application demonstrates several Redis caching patterns and breaking changes:

### Cache Performance Comparison
- **With Cache**: ~5ms response time (cache hit)
- **Without Cache**: ~1000ms response time (simulated data source latency)
- **Speedup**: 200x performance improvement

### Breaking Changes Demonstrated

#### StackExchange.Redis v1 → v2 Migration
- Connection multiplexing pattern changes
- Async/await API improvements
- Configuration API updates

#### Redis Server Version Compatibility
- Connection string format differences
- SSL/TLS configuration changes
- Command compatibility across Redis versions

## ⚠️ Known Issues & Breaking Changes

### Connection Management
- Pool configuration parameter changes between versions
- Connection timeout handling differences
- SSL/TLS certificate validation changes

### Command Behavior
- `SCAN` cursor behavior in cluster mode
- Transaction (`MULTI/EXEC`) limitations in cluster
- Lua script execution in cluster requiring key pre-declaration

### Serialization
- Binary vs string serialization defaults
- JSON encoding differences between client versions
- Character encoding handling changes

## 📚 Additional Resources

- [Redis Documentation](https://redis.io/docs/)
- [Redis Cluster Specification](https://redis.io/docs/reference/cluster-spec/)
- [Jedis GitHub](https://github.com/redis/jedis)
- [Lettuce Reference Guide](https://lettuce.io/core/release/reference/)
- [StackExchange.Redis Documentation](https://stackexchange.github.io/StackExchange.Redis/)

## 🤝 Contributing

To add new breaking change examples:
1. Create a new directory for your language/client
2. Include minimal reproducible code showing the breaking change
3. Document the "before" and "after" states clearly
4. Add comments explaining the migration path

## 📄 License

See the main [LICENSE](../../LICENSE) file in the repository root.
