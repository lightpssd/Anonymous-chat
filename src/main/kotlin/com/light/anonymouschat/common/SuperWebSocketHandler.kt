package com.light.anonymouschat.common

import com.light.anonymouschat.common.ClientId
import com.light.anonymouschat.utils.WebSocketHolder
import com.light.anonymouschat.utils.getLogger
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

/**
 * ClassName: WebSocketHandler
 * Author: 光子
 * Date:  2024/1/22
 * Project superBigApiAndTemplate
 **/

class SuperWebSocketHandler : TextWebSocketHandler() {
    private val log by getLogger()
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        WebSocketHolder(session.clientId(), message, session)
    }

    //连接成功后
    override fun afterConnectionEstablished(session: WebSocketSession) {
        session.attributes[WebSocketHolder.CLIENT_ID]?.let {
            WebSocketHolder[session.clientId()] = session
            WebSocketHolder.connectionAfterAction(session.clientId(), session)
        } ?: session.close()
    }

    //连接失败
    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        WebSocketHolder.closeBeforeAction(session.clientId(), session)
        WebSocketHolder.removeSession(session.clientId(), session)
    }


    //连接关闭
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        WebSocketHolder.closeBeforeAction(session.clientId(), session)
        WebSocketHolder.removeSession(session.clientId(), session)
        log.info("连接关闭:${session.clientId()}-${session.id}")

    }

    private fun WebSocketSession.clientId(): ClientId {
        return ClientId(this.attributes[WebSocketHolder.CLIENT_ID] as String)
    }
}
