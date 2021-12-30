package com.michael.demo.celllife.controller

import com.michael.demo.celllife.model.Cell
import com.michael.demo.celllife.model.Point2D
import com.michael.demo.celllife.vm.CellViewModelV2
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import java.net.URL
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

class MainController : IController {

    private var mScale = DEFAULT_SCALE
    private var mWidth = 0.0
    private var mHeight = 0.0
    private var mCenterX = 0.0
    private var mCenterY = 0.0

    private var mRunning = false
    private var mEditMode = false

    private val mTimer by lazy { Timer("evolution-timer") }

    private val mInitializeCell = arrayOf(
            Cell(0, 1), Cell(1, 0), Cell(-1, -1), Cell(0, -1), Cell(1, -1))

    private val mViewModel by lazy {
        CellViewModelV2().apply {
            initialize(mInitializeCell)
        }
    }

    private var mCells: Array<Cell> = mInitializeCell

    private val mEvolutionTask by lazy {
        Runnable {
            val now = System.currentTimeMillis()

            mCells = mViewModel.evolution()

            println("run evolution cells: ${mCells.size}, cost: ${System.currentTimeMillis() - now}ms")

            Platform.runLater {
                updateDraw()
            }
        }
    }

    @FXML
    var editPane: Pane? = null

    @FXML
    var controlPane: Pane? = null

    @FXML
    var reset: Button? = null

    @FXML
    var start: Button? = null

    @FXML
    var once: Button? = null

    @FXML
    var stop: Button? = null

    @FXML
    var canvas: Canvas? = null

    @FXML
    var startEdit: Button? = null

    @FXML
    var randomCell: Button? = null

    @FXML
    var clearEdit: Button? = null

    @FXML
    var completeEdit: Button? = null

    fun handleButtonAction(event: MouseEvent) {
        if (event.button != MouseButton.PRIMARY) {
            println("unsupported mouse event: $event")
            return
        }
        when (event.source) {

            startEdit -> startEdit()

            randomCell -> randomCell()

            clearEdit -> clearEdit()

            completeEdit -> completeEdit()

            reset -> resetCellEvolution()

            start -> startCellEvolution()

            once -> startCellEvolution(false)

            stop -> stopCellEvolution()

            canvas -> onCanvasClick(event.x, event.y)

            else -> println("unknown event: ${event.source}")
        }
    }

    private fun resetCellEvolution() {
        println("startCellEvolution")
        stopCellEvolution()
        mViewModel.initialize(mInitializeCell)
        mCells = mInitializeCell
        updateDraw()
    }

    private fun startCellEvolution(loop: Boolean = true) {
        if (mRunning) return
        println("startCellEvolution")

        startCellEvolutionInternal(loop)

        updateButtonState()
    }

    private fun startCellEvolutionInternal(loop: Boolean = true) {
        mRunning = true

        evolutionInternal(loop)
    }

    private fun evolutionInternal(loop: Boolean = true) {
        mTimer.schedule(object : TimerTask() {
            override fun run() {
                if (!mRunning) return

                mEvolutionTask.run()

                if (mCells.isNullOrEmpty()) {
                    Platform.runLater { stopCellEvolution() }
                }

                if (loop) {
                    evolutionInternal()
                } else {
                    Platform.runLater { stopCellEvolution() }
                }
            }

        }, DELAY)
    }

    private fun stopCellEvolution() {
        println("stopCellEvolution")

        stopCellEvolutionInternal()

        updateButtonState()
    }

    private fun stopCellEvolutionInternal() {
        mRunning = false
    }

    override fun onExit() {
        stopCellEvolution()
        mTimer.cancel()
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        updateButtonState()
        canvas?.let {
            mWidth = it.width
            mHeight = it.height
            mCenterX = mWidth / 2.0
            mCenterY = mHeight / 2.0
            mViewModel.updateRegion(getXIndex(0.0), getXIndex(mWidth), getYIndex(0.0), getYIndex(mHeight))
            updateDraw()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun drawLines(graphics: GraphicsContext, canvas: Canvas) {

        val centerX = mCenterX
        val centerY = mCenterY

        graphics.lineWidth = 1.0

        val step = getScaledStep()

        var x = centerX % step
        var total = (centerX / step).toInt()
        var count = 0
        while (x <= mWidth) {
            if (x > LONG_LINE && (total - count) % STEP_GROUP_COUNT == 0) {
                graphics.stroke = Color.BLACK
                graphics.strokeLine(x, 0.0, x, LONG_LINE)
                graphics.setLineDashes(*getScaledLineDash())
                graphics.stroke = Color.GRAY
                graphics.strokeLine(x, LONG_LINE + getLineDashLength(), x, mHeight)
            } else {
                graphics.setLineDashes(0.0)
                graphics.stroke = Color.BLACK
                graphics.strokeLine(x, 0.0, x, SHORT_LINE)
            }
            x += step
            count++
        }

        var y = centerY % step
        total = (mCenterY / step).toInt()
        count = 0
        while (y <= mHeight) {
            if (y > LONG_LINE && (total - count) % STEP_GROUP_COUNT == 0) {
                graphics.stroke = Color.BLACK
                graphics.strokeLine(0.0, y, LONG_LINE, y)
                graphics.setLineDashes(*getScaledLineDash())
                graphics.stroke = Color.GRAY
                graphics.strokeLine(LONG_LINE + getLineDashLength(), y, mWidth, y)
            } else {
                graphics.setLineDashes(0.0)
                graphics.stroke = Color.BLACK
                graphics.strokeLine(0.0, y, SHORT_LINE, y)
            }
            y += step
            count++
        }
    }

    private fun drawCells(graphics: GraphicsContext, canvas: Canvas) {
        if (mCells.isNullOrEmpty()) return
        mCells.forEach {
            drawCell(graphics, canvas, it)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun drawCell(graphics: GraphicsContext, canvas: Canvas, cell: Cell) {
        val cellSize = getScaledCellSize()
        graphics.fill = Color.web(cell.color)
        transformCoordinate(cell, mCenterX, mCenterY, cellSize, sPoint2D)
        graphics.fillRect(sPoint2D.x, sPoint2D.y, cellSize, cellSize)
    }

    private fun updateDraw() {
        val now = System.currentTimeMillis()
        canvas?.graphicsContext2D?.let {
            it.clearRect(0.0, 0.0, canvas!!.width, canvas!!.height)
//            it.fill = Color.RED
//            val r = 10.0
//            it.fillOval(mCenterX - r / 2.0, mCenterY - r / 2.0, r, r)
            it.stroke = Color.RED
            it.lineWidth = 1.0
            it.strokeLine(mCenterX, 0.0, mCenterX, mHeight)
            it.strokeLine(0.0, mCenterY, mWidth, mCenterY)
            drawLines(it, canvas!!)
            drawCells(it, canvas!!)
        }
        println("draw cost ${System.currentTimeMillis() - now}ms")
    }

    fun handleScrollAction(event: ScrollEvent) {
        val dy = event.textDeltaY
        mScale += SCROLL_SCALE_FACTOR * dy / mHeight
        if (mScale <= MIN_SCALE) {
            mScale = MIN_SCALE
        }
        if (mScale >= MAX_SCALE) {
            mScale = MAX_SCALE
        }
//        println("scale: ${mScale}, event: $event")
        mViewModel.updateRegion(getXIndex(0.0), getXIndex(mWidth), getYIndex(0.0), getYIndex(mHeight))
        updateDraw()
    }

    private fun getScaledStep() = DEFAULT_STEP_SIZE * mScale

    private fun getScaledLineDash() = doubleArrayOf(DASH_LINE_LENGTH, DASH_LENGTH)

    private fun getLineDashLength() = DASH_LINE_LENGTH + DASH_LENGTH

    private fun getScaledCellSize() = getScaledStep() * STEP_GROUP_COUNT

    private fun updateButtonState() {
        Platform.runLater {
            editPane?.isDisable = mRunning
            controlPane?.isDisable = mEditMode
            if (!mEditMode) {
                stop?.isDisable = !mRunning
                start?.isDisable = mRunning
                once?.isDisable = mRunning
                startEdit?.isDisable = false
                clearEdit?.isDisable = true
                randomCell?.isDisable = true
                completeEdit?.isDisable = true
            } else {
                startEdit?.isDisable = true
                clearEdit?.isDisable = false
                randomCell?.isDisable = false
                completeEdit?.isDisable = false
            }

        }
    }

    private fun startEdit() {
        stopCellEvolutionInternal()
        mEditMode = true
        mCells = emptyArray()
        updateDraw()
        updateButtonState()
    }

    private fun completeEdit() {
        mEditMode = false
        if (mCells.isEmpty()) {
            mCells = mInitializeCell
        }
        mViewModel.initialize(mCells)
        updateDraw()
        updateButtonState()
    }

    private fun getXIndex(x: Double) = floor((x - mCenterX) / getScaledCellSize()).toInt()
    private fun getYIndex(y: Double) = ceil((mCenterY - y) / getScaledCellSize()).toInt()

    private fun onCanvasClick(x: Double, y: Double) {
        if (!mEditMode) return
        val xIndex = getXIndex(x)
        val yIndex = getYIndex(y)
        val cell = Cell(xIndex, yIndex)
        val cells = mCells.toMutableList()
        if (cells.contains(cell)) {
            cells.remove(cell)
        } else {
            cells.add(cell)
        }
        mCells = cells.toTypedArray()
        updateDraw()
    }

    private fun clearEdit() {
        mCells = emptyArray()
        updateDraw()
    }

    private fun randomCell() {
        val minX = (floor(0 - mCenterX) / getScaledCellSize()).toInt()
        val maxX = (floor(mWidth - mCenterX) / getScaledCellSize()).toInt()
        val minY = (ceil(mCenterY - mHeight) / getScaledCellSize()).toInt()
        val maxY = (ceil(mCenterY) / getScaledCellSize()).toInt()

        val max = (maxX - minX) * (maxY - minY)
        val minCount = (max * .1).toInt()
        val maxCount = (max * .3).toInt()

        val cells = mutableListOf<Cell>()
        var count = (minCount..maxCount).random()
        while (count >= 0) {
            val cell = Cell((minX..maxX).random(), (minY..maxY).random())
            if (!cells.contains(cell)) {
                cells.add(cell)
                count--
            }
        }

        mCells = cells.toTypedArray()

        updateDraw()
    }

    companion object {
        private const val DEFAULT_STEP_SIZE = 5.0
        private const val MIN_CELL_SIZE = 3.0
        private const val MAX_CELL_SIZE = 50.0

        private var THRESHOLD = 1000 * Double.MIN_VALUE

        private val sPoint2D = Point2D(0.0, 0.0)

        const val SHORT_LINE = 5.0

        const val LONG_LINE = 10.0
        const val DASH_LINE_LENGTH = 1.0
        const val DASH_LENGTH = 5.0

        const val STEP_GROUP_COUNT = 5

        const val DEFAULT_SCALE = 1.0

        const val SCROLL_SCALE_FACTOR = 2

        const val MIN_SCALE = MIN_CELL_SIZE / (DEFAULT_STEP_SIZE * STEP_GROUP_COUNT)
        const val MAX_SCALE = MAX_CELL_SIZE / (DEFAULT_STEP_SIZE * STEP_GROUP_COUNT)

        const val DELAY = 100L

        fun isZero(number: Number): Boolean {
            println("number: $number, threshold: $THRESHOLD")
            return abs(number.toDouble()) <= THRESHOLD
        }

        fun transformCoordinate(cell: Cell, centerX: Double, centerY: Double,
                                cellSize: Double, out: Point2D) {
            out.x = centerX + cell.x * cellSize
            out.y = centerY - cell.y * cellSize
        }
    }
}