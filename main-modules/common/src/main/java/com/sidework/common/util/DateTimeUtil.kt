//package com.moyeobus.global.util
//
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//
//object DateTimeUtil {
//
//    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
//    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//
//    fun formatTime(date: LocalDateTime): String =
//        date.format(timeFormatter)
//
//    fun formatDate(date: LocalDateTime): String =
//        date.format(dateFormatter)
//}