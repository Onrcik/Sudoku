package com.onurincik.sudoku.modeligörüntüleme

import android.arch.lifecycle.ViewModel
import com.onurincik.sudoku.Oyun.SudokuOyun

class PlaySudokuViewModel : ViewModel() {
    val sudokuGame = SudokuOyun()
}