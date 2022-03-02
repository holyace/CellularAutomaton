package com.michael.demo.celllife.model

data class Point2D(var x: Double, var y: Double) {

    fun reset() {
        x = 0.0
        y = 0.0
    }
}