package com.light.anonymouschat.common.webSocketAction

import com.light.anonymouschat.annotation.WebActionClientId
import com.light.anonymouschat.common.WebSocketAction
import com.light.anonymouschat.utils.ChatManager

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import java.time.LocalDateTime

/**
 * ClassName: GroupNoticeAction
 * Author: 光子
 * Date:  2024/1/23
 * Project superBigApiAndTemplate
 **/
@WebActionClientId("chat")
@Component
class ChatAction : WebSocketAction {
    companion object {
        const val CHAT_SESSION = "chat_id"
        const val USER_NAME = "chat_name"

    }

    val chatSession = mutableMapOf<String, MutableList<WebSocketSession>>()
    val chatUserList = mutableMapOf<String, MutableSet<String>>()
    override fun invoke(textMessage: WebSocketMessage<*>, session: WebSocketSession) {
        val json = runCatching {
            ChatManager.parseMessage(textMessage.payload.toString())
        }.getOrNull() ?: return
        session.sendMessage(ChatManager.buildCommandMessage(200, "消息发送成功", json.get("id").asLong()))
        chatSession[session.attributes[CHAT_SESSION]]?.forEach {
            if (it != session)
                it.sendMessage(
                    ChatManager.buildChatMessage(
                        1, session.attributes[USER_NAME].toString(), json.get("msg").asText(),
                        LocalDateTime.parse(json.get("time").asText(), ChatManager.dataFormat)
                    )
                )
        }

    }

    override fun connectionAction(session: WebSocketSession) {

        session.attributes[CHAT_SESSION]?.let {
            val name = session.attributes[USER_NAME]
            if (name == null) {
                session.sendMessage(ChatManager.buildCommandMessage(500, "请先设置昵称。"))
                session.close()
                return
            }
            chatUserList.getOrPut(it.toString()) {
                mutableSetOf()
            }.takeIf {
                it.add(name.toString())
            } ?: run {
                session.sendMessage(ChatManager.buildCommandMessage(502, "该昵称已经加入聊天室。"))
                session.close()
                return
            }
            val rooms = chatSession.getOrPut(it.toString()) {
                mutableListOf()
            }
            rooms.add(session)
            session.binaryMessageSizeLimit = 1024 * 1024 * 10
            session.textMessageSizeLimit = 1024 * 1024 * 10
            rooms.forEach {
                it.sendMessage(ChatManager.buildCommandMessage(201, "<${name}>加入聊天室。",rooms.size))
            }
        } ?: run {
            session.sendMessage(ChatManager.buildCommandMessage(501, "请先加入聊天室。"))
            session.close()
        }
    }

    override fun closeAction(session: WebSocketSession) {
        (session.attributes[CHAT_SESSION] as? String)?.let { room ->
            chatSession[room]?.takeIf {
                it.remove(session)
            }?.let {
                if (it.isEmpty()) {
                    chatSession -= room
                }
                chatUserList[room]?.let {
                    it.remove(session.attributes[USER_NAME])
                    if (it.isEmpty()) {
                        chatUserList -= room
                    }

                }
                it.forEach {  iit->
                    iit.sendMessage(ChatManager.buildCommandMessage(202, "用户离开聊天室。",it.size))
                }
            }
        }
    }
}