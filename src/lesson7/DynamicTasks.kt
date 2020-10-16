@file:Suppress("UNUSED_PARAMETER")

package lesson7

import java.io.File
import kotlin.math.min

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
    //Асимптотика O(N*M)
    //Ресурсоемкость O(N*M)

    val maxLine: String
    val minLine: String
    if (first.length >= second.length) {
        maxLine = first
        minLine = second
    } else {
        maxLine = second
        minLine = first
    }

    val mapOfSubstring = Array(minLine.length + 1) { Array(maxLine.length + 1) { 0 } }
    val indicesOfMaxSubstring = Array(minLine.length + 1) { Array(maxLine.length + 1) { -1 to -1 } }

    var maxNum = 0
    var maxPos = -1 to -1

    for (i in 1..minLine.length)
        for (j in 1..maxLine.length) {
            mapOfSubstring[i][j] =
                if (minLine[i - 1] == maxLine[j - 1]) {
                    indicesOfMaxSubstring[i][j] = (i - 1) to (j - 1)
                    mapOfSubstring[i - 1][j - 1] + 1
                } else {
                    if (mapOfSubstring[i - 1][j] >= mapOfSubstring[i][j - 1]) {
                        indicesOfMaxSubstring[i][j] = (i - 1) to j
                        mapOfSubstring[i - 1][j]
                    } else {
                        indicesOfMaxSubstring[i][j] = i to (j - 1)
                        mapOfSubstring[i][j - 1]
                    }
                }
            if (mapOfSubstring[i][j] > maxNum) {
                maxNum = mapOfSubstring[i][j]
                maxPos = i to j
            }
        }

    val sb = StringBuilder()
    var count = maxNum
    var prPos = maxPos
    while (count != 0) {
        if (maxLine[prPos.second - 1] == minLine[prPos.first - 1]) {
            sb.append(maxLine[prPos.second - 1])
            count--
        }
        prPos = indicesOfMaxSubstring[prPos.first][prPos.second]
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
    //Асимптотика O(N^2)
    //Ресурсоемкость O(N)
    if (list.isEmpty()) return list
    val d = Array(list.size) { 0 }
    val k = Array(list.size) { 0 }
    d[0] = 1
    k[0] = 0
    var maxD = 1
    var indMaxD = 0
    for (i in 1 until list.size) {
        var maxCurrD = 0
        var ind = i
        for (j in 0 until i)
            if (d[j] > maxCurrD && list[i] > list[j]) {
                maxCurrD = d[j]
                ind = j
            }
        d[i] = maxCurrD + 1
        k[i] = ind
        if (d[i] > maxD) {
            maxD = d[i]
            indMaxD = i
        }
    }
    var count = maxD
    val result = mutableListOf<Int>()
    var nextInd = indMaxD
    do {
        result.add(0, list[nextInd])
        nextInd = k[nextInd]
        count--
    } while (count != 0)
    return result
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
    //Асимптотика O(N)
    //Ресурсоемкость O(N)
    val board = File(inputName).readLines().map { it.split(' ').map { it1 -> it1.toInt() } }
    val height = board.size
    val width = board[0].size
    val d = Array(height) { IntArray(width) { 0 } }
    d[0][0] = board[0][0]
    for (i in 1 until height) d[i][0] = d[i - 1][0] + board[i][0]
    for (j in 1 until width) d[0][j] = d[0][j - 1] + board[0][j]

    for (i in 1 until height)
        for (j in 1 until width)
            d[i][j] = min(d[i - 1][j], min(d[i - 1][j - 1], d[i][j - 1])) + board[i][j]

    return d[height - 1][width - 1]
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5