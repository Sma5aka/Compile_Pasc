import java.io.FileInputStream
import java.lang.reflect.Type
import javax.swing.plaf.nimbus.State

enum class Types {INT, FLOAT, STRING, OP, ID, KWORD, DELIM, EOF, EF, EMPF, ERR}
public data class Lexem(val pos_x: Int, val pos_y: Int, val type: Types, val value: String)

public class Lexer (val stream: FileInputStream): Iterable<Lexem> {

    override fun iterator(): Iterator<Lexem> {
        return Iterator(stream)
    }

    class Iterator<T>(_stream: FileInputStream) : kotlin.collections.Iterator<Lexem> {

        private enum class States { START, FINAL, INT, COMM, ERR, ID, STRL, OPER, END, NUMM, FLOAT }

        private val KWords: Array<String> = arrayOf("program", "var", "integer", "real",
                "bool", "begin", "if", "then", "else", "while", "do",
                "read", "write", "true", "false")
        private val Delimeters: Array<Char> = arrayOf(',', '.', ';', '(', ')')
        private val Operators: Array<Char> = arrayOf('+', '-', '=', '*', '/', '<', '>', ':')

        private val dx16: Array<Char> = arrayOf('A','B','C','D','E','F')
        private val dx8: Array<Char> = arrayOf('0','1','2','3','4','5','6','7')
        private val dx2: Array<Char> = arrayOf('0','1')

        private var buffer: String = ""
        //Coordinates
        private var n_line: Int = 1
        private var n_el: Int = 1

        private var r_type: Types = Types.EOF
        private var r_val: String = ""

        private var num_val: Double = 0.0

        private var state: States = States.START

        private val stream: FileInputStream

        init {
            stream = _stream
        }

        private var nxtchr: Int = 0
        override fun hasNext(): Boolean {
            return stream.read().also { nxtchr = it } != -1
        }

        override fun next(): Lexem {

            var _n_line = 1
            var _n_el = 1
            var chr: Int = nxtchr

            fun stepp(){
                n_el++
                buffer += chr.toChar()
                stream.read().also { chr = it }
            }
            num_val = 0.0

            state = States.START
            while (state != States.FINAL) {
                when (state) {
                    States.START -> {
                        if ((chr.toChar() == ' ') || (chr.toChar() == '\t') || (chr.toChar() == '\r')) {
                            n_el++
                            stream.read().also { chr = it }

                        } else if (chr.toChar() == '\n') {
                            n_el = 1
                            n_line++
                            stream.read().also { chr = it }

                        } else if (chr.toChar().lowercaseChar().isLetter() || chr.toChar().lowercaseChar()=='_') {
                            _n_el = n_el
                            _n_line = n_line
                            buffer = "${chr.toChar()}"
                            state = States.ID
                            stream.read().also { chr = it }

                        } else if (chr.toChar() == '{') {
                            state = States.COMM
                            stream.read().also { chr = it }

                        } else if (chr.toChar() == '\"')  {
                            _n_el = n_el
                            _n_line = n_line
                            buffer = "${chr.toChar()}"
                            state = States.STRL
                            stream.read().also { chr = it }

                        } else if (Delimeters.indexOf(chr.toChar()) != -1) {
                            _n_el = n_el
                            _n_line = n_line
                            n_el++
                            buffer = "${chr.toChar()}"
                            state = States.FINAL
                            r_type = Types.DELIM
                            r_val = buffer

                        } else if (Operators.indexOf(chr.toChar()) != -1) {
                            _n_el = n_el
                            _n_line = n_line
                            n_el++
                            buffer = ""
                            state = States.OPER

                        } else if (chr.toChar().isDigit()) {
                            n_el++
                            buffer = ""
                            _n_el = n_el
                            _n_line = n_line
                            state = States.INT

                        } else {
                            n_el++
                            stream.read().also { chr = it }
                            if(chr == -1){
                                state = States.FINAL
                                buffer = ""
                            }
                        }

                    }

                    States.ID -> {
                        if (chr.toChar().lowercaseChar().isLetterOrDigit() || chr.toChar().lowercaseChar()=='_'){
                            n_el++
                            buffer += chr.toChar()
                            stream.read().also { chr = it }
                        } else {
                            if (KWords.indexOf(buffer) != -1){
                                state = States.FINAL
                                r_type = Types.KWORD
                                r_val = buffer
                            } else if(buffer == "end"){
                                if (chr.toChar() == '.'){
                                    buffer += chr.toChar()
                                    r_type = Types.EF
                                    r_val = "end."
                                    state = States.FINAL
                                } else {
                                    state = States.FINAL
                                    r_type = Types.KWORD
                                    r_val = buffer
                                }
                            } else {
                                state = States.FINAL
                                r_type = Types.ID
                                r_val = buffer
                            }
                        }
                    }

                    States.COMM -> {
                        if (chr.toChar() == '}') {
                            n_el++
                            state = States.START
                            //stream.read().also { chr = it }
                        } else if (chr.toChar() == '\n') {
                            n_line++
                            stream.read().also { chr = it }
                        } else {
                            n_el++
                            stream.read().also { chr = it }
                        }
                    }

                    States.STRL -> {
                        buffer += chr.toChar()
                        if (chr.toChar() == '\"'){
                            n_el++
                            state = States.FINAL
                            r_type = Types.STRING
                            r_val = buffer
                        } else {
                            n_el++
                            stream.read().also { chr = it }
                        }
                    }

                    States.OPER -> {
                        if (chr.toChar() == ':'){
                            n_el++
                            buffer += chr.toChar()
                            stream.read().also { chr = it }
                            if (chr.toChar() == '='){
                                buffer += chr.toChar()
                                state = States.FINAL
                                r_type = Types.OP
                                r_val = buffer
                            } else {
                                state = States.FINAL
                                r_type = Types.OP
                                r_val = buffer
                            }
                        } else {
                            buffer += chr.toChar()
                            state = States.FINAL
                            r_type = Types.OP
                            r_val = buffer
                        }
                    }

                    States.INT -> {
                        fun cast_int(){
                            try {
                                var parsedNum = buffer.toInt()
                                state = States.FINAL
                                r_type = Types.INT
                                r_val = parsedNum.toString()
                            } catch (nfe: NumberFormatException) {
                                state = States.FINAL
                                r_type = Types.ERR
                                r_val = buffer
                            }
                        }

                        if (chr.toChar().isDigit()) {
                            stepp()
                        } else if (chr.toChar() == '.'){
                            state = States.FLOAT
                            buffer += chr.toChar()
                            stream.read().also { chr = it }

                        } else if (buffer == "$"){
                            stream.read().also { chr = it }
                            while((chr.toChar().isDigit()) || (chr.toChar() in dx16)){
                                stepp()
                            }
                            cast_int()

                        } else if (buffer == "&"){
                            stream.read().also { chr = it }
                            while(chr.toChar() in dx8){
                                stepp()
                            }
                            cast_int()

                        } else if (buffer == "%"){
                            stream.read().also { chr = it }
                            while(chr.toChar() in dx2){
                                stepp()
                            }
                            cast_int()
                        } else {
                            cast_int()
                        }
                    }

                    States.FLOAT -> {
                        fun cast_db() {
                            try {
                                var parsedNum = buffer.toDouble()
                                state = States.FINAL
                                r_type = Types.FLOAT
                                r_val = parsedNum.toString()
                            } catch (nfe: NumberFormatException) {
                                state = States.FINAL
                                r_type = Types.ERR
                                r_val = buffer
                            }
                        }

                        if (chr.toChar().isDigit()) {
                            stepp()
                        } else {
                            cast_db()
                        }
                    }

                    else -> {
                        buffer = ""
                        state = States.FINAL
                        r_type = Types.EMPF
                        r_val = buffer
                    }
                }

            }
            return Lexem(_n_line, _n_el,r_type, r_val)
        }
    }
}