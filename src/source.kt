import java.io.File
import java.io.FileInputStream

fun main(args: Array<String>){
    /*if ("-lex" in args){
        lex_analys(args[1], "6")
    }*/

    val Lexems: MutableList<Lexem> = mutableListOf()
    val str: FileInputStream = FileInputStream(args[1])
    for (lex in Lexer(str)){
        Lexems.add(lex)
    }
    print(Lexems)
    //val lexer: Lexer = Lexer(str)

    var parser: Parser = Parser(str, Lexems)
    var e = parser.ParseExpression()
    print(e)
}

fun lex_analys(_name: String, _fname: String) {
    val str: FileInputStream = FileInputStream(_name)
    val fname: String = _fname
    var ne: Boolean = true

    if(ne){
        for (i in Lexer(str)) {

            print("${i.pos_x}\t${i.pos_y}\t${i.type}\t${i.value}\n") // For debug

            ne = false
            val output: File = File("tests/inout/${fname}.out")
            output.appendText("${i.pos_x}\t${i.pos_y}\t${i.type}\t${i.value}\n")
        }
    }
    if (ne){
        val output: File = File("tests/inout/${fname}.out")
        output.writeText(" ")
    }
}