package com.michael.demo.celllife.vm

import com.michael.demo.celllife.model.Cell
import com.michael.demo.celllife.model.Tribe

class CellViewModelV3 : BaseCellViewModel() {

    override fun evolution(): List<Cell> {

        val tribes = splitTribes(mCells)

        println("splitTribes ${tribes.size}")

        tribes.forEach {
            evolutionTribe(it)
        }

        mCells = mergeTribes(tribes)

        return mCells
    }

    private fun evolutionTribe(tribe: Tribe) {

        println("evolutionTribe ${tribe.region}, members: ${tribe.members.size}")

        evolutionRegion(tribe.region, tribe.members)
    }
}