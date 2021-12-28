package com.michael.demo.celllife

import java.lang.Exception
import java.util.*

abstract class LoopTimerTask(private val timer: Timer,
                             private val delay: Long,
                             private val task: Runnable,
                             private var loop: Boolean = true) : TimerTask() {

    override fun run() {
        try {
            task.run()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (loop) {
                timer.schedule(this, delay)
            }
        }
    }

    abstract fun execute()

    fun stopLoop() {
        loop = false
        cancel()
    }
}