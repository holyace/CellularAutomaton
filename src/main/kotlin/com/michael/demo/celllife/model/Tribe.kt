package com.michael.demo.celllife.model

class Tribe(val cell: Cell, val members: MutableList<Cell>,
            val rx: Range, val ry: Range) {

    fun contains(cell: Cell): Boolean {
        return members.contains(cell)
    }
}