package lesson5

import java.util.*


/**
 * Множество(таблица) с открытой адресацией на 2^bits элементов без возможности роста.
 */
class KtOpenAddressingSet<T : Any>(private val bits: Int) : AbstractMutableSet<T>() {
    init {
        require(bits in 2..31)
    }

    private enum class Cell { DELETED }

    private val capacity = 1 shl bits

    private val storage = Array<Any?>(capacity) { null }

    override var size: Int = 0

    /**
     * Индекс в таблице, начиная с которого следует искать данный элемент
     */
    private fun T.startingIndex(): Int {
        return hashCode() and (0x7FFFFFFF shr (31 - bits))
    }

    /**
     * Проверка, входит ли данный элемент в таблицу
     */
    override fun contains(element: T): Boolean {
        var index = element.startingIndex()
        var current = storage[index]
        while (current != null) {
            if (current == element) {
                return true
            }
            index = (index + 1) % capacity
            current = storage[index]
        }
        return false
    }

    /**
     * Добавление элемента в таблицу.
     *
     * Не делает ничего и возвращает false, если такой же элемент уже есть в таблице.
     * В противном случае вставляет элемент в таблицу и возвращает true.
     *
     * Бросает исключение (IllegalStateException) в случае переполнения таблицы.
     * Обычно Set не предполагает ограничения на размер и подобных контрактов,
     * но в данном случае это было введено для упрощения кода.
     */
    override fun add(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        while (current != null && current != Cell.DELETED) {
            if (current == element) {
                return false
            }
            index = (index + 1) % capacity
            check(index != startingIndex) { "Table is full" }
            current = storage[index]
        }
        storage[index] = element
        size++
        return true
    }

    /**
     * Удаление элемента из таблицы
     *
     * Если элемент есть в таблица, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     *
     * Средняя
     */
    override fun remove(element: T): Boolean {
        //Производительность O(N)
        //Ресурсоемкость O(1)
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        while (current != null && current != Cell.DELETED) {
            if (current == element) {
                storage[index] = Cell.DELETED
                size--
                return true
            }
            index = (index + 1) % capacity
            current = storage[index]
        }
        return false
    }

    /**
     * Создание итератора для обхода таблицы
     *
     * Не забываем, что итератор должен поддерживать функции next(), hasNext(),
     * и опционально функцию remove()
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Средняя (сложная, если поддержан и remove тоже)
     */
    override fun iterator(): MutableIterator<T> = OpenAddressingSetIterator()

    inner class OpenAddressingSetIterator internal constructor() : MutableIterator<T> {
        private var lastNext: Any? = null
        private var index = 0
        private var count = 0

        //Производительность O(1)
        //Ресурсоемкость O(1)
        override fun hasNext(): Boolean = count < size

        //Производительность O(N)
        //Ресурсоемкость O(1)
        @Suppress("UNCHECKED_CAST")
        override fun next(): T {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            while (storage[index] == null || storage[index] == Cell.DELETED) {
                index++
            }
            lastNext = storage[index]
            count++
            index++
            return lastNext as T
        }

        //Производительность O(1)
        //Ресурсоемкость O(1)
        override fun remove() {
            checkNotNull(lastNext)
            storage[index - 1] = Cell.DELETED
            lastNext = null
            size--
            count--
        }
    }
}