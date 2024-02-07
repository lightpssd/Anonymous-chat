package com.light.anonymouschat.utils

import org.slf4j.LoggerFactory

/**
 * ClassName: logutils
 * Author: 光子
 * Date:  2022/11/14
 * Project RuoYi-Vue-master
 **/
inline fun <reified T>T.getLogger()= lazy {  LoggerFactory.getLogger(T::class.java)}

