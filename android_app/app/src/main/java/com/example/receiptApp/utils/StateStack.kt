package com.example.receiptApp.utils


/**
 * State stack
 *  i needed a stack with some particular functionalities, so i created this
 */
class StateStack<T>
{
    private val stack: MutableList<T> = mutableListOf()

    fun push(value: T) = stack.add(value)

    fun pop() = stack.removeLast()

    fun peek() = stack[stack.lastIndex]

    fun peekPrevious() = stack.getOrNull(stack.lastIndex - 1)

    fun clear() = stack.clear()

    override fun toString(): String
    {
        return "${stack.map { it.toString() }}"
    }
}