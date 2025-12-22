#!/bin/bash

echo "=========================================="
echo "Redis Restart Script"
echo "=========================================="

# Check if Redis is installed
if ! command -v redis-server &> /dev/null; then
    echo "❌ Redis is not installed!"
    echo "Install with: brew install redis"
    exit 1
fi

# Kill any existing Redis processes
echo "Checking for existing Redis processes..."
REDIS_PIDS=$(pgrep -f redis-server)

if [ -n "$REDIS_PIDS" ]; then
    echo "Found Redis process(es): $REDIS_PIDS"
    echo "Killing existing Redis instances..."
    pkill -9 redis-server 2>/dev/null
    sleep 1
    echo "✓ Redis processes terminated"
else
    echo "No existing Redis processes found"
fi

# Stop Redis service if running via Homebrew
echo ""
echo "Stopping Redis service (if running via brew)..."
brew services stop redis 2>/dev/null

# Clean up any existing dump file (optional - comment out if you want to keep data)
# echo ""
# echo "Cleaning Redis dump file..."
# rm -f dump.rdb

# Start Redis
echo ""
echo "=========================================="
echo "Starting Redis..."
echo "=========================================="

# Option 1: Start as background service (recommended for development)
echo "Starting Redis as background service..."
brew services start redis

# Wait a moment for Redis to start
sleep 2

# Verify Redis is running
echo ""
if redis-cli ping > /dev/null 2>&1; then
    echo "✓ Redis is running successfully!"
    echo ""
    echo "Redis info:"
    redis-cli info server | grep redis_version
    echo ""
    echo "To monitor Redis:"
    echo "  redis-cli monitor"
    echo ""
    echo "To stop Redis:"
    echo "  brew services stop redis"
else
    echo "❌ Failed to start Redis"
    echo "Try running manually:"
    echo "  redis-server"
    exit 1
fi

echo "=========================================="
