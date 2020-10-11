@file:Suppress("UNUSED_PARAMETER")

package lesson7

/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
 * При сравнении подстрок, регистр символов *имеет* значение.
 */
fun longestCommonSubSequence(first: String, second: String): String {
    val mapOfSubstring = mutableListOf<MutableList<Pair<Int, Pair<Int, Int>>>>()
    val maxLine: String
    val minLine: String
    if (first.length >= second.length) {
        maxLine = first
        minLine = second
    } else {
        maxLine = second
        minLine = first
    }
    var maxNum = 0
    var maxPos = -1 to -1
    for (i in 0..minLine.length) {
        val line = mutableListOf<Pair<Int, Pair<Int, Int>>>()
        for (j in 0..maxLine.length)
            line.add(
                if (i != 0 && j != 0 && minLine[i - 1] == maxLine[j - 1]) {
                    var topFound = 1 to (-1 to -1)
                    mainLoop@ for (deltaI in 1 until i)
                        for (deltaJ in 1 until j) {
                            if (mapOfSubstring[i - deltaI][j - deltaJ].first != 0) {
                                if (mapOfSubstring[i - deltaI][j - deltaJ].first >= topFound.first) {
                                    topFound =
                                        mapOfSubstring[i - deltaI][j - deltaJ].first + 1 to ((i - deltaI) to (j - deltaJ))
                                }
                                if (topFound.first - 1 == maxNum) break@mainLoop
                            }
                        }
                    if (topFound.first > maxNum) {
                        maxNum = topFound.first
                        maxPos = i to j
                    }
                    topFound
                } else 0 to Pair(-1, -1)
            )
        mapOfSubstring.add(line)
    }
    val sb = StringBuilder()
    var pr = maxNum
    var prPos = maxPos
    while (pr != 0) {
        sb.append(maxLine[prPos.second - 1])
        prPos = mapOfSubstring[prPos.first][prPos.second].second
        pr--
    }
    sb.reverse()
    return sb.toString()
}

/**
 * Наибольшая возрастающая подпоследовательность
 * Сложная
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 */
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    TODO()
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Средняя
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 */
fun shortestPathOnField(inputName: String): Int {
    TODO()
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5