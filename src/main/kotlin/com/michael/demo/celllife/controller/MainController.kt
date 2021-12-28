package com.michael.demo.celllife.controller

import com.michael.demo.celllife.model.Cell
import com.michael.demo.celllife.model.Point2D
import com.michael.demo.celllife.vm.CellViewModel
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
        CellViewModel().apply {
            initialize(mInitializeCell)
        }
    }

    private var mCells: Array<Cell> = mInitializeCell

    private val mEvolutionTask by lazy {
        Runnable {
            mCells = mViewModel.evolution()

            println("run evolution cells: ${mCells?.size}")

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
    var completeEdit: Button? = null

    fun handleButtonAction(event: MouseEvent) {
        if (event.button != MouseButton.PRIMARY) {
            println("unsupported mouse event: $event")
            return
        }
        when (event.source) {

            startEdit -> startEdit()

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
            updateDraw()
        }
    }

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
                graphics.setLineDashes(LINE_DASH)
                graphics.stroke = Color.GRAY
                graphics.strokeLine(x, LONG_LINE + LINE_DASH, x, mHeight)
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
                graphics.setLineDashes(LINE_DASH)
                graphics.stroke = Color.GRAY
                graphics.strokeLine(LONG_LINE + LINE_DASH, y, mWidth, y)
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

    private fun drawCell(graphics: GraphicsContext, canvas: Canvas, cell: Cell) {
        val cellSize = getScaledCellSize()
        graphics.fill = Color.web(cell.color)
        transformCoordinate(cell, mCenterX, mCenterY, cellSize, sPoint2D)
        graphics.fillRect(sPoint2D.x, sPoint2D.y, cellSize, cellSize)
    }

    private fun updateDraw() {
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
        updateDraw()
    }

    private fun getScaledStep() = DEFAULT_STEP_SIZE * mScale

    private fun getScaledCellSize() = getScaledStep() * STEP_GROUP_COUNT

    private fun updateButtonState() {
        Platform.runLater {
            editPane?.isDisable = mRunning
            controlPane?.isDisable = mEditMode
            if (!mEditMode) {
                stop?.isDisable = !mRunning
                start?.isDisable = mRunning
                startEdit?.isDisable = false
                completeEdit?.isDisable = true
            } else {
                startEdit?.isDisable = true
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
            mViewModel.initialize(mInitializeCell)
        } else {
            mViewModel.initialize(mCells)
        }
        updateDraw()
        updateButtonState()
    }

    private fun onCanvasClick(x: Double, y: Double) {
        if (!mEditMode) return
        val xIndex = floor((x - mCenterX) / getScaledCellSize())
        val yIndex = floor((y - mCenterY) / getScaledCellSize())
        mCells = mCells.toMutableList().apply {
            add(Cell(xIndex.toInt(), yIndex.toInt()))
        }.toTypedArray()
        updateDraw()
    }

    companion object {
        private const val DEFAULT_STEP_SIZE = 5.0
        private const val MIN_CELL_SIZE = 10.0
        private const val MAX_CELL_SIZE = 50.0

        private var THRESHOLD = 1000 * Double.MIN_VALUE

        private val sPoint2D = Point2D(0.0, 0.0)

        const val SHORT_LINE = 5.0

        const val LONG_LINE = 10.0
        const val LINE_DASH = 5.0

        const val STEP_GROUP_COUNT = 5

        const val DEFAULT_SCALE = 1.0

        const val SCROLL_SCALE_FACTOR = 2

        const val MIN_SCALE = MIN_CELL_SIZE / (DEFAULT_STEP_SIZE * STEP_GROUP_COUNT)
        const val MAX_SCALE = MAX_CELL_SIZE / (DEFAULT_STEP_SIZE * STEP_GROUP_COUNT)

        const val DELAY = 300L

        fun isZero(number: Number): Boolean {
            println("number: $number, threshold: $THRESHOLD")
            return abs(number.toDouble()) <= THRESHOLD
        }

        fun transformCoordinate(cell: Cell, centerX: Double, centerY: Double,
                                cellSize: Double, out: Point2D) {
            out.x = centerX + cell.x * cellSize
            out.y = centerY + cell.y * cellSize
        }
    }
}