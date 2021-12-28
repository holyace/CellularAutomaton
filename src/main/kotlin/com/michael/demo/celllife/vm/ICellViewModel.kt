package com.michael.demo.celllife.vm

import com.michael.demo.celllife.model.Cell

interface ICellViewModel {

    fun initialize(cells: Array<Cell>)

    fun evolution(): Array<Cell>
}