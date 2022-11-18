class Node

enum class Operation

data class BinOp (val op: Operation, val left: Node, val right: Node)

data class Number<T> (val value: T)

data class Variable (val name: String)

class Parser {
    private val in_lexems: MutableList<Lexem> = mutableListOf()
}