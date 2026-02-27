namespace RedisSampleApp.Models;

public class ItemResponse
{
    public string Key { get; set; } = string.Empty;
    
    public string? Value { get; set; }
    
    public bool CacheHit { get; set; }
    
    public long TimeMs { get; set; }
}
