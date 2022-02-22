package com.michael.demo.celllife.vm

import com.michael.demo.celllife.model.Cell
import com.michael.demo.celllife.model.Region
import kotlin.math.max
import kotlin.math.min

class FireSpreadViewModel: BaseCellViewModel() {

    private lateinit var mRegion: Region
    private var mBoundCell = setOf<Cell>()

    override fun initialize(cells: List<Cell>) {
//        super.initialize(cells)
        var minX = 0
        var maxX = 0
        var minY = 0
        var maxY = 0

        val boundCell = mutableMapOf<Int, Cell>()

        mCells.clear()

        cells.forEach {
            it.state = 1
            it.neighbors = findNeighbors(it, cells)

            mCells.add(it)

            minX = min(minX, it.x)
            maxX = max(maxX, it.x)

            minY = min(minY, it.y)
            maxY = max(maxY, it.y)

            val old = boundCell[it.y]
            if (old == null || it.x < old.x) {
                boundCell[it.y] = it
            }
        }

        mRegion = Region(minX, maxX, minY, maxY)

        mBoundCell = boundCell.values.toSet()
    }

    override fun evolution(): List<Cell> {
        val newBound = mutableSetOf<Cell>()

        mBoundCell.forEach {
            it.state = 0
            newBound.addAll(it.neighbors.filter { neighbor ->  neighbor.isAlive() })
        }

        mBoundCell = newBound

        return mCells
    }
}