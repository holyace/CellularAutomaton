package com.michael.demo.celllife.vm

import com.michael.demo.celllife.model.Cell

interface ICellViewModel {

    fun initialize(cells: List<Cell>)

    fun updateRegion(minX: Int, maxX: Int, minY: Int, maxY: Int)

    fun evolution(): List<Cell>
}