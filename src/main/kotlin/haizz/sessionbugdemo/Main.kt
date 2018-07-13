package haizz.sessionbugdemo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@SpringBootApplication
class App {
    @Bean
    fun redisConnectionFactory() = LettuceConnectionFactory("localhost", 6379)

    @Bean
    fun webSocketMapping(myWebSocketHandler: MyWebSocketHandler): HandlerMapping =
            SimpleUrlHandlerMapping().apply {
                urlMap = mapOf(
                        "/websocket" to myWebSocketHandler
                )
                order = 10
            }

    @Bean
    fun handlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(App::class.java, *args)
}
