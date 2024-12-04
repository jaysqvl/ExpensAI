package com.example.cmpt362_finalproject.api

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

object ApiUtilities {
    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    fun isValidJson(json: String): Boolean {
        return try {
            val jsonObject = org.json.JSONObject(json)
            jsonObject.keys().hasNext()
        } catch (e: Exception) {
            false
        }
    }

    fun getDefaultHeaders(): Map<String, String> {
        return mapOf(
            "Content-Type" to "application/json",
            "Authorization" to "[Hidden for Video Purposes]" // Replace with dynamic token if needed
        )
    }
}
