package io.antlerscript

import java.io.EOFException
import java.io.RandomAccessFile
import kotlin.text.isWhitespace

// TODO: rename to SymbolType
enum class OperatorType(val text: String) {
	// mathematical
	ADD("+"),
	DIVIDE("/"),
	FLOOR_DIVIDE("//"),
	REMAINDER("%"),
	MODULO("%%"),
	MULTIPLY("*"),
	EXPONENT("**"),
	SUBTRACT("-"),
	ASSIGN("="),
	EQUALITY("=="),
	INEQUALITY("!="),
	IDENTITY("==="),
	INIDENTITY("!=="),
	CONCAT("++"),
	DECORATE("@"),
	COLON(":"),
	ARGUMENT_SEPARATOR(","),
	DOT("."),
	DOLLAR_SIGN("$"),
	GREATER_THAN(">"),
	LESS_THAN("<"),
	QUESTION_MARK("?"),
	DOUBLE_QUESTION_MARK("??"),
	SEMICOLON(";"),
	PIPE("|>"),

	// bitwise
	BIT_SHIFT_LEFT("<<"),
	BIT_SHIFT_RIGHT(">>"),
	BIT_AND("&"),
	BIT_OR("|"),
	BIT_XOR("^"),
	BIT_NOT("~"),

	// grouping
	LEFT_PAREN("("),
	RIGHT_PAREN(")"),
	LEFT_SQUARE_BRACKET("["),
	RIGHT_SQUARE_BRACKET("]"),
	LEFT_CURLY_BRACE("{"),
	RIGHT_CURLY_BRACE("}"),
	LEFT_ARROW("<-"),
	RIGHT_ARROW("->"),
}

enum class KeywordType(val text: String) {
	// Declarations
	CONST("const"),
	LET("let"),
	MUT("mut"),
	STATIC("static"),

	// Class instructions
	CONSTRUCTOR("constructor"),
	DESTRUCTOR("destructor"),
	CAST("cast"),

	// Boolean logic
	AND("and"),
	IS("is"), // Type checking
	IN("in"), // iterable-contains check
	NOT("not"),
	OR("or"),

	// Control flow
	BREAK("break"),
	CONTINUE("continue"),
	IF("if"),
	MATCH("match"),
	RETURN("return"),
	// ? maybe yield?
}

sealed class Token

class CommentToken(val content: String) : Token() {
	override fun toString() = "#${content}"
}

class KeywordToken(val type: KeywordType) : Token() {
	override fun toString() = type.text
}

class NumericalToken(val value: Double) : Token() {
	override fun toString() = value.toString()
}

class OperatorToken(val type: OperatorType) : Token() {
	override fun toString() = type.text
}

class TextToken(val content: String) : Token() {
	override fun toString() = "\"$content\""
}

class NameToken(val name: String) : Token() {
	override fun toString() = name
}

class Newline : Token() {
	override fun toString() = "\n"
}

class Whitespace(val whitespace: String) : Token() {
	override fun toString() = whitespace
}

fun RandomAccessFile.readAscii(): Char {
	val i = this.read()
	if (i < 0) {
		throw EOFException()
	}
	return i.toChar()
}

fun parseComment(file: RandomAccessFile): Token {
	val c = file.readAscii()
	if (c != '#') {
		throw RuntimeException("Expected hastag")
	}

	val tokenBuffer = StringBuilder()
	while (true) {
		val d = file.readAscii()
		if (d == '\n') {
			file.seek(file.filePointer - 1)
			val content = tokenBuffer.toString()
			tokenBuffer.clear()
			return CommentToken(content)
		} else {
			tokenBuffer.append(d)
		}
	}
}

fun parseNumber(file: RandomAccessFile): Token {
	val c = file.readAscii()
	if (!c.isDigit()) {
		throw RuntimeException("Expected digit")
	}

	var encounteredDot = false
	val tokenBuffer = StringBuilder()
	tokenBuffer.append(c)

	while (true) {
		val d = file.readAscii()
		if (d == '_') {
			continue // do nothing, just for looks
		} else if (d == '.') {
			if (encounteredDot) {
				throw RuntimeException("Cannot have multiple periods in number")
			}
			encounteredDot = true
			tokenBuffer.append('.')
		} else if (!d.isDigit()) {
			file.seek(file.filePointer - 1)

			val content = tokenBuffer.toString()
			if (content.endsWith(".")) {
				throw RuntimeException("Number cannot end with a period")
			}

			tokenBuffer.clear()
			return NumericalToken(content.toDouble())
		} else {
			tokenBuffer.append(d)
		}
	}
}

fun parseNewline(file: RandomAccessFile): Token {
	val c = file.readAscii()
	if (c != '\n') {
		throw RuntimeException("Expected newline")
	}
	return Newline()
}

fun parseString(file: RandomAccessFile): Token {
	val c = file.readAscii()
	if (c != '\"') {
		throw RuntimeException("Expected quotation mark")
	}

	val tokenBuffer = StringBuilder()
	while (true) {
		val d = file.readAscii()
		if (d == '\\') {
			val e = file.readAscii()
			tokenBuffer.append(when (e) {
				'n' -> "\n"
				'r' -> "\r"
				't' -> "\t"
				'\\' -> "\\"
				'\'' -> "\'"
				'\"' -> "\""
				else -> throw RuntimeException("Invalid escape character ${e}")
			})
		} else if (d == '\"') {
			val content = tokenBuffer.toString()
			tokenBuffer.clear()
			return TextToken(content)
		} else {
			tokenBuffer.append(d)
		}
	}
}

fun parseWhitespace(file: RandomAccessFile): Token {
	val c = file.readAscii()
	if (!c.isWhitespace()) {
		throw RuntimeException("Expected whitespace character")
	}

	val tokenBuffer = StringBuilder()
	tokenBuffer.append(c)

	while (true) {
		val w = file.readAscii()
		if (!w.isWhitespace()) {
			file.seek(file.filePointer - 1)
			break
		}
		tokenBuffer.append(w)
	}

	val content = tokenBuffer.toString()
	tokenBuffer.clear()
	return Whitespace(content)
}

fun parseWord(file: RandomAccessFile): Token {
	val c = file.readAscii()
	if (c != '_' && !c.isLetter()) {
		throw RuntimeException("Expected letter or underscore")
	}

	val tokenBuffer = StringBuilder()
	tokenBuffer.append(c)

	while (true) {
		val d = file.readAscii()
		if (d == '_' || d.isLetter() || d.isDigit()) {
			tokenBuffer.append(d)
			continue
		}

		file.seek(file.filePointer - 1)
		val content = tokenBuffer.toString()
		tokenBuffer.clear()

		val keywordType = KeywordType.entries.firstOrNull({ it.text == content })
		if (keywordType != null) {
			return KeywordToken(keywordType)
		} else {
			return NameToken(content)
		}
	}
}

fun parseSymbol(file: RandomAccessFile): Token {
	val tokenBuffer = StringBuilder()

	val c = file.readAscii()
	if (c.isWhitespace()) {
		throw RuntimeException("Symbol cannot start with whitespace")
	}

	file.seek(file.filePointer - 1)

	while (true) {
		val d = file.readAscii()
		if (d.isWhitespace()) {
			file.seek(file.filePointer - 1)
			break /* no more symbol characters */
		}

		tokenBuffer.append(d)
		val content = tokenBuffer.toString()

		val opType = OperatorType.entries.firstOrNull({ it.text == content })
		if (opType == null) {
			tokenBuffer.deleteCharAt(tokenBuffer.length - 1)
			file.seek(file.filePointer - 1)
			break /* no more symbols with such characters */
		}
	}

	val content = tokenBuffer.toString()
	tokenBuffer.clear()

	val opType = OperatorType.entries.first({ it.text == content })
	return OperatorToken(opType)
}

fun parseToken(file: RandomAccessFile): Token {
	val c = file.readAscii()
	file.seek(file.filePointer - 1)

	return if (c == '#') {
		parseComment(file)
	} else if (c.isDigit()) {
		parseNumber(file)
	} else if (c == '\n') {
		parseNewline(file)
	} else if (c == '\"') {
		parseString(file)
	} else if (c.isWhitespace()) {
		parseWhitespace(file)
	} else if (c == '_' || c.isLetter()) {
		parseWord(file)
	} else {
		parseSymbol(file)
	}
}

fun readTokens(file: RandomAccessFile): List<Token> {
	val tokens = mutableListOf<Token>()
	while (true) {
		try {
			tokens += parseToken(file)
		} catch (e: EOFException) {
			break
		}
	}
	return tokens
}
