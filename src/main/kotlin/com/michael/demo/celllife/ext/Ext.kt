package com.michael.demo.celllife.ext

import java.util.*

internal inline fun measureTime(block: () -> Unit): Long {
    val now = System.currentTimeMillis()
    block()
    return System.currentTimeMillis() - now
}