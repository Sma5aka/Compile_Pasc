import java.io.File
import java.io.FileInputStream

fun main(args: Array<String>){
    /*if (args.indexOf("-lex") != -1){
        lex_analys(args[1])
    }*/
    testing(args[1])

    /*val stream: FileInputStream = FileInputStream(args[1])
    for (i in Lexer(stream)){
        println("${i.pos_x}\t${i.pos_y}\t${i.type}\t${i.value}")
    }*/
}

fun testing(dir: String){

    var reader1 : FileInputStream
    var reader2 : FileInputStream

    var i: Int = 1
    while (i != 6){
        var buffer1: String = ""
        var buffer2: String = ""

        lex_analys(dir+"$i.in", i.toString())

        reader1 = FileInputStream(dir+"$i.out")
        reader2 = FileInputStream("tests\\answ\\$i.txt")
        var iter: Int
        while (reader1.read().also { iter = it } != -1) {
            buffer1 += iter.toChar()
        }
        while (reader2.read().also { iter = it } != -1) {
            buffer2 += iter.toChar()
        }

        if(buffer1 == buffer2){
            println("$i - TRUE")
        } else {
            println("$i - FALSE")
        }
        i++
    }
}

fun lex_analys(_name: String, _fname: String) {
    val str: FileInputStream = FileInputStream(_name)
    val fname: String = _fname
    var ne: Boolean = true

    if(ne){
        for (i in Lexer(str)) {
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