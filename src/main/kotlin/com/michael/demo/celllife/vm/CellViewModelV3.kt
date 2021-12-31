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
        val rebirthCell = mutableListOf<Cell>()
        val diedCell = mutableListOf<Cell>()

        for (x in tribe.rx.min - 1..tribe.rx.max + 1) {
            for (y in tribe.ry.min - 1..tribe.ry.max + 1) {
                val (cell, neighbors) = findRegion(x, y, tribe.members)
                val count = neighbors.size
                if (cell == null && count >= REBIRTH_COUNT && count <= MAX_LIVE_COUNT) {
                    rebirthCell.add(Cell(x, y, neighbors = neighbors))
                } else if (cell != null) {
                    updateCellState(cell, neighbors, diedCell)
                }
            }
        }

        tribe.members.removeAll(diedCell)

        rebirthCell.forEach { cell ->
            cell.neighbors.forEach { neighbor ->
                neighbor.neighbors.add(cell)
            }
            tribe.members.add(cell)
        }
    }
}