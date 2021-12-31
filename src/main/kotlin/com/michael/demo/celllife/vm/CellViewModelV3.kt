package com.michael.demo.celllife.vm

import com.michael.demo.celllife.model.Cell
import com.michael.demo.celllife.model.Tribe

class CellViewModelV3 : BaseCellViewModel() {

    override fun evolution(): Array<Cell> {

        val tribes = splitTribes(mCells)

        tribes.forEach {
            evolutionTribe(it)
        }

        mCells = mergeTribes(tribes)

        return mCells.toTypedArray()
    }

    private fun evolutionTribe(tribe: Tribe) {

        evolutionRegion(tribe.rx, tribe.ry, tribe.members)
    }
}