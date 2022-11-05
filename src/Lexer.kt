import java.io.FileInputStream

public class Lexem(val pos_x: Int, val pos_y: Int, val type: String, val value: String)

public class Lexer (val stream: FileInputStream): Iterable<Lexem> {

    override fun iterator(): Iterator<Lexem> {
        return Iterator(stream)
    }

    class Iterator<T>(_stream: FileInputStream) : kotlin.collections.Iterator<Lexem> {

        private enum class States { START, FINAL, NUM, COMM, ERR, ID, STRL, ASGN, END, NUMM }

        private val KWords: Array<String> = arrayOf("program", "var", "integer", "real",
                "bool", "begin"/*, "end", "end."*/, "if", "then", "else", "while", "do",
                "read", "write", "true", "false")
        private val Delimers: Array<Char> = arrayOf(',', '.', ';', '(', ')')
        private val Operators: Array<Char> = arrayOf('+', '-', '=', '*', '/', '<', '>', ':')

        private var buffer: String = ""
        private var n_line: Int = 1
        private var n_el: Int = 1
        private var r_type: String = ""
        private var r_val: String = ""
        private var r_val_num: Int = 0

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
            var chr: Int = nxtchr
            r_val_num = 0
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
                        } else if (chr.toChar().isLetter()) {
                            n_el++
                            buffer = ""
                            buffer += chr.toChar()
                            state = States.ID
                            stream.read().also { chr = it }
                        } else if (chr.toChar() == '{') {
                            n_el++
                            state = States.COMM
                            stream.read().also { chr = it }
                        } else if (chr.toChar() == '\"')  {
                            n_el++
                            buffer = ""
                            buffer += chr.toChar()
                            state = States.STRL
                            stream.read().also { chr = it }
                        } else if (Delimers.indexOf(chr.toChar()) != -1) {
                            n_el++
                            buffer = ""
                            buffer += chr.toChar()
                            state = States.FINAL
                            r_type = "Delimiter"
                            r_val = buffer
                        } else if (Operators.indexOf(chr.toChar()) != -1) {
                            n_el++
                            buffer = ""
                            buffer += chr.toChar()
                            state = States.ASGN
                            stream.read().also { chr = it }
                        } else if (chr.toChar().isDigit()) {
                            n_el++
                            buffer = ""
                            buffer += chr.toChar()
                            state = States.NUMM
                            stream.read().also { chr = it }
                        } else {
                            n_el++
                            stream.read().also { chr = it }
                        }

                    }

                    States.ID -> {
                        if (chr.toChar().isLetterOrDigit()){
                            n_el++
                            buffer += chr.toChar()
                            stream.read().also { chr = it }
                        } else {
                            if (KWords.indexOf(buffer) != -1){
                                state = States.FINAL
                                r_type = "KWord"
                                r_val = buffer
                            } else if(buffer == "end"){
                                if (chr.toChar() == '.'){
                                    buffer += chr.toChar()
                                    r_type = "End_file"
                                    r_val = "end."
                                    state = States.FINAL
                                } else {
                                    state = States.FINAL
                                    r_type = "KWord"
                                    r_val = buffer
                                }
                            } else {
                                state = States.FINAL
                                r_type = "Identifier"
                                r_val = buffer
                            }
                        }
                    }

                    States.COMM -> {
                        if (chr.toChar() == '}') {
                            n_el++
                            state = States.START
                            stream.read().also { chr = it }
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
                            r_type = "String"
                            r_val = buffer
                        } else {
                            n_el++
                            stream.read().also { chr = it }
                        }
                    }

                    States.ASGN -> {
                        if (':' in buffer && chr.toChar() == '='){
                            n_el++
                            buffer += chr.toChar()
                            r_type = "Operator"
                            r_val = buffer
                            state = States.FINAL
                        } else {
                            n_el++
                            state = States.FINAL
                            r_type = "Operator"
                            r_val = buffer
                        }
                    }

                    States.NUM -> {                     // На данный момент не используется другой способ обработки чисел
                        if (chr.toChar().isDigit()){
                            n_el++
                            buffer += chr.toChar()
                            stream.read().also { chr = it }
                        } else if (chr.toChar() == '.'){
                            n_el++
                            buffer += chr.toChar()
                            stream.read().also { chr = it }
                            if (chr.toChar().isDigit()){
                                n_el++
                                buffer += chr.toChar()
                                stream.read().also { chr = it }
                            } else if (!chr.toChar().isDigit()) {
                                n_el++
                                buffer += chr.toChar()
                                state = States.FINAL
                                r_type = "Identifier"
                                r_val = buffer
                            } else {
                                n_el++
                                buffer += chr.toChar()
                                state = States.FINAL
                                r_type = "Float"
                                r_val = buffer
                            }
                        } else {
                            n_el++
                            buffer += chr.toChar()
                            state = States.START
                        }
                    }

                    States.NUMM -> {
                        if (chr.toChar().isDigit()){
                            buffer += chr.toChar()
                            r_val_num = r_val_num * 10 + (chr.toChar()-'0')
                            stream.read().also { chr = it }
                        } else {
                            n_el++
                            buffer += chr.toChar()
                            state = States.FINAL
                            r_type = "Digit"
                            r_val = r_val_num.toString()
                        }
                    }

                    else -> {
                        buffer = ""
                        state = States.FINAL
                        r_type = "Epty_File"
                        r_val = buffer
                    }
                }

            }
            return Lexem(n_line, n_el,r_type, r_val)
        }
    }
}