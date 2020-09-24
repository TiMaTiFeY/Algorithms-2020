@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File

/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
 * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
 *
 * Пример:
 *
 * 01:15:19 PM
 * 07:26:57 AM
 * 10:00:03 AM
 * 07:56:14 PM
 * 01:15:19 PM
 * 12:40:31 AM
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 12:40:31 AM
 * 07:26:57 AM
 * 10:00:03 AM
 * 01:15:19 PM
 * 01:15:19 PM
 * 07:56:14 PM
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun sortTimes(inputName: String, outputName: String) {
    //Асимптотика O(NlogN)
    //Ресурсоемкость O(N)
    data class Time(
        val hours: Int,
        val minutes: Int,
        val seconds: Int
    )

    fun String.toTime(): Time {
        val timeFormat = Regex("""^\d\d:\d\d:\d\d [A|P]M$""")
        require(timeFormat.matches(this))
        val result = mutableListOf<Int>()

        val parts = this.split(' ')
        parts.first().split(':').forEach {
            result += it.toInt()
        }

        if (result[0] == 12) {
            if (parts.last() == "AM") result[0] = 0
        } else {
            if (parts.last() == "PM") result[0] += 12
        }

        return Time(result[0], result[1], result[2])
    }

    File(outputName).writeText(
        File(inputName).readLines().map { Pair(it, it.toTime()) }
            .sortedWith(compareBy({ it.second.hours }, { it.second.minutes }, { it.second.seconds }))
            .joinToString(separator = "\n") { it.first }
    )
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun sortAddresses(inputName: String, outputName: String) {
    //Асимптотика O(NlogN)
    //Ресурсоемкость O(N)
    data class Person(
        val lastname: String,
        val firstname: String,
    )

    data class Address(
        val streetName: String,
        val houseNumber: Int
    )

    fun String.toAddress(): Pair<Person, Address> {
        val addressFormat = Regex("""^\S+ \S+ - \S+ \d+$""")
        require(addressFormat.matches(this))
        val result = mutableListOf<String>()

        val parts = this.split(" - ")
        result += parts.first().split(' ')
        result += parts.last().split(' ')

        val person = Person(result[0], result[1])
        val address = Address(result[2], result[3].toInt())

        return person to address
    }

    val addresses = File(inputName).readLines().map { it.toAddress() }
        .sortedWith(compareBy(
            { it.second.streetName },
            { it.second.houseNumber },
            { it.first.lastname },
            { it.first.firstname }
        ))

    val outputStream = File(outputName).bufferedWriter()
    var previousAddress: Address? = null
    for (address in addresses) {
        if (previousAddress == null || address.second != previousAddress)
            outputStream.write(
                "${if (previousAddress != null) "\n" else ""}${address.second.streetName} " +
                        "${address.second.houseNumber} - ${address.first.lastname} ${address.first.firstname}"
            )
        else
            outputStream.write(", ${address.first.lastname} ${address.first.firstname}")
        previousAddress = address.second
    }
    outputStream.close()
}

/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */
fun sortTemperatures(inputName: String, outputName: String) {
    //Асимптотика O(N)
    //Ресурсоемкость O(1)
    val numberOfPossibleValues = 5000 - (-2730) + 1
    val countOfValues = mutableListOf<Int>()
    for (i in 1..numberOfPossibleValues) countOfValues.add(0)
    for (n in File(inputName).readLines()) {
        val index = ((n.toDouble() * 10).toInt() + 2730)
        countOfValues[index]++
    }
    val outputStream = File(outputName).bufferedWriter()
    for ((index, value) in countOfValues.withIndex()) {
        if (value != 0) {
            val n = (index - 2730) / 10.0
            for (i in 1..value) {
                outputStream.write("$n\n")
            }
        }
    }
    outputStream.close()
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 */
fun sortSequence(inputName: String, outputName: String) {
    //Асимптотика O(N)
    //Ресурсоемкость O(N)
    val numbers = File(inputName).readLines().map { it.toInt() }
    val mode = mutableMapOf<Int, Int>()
    for (n in numbers) mode[n] = mode.getOrDefault(n, 0) + 1
    var maxCountNum: Pair<Int?, Int?> = null to null
    for ((num, count) in mode.entries) {
        if (maxCountNum.second == null || maxCountNum.second!! < count)
            maxCountNum = num to count
        if (maxCountNum.second!! == count && num < maxCountNum.first!!)
            maxCountNum = num to count
    }
    val outputStream = File(outputName).bufferedWriter()
    for (num in numbers)
        if (num != maxCountNum.first)
            outputStream.write("$num\n")
    for (i in 1..maxCountNum.second!!)
        outputStream.write("${maxCountNum.first!!}\n")
    outputStream.close()
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 */
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    //Асимптотика O(NlogN)
    //Ресурсоемкость O(N)
    for (i in first.indices) second[i] = first[i]
    second.sort()
}

