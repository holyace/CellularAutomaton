package com.michael.demo.celllife.model

class Tribe(val cell: Cell, val members: MutableList<Cell>,
            val region: Region) {

    fun contains(cell: Cell): Boolean {
        return members.contains(cell)
    }
}