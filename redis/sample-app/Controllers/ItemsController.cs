using System.Diagnostics;
using Microsoft.AspNetCore.Mvc;
using RedisSampleApp.Models;
using RedisSampleApp.Services;

namespace RedisSampleApp.Controllers;

[ApiController]
[Route("v1/[controller]")]
public class ItemsController : ControllerBase
{
    private readonly ICacheService _cacheService;
    private readonly ILogger<ItemsController> _logger;

    public ItemsController(ICacheService cacheService, ILogger<ItemsController> logger)
    {
        _cacheService = cacheService;
        _logger = logger;
    }

    /// <summary>
    /// Get item by key with cache performance metrics
    /// </summary>
    /// <param name="itemKey">GUID-formatted item key</param>
    /// <returns>Item information with cache hit status and timing</returns>
    [HttpGet("{itemKey}")]
    [ProducesResponseType(typeof(ItemResponse), StatusCodes.Status200OK)]
    public async Task<ActionResult<ItemResponse>> GetItem(string itemKey)
    {
        var stopwatch = Stopwatch.StartNew();
        
        // Validate GUID format
        if (!Guid.TryParse(itemKey, out _))
        {
            return BadRequest(new { error = "Item key must be a valid GUID" });
        }

        string? value = null;
        bool cacheHit = false;

        // Try to get from cache
        value = await _cacheService.GetAsync(itemKey);
        
        if (value != null)
        {
            cacheHit = true;
            _logger.LogInformation("Cache HIT for key: {Key}", itemKey);
        }
        else
        {
            _logger.LogInformation("Cache MISS for key: {Key}", itemKey);
            
            // Simulate data source latency (e.g., database query)
            await Task.Delay(1000);
            
            // Generate value: key + " value"
            value = $"{itemKey} value";
            
            // Store in cache for future requests
            await _cacheService.SetAsync(itemKey, value);
        }

        // Track this item in recent items list
        await _cacheService.AddToRecentItemsAsync(itemKey);

        stopwatch.Stop();

        var response = new ItemResponse
        {
            Key = itemKey,
            Value = value,
            CacheHit = cacheHit,
            TimeMs = stopwatch.ElapsedMilliseconds
        };

        return Ok(response);
    }

    /// <summary>
    /// Get the latest 100 accessed items
    /// </summary>
    /// <returns>List of recently accessed item keys</returns>
    [HttpGet]
    [ProducesResponseType(typeof(List<string>), StatusCodes.Status200OK)]
    public async Task<ActionResult<List<string>>> GetRecentItems()
    {
        var items = await _cacheService.GetRecentItemsAsync();
        _logger.LogInformation("Retrieved {Count} recent items", items.Count);
        return Ok(items);
    }
}
