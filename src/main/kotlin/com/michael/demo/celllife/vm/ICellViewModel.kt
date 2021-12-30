package com.michael.demo.celllife.vm

import com.michael.demo.celllife.model.Cell

interface ICellViewModel {

    fun initialize(cells: Array<Cell>)

    fun updateRegion(minX: Int, maxX: Int, minY: Int, maxY: Int)

    fun evolution(): Array<Cell>
}