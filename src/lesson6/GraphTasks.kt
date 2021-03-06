@file:Suppress("UNUSED_PARAMETER", "unused")
package lesson6

import lesson6.Graph.*
import lesson6.impl.GraphBuilder

/**
 * Эйлеров цикл.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
 * Если в графе нет Эйлеровых циклов, вернуть пустой список.
 * Соседние дуги в списке-результате должны быть инцидентны друг другу,
 * а первая дуга в списке инцидентна последней.
 * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
 * Веса дуг никак не учитываются.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
 *
 * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
 * связного графа ровно по одному разу
 */
/*fun Graph.dfs(start: Vertex, visited: MutableSet<Vertex>) {
    getConnections(start)
        .filter { it.value.end !in visited }
        .forEach {
            visited += start
            dfs(it.key, visited)
        }
}

fun Graph.deg(vertex: Vertex): Int = this.getConnections(vertex).size

fun checkForEulerPath(graph: Graph): Boolean {
    val oddVertex = graph.vertices.map { if (graph.deg(it) % 2 != 0) 1 else 0 }.sum()
    if (oddVertex > 2) {
        return false
    }
    val visited = mutableSetOf<Vertex>()
    for (vertex in graph.vertices) {
        if (graph.deg(vertex) > 0) {
            graph.dfs(vertex, visited)
            break
        }
    }
    for (vertex in graph.vertices) {
        if (graph.deg(vertex) > 0 && !visited.contains(vertex)) {
            return false
        }
    }
    return true
}*/

fun Graph.findEulerLoop(): List<Edge> {
//    if (this.vertices.size < 3 || !checkForEulerPath(this)) return listOf()
//    var v: Vertex? = null
//    for (vertex in this.vertices) {
//        if (this.deg(vertex) % 2 != 0) {
//            v = vertex
//            break
//        }
//    }
//    if (v == null) return listOf()
//    val visitedEdges = mutableListOf<Edge>()
//    val stack = ArrayDeque<Vertex>()
//    stack.add(v)
//    while (stack.isNotEmpty()) {
//        val w = stack.last()
//        var foundEdge = false
//        for (edge in this.getConnections(w).values) {
//            if (!visitedEdges.contains(edge)) {
//                stack.add(edge.end)
//                visitedEdges.add(edge)
//                foundEdge = true
//                break
//            }
//        }
//        if (!foundEdge) {
//            stack.removeLast()
//        }
//    }
//    println(visitedEdges)
//    return visitedEdges
    TODO()
}

/**
 * Минимальное остовное дерево.
 * Средняя
 *
 * Дан связный граф (получатель). Найти по нему минимальное остовное дерево.
 * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
 * вернуть любое из них. Веса дуг не учитывать.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ:
 *
 *      G    H
 *      |    |
 * A -- B -- C -- D
 * |    |    |
 * E    F    I
 * |
 * J ------------ K
 */
fun Graph.minimumSpanningTree(): Graph {
    //Производительность O(E*log(V))
    //Ресурсоемкость O(E+V)
    val result = GraphBuilder()
    val root = vertices.firstOrNull() ?: return result.build()
    primsAlg(result, mutableSetOf(root))
    return result.build()
}

private fun Graph.primsAlg(gb: GraphBuilder, vertexes: MutableSet<Vertex>) {
    //Алгоритм Прима
    var minEdge: Edge? = null
    vertexes.forEach { vertex ->
        val probablyMinEdge = getConnections(vertex)
            .filter { it.value.begin !in vertexes || it.value.end !in vertexes }
            .minByOrNull { it.value.weight }?.value
        if (minEdge == null || probablyMinEdge != null && minEdge!!.weight > probablyMinEdge.weight) {
            minEdge = probablyMinEdge
        }
    }
    if (minEdge != null) {
        if (minEdge!!.begin in vertexes) {
            vertexes.add(minEdge!!.end)
            gb.addVertex(minEdge!!.end.name)
        } else {
            vertexes.add(minEdge!!.begin)
            gb.addVertex(minEdge!!.begin.name)
        }
        gb.addConnection(minEdge!!)
        return primsAlg(gb, vertexes)
    } else {
        return
    }
}

/**
 * Максимальное независимое множество вершин в графе без циклов.
 * Сложная
 *
 * Дан граф без циклов (получатель), например
 *
 *      G -- H -- J
 *      |
 * A -- B -- D
 * |         |
 * C -- F    I
 * |
 * E
 *
 * Найти в нём самое большое независимое множество вершин и вернуть его.
 * Никакая пара вершин в независимом множестве не должна быть связана ребром.
 *
 * Если самых больших множеств несколько, приоритет имеет то из них,
 * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
 *
 * В данном случае ответ (A, E, F, D, G, J)
 *
 * Если на входе граф с циклами, бросить IllegalArgumentException
 *
 * Эта задача может быть зачтена за пятый и шестой урок одновременно
 */
fun Graph.largestIndependentVertexSet(): Set<Vertex> {
    TODO()
}

/**
 * Наидлиннейший простой путь.
 * Сложная
 *
 * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
 * Простым считается путь, вершины в котором не повторяются.
 * Если таких путей несколько, вернуть любой из них.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ: A, E, J, K, D, C, H, G, B, F, I
 */
fun Graph.longestSimplePath(): Path {
    //Производительность O(W*E)
    //Ресурсоемкость O(W)
    //W (ways) - количество путей; E - количетсво рёбер каждой вершины
    var result = Path()
    val possiblePaths = ArrayDeque<Path>()
    vertices.forEach { possiblePaths.addLast(Path(it)) }
    while (possiblePaths.isNotEmpty()) {
        val currentPath = possiblePaths.removeLast()
        if (result.length < currentPath.length) {
            result = currentPath
        }
        val neighbours = getNeighbors(currentPath.vertices[currentPath.length])
        neighbours.forEach {
            if (it !in currentPath) {
                possiblePaths.addLast(Path(currentPath, this, it))
            }
        }
    }
    return result
}

/**
 * Балда
 * Сложная
 *
 * Задача хоть и не использует граф напрямую, но решение базируется на тех же алгоритмах -
 * поэтому задача присутствует в этом разделе
 *
 * В файле с именем inputName задана матрица из букв в следующем формате
 * (отдельные буквы в ряду разделены пробелами):
 *
 * И Т Ы Н
 * К Р А Н
 * А К В А
 *
 * В аргументе words содержится множество слов для поиска, например,
 * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
 *
 * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
 * и вернуть множество найденных слов. В данном случае:
 * ТРАВА, КРАН, АКВА, НАРТЫ
 *
 * И т Ы Н     И т ы Н
 * К р а Н     К р а н
 * А К в а     А К В А
 *
 * Все слова и буквы -- русские или английские, прописные.
 * В файле буквы разделены пробелами, строки -- переносами строк.
 * Остальные символы ни в файле, ни в словах не допускаются.
 */
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
    TODO()
}