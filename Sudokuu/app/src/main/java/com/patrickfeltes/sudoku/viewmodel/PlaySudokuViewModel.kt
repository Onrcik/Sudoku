package com.patrickfeltes.sudoku.viewmodel

import android.arch.lifecycle.ViewModel
import com.patrickfeltes.sudoku.game.SudokuGame

class PlaySudokuViewModel : ViewModel() {
    val sudokuGame = SudokuGame()
}