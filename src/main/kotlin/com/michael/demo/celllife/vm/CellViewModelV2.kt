package com.michael.demo.celllife.vm

import com.michael.demo.celllife.model.Cell

class CellViewModelV2 : BaseCellViewModel() {

    override fun evolution(): List<Cell> {

        val region = getTribeRegion(mCells)

        println("evolution region: $region, cells: ${mCells.size}")

        evolutionRegion(region, mCells)

        return mCells
    }

}