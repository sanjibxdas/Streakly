package com.streakly.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.text.NumberFormat
import java.util.Locale

fun <T> Flow<T>.safeCatch(fallback: T): Flow<T> = this.catch { emit(fallback) }

fun Double.format1(): String = String.format(Locale.US, "%.1f", this)

fun Float.format1(): String = String.format(Locale.US, "%.1f", this)

fun Int.formatThousands(): String = NumberFormat.getNumberInstance(Locale.getDefault()).format(this)

fun Long.formatThousands(): String = NumberFormat.getNumberInstance(Locale.getDefault()).format(this)
