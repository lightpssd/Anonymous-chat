package com.light.anonymouschat.common

import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession

/**
 * ClassName: WebSocketEvent
 * Author: 光子
 * Date:  2024/1/22
 * Project superBigApiAndTemplate
 **/
fun interface WebSocketAction {
    operator fun invoke(textMessage: WebSocketMessage<*>, session: WebSocketSession)

    fun String.toClientId() = ClientId(this)

    fun connectionAction(session: WebSocketSession){}
    fun closeAction(session: WebSocketSession){}
}
