package com.michael.demo.celllife.util

class AutoList<E>(private val defVal: E) : MutableList<E> {

    private var mList = mutableListOf<E>()

    override val size: Int
        get() = mList.size

    override fun contains(element: E) = mList.contains(element)

    override fun containsAll(elements: Collection<E>) = mList.containsAll(elements)

    override fun get(index: Int) = if (checkBound(index)) mList[index] else defVal

    override fun indexOf(element: E) = mList.indexOf(element)

    override fun isEmpty() = mList.isEmpty()

    override fun iterator() = mList.iterator()

    override fun lastIndexOf(element: E) = mList.lastIndexOf(element)

    override fun add(element: E) = mList.add(element)

    override fun add(index: Int, element: E) {
        if (checkBound(index)) {
            mList.add(index, element)
        } else {
            set(index, element)
        }
    }

    override fun addAll(index: Int, elements: Collection<E>) = mList.addAll(index, elements)

    override fun addAll(elements: Collection<E>) = mList.addAll(elements)

    override fun clear() = mList.clear()

    override fun listIterator() = mList.listIterator()

    override fun listIterator(index: Int) = mList.listIterator(index)

    override fun remove(element: E) = mList.remove(element)

    override fun removeAll(elements: Collection<E>) = mList.removeAll(elements)

    override fun removeAt(index: Int) = mList.removeAt(index)

    override fun retainAll(elements: Collection<E>) = mList.retainAll(elements)

    override fun set(index: Int, element: E): E {
        if (!checkBound(index)) {
            for (i in size..index) {
                add(defVal)
            }
        }
        return mList.set(index, element)
    }

    override fun subList(fromIndex: Int, toIndex: Int) = mList.subList(fromIndex, toIndex)

    private fun checkBound(index: Int) = index < size
}