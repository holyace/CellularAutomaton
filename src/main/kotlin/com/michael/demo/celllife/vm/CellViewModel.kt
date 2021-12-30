package com.michael.demo.celllife.vm

import com.michael.demo.celllife.model.Cell

class CellViewModel : BaseCellViewModel() {

    override fun evolution(): Array<Cell> {

        if (mCells.isNullOrEmpty()) return emptyArray()

        val (rx, ry) = getCellRegion(mCells)

        println("evolution range: x[${rx.min}, ${rx.max}], y[${ry.min}, ${ry.max}]")

        val rebirthCell: MutableList<Cell> = mutableListOf()
        for (x in rx.min - 1..rx.max + 1) {
            for (y in ry.min - 1..ry.max + 1) {
                val cell = findCell(x, y, mCells)
                val neighbors = findNeighbors(x, y, mCells)
                val count = neighbors.size
                if (cell == null && count >= REBIRTH_COUNT && count <= MAX_LIVE_COUNT) {
                    rebirthCell.add(Cell(x, y, neighbors = neighbors))
                } else if (cell != null) {
                    updateCellState(cell, neighbors)
                }
            }
        }

        val iter = mCells.iterator()
        iter.forEach {
            if (!it.isAlive()) iter.remove()
        }

        if (!rebirthCell.isNullOrEmpty()) {
            mCells.addAll(rebirthCell)
        }

        return mCells.toTypedArray().copyOf()
    }
}