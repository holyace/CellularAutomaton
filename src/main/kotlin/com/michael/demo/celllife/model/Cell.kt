package com.michael.demo.celllife.model

data class Cell(var x: Int, var y: Int,
                var color: String = "0x333333", var state: Int = 1,
                var neighbors: Array<Cell>? = null) {

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return "Cell([$x, $y], state: $state, neighbors: ${neighbors?.size?:0})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cell

        if (x != other.x) return false
        if (y != other.y) return false
        if (state != other.state) return false

        return true
    }

    fun isAlive() = state > 0
}