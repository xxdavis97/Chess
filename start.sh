#!/bin/bash

echo "=========================================="
echo "Chess API Startup Script"
echo "=========================================="

# Set Java 21 (Homebrew installation)
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
export PATH="$JAVA_HOME/bin:$PATH"

echo "Using Java version:"
java -version

# Check Redis
echo ""
echo "Checking Redis connection..."
if redis-cli ping > /dev/null 2>&1; then
    echo "✓ Redis is running"
else
    echo "✗ Redis is NOT running!"
    echo "  Please start Redis first:"
    echo "    brew services start redis"
    echo "  Or run manually:"
    echo "    redis-server"
    exit 1
fi

# Kill existing process on port 8080
echo ""
echo "Checking for existing server on port 8080..."
PID=$(lsof -ti:8080)
if [ -n "$PID" ]; then
    echo "Killing existing process: $PID"
    kill -9 $PID 2>/dev/null
    sleep 1
    echo "Process killed successfully"
else
    echo "No existing process found"
fi

# Clean and compile
echo ""
echo "=========================================="
echo "Building project..."
echo "=========================================="
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Build failed! Please check the errors above."
    exit 1
fi

# Start the server
echo ""
echo "=========================================="
echo "Starting Chess API Server..."
echo "=========================================="
echo "Server will be available at: http://localhost:8080"
echo "WebSocket endpoint: ws://localhost:8080/ws/chess"
echo "Press Ctrl+C to stop the server"
echo "=========================================="
echo ""

mvn spring-boot:run
