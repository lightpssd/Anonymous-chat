package com.light.anonymouschat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AnonymousChatApplication

fun main(args: Array<String>) {
    runApplication<AnonymousChatApplication>(*args)
}
