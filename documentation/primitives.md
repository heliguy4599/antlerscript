# Primitive Types

## Bool
A data type representing a true or false value.\
Literals: `true`, `false`

Casts:\
`(U)Int`: `true` -> `1`, `false` -> `0`\
`Float`: `true` -> `1.0`, `false` -> `0.0`

## Int
A data type representing a signed, whole number, positives and negatives. Ints have multiple different sizes:\
(Int8, Int16, Int32, Int64, Int128), Int is an alias for Int64\
Literals:\
`1`, `27`, `-5`, etc

Casts:\
`Float`: the_int.0\
`Bool`: `0` -> `false`, not `0` -> `true`

## UInt
A data type representing an unsigned, whole number, only positives. UInts have multiple different sizes:\
(UInt8, UInt16, UInt32, UInt64, UInt128), UInt is an alias for UInt64\
Literals:\
`2`, `14`, `5`, etc

Casts: see **Int**, but wraps large numbers to negatives

## Float
A data type representing a signed, fractional number, in accordance to IEEE 754. Floats have multiple different sizes:\
(Float16, Float32, Float64, Float128), Float is an alias for Float64\
Literals:\
`2.0`, `-9.7`, `3.1415926`, etc

Casts:\
`(U)Int`: the value, floored\
`Bool`: `0.0` -> `false`, not `0.0` -> `true`

### Changing the base of Ints, UInts, and Floats
All Ints, UInts, and Floats can have their base change according to prefixes. By default, these literals are in base 10, decimal.\
`0x` -> Hexadecimal, base 16\
`0o` -> Octal, base 8\
`0s` -> Senary / Seximal, base 6\
`0b` -> Binary, base 2\
Example: `0xF8.D`, `0b101`, `0o17.2`, etc

## String
A data type representing a length of text. Strings are the most complex of the primitives. Strings are immutable.\
Literals:\
`"hello, world"`, `"a"`, `"123"`, etc

All strings are multi-line, there is no difference between single and multiple lines in a string. Multi-line strings set their horizontal baseline to the left-most non-whitespace character.

Strings cannot be casted to anything. Instead, they feature methods to convert directly to the desired outcome like `parse_int`, `parse_float`, and `is_empty` for example.
