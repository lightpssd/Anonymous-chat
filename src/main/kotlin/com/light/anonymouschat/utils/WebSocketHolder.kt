package com.light.anonymouschat.utils

import com.light.anonymouschat.common.ClientId
import com.light.anonymouschat.common.WebSocketAction
import com.light.anonymouschat.common.WebSocketEvent
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

object WebSocketHolder {
    const val CLIENT_ID = "client_id"
    const val CLIENT_URL = "client_url"
    val sessionGroup = ConcurrentHashMap<ClientId, MutableList<WebSocketSession>>()
    val sessionEventMap = ConcurrentHashMap<ClientId, WebSocketEvent>()
    fun removeSession(clientId: ClientId) {
        sessionGroup.remove(clientId)
    }

    fun removeSession(clientId: ClientId, session: WebSocketSession) {
        sessionGroup[clientId]?.let {
            it.remove(session)
            if (it.size == 0) {
                sessionGroup.remove(clientId)
            }
        }
    }

    fun send(clientId: ClientId, message: WebSocketMessage<*>) {
        sessionGroup[clientId]?.forEach {
            it.sendMessage(message)
        }
    }

    fun sendNotToMyself(clientId: ClientId, message: WebSocketMessage<*>, selfSession: WebSocketSession) {
        sessionGroup[clientId]?.forEach {
            if (it != selfSession) {
                it.sendMessage(message)
            }
        }
    }

    fun addAction(clientId: ClientId, event: WebSocketAction) {
        sessionEventMap.getOrPut(clientId) { WebSocketEvent() } += event
    }

    fun removeAction(clientId: ClientId, event: WebSocketAction) {
        sessionEventMap[clientId]?.let {
            it -= event
            if (it.size() == 0) {
                sessionEventMap.remove(clientId)
            }
        }
    }

    fun removeAllAction(clientId: ClientId) {
        sessionEventMap.remove(clientId)
    }

    fun removeAllEvent() {
        sessionEventMap.clear()
    }

    fun runAction(clientId: ClientId, message: WebSocketMessage<*>, session: WebSocketSession) {
        sessionEventMap[clientId]?.invoke(message, session)
    }

    fun connectionAfterAction(clientId: ClientId, session: WebSocketSession) {

        sessionEventMap[clientId]?.connectionAction(session)
    }

    fun closeBeforeAction(clientId: ClientId, session: WebSocketSession) {
        sessionEventMap[clientId]?.closeAction(session)
    }

    operator fun invoke(clientId: ClientId, message: WebSocketMessage<*>, session: WebSocketSession) {
        runAction(clientId, message, session)
    }

    fun event(clientId: ClientId): WebSocketEvent {
        return sessionEventMap.getOrPut(clientId) { WebSocketEvent() }
    }

    operator fun get(clientId: ClientId): MutableList<WebSocketSession> {
        return sessionGroup[clientId] ?: mutableListOf()
    }

    operator fun set(clientId: ClientId, session: WebSocketSession) {
        sessionGroup.getOrPut(clientId) { mutableListOf() }.add(session)
    }
}