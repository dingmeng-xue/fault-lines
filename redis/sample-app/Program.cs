using RedisSampleApp.Models;
using RedisSampleApp.Services;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container
builder.Services.Configure<RedisSettings>(builder.Configuration.GetSection("Redis"));
builder.Services.AddSingleton<ICacheService, RedisCacheService>();
builder.Services.AddSingleton<IRecentItemsTracker, RecentItemsTracker>();

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new() 
    { 
        Title = "Redis Sample API", 
        Version = "v1",
        Description = "A sample API demonstrating Redis caching patterns with performance metrics"
    });
});

var app = builder.Build();

// Configure the HTTP request pipeline
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(c =>
    {
        c.SwaggerEndpoint("/swagger/v1/swagger.json", "Redis Sample API v1");
        c.RoutePrefix = string.Empty; // Swagger UI at root
    });
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

app.Run();
