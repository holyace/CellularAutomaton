package com.michael.demo.celllife.model

data class Region constructor(val rx: Range, val ry: Range) {

    constructor(minX: Int, maxX: Int, minY: Int, maxY: Int):
            this(Range(minX, maxX), Range(minY, maxY))

    fun rangeX() = rx.range()

    fun rangeY() = ry.range()

    fun contains(x: Int, y: Int): Boolean {
        return rx.contains(x) and ry.contains(y)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Region

        if (rx != other.rx) return false
        if (ry != other.ry) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rx.hashCode()
        result = 31 * result + ry.hashCode()
        return result
    }

    override fun toString(): String {
        return "Region($rx, $ry)"
    }
}