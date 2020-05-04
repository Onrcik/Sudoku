package com.onurincik.sudoku.Oyun

class Hücre(
        val row: Int,
        val col: Int,
        var value: Int,
        var baslangıchücre: Boolean = false,
        var notlar: MutableSet<Int> = mutableSetOf<Int>()
)