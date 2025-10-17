package io.antlerscript

import kotlin.collections.getOrNull

enum class ParserState {
	INITIAL,

}

class AntlerClass(val name: String?) {



}

class ParserContext(
    val tokens: List<Token>,
) {

    private var state = ParserState.INITIAL
    private var index = 0

    private var currentClass: AntlerClass? = null

    fun goToNextToken(
        skipWhitespace: Boolean,
    ): Int {
        var skipped = 1
        if (!skipWhitespace) {
            return skipped
        }

        var currentToken = tokens.getOrNull(index + skipped)
        while(currentToken is Whitespace) {
            skipped += 1
            currentToken = tokens.getOrNull(index + skipped)
        }

        index += skipped
        return skipped
    }

    fun parseOperator(token: OperatorToken) {
        if (token.type == SymbolType.DOLLAR_SIGN) {
            val indexAtOccurred = this.index
            
            index += 1

            this.goToNextToken(skipWhitespace = false)
            
            val nextToken = tokens.getOrNull(index)
            if (nextToken !is NameToken) {
                throw RuntimeException("Expected directive")
            }

            val directiveName = nextToken.name
            
            val directive = FileDirective
                .entries
                .firstOrNull({ it.text == directiveName })
            
            if (directive == null) {
                throw RuntimeException("Unknown directive $directiveName")
            }

            if (directive == FileDirective.CLASS_NAME) {
                if (indexAtOccurred != 0) {
                    throw RuntimeException("Class directive must be the first line")
                } else if (currentClass != null) {
                    throw RuntimeException("Class already defined")
                }

                this.goToNextToken(skipWhitespace = true)
                val classNameToken = tokens.getOrNull(index)
                if (classNameToken !is NameToken) {
                    throw RuntimeException("Expected class name")
                }

                val className = classNameToken.name
                println("We are now in the $className class!")
                this.currentClass = AntlerClass(className)
            }
        }
    }

    // fun parseMethod()

    fun parseKeyword(token: KeywordToken) {
        index += 1
        
        if (token.type == KeywordType.CONSTRUCTOR) {
            if (currentClass == null) {
                // must be defined in a class
            }
        }


    }

    fun parse() {
        while(true) {
            val token = tokens[index]
            if (token is Whitespace) {
                index += 1
            } else if (token is Newline) {
                index += 1
            } else if (token is OperatorToken) {
                parseOperator(token)
            } else {
                throw RuntimeException("I dunno what to do :(")
            }
        }
    }

}
