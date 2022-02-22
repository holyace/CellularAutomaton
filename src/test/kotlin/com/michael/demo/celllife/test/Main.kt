package com.michael.demo.celllife.test

import com.michael.demo.celllife.util.AutoList

fun main(args: Array<String>) {
    Main().test()
}

class Main {

    fun test() {
        val list = AutoList(0)
        printList(list)
        list[3] = 3
        printList(list)
    }

    companion object {

        fun printList(list: List<Any?>) {
            if (list.isEmpty()) {
                println("empty")
                return
            }
            list.forEach {
                println(it.toString())
            }
        }
    }
}