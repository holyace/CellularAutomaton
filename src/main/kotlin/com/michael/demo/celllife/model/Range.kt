package com.michael.demo.celllife.model

class Range(val min: Int, val max: Int) {

    fun hasIntersection(other: Range): Boolean {
        return contains(other.min) or contains(other.max)
    }

    fun contains(other: Range): Boolean {
        return contains(other.min) and contains(other.max)
    }

    fun contains(x: Int): Boolean {
        return x in min..max
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Range

        if (min != other.min) return false
        if (max != other.max) return false

        return true
    }

    override fun hashCode(): Int {
        var result = min
        result = 31 * result + max
        return result
    }

    override fun toString(): String {
        return "[$min, $max]"
    }
}