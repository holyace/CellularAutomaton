package com.michael.demo.celllife.vm

import com.michael.demo.celllife.model.Cell

class CellViewModel : ICellViewModel {

    private var mCells: MutableList<Cell> = mutableListOf()
    private var mXRange = arrayOf(0, 0)
    private var mYRange = arrayOf(0, 0)

    override fun initialize(cells: Array<Cell>) {
        mCells.clear()
        cells.forEach {
//            updateRange(it)
            mCells.add(it)
        }
    }

    private fun updateRange(cell: Cell) {
        if (cell.x < mXRange[0]) {
            mXRange[0] = cell.x
        }
        if (cell.x > mXRange[1]) {
            mXRange[1] = cell.x
        }
        if (cell.y < mYRange[0]) {
            mYRange[0] = cell.y
        }
        if (cell.y > mYRange[1]) {
            mYRange[1] = cell.y
        }
    }

    override fun evolution(): Array<Cell> {

        if (mCells.isNullOrEmpty()) return emptyArray()

        var startX = 0
        var endX = 0
        var startY = 0
        var endY = 0

        mCells.forEach {
            if (it.x <= startX) {
                startX = it.x
            }

            if (it.x >= endX) {
                endX = it.x
            }

            if (it.y <= startY) {
                startY = it.y
            }

            if (it.y >= endY) {
                endY = it.y
            }
        }

        val rebirthCell: MutableList<Cell> = mutableListOf()
        for (x in startX - 1..endX + 1) {
            for (y in startY - 1..endY + 1) {
                val cell = findCell(x, y)
                val neighbors = findNeighbors(x, y)
                val count = neighbors.size
                if (cell == null && count >= REBIRTH_COUNT && count <= MAX_LIVE_COUNT) {
                    rebirthCell.add(Cell(x, y, neighbors = neighbors))
                } else if (cell != null) {
                    when {
                        count < MIN_DIE_COUNT -> {
                            cell.state = 0
                        }

                        count <= MAX_LIVE_COUNT -> {
                            cell.state = 1

                            cell.neighbors = neighbors

                        }

                        else -> {
                            cell.state = 0
                        }
                    }
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

    private fun isVisible(cell: Cell, xRange: Array<Int>, yRange: Array<Int>): Boolean {
        return xRange[0] <= cell.x && cell.x <= xRange[1] &&
                cell.y >= yRange[0] && cell.y <= yRange[1]
    }

    private fun findCell(x: Int, y: Int): Cell? {
        return mCells.lastOrNull { it.x == x && it.y == y }
    }

    private fun findNeighbors(cell: Cell): Array<Cell> {
        val neighbors = mutableListOf<Cell>()
        mCells.forEach {
            if (isNeighbors(cell, it)) {
                neighbors.add(it)
            }
        }
        return neighbors.toTypedArray()
    }

    private fun findNeighbors(x: Int, y: Int): Array<Cell> {
        val neighbors = mutableListOf<Cell>()
        mCells.forEach {
            if (isNeighbors(x, y, it)) {
                neighbors.add(it)
            }
        }
        return neighbors.toTypedArray()
    }

    private fun isNeighbors(cell1: Cell, cell2: Cell): Boolean {
        return isNeighbors(cell1.x, cell1.y, cell2)
    }

    private fun isNeighbors(x: Int, y:Int, cell2: Cell): Boolean {
        return (x != cell2.x || y != cell2.y) &&
                x <= cell2.x + 1 && x >= cell2.x - 1 &&
                y <= cell2.y + 1 && y >= cell2.y - 1
    }

    companion object {
        private const val MIN_DIE_COUNT = 2
        private const val MAX_LIVE_COUNT = 3
        private const val REBIRTH_COUNT = 3
    }
}