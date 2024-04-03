package com.app.movieviewr.util

import java.text.SimpleDateFormat
import java.util.*

fun convertDate(dateString: String, inputFormat: String, outputFormat: String): String {
    if (dateString.isEmpty() || inputFormat.isEmpty() || outputFormat.isEmpty()) {
        return ""
    }

    return try {
        val inputFormatter = SimpleDateFormat(inputFormat, Locale.getDefault())
        val outputFormatter = SimpleDateFormat(outputFormat, Locale.getDefault())

        val date = inputFormatter.parse(dateString)
        date?.let { outputFormatter.format(it) } ?: ""
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}