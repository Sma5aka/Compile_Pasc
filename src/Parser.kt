import java.io.FileInputStream

open class Node

data class BinOp(val op: String, val left: Node, val right: Node): Node()

data class Number<T>(val value: T): Node()

data class Variable(val name: String): Node()

class SyntaxError(message: String?) : Error(message)

class Parser (val dir: FileInputStream, val lexems: MutableList<Lexem>) {
    var vars = mutableMapOf<String,Any>()
    val lexer: Lexer = Lexer(dir)
    var token_id: Int = 0

    fun ParseExpression(): Node {

        var left = ParseTerm()
        var lex = lexems[token_id]

        while (lex.value in mutableListOf("+", "-")){
            token_id = if(lexems[token_id+1].type != Types.EOF) +1 else token_id
            left = BinOp(lex.value, left, ParseTerm())
            lex = lexems[token_id]
        }
        return left
    }

    fun ParseTerm(): Node {
        var left = ParseFactor()
        var lex = lexems[token_id]

        while (lex.value in mutableListOf("*", "/")){
            token_id = if(lexems[token_id+1].type != Types.EOF) +1 else token_id
            left = BinOp(lex.value, left, ParseTerm())
            lex = lexems[token_id]
        }
        return left
    }

    fun ParseFactor(): Node {
        var lex = lexems[token_id]

        if (lex.type == Types.FLOAT) {
            token_id = if(lexems[token_id+1].type != Types.EOF) +1 else token_id
            return Number<Float>(lex.value.toFloat())
        }
        if (lex.type == Types.INT) {
            token_id = if(lexems[token_id+1].type != Types.EOF) +1 else token_id
            return Number<Int>(lex.value.toInt())
        }
        if (lex.type == Types.ID) {
            token_id = if(lexems[token_id+1].type != Types.EOF) +1 else token_id
            return Variable(lex.value)
        }
        return Node()
        /*if (lex.value == "("){
            token_id++
            var e = ParseExpression()
            if (lexems[token_id].value != ")"){
                return "No right bracket"
            }
            token_id++
            return e
        }
        return "Factor expected"*/
    }
}