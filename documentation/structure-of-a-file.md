# The Structure of a File
AntlerScript is particular about how its files are structured. There are two types of files in AntlerScript: the Main File and Class Files.

Regardless of the type of file, there is a generalized structure they all share. The first lines of the file are meta-directives. These cannot be interacted with at compile, nor runtime, at all. All meta-directives begin with a dollar sign (`$`)

These are the meta-directives and their uses (All-Caps represents user supplied text):

- `$import CLASS_NAME_ONE, CLASS_NAME_TWO`: imports the class(es) for use in the current file
- `$classname CLASS_NAME`: names a class file's class

## File Extension: `.atl`
It is an extremely common practice for a programming language to feature a custom file extension, denoting the file. This is no different for AntlerScript. The only file extension officially recognized by AntlerScript is `.atl`

## Main File
AntlerScript needs to know where to start execution at. Much like how Java has a main method, and C has a main function, AntlerScript has a main file.

This file's name must be `main.atl`, which the AntlerScript compiler will look for in a directory it's pointed to at invocation.

A main file has a very simple structure. It is simply a top-to-bottom script. It executes from the top, and exits at the bottom. There is no main function, as this file's job is to essentially *be* the main function.

A `return` statement can be used to exit from the main file early, and an exit code of type `Int64` may be returned, but is not required.

Here is an example `main.atl` file:

```rb
$import Rectangle, Circle

let my_rect = Rectangle{10, 5}
let my_circ = Circle{ Math.floor(Math.random_between(1, 100)) }

print("Made new Rectangle with area: <my_rect.area>")
print("Made new Circle with area: <my_circ.area>")



if(my_circ.radius % 2 != 0: {
	print("Oops, I only want circles with an even radius!")
	return 1 # exits with code 1
})

# implicitly exits with code 0
```

## Class Files
The structure of class files are a little more complex, these files *are* classes. Please read `classes.md` if you need to familiarize yourself more with AntlerScript's classes.

The beginning of a class file can start with imports, but *must* contain  `$classfile SomeClassName` (the SomeClassName part is up to you to change).

It is good practice to ensure the filename is a snake_case version of the class name, but this is not required.

Essentially, these files are a class expression, but without the `Class(...)` surrounding the body. This means that the only things allowed at the top level of class files are variable declarations and class-directives.

Here is an example of a class file, `circle.atl`:

```rb
$classname Circle

let Float radius
let Float diameter = radius * 2
let Float area = (Math.PI * radius) ** 2

constructor(radius) {}
```

This example file can be thought of as being imported and used in the example main file!
