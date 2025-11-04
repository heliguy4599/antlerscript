# Intro to Classes

AntlerScript features many of the OOP paradigms from other languages, but with specific differences to align with the language.

This document covers the introductory topics of classes, but does not touch on the more complex uses, like inheritance, inheritance chains, multiple inheritance, generic types, nor properties. Please see `classes-continued.md` for further details.

## Class Structures

In AntlerScript, classes consist of only three types of elements:

- Instance Members: variables of any value that will exist on an instance
- Static Members: variables of any value that will exist on the class itself, like a namespace
- Class Directives: information to the class itself, to control different features and behaviors

### Overview

Class bodies are essentially a list of members and directives. Statement-by-statement code is not allowed in class bodies. There is no order in which items must be supplied, as this styling is up to a project's developer(s) to decide.

One important concept to grasp is that, unlike in code-blocks, you are allowed to reference a class member before it appears vertically in the file, and methods are allowed to circularly refer to each other.

Here is an example of a simple class. Each part will be covered below:

```ruby
let Circle = Class(
	# Static Members
	let static shape_name = "circle"

	# Static Method
	let static print_info = Func(:) {
		print("I am the class Circle, my shape_name is: <shape_name>")
	}

	# Instance Members
	let Uuid _id = Uuid{} # Protected member
	let mut Float radius # Value absent, initialized by the constructor
	let mut Float diameter = radius * 2

	# Instance Method
	let get_area = Func(: Float) {
		return (Math.PI * radius) ** 2
	}

	# The class constructor. It is one of the class directives available
	# This constructor's only argument is for the radius instance member
	constructor(radius) {}
)
```

### Instance Members

Declaring an instance member follows the same rules as declaring a regular variable. A creation keyword is supplied, then optionally the type, then the variable's name, and then optionally the value.

Here is an example of an instance member declaration:

```ruby
Class(
	let Int i = 0
	let j = 0 # `j's` type is inferred from its value to be Int
)
```

As you can see, it is identical to a variable declaration in something like a function body.

Something that can be done with instance member declarations that cannot be done with variable declarations is leaving the value absent.

Recall above to the `radius` instance member of Overview's example. It did not have a value assigned (note the lack of `=`). Instead, its value was supplied by the constructor, which we will go over later.

AntlerScript has no concept of "undefined" or "void value", nor does AntlerScript feature default values. Instead, this is considered an uninitialized field, and is required to be initialized by the class's constructor, if a constructor is present.

Instance members are accessed via dot-notation, regardless of if they're a primitive or not.

Here is an example of accessing Overview's `radius` instance member:

```ruby
let radius = some_circle.radius
```

Note the fact that we must access `radius` via an instance object of the class.

### Non-Primitive Instance Members

In AntlerScript, instance members may be any type, including other classes or objects. It is important to understand that when an instance of a class is constructed, all instance members are constructed uniquely. This means that if you declare, say, a List instance member, you will get a new list for each class instance. If this List contains items, these items will all be constructed new again for every class instance. If a function supplies an instance member’s value, it will be called separately for every instance.

### Instance Functions, aka Instance Methods

In AntlerScript, functions are directly applied to the class, within the class body. To disambiguate a function belonging to a class from a function declared in a scope, we call class functions "methods". Unlike in other languages, AntlerScript features no different syntax for attaching a method to a class, and instead reuses the regular variable assignment made from the class.

In AntlerScript, instance methods are, in fact, also instance members. Instance methods are a subset of instance members, so keep in mind that everything that applies to instance members also applies to instance methods, including allowing their values to be set via the constructor.

Instance methods may access static or instanced variables directly, no access syntax needed. AntlerScript does not feature variable shadowing (except in scope access, described in `classes-continued.md`), so any member may not have its name re-declared in a method's scope. As such, you will never need to do `self.some_var`.

Speaking of `self`, that is how you refer to the current instance. Lower-case "s" `self` references the current instance that the instance function is running on. Capital "S" `Self` references the current class. Because classes do not know their own names, the only way to access the class you are in is using the `Self` keyword.

Here is an example of instance method declarations:

```ruby
Class(
	let static Int s = 10
	let Int i = 20

	# Explicit typing
	let Func(:) some_method_1 = Func(:) {
		print(s) # Accessing the static member `s`
		print(i) # Accessing the instance member `i`
		print("I am some_method_1!")
	}

	# Type inferred from value
	let some_method_2 = Func(:) {
		print(s) # Accessing the static member `s`
		print(i) # Accessing the instance member `i`
		print("I am some_method_2!")
	}

	# Value is target-typed to the variable's type
	let Func(:) some_method_3 = {
		print(s) # Accessing the static member `s`
		print(i) # Accessing the instance member `i`
		print("I am some_method_3!")
	}
)
```

It's common practice to have methods (and stand-alone functions, for that matter) use either type inference or target typing, as seeing the signature twice isn't helpful, and is arguably less readable.

### Static Members

Static members are nearly identical to instance members, they share the same syntax, apart from including the `static` keyword. If a variable is both static and mutable, the `mut` keyword must come after the `static` keyword.

Here is an example of a static member declaration:

```ruby
Class(
	let static Int i = 0
	let static j = 0 # `j`'s type is inferred from its value to be Int
	let static mut Int k = 0 # The `mut` keyword comes after the `static` keyword
)
```

The important difference between static and instance members is that static members do *not* get rebuilt for each instance, and in fact, are not reachable directly through instances. Static members are reachable directly through the class instead.

Static members are not allowed to be constructed from the class's constructor.

There are additional rules to `static` members, but these will be discussed in `classes-continued.md`, due to being fairly more complex.

Here is an example of accessing the `shape_name` static member of Overview's example class Circle:

```ruby
let name_of_shape = Circle.shape_name
```

Note that we used the class variable itself. If we tried to access `shape_name` via `some_circle`, we would get a compile-time error.

### Static Functions, aka Static Methods

Static methods are nearly identical to instance methods, they share the same syntax, apart from using the `static` declaration keyword to denote this member kind.

Static methods may refer only to other static members. A static method cannot access an instance member, as those members haven't been created. Accessing static members from a static method is identical to that seen in the instance methods, which is, refer to the static member by name, no access syntax needed. The lowercase "s" `self` keyword is not available inside static methods, as there is no current instance. The capital "S" `Self` keyword is, however, as there is, of course, still the class itself.

Here is an example of method declarations:

```ruby
Class(
	let static Int s = 10
	let Int i = 20

	# Explicit typing
	let static Func(:) some_method_1 = Func(:) {
		print(s) # Accesses the static field `s`
		# print(i) would give a compile-time error, because `i` is not static
		print("I am some_method_1!")
	}

	# Type inferred from value
	let static some_method_2 = Func(:) {
		print(s) # Accesses the static field `s`
		# print(i) would give a compile-time error, because `i` is not static
		print("I am some_method_2!")
	}

	# Value is target-typed to the variable's type
	let static Func(:) some_method_3 = {
		print(s) # Accesses the static field `s`
		# print(i) would give a compile-time error, because `i` is not static
		print("I am some_method_3!")
	}
)
```

Static methods are a subset of static members, and share the same rules, including being unable to be constructed from the class's constructor, and the other rules discussed in `classes-continued.md`.

## Access Control (Publicity)

AntlerScript features the common trio of publicity access modifiers: Public, Protected, and Private. These modifiers can be applied to both static and instance members, regardless of type.

This is a short summary of each:

- Public: Accessible anywhere in the program
- Protected: Accessible within the class itself, and in classes that include this class
- Private: Accessible only within the class itself, nowhere else.

Instead of using keywords like other languages, AntlerScript instead uses the presence of leading underscores in the field's name. A single underscore means Protected, and two underscores means Private.

Here is an example of these:

```ruby
let Thing = Class(
	let Int first = 10 # public
	let Int _second = 20 # protected
	let Int __third = 30 # private

	constructor() {}
)

let thing = Thing{}

print(first) # `first` is public, so we can access it
print(_second) # `_second` is protected, so we can't access it
print(__third) # ERROR: `__third` is private, so we can't access it

let ExtendedThing = Class(
	includes Thing # this will be covered in `classes-continued.md`

	let test = Func(:) {
		print(first) # `first` is public, so we can access it
		print(_second) # `_second` is protected, so we as a subclass (see the includes) can access it
		print(__third) # ERROR: `__third` is private, so we can't access it
	}
)
```

## Class Directives

Apart from members, class bodies may also contain class directives. Not all of them will be covered here, as some are too complex for this intro. Please see `classes-continued.md` for more.

### Constructor

The constructor function of a class is required if you want to be able to make instances of that class. It is important to know that AntlerScript does *not* have default constructors. If a constructor is not present, the class cannot be constructed into an instance. A class is allowed to have up to two constructors: a public constructor and a private constructor.

Because the constructor is not a simple field, it is not written like one. Instead, the word `constructor` is used, followed by parenthesis that denote the arguments, much like a function. The constructor body is required if you want the class to be constructable, but may be empty. If any instance members have no values assigned to them in class body, they are required to be assigned to in the constructor via the constructor body or the argument shorthand.

Here is an example of a constructor:

```ruby
Class(
	let Int i # instance member, no value
	let Int j # instance member, no value
	let Int k = 10 # instance member, has value

	# `i` is assigned via argument shorthand
	constructor(i, Int j_in) {
		j = j_in # `j` is assigned in the body
		# k = 5 <-- This would fail, as `k` is not mutable, and already has a value
	}
)
```

As you can see, `i` and `j` had to be assigned to, since they were lacking values. It's important to note that `i` and `j` are *not* mutable, yet can still be assigned to, because and only because we are in the constructor. The immutable field `k` could not be assigned to either, even in the constructor, because it already had a value. If this constructor did not supply `i` or `j` or both, then there would be a compile-time error.

### Destructor

The destructor is a lot like the constructor, as it is a special function. A destructor may not be present if a constructor also is not present, but a destructor is not required for a constructable class at all. However, instead of running on instantiation, it instead runs when the instance is garbage-collected. Once an instance falls out of scope and its memory set to be reclaimed, the destructor will run if provided. The destructor takes no arguments and returns nothing.

Here is an example of a destructor and its behavior:

```ruby
let Example = Class(
	let Int i = 10

	constructor() {}

	destructor() {
		print("Example instance with i of <i> is being destructed")
	}
)

let fn = Func(:) {
	let ex = Example{}
}

fn() # Here we would see "Example instance with i of 10 is being destructed" because `ex` fell out of `fn`'s scope, and was garbage-collected
```

### Cast

Casts are common, and AntlerScript provides a way for classes to explicitly state what types their instances may be cast to, and how that casting should behave. Cast declarations are simple, being the `cast` word followed by the target type. Here is an example:

```ruby
let Example = Class(
	let Int i = 10

	constructor() {}

	cast String {
		return "Example [i: <i>]"
	}

	cast Float {
		return i as Float
	}
)

let ex = Example{}
let str = ex as String
print(str) # prints "Example [i: 10]"
```

Here you can see that casting the Example instance to a string triggered the cast behavior declared in Example.

There's no limit to how many casts you may have, so long as each one is to a different type. The casting behavior can be triggered with any instance of the class, include `self` instance inside instance methods. Having a cast declaration on the class means that a cast to those types will be known to not fail (thus not evaluate out to an nullable). Cast declaration may rely on other cast declarations too, to reduce code duplication if needed.

## Constructing an Instance

To construct an instance of a constructable class is simple. Reference the class (usually through a variable that points to it) and use braces to supply the class's constructor with its required or optional values. If no values are required, the braces must still be present, but won't need any values within them. Here is an example of constructing a class that requires one value:

```ruby
let Example = Class(
	let Int i
	let Int j

	# `i` being an argument shorthand means that it is required for creating instances
	constructor(i, Int j_in) {
		j = j_in
	}
)

let ex = Example{10, 20}
```

At the last line, the value `10` is provided to the constructor's first argument (`i`). The constructor then assigns that value to instance member `i` via the argument shorthand. The value `20` is provided to the constructor's second argument (`j_in`). The constructor then assigns that value to instance member `j` via the assignment statement in the constructor's body.
