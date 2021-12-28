package com.michael.demo.celllife.ext

import javafx.scene.paint.Color

class ColorExt {

    companion object {
        fun parse(color: Int): Color {
            return Color.web(color.toString(16))
        }
    }
}