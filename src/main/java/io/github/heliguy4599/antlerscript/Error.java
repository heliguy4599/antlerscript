package io.github.heliguy4599.antlerscript;

import java.util.*;

import org.antlr.v4.runtime.Token;

public interface Error {
	String error();
	Optional<Error> unwrap();

	default List<String> chain() {
		Error next = unwrap().orElse(null);
		if (next != null) {
			List<String> errors = next.chain();
			errors.add(0, error());
			return errors;
		}

		List<String> errors = new LinkedList<>();
		errors.add(error());

		return errors;
	}
}

record SimpleError(String message, Optional<Error> cause) implements Error {
	public String error() { return message; }
	public Optional<Error> unwrap() { return cause; }

	public SimpleError(String message) {
		this(message, Optional.empty());
	}

	public SimpleError(String message, Error cause) {
		this(message, Optional.of(cause));
	}
}

record SyntaxError(
	String message,
	Token token,
	Optional<Error> cause
) implements Error{
	public String error() {
		return String.format(
			"%s:%d:%d:",
			message,
			token.getLine(),
			token.getCharPositionInLine()
		);
	}
	public Optional<Error> unwrap() { return cause; }

	public SyntaxError(String message, Token token) {
		this(message, token, Optional.empty());
	}

	public SyntaxError(String message, Token token, Error cause) {
		this(message, token, Optional.of(cause));
	}
}
