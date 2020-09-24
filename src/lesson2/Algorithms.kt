@file:Suppress("UNUSED_PARAMETER")

package lesson2

import java.io.File

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    //Асимптотика O(N)
    //Ресурсоемкость O(N)
    val numbers = File(inputName).readLines().map { it.toInt() }
    val dif = mutableListOf<Int>()
    for (i in 0..numbers.size - 2) {
        dif.add(numbers[i + 1] - numbers[i])
    }
    //Алгоритм Кадана
    var ans = dif[0]
    var ansLeft = 0
    var ansRight = 0
    var sum = 0
    var minPosition = -1
    for (r in 0 until dif.size) {
        sum += dif[r]
        if (sum > ans) {
            ans = sum
            ansLeft = minPosition + 1
            ansRight = r
        }
        if (sum < 0) {
            sum = 0
            minPosition = r
        }
    }
    return Pair(ansLeft + 1, ansRight + 2)
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 *
 * Общий комментарий: решение из Википедии для этой задачи принимается,
 * но приветствуется попытка решить её самостоятельно.
 */
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    //Рекурентный способ из Википедии
    //Асимптотика O(N)
    //Ресурсоемкость O(1)
    var res = 1
    for (i in 2..menNumber) {
        res = (res + choiceInterval - 1) % i + 1
    }
    return res
}

/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 */
fun longestCommonSubstring(first: String, second: String): String {
    //Асимптотика O(first.length * second.length)
    //Ресурсоемкость O(first.length * second.length)
    val mapOfSubstring = mutableListOf<MutableList<Int>>()
    var maxNum = 0
    var maxPos = 0 to 0
    for (i in 0..first.length) {
        val line = mutableListOf<Int>()
        for (j in 0..second.length)
            line.add(
                if (!(i == 0 || j == 0) && first[i - 1] == second[j - 1]) {
                    val k = mapOfSubstring[i - 1][j - 1] + 1
                    if (k > maxNum) {
                        maxNum = k
                        maxPos = i to j
                    }
                    k
                } else 0
            )
        mapOfSubstring.add(line)
    }
    return first.substring(maxPos.first - maxNum, maxPos.first)
}

/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 */
fun calcPrimesNumber(limit: Int): Int {
    //Асимптотика O(Nlog(log(N)))
    //Ресурсоемкость O(N)
    if (limit <= 1) return 0
    if (limit == 2) return 1
    var count = limit - 1
    val n = mutableListOf<Boolean>()
    for (i in 1..limit) n.add(true)
    var k = 2
    while (k + 1 < limit) {
        var i = k - 1
        while (i + k < limit) {
            i += k
            if (n[i]) {
                count--
                n[i] = false
            }
        }
        k += 1
    }
    return count
}
