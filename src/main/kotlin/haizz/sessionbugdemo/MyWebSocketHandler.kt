package haizz.sessionbugdemo

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.session.ReactiveSessionRepository
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*
import java.util.regex.Pattern

@Component
class MyWebSocketHandler(private val reactiveSessionRepository: ReactiveSessionRepository<*>) : WebSocketHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handle(session: WebSocketSession): Mono<Void> {
        val sessionId = getSessionId(session)

        return if (sessionId == null) {
            logger.error("No session cookie")
            session.close()
        } else {
            logger.info("Connected to socket ${session.id}, web session = $sessionId")
            val pingFlux = pingFlux(session)
                    .doFinally {
                        logger.info("Socket ${session.id} pingFlux finally done")
                    }
            val input = session.receive()
            session
                    .send(pingFlux)
                    .and(input)
                    .or(sessionCheckFlux(session, sessionId))
                    .doFinally {
                        logger.info("Socket ${session.id} finally done")
                    }
        }
    }

    private fun pingFlux(session: WebSocketSession): Flux<WebSocketMessage> {
        return Flux.interval(Duration.ofSeconds(10))
                .map {
                    session.pingMessage {
                        it.allocateBuffer(0)
                    }
                }
    }

    private fun sessionCheckFlux(session: WebSocketSession, sessionId: String): Mono<Void> {
        return Flux.interval(Duration.ofSeconds(10))
                .flatMap { tick ->
                    reactiveSessionRepository
                            .findById(sessionId)
                            .map { Pair(tick, it) }
                            .defaultIfEmpty(Pair(tick, null))
                }
                .doOnError {
                    logger.error("SessionCheck ${session.id} error, sessionid = $sessionId", it)
                }
                .doOnNext { (_, sess) ->
                    if (sess == null) {
                        logger.info("Web session $sessionId for connection ${session.id} is null")
                    } else if (sess.isExpired) {
                        logger.info("Web session $sessionId for connection ${session.id} is expired")
                    }
                }
                .takeWhile { (_, sess) -> sess != null && !sess.isExpired }
                .then()
    }


    val cookiePattern: Pattern = Pattern.compile("([^=]+)=([^\\;]*);?\\s?")

    private fun getSessionId(session: WebSocketSession): String? {
        return session.handshakeInfo.headers[HttpHeaders.COOKIE]
                ?.asSequence()
                ?.map { parseCookies(it) }
                ?.mapNotNull { it["SESSION"] }
                ?.firstOrNull()
    }

    private fun parseCookies(cookies: String): Map<String, String> {
        val matcher = cookiePattern.matcher(cookies)
        val m = HashMap<String, String>()

        while (matcher.find()) {
            val key = matcher.group(1)
            val value = matcher.group(2)
            m[key] = value
        }

        return m
    }
}