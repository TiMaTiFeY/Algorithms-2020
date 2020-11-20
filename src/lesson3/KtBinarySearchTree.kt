package lesson3

import java.lang.IllegalArgumentException
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayDeque
import kotlin.math.max

// attention: Comparable is supported but Comparator is not
class KtBinarySearchTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private class Node<T>(
        val value: T
    ) {
        var left: Node<T>? = null
        var right: Node<T>? = null
        var parent: Node<T>? = null
    }

    private var root: Node<T>? = null

    override var size = 0
        private set

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    /**
     * Добавление элемента в дерево
     *
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     *
     * Спецификация: [java.util.Set.add] (Ctrl+Click по add)
     *
     * Пример
     */
    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
                newNode.parent = closest
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
                newNode.parent = closest
            }
        }
        size++
        return true
    }

    /**
     * Обмен поддерревьев с корнями в узлах {@code nodeA} и {@code nodeB}
     */
    private fun swap(nodeA: Node<T>, nodeB: Node<T>?) {
        when {
            nodeA.parent == null /* nodeA - корень */ -> root = nodeB
            nodeA == nodeA.parent!!.right /* nodeA - правый потомок своего родителя */ -> nodeA.parent!!.right = nodeB
            else /* nodeA - левый потомок своего родителя */ -> nodeA.parent!!.left = nodeB
        }
        if (nodeB != null) nodeA.parent = nodeB.parent //узел А перемещаем на место узла B
    }

    /**
     * Удаление элемента из дерева
     *
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     * (в Котлине тип параметера изменён с Object на тип хранимых в дереве данных)
     *
     * Средняя
     */

    override fun remove(element: T): Boolean {
        //Производительность O(N) - полностью зависит от find(). Т.к. дерево без балансировки, то за линию
        //Ресурсоемкость O(1)
        val closestElement = find(element) ?: return false
        if (closestElement.value != element) return false
        when {
            closestElement.left == null -> swap(closestElement, closestElement.right) //"забываем" closestElement
            closestElement.right == null -> swap(closestElement, closestElement.left) //"забываем" closestElement
            else /* closestElement имеет 2 потомка */ -> {
                //взято из https://ru.wikipedia.org/wiki/Двоичное_дерево_поиска
                var swapNode = closestElement.right
                while (swapNode!!.left != null) swapNode = swapNode.left
                //сохраняем правое поддерево, если оно есть
                if (swapNode.parent != closestElement) {
                    swap(swapNode, swapNode.right)
                    swapNode.right = closestElement.right
                    swapNode.right!!.parent = swapNode
                }
                swap(closestElement, swapNode)
                swapNode.left = closestElement.left
                swapNode.left!!.parent = swapNode
            }
        }
        size--
        return true
    }

    override fun comparator(): Comparator<in T>? =
        null

    override fun iterator(): MutableIterator<T> =
        BinarySearchTreeIterator()

    inner class BinarySearchTreeIterator internal constructor() : MutableIterator<T> {

        private val stack = ArrayDeque<Node<T>?>()
        private var current = root
        private var lastNext: Node<T>? = null

        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         *
         * Спецификация: [java.util.Iterator.hasNext] (Ctrl+Click по hasNext)
         *
         * Средняя
         */
        //O(1)
        override fun hasNext(): Boolean = stack.isNotEmpty() || current != null

        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         *
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         *
         * Спецификация: [java.util.Iterator.next] (Ctrl+Click по next)
         *
         * Средняя
         */
        override fun next(): T {
            //Производительность O(N) - полностью зависит от высоты дерева. Т.к. дерево без балансировки, то
            // в худшем случаи за линию.
            //Ресурсоемкость O(N)
            while (current != null) {
                stack.add(current)
                current = current?.left
            }
            if (!hasNext()) throw NoSuchElementException()
            current = stack.removeLast()
            val value = current?.value
            lastNext = current!!
            current = current?.right
            return value!!
        }

        /**
         * Удаление предыдущего элемента
         *
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         *
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         *
         * Спецификация: [java.util.Iterator.remove] (Ctrl+Click по remove)
         *
         * Сложная
         */
        override fun remove() {
            //Производительность O(N) - полностью зависит от find(). Т.к. дерево без балансировки, то за линию
            //Ресурсоемкость O(1)
            if (lastNext == null) throw IllegalStateException()
            remove(lastNext!!.value)
            lastNext = null
        }

    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.subSet] (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
//        val listElements = mutableListOf<T>()
//        if (fromElement == toElement) return Collections.emptySortedSet()
//        var addingHadStarted = false
//        for (element in this) {
//            if (element >= fromElement && element < toElement) {
//                addingHadStarted = true
//                listElements.add(element)
//            } else {
//                if (addingHadStarted) throw IllegalArgumentException()
//            }
//        }
//        return listElements.toSortedSet()
        TODO()
    }

    /**
     * Подмножество всех элементов строго меньше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.headSet] (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.tailSet] (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        TODO()
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }

    override fun height(): Int =
        height(root)

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

}