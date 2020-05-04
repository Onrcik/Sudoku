package com.onurincik.sudoku.görünüm.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.onurincik.sudoku.Oyun.Hücre
import kotlin.math.min

class SudokuBoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var sqrtSize = 3
    private var size = 9

    // these are set in onDraw
    private var numaraYaziPikseli = 0F
    private var notYaziPikseli = 0F

    private var secRow = 0
    private var secCol = 0

    private var listener: SudokuBoardView.OnTouchListener? = null

    private var hücres: List<Hücre>? = null

    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 4F
    }
//İç hücre çizgilerini ayarlayan renk
    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 2F
    }

    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#6ead3a")
    }

    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#efedef")
    }
//Yazıları ayarlayan renk kısmı
    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }

    private val startingCellTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        typeface = Typeface.DEFAULT_BOLD
    }

    private val noteTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLUE
    }

    private val startingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#acacac")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    override fun onDraw(canvas: Canvas) {
        updateMeasurements(width)
        fillCells(canvas)
        drawLines(canvas)
        drawText(canvas)
    }

    private fun updateMeasurements(width: Int) {
        numaraYaziPikseli = width / size.toFloat()
        notYaziPikseli = numaraYaziPikseli / sqrtSize.toFloat()
        noteTextPaint.textSize = numaraYaziPikseli / sqrtSize.toFloat()
        textPaint.textSize = numaraYaziPikseli / 1.5F
        startingCellTextPaint.textSize = numaraYaziPikseli / 1.5F
    }

    private fun fillCells(canvas: Canvas) {
        hücres?.forEach {
            val r = it.row
            val c = it.col

            if (it.baslangıchücre) {
                fillCell(canvas, r, c, startingCellPaint)
            } else if (r == secRow && c == secCol) {
                fillCell(canvas, r, c, selectedCellPaint)
            } else if (r == secRow || c == secCol) {
                fillCell(canvas, r, c, conflictingCellPaint)
            } else if (r / sqrtSize == secRow / sqrtSize && c / sqrtSize == secCol / sqrtSize) {
                fillCell(canvas, r, c, conflictingCellPaint)
            }
        }
    }

    private fun fillCell(canvas: Canvas, r: Int, c: Int, paint: Paint) {
        canvas.drawRect(c * numaraYaziPikseli, r * numaraYaziPikseli, (c + 1) * numaraYaziPikseli, (r + 1) * numaraYaziPikseli, paint)
    }

    private fun drawLines(canvas: Canvas) {
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), thickLinePaint)

        for (i in 1 until size) {
            val paintToUse = when (i % sqrtSize) {
                0 -> thickLinePaint
                else -> thinLinePaint
            }

            canvas.drawLine(
                    i * numaraYaziPikseli,
                    0F,
                    i * numaraYaziPikseli,
                    height.toFloat(),
                    paintToUse
            )

            canvas.drawLine(
                    0F,
                    i * numaraYaziPikseli,
                    width.toFloat(),
                    i * numaraYaziPikseli,
                    paintToUse
            )
        }
    }

    private fun drawText(canvas: Canvas) {
        hücres?.forEach { cell ->
            val value = cell.value

            if (value == 0) {
                // draw notes
                cell.notlar.forEach { note ->
                    val rowInCell = (note - 1) / sqrtSize
                    val colInCell = (note - 1) % sqrtSize
                    val valueString = note.toString()

                    val textBounds = Rect()
                    noteTextPaint.getTextBounds(valueString, 0, valueString.length, textBounds)
                    val textWidth = noteTextPaint.measureText(valueString)
                    val textHeight = textBounds.height()
                    canvas.drawText(
                        valueString,
                        (cell.col * numaraYaziPikseli) + (colInCell * notYaziPikseli) + notYaziPikseli / 2 - textWidth / 2f,
                        (cell.row * numaraYaziPikseli) + (rowInCell * notYaziPikseli) + notYaziPikseli / 2 + textHeight / 2f,
                        noteTextPaint
                    )
                }
            } else {
                val row = cell.row
                val col = cell.col
                val valueString = cell.value.toString()

                val paintToUse = if (cell.baslangıchücre) startingCellTextPaint else textPaint
                val textBounds = Rect()
                paintToUse.getTextBounds(valueString, 0, valueString.length, textBounds)
                val textWidth = paintToUse.measureText(valueString)
                val textHeight = textBounds.height()

                canvas.drawText(valueString, (col * numaraYaziPikseli) + numaraYaziPikseli / 2 - textWidth / 2,
                        (row * numaraYaziPikseli) + numaraYaziPikseli / 2 + textHeight / 2, paintToUse)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }
    }

    private fun handleTouchEvent(x: Float, y: Float) {
        val possibleSelectedRow = (y / numaraYaziPikseli).toInt()
        val possibleSelectedCol = (x / numaraYaziPikseli).toInt()
        listener?.onCellTouched(possibleSelectedRow, possibleSelectedCol)
    }

    fun updateSelectedCellUI(row: Int, col: Int) {
        secRow = row
        secCol = col
        invalidate()
    }

    fun updateCells(hücres: List<Hücre>) {
        this.hücres = hücres
        invalidate()
    }

    fun registerListener(listener: SudokuBoardView.OnTouchListener) {
        this.listener = listener
    }

    interface OnTouchListener {
        fun onCellTouched(row: Int, col: Int)
    }
}
