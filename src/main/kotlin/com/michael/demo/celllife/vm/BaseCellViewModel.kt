@file:Suppress("MemberVisibilityCanBePrivate")

package com.michael.demo.celllife.vm

import com.michael.demo.celllife.model.Cell
import com.michael.demo.celllife.model.Range
import com.michael.demo.celllife.model.Tribe

abstract class BaseCellViewModel : ICellViewModel {
    protected var mCells: MutableList<Cell> = mutableListOf()

    private var mMinX = 0
    private var mMaxX = 0
    private var mMinY = 0
    private var mMaxY = 0

    override fun initialize(cells: Array<Cell>) {
        mCells.clear()
        cells.forEach {
            it.neighbors = findNeighbors(it, cells)
        }
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

        fun updateCellState(cell: Cell, neighbors: MutableList<Cell>, diedCell: MutableList<Cell>) {
            val count = neighbors.size
            when {
                count < MIN_DIE_COUNT -> {
                    cell.state = 0
                    diedCell.add(cell)
                }

                count <= MAX_LIVE_COUNT -> {
                    cell.state = 1

                    cell.neighbors = neighbors

                }

                else -> {
                    cell.state = 0
                    diedCell.add(cell)
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

        fun getTribeRegion(cell: Collection<Cell>): Pair<Range, Range> {
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

        fun findNeighbors(x: Int, y: Int, cells: Collection<Cell>): MutableList<Cell> {
            val neighbors = mutableListOf<Cell>()
            cells.forEach {
                if (isNeighbors(x, y, it)) {
                    neighbors.add(it)
                }
            }
            return neighbors
        }

        fun findNeighbors(cell: Cell, cells: Array<Cell>): MutableList<Cell> {
            val neighbors = mutableListOf<Cell>()
            cells.forEach {
                if (isNeighbors(cell, it)) {
                    neighbors.add(it)
                }
            }
            return neighbors
        }

        fun findRegion(x: Int, y: Int, cells: Collection<Cell>): Pair<Cell?, MutableList<Cell>> {
            var cell: Cell? = null
            var neighbors = mutableListOf<Cell>()
            cells.forEach {
                if (it.x == x && it.y == y) {
                    cell = it
                } else if (isNeighbors(x, y, it)) {
                    neighbors.add(it)
                }
            }
            return Pair(cell, neighbors)
        }

        fun findCell(x: Int, y: Int, cells: Collection<Cell>): Cell? {
            return cells.lastOrNull { it.x == x && it.y == y }
        }

        fun getTribe(cell: Cell): Tribe {
            val members = mutableListOf<Cell>()
            getTribeMembers(cell, mutableListOf(), members)
            val (xr, yr) = getTribeRegion(members)
            return Tribe(cell, members, xr, yr)
        }

        private fun getTribeMembers(cell: Cell?, visited: MutableList<Cell>, members: MutableList<Cell>) {
            cell?: return

            if (visited.contains(cell)) return

            visited.add(cell)

            if (!members.contains(cell)) {
                members.add(cell)
            }

            val neighbors = cell.neighbors!!.toMutableList()
            while (neighbors.isNotEmpty()) {
                getTribeMembers(neighbors.removeAt(0), visited, members)
            }
        }

        fun mergeTribes(tribes: Collection<Tribe>): MutableList<Cell> {

            val cells = mutableListOf<Cell>()

            tribes.forEach {
                it.members.forEach { cell ->
                    if (!cells.contains(cell)) {
                        cells.add(cell)
                    }
                }
            }

            return cells
        }

        fun splitTribes(cells: MutableList<Cell>): MutableList<Tribe> {

            val cellsCopy = cells.toMutableList()

            val tribes = mutableListOf<Tribe>()

            while (cellsCopy.isNotEmpty()) {
                val tribe = getTribe(cellsCopy[0])
                tribes.add(tribe)
                cellsCopy.removeAll(tribe.members)
            }

            return tribes
        }

        fun removeDiedMember(members: MutableList<Cell>) {
            val iter = members.iterator()
            iter.forEach { member ->
                if (!member.isAlive()) {
                    iter.remove()
                } else {
                    member.neighbors?: return@forEach
                    val miter = member.neighbors!!.iterator()
                    miter.forEach { neighbor ->
                        if (!neighbor.isAlive()) miter.remove()
                    }
                }
            }
        }
    }
}