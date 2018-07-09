package world.zhx.example.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import world.zhx.example.websocket.interceptor.WebSocketInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 将 ChatTextHandler 处理器 映射到  /websocket/p2ptext 路径下.
        registry.addHandler(getTextHandler(), "/webSocketIMServer")
                .setAllowedOrigins("*").addInterceptors(webSocketInterceptor());
    }

    @Bean
    public ChatTextHandler getTextHandler() {
        return new ChatTextHandler();
    }

    @Bean
    public WebSocketInterceptor webSocketInterceptor(){
        return new WebSocketInterceptor();
    }
}
