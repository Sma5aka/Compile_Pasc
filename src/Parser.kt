import java.io.FileInputStream
open class Node
data class BinOp(val op: String, val left: Node, val right: Node): Node()
data class Number<T>(val value: T): Node()
data class Variable(val name: String): Node()

class Parser (val dir: FileInputStream, val lexems: MutableList<Lexem>) {
    var vars = mutableMapOf<String,Any>()
    val lexer: Lexer = Lexer(dir)
    fun ParseExpression(): Node {
        var left = ParseTerm()
        var lex = if (lexer.iterator().hasNext()) lexer.iterator().next() else Lexem(0,0, Types.ERR, "Error")
        println(lex)
        while (lex.value in mutableListOf("+", "-")){
            left = BinOp(lex.value, left, ParseExpression())
            lex = if (lexer.iterator().hasNext()) lexer.iterator().next() else Lexem(0,0, Types.ERR, "Error")
        }
        return left
    }

    fun ParseTerm(): Node {
        var left = ParseFactor()
        var lex = if (lexer.iterator().hasNext()) lexer.iterator().next() else Lexem(0,0, Types.ERR, "Error")
        println(lex)
        while (lex.value in mutableListOf("*", "/")){
            left = BinOp(lex.value, left, ParseTerm())
            lex = if (lexer.iterator().hasNext()) lexer.iterator().next() else Lexem(0,0, Types.ERR, "Error")
        }
        return left
    }

    fun ParseFactor(): Node {
        var lex = if (lexer.iterator().hasNext()) lexer.iterator().next() else Lexem(0,0, Types.ERR, "Error")
        println(lex)
        if (lex.type == Types.FLOAT) {

            return Number<Float>(lex.value.toFloat())
        }
        if (lex.type == Types.INT) {

            return Number<Int>(lex.value.toInt())
        }
        if (lex.type == Types.ID) {

            return Variable(lex.value)
        }
        return Node()
        /*if (lex.value == "("){
            token++
            var e = ParseExpression()
            if (lexems[token].value != ")"){
                return "No right bracket"
            }
            token++
            return e
        }
        return "Factor expected"*/
    }
}