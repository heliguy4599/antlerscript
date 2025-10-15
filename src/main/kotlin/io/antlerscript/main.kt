package io.antlerscript

import java.io.RandomAccessFile

fun main() {
	println("Parsing file")
	val file = RandomAccessFile("./test.atl", "r")
	val tokens = readTokens(file)
	tokens.forEach({
		if (it is Whitespace) {
			print(it.whitespace)
		} else if (it is Newline) {
			println()
		} else {
			print(it)
		}
	})
}
