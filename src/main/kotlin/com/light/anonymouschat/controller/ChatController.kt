package com.light.anonymouschat.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

/**
 * ClassName: ChatController
 * Author: 光子
 * Date:  2024/1/23
 * Project superBigApiAndTemplate
 **/
@Controller
@RequestMapping(path = ["/chat"])
class ChatController {

    @GetMapping(path = ["/index/{roomId}"])
    fun index(@PathVariable("roomId") roomId: String, model: Model): String {
        model.addAttribute("roomId", roomId)
        return "chat/index"
    }
}