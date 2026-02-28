using Microsoft.Extensions.Options;
using RedisSampleApp.Models;
using StackExchange.Redis;

namespace RedisSampleApp.Services;

public class RedisCacheService : ICacheService
{
    private readonly IConnectionMultiplexer? _redis;
    private readonly IDatabase? _db;
    private readonly TimeSpan _cacheTTL;
    private readonly ILogger<RedisCacheService> _logger;

    public RedisCacheService(IOptions<RedisSettings> settings, ILogger<RedisCacheService> logger)
    {
        _logger = logger;
        var connectionString = settings.Value.ConnectionString;
        _cacheTTL = TimeSpan.FromMinutes(settings.Value.CacheTTLMinutes);

        if (!string.IsNullOrWhiteSpace(connectionString))
        {
            try
            {
                _redis = ConnectionMultiplexer.Connect(connectionString);
                _db = _redis.GetDatabase();
                _logger.LogInformation("Redis cache enabled with TTL: {TTL} minutes", settings.Value.CacheTTLMinutes);
            }
            catch (Exception ex)
            {
                _logger.LogWarning(ex, "Failed to connect to Redis. Cache is disabled.");
                _redis = null;
                _db = null;
            }
        }
        else
        {
            _logger.LogInformation("Redis connection string not configured. Cache is disabled.");
        }
    }

    public async Task<string?> GetAsync(string key)
    {
        if (_db == null)
            return null;

        try
        {
            var value = await _db.StringGetAsync(key);
            return value.HasValue ? value.ToString() : null;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting key {Key} from cache", key);
            return null;
        }
    }

    public async Task SetAsync(string key, string value)
    {
        if (_db == null)
            return;

        try
        {
            await _db.StringSetAsync(key, value, _cacheTTL);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error setting key {Key} in cache", key);
        }
    }
}
