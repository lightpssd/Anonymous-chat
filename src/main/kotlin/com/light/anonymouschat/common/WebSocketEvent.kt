package com.light.anonymouschat.common

import com.light.anonymouschat.common.WebSocketAction
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession

/**
 * ClassName: WebSocketEvent
 * Author: 光子
 * Date:  2024/1/23
 * Project superBigApiAndTemplate
 **/
class WebSocketEvent {
    private val handler = mutableListOf<WebSocketAction>()
    operator fun plusAssign(action: WebSocketAction) {
        handler.add(action)
    }

    operator fun minusAssign(action: WebSocketAction) {
        handler.remove(action)
    }

    operator fun invoke(TextMessage: WebSocketMessage<*>, session: WebSocketSession) {
        handler.forEach {
            it(TextMessage, session)
        }
    }

    fun connectionAction(session: WebSocketSession) {
        handler.forEach {
            it.connectionAction(session)
        }
    }
    fun closeAction(session: WebSocketSession) {
        handler.forEach {
            it.closeAction(session)
        }
    }
    fun size(): Int {
        return handler.size
    }

    fun clear() {
        handler.clear()
    }
}