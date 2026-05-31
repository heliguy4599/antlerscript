package io.github.heliguy4599.antlerscript;

import java.util.*;
import java.util.stream.*;
import java.util.stream.Collectors;
import java.nio.file.*;
import java.io.*;

import org.antlr.v4.runtime.Token;

public interface Error {
	String toString();
	Optional<Error> unwrap();

	default List<String> chain() {
		Error next = unwrap().orElse(null);
		if (next != null) {
			List<String> errors = next.chain();
			errors.add(0, toString());
			return errors;
		}

		List<String> errors = new LinkedList<>();
		errors.add(toString());

		return errors;
	}
}

record SimpleError(String message, Optional<Error> cause) implements Error {
	public String toString() { return message; }
	public Optional<Error> unwrap() { return cause; }

	public SimpleError(String message, Optional<Error> cause) {
		assert message != null;
		assert cause != null;

		this.message = message;
		this.cause = cause;
	}

	public SimpleError(String message) {
		this(message, Optional.empty());
	}

	public SimpleError(String message, Error cause) {
		this(message, Optional.of(cause));
	}
}

record SyntaxError(
	String message,
	Token startToken,
	Token endToken,
	Optional<Error> cause
) implements Error{
	public String toString() {
		return String.format(
			"%s\n  -> %s:%d:%d\n",
			message,
			startToken.getInputStream().getSourceName(),
			startToken.getLine(),
			startToken.getCharPositionInLine()
		);
	}
	public Optional<Error> unwrap() { return cause; }

	public SyntaxError(
		String message,
		Token startToken,
		Token endToken,
		Optional<Error> cause
	) {
		assert message != null;
		assert startToken != null;
		assert endToken != null;
		assert cause != null;

		this.message = message;
		this.startToken = startToken;
		this.endToken = endToken;
		this.cause = cause;
	}

	public SyntaxError(String message, Token startToken, Token endToken) {
		this(message, startToken, endToken, Optional.empty());
	}

	public SyntaxError(
		String message,
		Token startToken,
		Token endToken,
		Error cause
	) {
		this(message, startToken, endToken, Optional.of(cause));
	}
}

record FatalError(Error err) {
	public String toString() {
		return "error: " + err;
	}

	public Optional<Error> unwrap() {
		return Optional.of(err);
	}

	public FatalError(Error err) {
		assert err != null;

		this.err = err;
	}
}

record WarningError(Error err) {
	public String toString() {
		return "warning: " + err;
	}

	public Optional<Error> unwrap() {
		return Optional.of(err);
	}

	public WarningError(Error err) {
		assert err != null;

		this.err = err;
	}
}
