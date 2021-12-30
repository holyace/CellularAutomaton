@file:Suppress("MemberVisibilityCanBePrivate")

package com.michael.demo.celllife.vm

import com.michael.demo.celllife.model.Cell
import com.michael.demo.celllife.model.Range

abstract class BaseCellViewModel : ICellViewModel {
    protected var mCells: MutableList<Cell> = mutableListOf()

    private var mMinX = 0
    private var mMaxX = 0
    private var mMinY = 0
    private var mMaxY = 0

    override fun initialize(cells: Array<Cell>) {
        mCells.clear()
        mCells.addAll(cells)
    }

    override fun updateRegion(minX: Int, maxX: Int, minY: Int, maxY: Int) {
        mMinX = minX
        mMaxX = maxX
        mMinY = minY
        mMaxY = maxY
    }

    protected companion object {
        const val MIN_DIE_COUNT = 2
        const val MAX_LIVE_COUNT = 3
        const val REBIRTH_COUNT = 3

        fun updateCellState(cell: Cell, neighbors: Array<Cell>?) {
            val count = neighbors?.size?:0
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

        fun isNeighbors(x: Int, y:Int, cell2: Cell): Boolean {
            return (x != cell2.x || y != cell2.y) &&
                    x <= cell2.x + 1 && x >= cell2.x - 1 &&
                    y <= cell2.y + 1 && y >= cell2.y - 1
        }

        fun isNeighbors(cell1: Cell, cell2: Cell): Boolean {
            return isNeighbors(cell1.x, cell1.y, cell2)
        }

        fun getCellRegion(cell: Collection<Cell>): Pair<Range, Range> {
            var startX = 0
            var endX = 0
            var startY = 0
            var endY = 0

            cell.forEach {
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

            return Pair(Range(startX, endX), Range(startY, endY))
        }

        fun isVisible(cell: Cell, xRange: Range, yRange: Range): Boolean {
            return xRange.min <= cell.x && cell.x <= xRange.max &&
                    cell.y >= yRange.min && cell.y <= yRange.max
        }

        fun findNeighbors(x: Int, y: Int, cells: Collection<Cell>): Array<Cell> {
            val neighbors = mutableListOf<Cell>()
            cells.forEach {
                if (isNeighbors(x, y, it)) {
                    neighbors.add(it)
                }
            }
            return neighbors.toTypedArray()
        }

        fun findNeighbors(cell: Cell, cells: Collection<Cell>): Array<Cell> {
            val neighbors = mutableListOf<Cell>()
            cells.forEach {
                if (isNeighbors(cell, it)) {
                    neighbors.add(it)
                }
            }
            return neighbors.toTypedArray()
        }

        fun findRegion(x: Int, y: Int, cells: Collection<Cell>): Pair<Cell?, Array<Cell>> {
            var cell: Cell? = null
            var neighbors = mutableListOf<Cell>()
            cells.forEach {
                if (it.x == x && it.y == y) {
                    cell = it
                } else if (isNeighbors(x, y, it)) {
                    neighbors.add(it)
                }
            }
            return Pair(cell, neighbors.toTypedArray())
        }

        fun findCell(x: Int, y: Int, cells: Collection<Cell>): Cell? {
            return cells.lastOrNull { it.x == x && it.y == y }
        }
    }
}