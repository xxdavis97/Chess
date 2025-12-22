package chess.api.service;

import chess.api.dto.websocket.GameUpdateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameUpdatePublisher {

    private static final Logger logger = LoggerFactory.getLogger(GameUpdatePublisher.class);
    private static final String CHANNEL_PREFIX = "game-updates:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public GameUpdatePublisher(RedisTemplate<String, Object> redisTemplate,
                              SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Publish a game update to Redis
     * This allows other server instances to receive the update
     */
    public void publishGameUpdate(String gameId, GameUpdateMessage message) {
        // Send the object directly - RedisTemplate will serialize it automatically
        redisTemplate.convertAndSend(CHANNEL_PREFIX + gameId, message);
        logger.debug("Published game update for game: {}", gameId);
    }

    /**
     * Handle game update received from Redis
     * This method is called when another server instance publishes an update
     */
    public void handleGameUpdate(GameUpdateMessage message) {
        // Message is already deserialized by RedisTemplate
        String gameId = message.getGameId();

        // Broadcast to WebSocket clients connected to this server instance
        messagingTemplate.convertAndSend("/topic/game/" + gameId, message);

        logger.debug("Broadcasted game update for game: {}", gameId);
    }
}
