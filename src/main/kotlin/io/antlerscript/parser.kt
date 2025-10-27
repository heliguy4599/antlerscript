package io.antlerscript

import java.lang.reflect.Constructor
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
		while (currentToken is Whitespace) {
			skipped += 1
			currentToken = tokens.getOrNull(index + skipped)
		}

		index += skipped
		return skipped
	}

	private inline fun <reified T : Token> expectToken(): T {
		val currentToken = tokens[index]
		if (currentToken !is T) {
			throw RuntimeException("Expected a ${T::class.simpleName} got a $currentToken")
		}
		return currentToken
	}

	private fun expectSymbol(
		symbolType: SymbolType,
	): OperatorToken {
		val token = this.expectToken<OperatorToken>()
		if (token.type != symbolType) {
			throw RuntimeException("Expected $symbolType symbol")
		}
		return token
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
				index += 1
			}
		} else if (token.type == SymbolType.LEFT_CURLY_BRACE) {

		} else {
			throw RuntimeException("Unexpected operator $token")
		}
	}

	sealed class ConstructorParam {
		class Field(val name: String) : ConstructorParam()
		class Local(val name: String, val type: String) : ConstructorParam()
	}

	// fun parseMethod()

	private fun parseConstructor() {
		if (currentClass == null) {
			throw RuntimeException("Must be in a class to define a constructor")
		}

		this.expectSymbol(
			symbolType = SymbolType.LEFT_PAREN,
		)

		// 0 - no names seen
		// 1 - one name seen, may be a field
		// 2 - two names seen, is a parameter

		var nameState = 0
		val nameTokens = mutableListOf<NameToken>()

		val params = mutableListOf<ConstructorParam>()

		fun addParam() {
			if (nameTokens.size == 1) {
				params.add(
					ConstructorParam.Field(
						name = nameTokens[0].name,
					)
				)
			} else if (nameTokens.size == 2) {
				params.add(
					ConstructorParam.Local(
						name = nameTokens[1].name,
						type = nameTokens[0].name,
					)
				)
			} else {
				throw RuntimeException("Expected one or two name tokens")
			}
			nameTokens.clear()
			nameState = 0
		}

		while (true) {
			this.goToNextToken(skipWhitespace = true)
			val token = tokens[index]

			if (token is NameToken) {
				when (nameState) {
					0 -> {
						nameState = 1
						nameTokens.add(token)
					}

					1 -> {
						nameState = 2
						nameTokens.add(token)
					}

					else -> {
						throw RuntimeException("Expecting a comma or closing parentheses")
					}
				}
			} else if (token is OperatorToken) {
				if (token.type == SymbolType.ARGUMENT_SEPARATOR) {
					if (nameState == 0) {
						throw RuntimeException("Not expecting a comma here !!! :(")
					}
					addParam()
				} else if (token.type == SymbolType.RIGHT_PAREN) {
					index += 1
					addParam()
					break /* no more arguments */
				}
			}
		}

		println("Got constructor with:")
		params.forEach {
			when (it) {
				is ConstructorParam.Field -> {
					println("\tFIELD: ${it.name}")
				}

				is ConstructorParam.Local -> {
					println("\tLOCAL ${it.name} of ${it.type}")
				}
			}
		}
	}

	fun parseKeyword(token: KeywordToken) {
		index += 1

		if (token.type == KeywordType.CONSTRUCTOR) {
			this.parseConstructor()

		}


	}

	fun parse() {
		while (true) {
			val token = tokens[index]
			if (token is Whitespace) {
				index += 1
			} else if (token is Newline) {
				index += 1
			} else if (token is OperatorToken) {
				parseOperator(token)
			} else if (token is KeywordToken) {
				parseKeyword(token)
			} else {
				throw RuntimeException("I dunno what to do :(")
			}
		}
	}

}
