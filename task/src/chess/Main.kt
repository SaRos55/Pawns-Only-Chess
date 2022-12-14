package chess

import kotlin.math.abs

fun main() {
    val chessboard = MutableList(8) { MutableList(8) { ' ' } }
    for (files in chessboard) {
        files[1] = 'W'
        files[6] = 'B'
    }
    println("Pawns-Only Chess")
    println("First Player's name:")
    val firstPlayerName = readln()
    println("Second Player's name:")
    val secondPlayerName = readln()
    printChessboard(chessboard)
    var firstTurn = true
    var enPassantB = false
    var enPassantW = false
    var xPassant = 0
    var yPassant = 0
    do {
        println(
            "${if (firstTurn) firstPlayerName else secondPlayerName}'s turn:"
        )
        val input = readln()
        if (input == "exit") break
        val regex = Regex("[a-h][1-8][a-h][1-8]")
        val coordinates = chessToList(input)
        if (!regex.matches(input)) {
            println("Invalid Input")
            continue
        }
        val x1 = coordinates[0].digitToInt()
        val y1 = coordinates[1].digitToInt()
        val x2 = coordinates[2].digitToInt()
        val y2 = coordinates[3].digitToInt()
        if (!((firstTurn && chessboard[x1][y1] == 'W') ||
                    (!firstTurn && chessboard[x1][y1] == 'B'))
        ) {
            println("No ${if (firstTurn) "white" else "black"} pawn at ${input[0]}${input[1]}")
            continue
        }
        if (enPassantW) chessboard[xPassant][yPassant] = 'W'
        if (enPassantB) chessboard[xPassant][yPassant] = 'B'
        if (!inputIsValid(firstTurn, coordinates, chessboard)) {
            println("Invalid Input")
            continue
        }
        if (x2 == xPassant && y2 == yPassant) {
            if (enPassantW) chessboard[xPassant][3] = ' '
            if (enPassantB) chessboard[xPassant][4] = ' '
        }
        if (enPassantW || enPassantB) chessboard[xPassant][yPassant] = ' '
        chessboard[x1][y1] = ' '
        chessboard[x2][y2] = if (firstTurn) 'W' else 'B'
        if (firstTurn && x1 == x2 && y1 == 1 && y2 == 3) {
            enPassantW = true
            xPassant = x1
            yPassant = 2
        }
        if (!firstTurn && x1 == x2 && y1 == 6 && y2 == 4) {
            enPassantB = true
            xPassant = x1
            yPassant = 5
        }
        if (firstTurn) enPassantB = false else enPassantW = false

        printChessboard(chessboard)
        var condition = conditions(firstTurn, chessboard)
        if (condition.isNotEmpty()) {
            println(condition)
            break
        }
        firstTurn = !firstTurn
        condition = conditions(firstTurn, chessboard)
        if (condition.isNotEmpty()) {
            println(condition)
            break
        }
    } while (true)
    println("Bye!")
}

fun printChessboard(chessboard: MutableList<MutableList<Char>>) {
    println("  +---+---+---+---+---+---+---+---+")
    for (i in 8 downTo 1) {
        print("$i |")
        for (j in 1..8) {
            print(" ")
            print(chessboard[j - 1][i - 1])
            print(" |")
        }
        println()
        println("  +---+---+---+---+---+---+---+---+")
    }
    print(" ")
    for (c in 'a'..'h') {
        print("   $c")
    }
    println()
    println()
}

fun chessToList(coordinates: String): String {
    var result = ""
    for (c in coordinates) {
        result += when (c) {
            'a' -> 0
            'b' -> 1
            'c' -> 2
            'd' -> 3
            'e' -> 4
            'f' -> 5
            'g' -> 6
            'h' -> 7
            else -> c.digitToInt() - 1
        }
    }
    return result
}

fun inputIsValid(firstTurn: Boolean, coordinates: String, chessboard: MutableList<MutableList<Char>>): Boolean {
    val x1 = coordinates[0].digitToInt()
    val y1 = coordinates[1].digitToInt()
    val x2 = coordinates[2].digitToInt()
    val y2 = coordinates[3].digitToInt()
    if (y2 !in 0..7) return false
    if (x1 == x2) {
        if (chessboard[x2][y2] != ' ') return false
        if (firstTurn) {
            if (y2 - y1 == 1 || (y1 == 1 && y2 - y1 == 2)) return true
        } else {
            if (y1 - y2 == 1 || (y1 == 6 && y1 - y2 == 2)) return true
        }
    } else {
        if (firstTurn && chessboard[x2][y2] == 'B' && (abs(x1 - x2) == 1 && y2 - y1 == 1)) return true
        if (!firstTurn && chessboard[x2][y2] == 'W' && (abs(x1 - x2) == 1 && y1 - y2 == 1)) return true
    }
    return false
}

fun conditions(firstTurn: Boolean, chessboard: MutableList<MutableList<Char>>): String {
    var stalemate = true
    var x = 0
    while (stalemate && x <= 7) {
        var y = 0
        while (stalemate && y <= 7) {
            if (chessboard[x][y] == if (firstTurn) 'W' else 'B') {
                val deltaY = if (firstTurn) 1 else -1
                val minX = if (x - 1 < 0) 0 else x - 1
                val maxX = if (x + 1 > 7) 7 else x + 1
                for (x2 in minX..maxX) {
                    val coordinates = "$x$y$x2${y + deltaY}"
                    if (inputIsValid(firstTurn, coordinates, chessboard)) stalemate = false
                }
            }
            y++
        }
        x++
    }
    if (stalemate) return "Stalemate!"
    for (y in 0..7) {
        if (chessboard[y][0] == 'B') return "Black Wins!"
        if (chessboard[y][7] == 'W') return "White Wins!"
    }
    var isW = false
    var isB = false
    x = 0
    while (x <= 7 && (!isW || !isB)) {
        var y = 0
        while (y <= 7 && (!isW || !isB)) {
            when (chessboard[x][y]) {
                'W' -> isW = true
                'B' -> isB = true
            }
            y++
        }
        x++
    }
    if (!isW) return "Black Wins!"
    if (!isB) return "White Wins!"
    return ""
}