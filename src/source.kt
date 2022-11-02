import java.io.FileInputStream

fun main(){
    val str: FileInputStream = FileInputStream("text.txt")

    val arr: Array<Int> = arrayOf(1,2,3,4,5,6)
    //var aa = Lexer(str)
    //println("${aa.iterator().next().pos_x} ${aa.iterator().next().pos_y} ${aa.iterator().next().type} ${aa.iterator().next().value}")

    for (i in Lexer(str)){
        println("${i.pos_x}\t${i.pos_y}\t${i.type}\t${i.value}")
    }

}

fun testing(dir: String){

    var reader1 : FileInputStream
    var reader2 : FileInputStream

    var i: Int = 1
    while (i != 6){
        var buffer1: String = ""
        var buffer2: String = ""

        reader1 = FileInputStream(dir+"\\inout\\0$i.out")
        reader2 = FileInputStream(dir+"\\answ\\0$i.txt")
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
        //println(buffer1)
        //println(buffer2+"\n")
        //println("------------------------------------------\n")
    }
}