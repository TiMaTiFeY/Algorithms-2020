package lesson4

import java.util.*
import kotlin.collections.ArrayDeque

/**
 * Префиксное дерево для строк
 */
class KtTrie : AbstractMutableSet<String>(), MutableSet<String> {

    private class Node {
        val children: MutableMap<Char, Node> = linkedMapOf()
    }

    private var root = Node()

    private var needDeleteNode = root
    private var needDeleteChar: Char = '\u0000'

    override var size: Int = 0
        private set

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

    private fun findNode(element: String): Node? {
        var current = root
        needDeleteChar = element[0]
        for (char in element) {
            if (current.children.keys.size > 1) {
                needDeleteNode = current
                needDeleteChar = char
            }
            current = current.children[char] ?: return null
        }
        if (current.children.keys.size > 1) {
            needDeleteNode = current
            needDeleteChar = 0.toChar()
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node()
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        findNode(element) ?: return false
        if (needDeleteNode.children.remove(needDeleteChar) != null) {
            size--
            return true
        }
        return false
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    override fun iterator(): MutableIterator<String> = TrieIterator()

    inner class TrieIterator internal constructor() : MutableIterator<String> {

        private val stack = ArrayDeque<Node>()
        private var current = root
        private val currentString = StringBuilder()
        private val setOfNextCharsByNode = mutableMapOf<Node, Set<Char>>()
        private val lengthOnNode = mutableMapOf<Node, Int>()
        private var lastNext: String? = null
        private var count = 0

        //Производительность O(1)
        //Ресурсоемкость O(1)
        override fun hasNext(): Boolean = count < size

        //Производительность O(N)
        //Ресурсоемкость O(N)
        override fun next(): String {
            while (!current.children.keys.contains(0.toChar()) && current.children.keys.size != 0) {
                //Если "развилка"
                if (current.children.keys.size > 1) {
                    stack.add(current)
                }
                if (!setOfNextCharsByNode.containsKey(current)) {
                    setOfNextCharsByNode[current] = current.children.keys
                }
                val char = setOfNextCharsByNode[current]!!.first()
                setOfNextCharsByNode[current] = setOfNextCharsByNode[current]!! - char
                currentString.append(char)
                lengthOnNode[current] = currentString.length - 1
                current = current.children[char]!!
            }
            if (!hasNext()) throw NoSuchElementException()
            val result = currentString.toString()

            if (current.children.keys.size > 1) {
                //Не на нижнем уровне: возвращаем строку и "курсор" сдвигаем на следующую ветку
                if (!setOfNextCharsByNode.containsKey(current)) {
                    setOfNextCharsByNode[current] = (current.children.keys - 0.toChar())
                }
                val char = setOfNextCharsByNode[current]!!.first()
                setOfNextCharsByNode[current] = setOfNextCharsByNode[current]!! - char
                //Пушим в стэк, т.к. в while не запушили current (current.children.keys.size != 0)
                if (setOfNextCharsByNode[current]?.isNotEmpty()!!) {
                    stack.add(current)
                }
                currentString.append(char)
                lengthOnNode[current] = currentString.length - 1
                current = current.children[char]!!
            } else {
                //Нижний уровень дерева: возвращаем строку и "курсор" передвигаем на последнюю "развилку"
                if (stack.isNotEmpty()) {
                    current = stack.last()
                    val char = setOfNextCharsByNode[current]!!.first()
                    setOfNextCharsByNode[current] = setOfNextCharsByNode[current]!! - char
                    currentString.delete(lengthOnNode[current]!!, currentString.length)
                    //Если у данной "развилки" все ветки пройдены
                    if (setOfNextCharsByNode[current]!!.isEmpty()) {
                        stack.removeLast()
                    }
                    currentString.append(char)
                    current = current.children[char]!!
                }
            }
            lastNext = result
            count++
            return result
        }

        //Производительность O(N) - зависит от длины последнего удаленного элемента
        //Ресурсоемкость O(1)
        override fun remove() {
            if (lastNext == null) throw IllegalStateException()
            if (remove(lastNext!!)) count--
            lastNext = null
        }
    }
}