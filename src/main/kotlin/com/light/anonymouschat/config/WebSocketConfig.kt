package com.light.anonymouschat.config

import com.light.anonymouschat.annotation.WebActionClientId
import com.light.anonymouschat.common.ClientId
import com.light.anonymouschat.common.SuperWebSocketHandler
import com.light.anonymouschat.common.WebSocketAction
import com.light.anonymouschat.utils.WebSocketHolder
import com.light.anonymouschat.utils.getLogger
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.*
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor
import kotlin.reflect.full.findAnnotations


/**
 * ClassName: WebScoketConfig
 * Author: 光子
 * Date:  2024/1/22
 * Project superBigApiAndTemplate
 **/
@Configuration
@EnableWebSocket
class WebSocketConfig(
    val initActions: List<WebSocketAction>
) : WebSocketConfigurer, WebSocketMessageBrokerConfigurer {
    val log by getLogger()
    override fun configureWebSocketTransport(registration: WebSocketTransportRegistration) {
        registration.setSendBufferSizeLimit(1024 * 1024) // 设置发送缓冲区大小为1MB
        registration.setMessageSizeLimit(1024 * 1024) // 设置消息大小限制为1MB
    }
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(SuperWebSocketHandler(), "/ws/*")
            .addInterceptors(object : HttpSessionHandshakeInterceptor() {
                override fun beforeHandshake(
                    request: ServerHttpRequest,
                    response: ServerHttpResponse,
                    wsHandler: WebSocketHandler,
                    attributes: MutableMap<String, Any>
                ): Boolean {
                    attributes[WebSocketHolder.CLIENT_ID] = request.uri.path.removePrefix("/ws/")
                    attributes[WebSocketHolder.CLIENT_URL] = request.uri.path
                    if (request is ServletServerHttpRequest) {
                        attributes.putAll(request.servletRequest.parameterMap.mapValues {
                            it.value.singleOrNull() ?: it.value
                        })
                    }
                    return super.beforeHandshake(request, response, wsHandler, attributes)
                }
            });
    }

    @PostConstruct
    fun initActions() {
        initActions.forEach {
            it::class.findAnnotations(WebActionClientId::class).singleOrNull()?.let { annotation ->
                WebSocketHolder.addAction(ClientId(annotation.value), it)

            }
        }
    }

}