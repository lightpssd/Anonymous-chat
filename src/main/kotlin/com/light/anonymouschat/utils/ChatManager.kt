package com.light.anonymouschat.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.web.socket.TextMessage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * ClassName: ChatManager
 * Author: 光子
 * Date:  2024/1/23
 * Project superBigApiAndTemplate
 **/
object ChatManager {
    val dataFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val chatFormatterRegex = Regex("##\\*\\{.*\\}\\*##")
    val objectMapper = ObjectMapper()
    fun buildCommandMessage(code: Int, msg: String,data:Any="") = TextMessage(
        objectMapper.createObjectNode().put("type", "command")
            .put("code", code)
            .putPOJO("data",data)
            .put("msg", msg).toResult()
    )

    fun buildChatMessage(mesType: Int, userName: String, msg: String, time: LocalDateTime) = TextMessage(
        objectMapper.createObjectNode().put("type", "chat")
            .put("mesType", mesType)
            .put("userName", userName)
            .put("msg", msg)
            .put("time", time.format(dataFormat)).toResult()
    )

    fun parseMessage(message: String): JsonNode? {
        if (!message.matches(chatFormatterRegex))
            throw RuntimeException("消息格式错误")

        return objectMapper.readTree(message.slice(3 until message.length - 3))

    }

    private fun ObjectNode.toResult() = "##*${this}*##"


}