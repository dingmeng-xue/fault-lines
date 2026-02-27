namespace RedisSampleApp.Services;

public interface ICacheService
{
    Task<string?> GetAsync(string key);
    Task SetAsync(string key, string value);
    Task AddToRecentItemsAsync(string key);
    Task<List<string>> GetRecentItemsAsync();
}
