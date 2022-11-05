import java.io.File
import java.io.FileInputStream

fun main(args: Array<String>){
    if (args.indexOf("-lex") != -1){
        lex_analys(args[1])
    }
}

fun testing(dir: String){

    var reader1 : FileInputStream
    var reader2 : FileInputStream

    var i: Int = 1
    while (i != 6){
        var buffer1: String = ""
        var buffer2: String = ""

        reader1 = FileInputStream(dir+"\\inout\\$i.out")
        reader2 = FileInputStream(dir+"\\answ\\$i.txt")
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

fun lex_analys(_name: String){
    val str: FileInputStream = FileInputStream(_name)
    var cname: Int = 1
    for (i in Lexer(str)){
        val output: File = File("tests/inout/${cname}.out")
        output.writeText("${i.pos_x}\t${i.pos_y}\t${i.type}\t${i.value}")
        //println("${i.pos_x}\t${i.pos_y}\t${i.type}\t${i.value}")
    }
}