package com.michael.demo.celllife.vm

import com.michael.demo.celllife.model.Cell

class CellViewModelV2 : BaseCellViewModel() {

    override fun evolution(): Array<Cell> {

        val (rx, ry) = getTribeRegion(mCells)

        println("evolution range: x[${rx.min}, ${rx.max}], y[${ry.min}, ${ry.max}]")

        evolutionRegion(rx, ry, mCells)

        return mCells.toTypedArray()
    }

}