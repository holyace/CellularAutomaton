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

        val region = Region()

        val boundCell = mutableMapOf<Int, Cell>()

        mCells.clear()

        cells.forEach {
            it.state = 1
            it.neighbors = findNeighbors(it, cells)

            mCells.add(it)

            region.rx.min = min(region.rx.min, it.x)
            region.rx.max = max(region.rx.max, it.x)

            region.ry.min = min(region.ry.min, it.y)
            region.ry.max = max(region.ry.max, it.y)

            val old = boundCell[it.y]
            if (old == null || it.x < old.x) {
                boundCell[it.y] = it
            }
        }

        mRegion = region

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