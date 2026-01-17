//package com.moyeobus.global.util
//
//import java.time.Instant
//import java.util.Base64
//
//data class CursorWrapper(
//    val cursorCreatedAt: Instant?,
//    val cursorId: Long?
//)
//
//object CursorUtil {
//    fun encode(cursor: CursorWrapper): String? {
//        val (createdAt, id) = cursor
//        if (createdAt == null || id == null) return null
//        val raw = "${createdAt.toEpochMilli()}:$id"
//        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.toByteArray())
//    }
//
//    fun decode(cursor: String?): CursorWrapper {
//        if (cursor.isNullOrBlank()) return CursorWrapper(null, null)
//
//        return try {
//            val raw = String(Base64.getUrlDecoder().decode(cursor))
//            val (ts, id) = raw.split(":")
//            CursorWrapper(
//                Instant.ofEpochMilli(ts.toLong()),
//                id.toLong()
//            )
//        } catch (_: Exception) {
//            CursorWrapper(null, null)
//        }
//    }
//}