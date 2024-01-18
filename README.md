GryphonScript is a dynamically typed, tree-walk interpreted, functional programming language I developed for fun, which
explores the power of argument holes via the Scala-esque `_` operator (which you can learn more about in the 
Language Tour below!)

After reading Robert Nystrom's [Crafting Interpreters](https://craftinginterpreters.com/), I was inspired to design and
build my own programming language. GryphonScript is an original language implementation which is loosely based on the 
core concepts taught in this book. I've used this project as an opportunity to explore some exciting new Java 21 
language features, such as sealed classed to create [algebraic sum types](https://en.wikipedia.org/wiki/Algebraic_data_type),
as well as pattern matching `switch` expressions to name a few.

After creating this language, I wanted to prove its efficacy by using it in some real-world scenario. Given that I 
completed the MVP in the final weeks of November, [Advent of Code](https://adventofcode.com/2023/about) was the perfect
opportunity _(Advent of Code is an Advent calendar of small programming puzzles that can be solved in any programming
language)_.
* `scripts/aoc_2023_1_1.hs` showcases the elegance of GryphonScript, as Day 1 Part 1 conforms very well to a functional
programming style
* `scripts/aoc_2023_1_2.hs` illustrates the power of GryphonScript, as I solved Day 1 Part 2 using the 
[Aho-Corasick algorithm](https://en.wikipedia.org/wiki/Aho%E2%80%93Corasick_algorithm) for efficiently matching multiple 
strings from a text corpus. This implementation requires complex datastructures to build a trie-based automaton.

_Note: the scripts in the `scripts/*.hs` folder are suffixed with `.hs` (Haskell file type) to leverage the Haskell
syntax highlighting, as this syntax generally maps well to GryphonScrips (although there are some highlighting issues)_ 

## Running the Project
This is an IntelliJ project, and therefore it must be opened in IntelliJ to load
the `.iml` file which defines this project's dependencies: [Lombok](https://projectlombok.org/)
to reduce boilerplate, and [Guice](https://github.com/google/guice) for dependency injection.

Once opened, it can be run in 2 ways:
1. Executing a file by providing a CLI argument of the file path, which is done by creating a Run Configuration in IntelliJ
2. Running the REPL by providing no arguments, which is done by simply running the project in IntelliJ

If your script uses recursion, it is recommended to increase your JVM stack size to prevent
StackOverflowExceptions, as GryphonScript does not support tail-call optimization. Increasing the
stack size to 100MB should be more than sufficient for the majority of scripts, with the JVM arg:
`-Xss100M`

## A Quick Language Tour by Example

```agsl
// Variables are declared with the `let` keyword
let two = 2
let three = 3

// A built-in `print` function is exposed by the `StdLib` module, accessed with dot-notation
StdLib.print( subtract( three, two ) ) // "1"
// To make our lives easier, we can use object destructuring to extract and assign this to a local variable
let { print } = StdLib 

// Everything in GryphonScript is an expression:
let largerNumber = if (two > three) two else three
print( largerNumber ) // "3"

// Block expressions evaluate to their last line. This includes if-expressions, as well as loops:
let i = 1
let newI = while (i < largerNumber) {
    let doubledI = i * 2
    i = doubledI
}
print( newI ) // "4"

// Functions are declared by assigning a lambda expression to a variable
let subtract = \(a, b) -> a - b
print( subtract( three, two) ) // "1"

// Longer functions can be declared using blocks, where the last line of the block will implicitly be returned
let longFormSubtract = \(a, b) -> {
    let result = a - b
    result
}
print( longFormSubtract( three, two ) ) // "1"

// Lambdas can be partially applied using the argument hole `_` literal.
// Provided positional arguments are captured in a closure, and a new lambda is created with arity equal to the number
// of argument holes:
let subtractTwo = subtract( _, 2 )
print( subtractTwo( three ) ) // "1"

// The `_` literal can be used to partially apply any positional argument
let negative = subtract( 0, _ )
print( negative( 10 ) ) // "-10"

// The `_` literal can be used for more than just partial function application. When used outside of a lambda call,
// it will cast its enclosing expression into a lambda, and act as a positional argument. For instance:
let add = _ + _
print( add( 1, 2 ) ) // "3"

// The pipe operator `|>` is a left-associative operator that allows us to pass the left-hand operand in as an argument
// to the right-hand lambda operand
'Hello World' |> print // "Hello World:"

// The pipe operator and argument hole literal can be used together to implement powerful functional programming 
// concepts, as it makes defining functors and predicates effortless. See the below example where we use it to print all
// even squares from the first 10 natural numbers:
let { map, filter } = StdLib.List
let naturalNumbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
naturalNumbers
    |> map( _, _ ** 2 )
    |> filter( _, _ % 2 == 0 )
    |> print // "[4,16,36,64,100]"

// We can take this one step further, and use array destructuring in our lambdas to compute which natural numbers have
// even squares, and only print these natural numbers (not their squares)
naturalNumbers
    |> map( _, \(n) -> [ n, n ** 2 ] ) // map each naturnal number to a list pair of [n, n**2]
    |> filter( _, \([ n, nSquared ]) -> nSquared % 2 == 0 ) // filter odd squares out
    |> map( _, \([ n, nSquared ]) -> n ) // extract the original natural number from each pair
    |> print // "[2,4,6,8,10]"
    
// The StdLib.List module provides many useful list lambdas. One last one we will explore is the `fold` lambda, also
// known as reduce in other languages. Using this we can find the sum of the squares of the first 10 natural numbers:
let { fold } = StdLib.List   
naturalNumbers
    |> map( _, _ ** 2 ) 
    |> fold( _, 0, _ + _ )
    |> print // "385"
    
// To end our tour, we'll look at one more feature of the `_` operator, which is string interpolation.
// When used in a string literal, the `_` will create a function whose positional arguments will be inserted into the
// string:
add(1, 2)
    |> 'The sum of 1 + 2 is _'
    |> print // "The sum of 1 + 2 is 3"
```
