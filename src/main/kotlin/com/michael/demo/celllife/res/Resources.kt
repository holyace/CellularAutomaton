package com.michael.demo.celllife.res

import java.util.*

class Resources {

    companion object {

        private const val STRING_BUNDLE = "values"

        private val stringBundle = ResourceBundle.getBundle(STRING_BUNDLE)

        fun getString(name: String): String = stringBundle.getString(name)
    }
}