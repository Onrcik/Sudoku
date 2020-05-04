package com.onurincik.sudoku.Oyun

import android.arch.lifecycle.MutableLiveData

class SudokuOyun {

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Hücre>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()

    private var selectedRow = -1
    private var selectedCol = -1
    private var isTakingNotes = false

    private val yazıTahtam: YazıTahtam

    init {
        val cells = List(9 * 9) {i -> Hücre(i / 9, i % 9, i % 9)}
        cells[0].notlar = mutableSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        yazıTahtam = YazıTahtam(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(yazıTahtam.hücres)
        isTakingNotesLiveData.postValue(isTakingNotes)
    }

    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = yazıTahtam.getCell(selectedRow, selectedCol)
        if (cell.baslangıchücre) return

        if (isTakingNotes) {
            if (cell.notlar.contains(number)) {
                cell.notlar.remove(number)
            } else {
                cell.notlar.add(number)
            }
            highlightedKeysLiveData.postValue(cell.notlar)
        } else {
            cell.value = number
        }
        cellsLiveData.postValue(yazıTahtam.hücres)
    }


    fun updateSelectedCell(row: Int, col: Int) {
        val cell = yazıTahtam.getCell(row, col)
        if (!cell.baslangıchücre) {
            selectedRow = row
            selectedCol = col
            selectedCellLiveData.postValue(Pair(row, col))

            if (isTakingNotes) {
                highlightedKeysLiveData.postValue(cell.notlar)
            }
        }
    }

    fun changeNoteTakingState() {
        isTakingNotes = !isTakingNotes
        isTakingNotesLiveData.postValue(isTakingNotes)

        val curNotes = if (isTakingNotes) {
            yazıTahtam.getCell(selectedRow, selectedCol).notlar
        } else {
            setOf<Int>()
        }
        highlightedKeysLiveData.postValue(curNotes)
    }

    fun delete() {
        val cell = yazıTahtam.getCell(selectedRow, selectedCol)
        if (isTakingNotes) {
            cell.notlar.clear()
            highlightedKeysLiveData.postValue(setOf())
        } else {
            cell.value = 0
        }
        cellsLiveData.postValue(yazıTahtam.hücres)
    }
}
