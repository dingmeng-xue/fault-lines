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
    private const string RecentItemsKey = "recent_items";
    private const int MaxRecentItems = 100;

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

    public async Task AddToRecentItemsAsync(string key)
    {
        if (_db == null)
            return;

        try
        {
            // Use sorted set with timestamp as score to maintain order
            var timestamp = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
            await _db.SortedSetAddAsync(RecentItemsKey, key, timestamp);
            
            // Keep only the latest 100 items
            var count = await _db.SortedSetLengthAsync(RecentItemsKey);
            if (count > MaxRecentItems)
            {
                // Remove oldest items
                await _db.SortedSetRemoveRangeByRankAsync(RecentItemsKey, 0, count - MaxRecentItems - 1);
            }
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error adding key {Key} to recent items", key);
        }
    }

    public async Task<List<string>> GetRecentItemsAsync()
    {
        if (_db == null)
            return new List<string>();

        try
        {
            // Get latest items in descending order (newest first)
            var items = await _db.SortedSetRangeByRankAsync(RecentItemsKey, 0, -1, Order.Descending);
            return items.Select(item => item.ToString()).ToList();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting recent items from cache");
            return new List<string>();
        }
    }
}
