namespace RedisSampleApp.Models;

public class RedisSettings
{
    public string ConnectionString { get; set; } = string.Empty;
    
    public int CacheTTLMinutes { get; set; } = 5;
}
