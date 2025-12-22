package chess.api.config;

import chess.api.service.GameUpdatePublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisPubSubConfig {

    /**
     * Container for Redis message listeners
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter gameUpdateListener) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Subscribe to game update channel
        container.addMessageListener(gameUpdateListener, new PatternTopic("game-updates:*"));

        return container;
    }

    /**
     * Adapter to convert Redis messages to method calls
     */
    @Bean
    public MessageListenerAdapter gameUpdateListener(GameUpdatePublisher gameUpdatePublisher,
                                                     RedisTemplate<String, Object> redisTemplate) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(gameUpdatePublisher, "handleGameUpdate");
        // Use the same serializer as RedisTemplate for deserialization
        adapter.setSerializer((RedisSerializer<?>) redisTemplate.getValueSerializer());
        return adapter;
    }
}
