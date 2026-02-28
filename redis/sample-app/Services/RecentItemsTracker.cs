using System.Collections.Concurrent;

namespace RedisSampleApp.Services;

public interface IRecentItemsTracker
{
    void AddItem(string itemKey);
    List<string> GetRecentItems();
}

public class RecentItemsTracker : IRecentItemsTracker
{
    private readonly ConcurrentQueue<string> _recentItems = new();
    private readonly HashSet<string> _itemSet = new();
    private readonly object _lock = new();
    private const int MaxItems = 100;
    private readonly ILogger<RecentItemsTracker> _logger;

    public RecentItemsTracker(ILogger<RecentItemsTracker> logger)
    {
        _logger = logger;
    }

    public void AddItem(string itemKey)
    {
        lock (_lock)
        {
            // If item already exists, don't add duplicate
            if (_itemSet.Contains(itemKey))
            {
                return;
            }

            _recentItems.Enqueue(itemKey);
            _itemSet.Add(itemKey);

            // Remove oldest items if we exceed the limit
            while (_recentItems.Count > MaxItems)
            {
                if (_recentItems.TryDequeue(out var oldestItem))
                {
                    _itemSet.Remove(oldestItem);
                }
            }

            _logger.LogDebug("Added item {Key} to recent items tracker. Total: {Count}", itemKey, _recentItems.Count);
        }
    }

    public List<string> GetRecentItems()
    {
        lock (_lock)
        {
            return _recentItems.ToList();
        }
    }
}
