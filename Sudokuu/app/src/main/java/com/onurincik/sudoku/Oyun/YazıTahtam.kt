package com.onurincik.sudoku.Oyun

class YazıTahtam(val size: Int, val hücres: List<Hücre>) {
    fun getCell(row: Int, col: Int) = hücres[row * size + col]
}