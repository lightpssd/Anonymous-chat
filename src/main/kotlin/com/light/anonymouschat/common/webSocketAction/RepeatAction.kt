package com.light.anonymouschat.common.webSocketAction

import com.light.anonymouschat.annotation.WebActionClientId
import com.light.anonymouschat.common.WebSocketAction
import com.light.anonymouschat.utils.WebSocketHolder
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession

/**
 * ClassName: RepeatAction
 * Author: 光子
 * Date:  2024/1/23
 * Project superBigApiAndTemplate
 **/
@WebActionClientId("repeat_action")
@Component
class RepeatAction : WebSocketAction {
    override fun invoke(textMessage: WebSocketMessage<*>, session: WebSocketSession) {
        session.sendMessage(TextMessage("服务器返回消息:来自${session.attributes[WebSocketHolder.CLIENT_ID]}:${textMessage.payload}"))
    }
}