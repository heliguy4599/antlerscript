# Types Overview
AntlerScript is a statically typed language, meaning that data types of variables cannot be changed at runtime.

Types consist of the data type, and the nullability (if the data type can also represent null).

In AntlerScript, types are values, first-class citizens. You can pass them around, return them, and compare between them. However, due to being strictly typed, AS still enforces type safety, even when doing actions like this.

To make a type nullable, append a question mark: `Int` -> `Int?`. This works anywhere that a type is used, be that function arguments, class member types, or list item types.

AntlerScript has primitive types, which have a direct way of writing them in your code, and complex types, which require special syntax of evoking a new variable of them.

The syntax for complex types is as follows: `TypeName(argument syntax){initializing data}`. The `(argument syntax)` part is omitted when the type has no argument syntax, but the `{}` are always present even if there is no initializing data.
