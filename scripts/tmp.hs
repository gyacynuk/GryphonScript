let { print } = StdLib
let { map, filter, size } = StdLib.List

let name = 'Scout'
let message = 'My dog\'s name is _ and he is _ years old'
print( message( name, 15 ))

let subtract = \(a, b) -> a - b
print( 'subtract result: ' @ subtract( 10, 3 ) )

let negative = subtract( 0, _ )
print( 'negative result: ' @ negative( 5 ) )

let negative2 = 0 - _
print( 'negative2 result: ' @ negative2( 5 ) )

let list = [ 1, 2, 3, 4, 5  ]
let isEven = _ % 2 == 0
let isTrue = !!_

let result = size( filter( map( list, isEven ), isTrue ) )
print( 'composed list lambda result: ' @ result )

let pipedResult = list
    |> map(_, isEven)
    |> filter(_, isTrue)
    |> size
print( 'piped list lambda result: ' @ pipedResult )

print (2 ** 3)

let naturalNumbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
naturalNumbers
    |> StdLib.List.map( _, _ ** 2 )
    |> StdLib.List.filter( _, _ % 2 == 0 )
    |> print

naturalNumbers
    |> StdLib.List.map( _, \(n) -> [ n, n ** 2] )
    |> StdLib.List.filter( _, \([n, power])-> power % 2 == 0 )
    |> StdLib.List.map( _, \([n, ignored]) -> n )
    |> print

    // Variables are declared with the `let` keyword
    let two = 2
    let three = 3

    // Functions are declared by assigning a lambda expression to a variable
    let subtract = \(a, b) -> a - b

    // A built-in `print` function is exposed by the `StdLib` module, accessed with dot-notation
    StdLib.print( subtract( three, two ) ) // "1"
    // To make our lives easier, we can use object destructuring to extract and assign this to a local variable
    let { print } = StdLib
    print( subtract( three, two) ) // "1"

    // Lambdas can be partially applied using the argument hole `_` literal.
    // Provided positional arguments are captured in a closure, and a new lambda is created with arity equal to the number
    // of argument holes:
    let subtractTwo = subtract( _, 2 )
    print( subtractTwo( three ) ) // "1"

    // The `_` literal can be used to partially apply any positional argument
    let negative = subtract( 0, _ )
    print( negative( 10 ) ) // "-10"

    // The `_` literal can be used for more than just partial function application. When used outside of a lambda call,
    // it will cast its enclosing expression into a lamda, and act as a positional argument. For instance:
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
        |> print

    // We can take this one step further, and use array destructuring in our lambdas to compute which natural numbers have
    // even squares, and only print these natural numbers (not their squares)
    naturalNumbers
        |> map( _, \(n) -> [ n, n ** 2 ] ) // map each naturnal number to a list pair of [n, n**2]
        |> filter( _, \([ n, nSquared ]) -> nSquared % 2 == 0 ) // filter odd squares out
        |> map( _, \([ n, nSquared ]) -> n ) // extract the original natural number from each pair
        |> print

    // To end our tour, we'll look at one more feature of the `_` operator, which is string interpolation.
    // When used in a string literal, the `_` will create a function whose positional arguments will be inserted into the
    // string:
    add(1, 2)
        |> 'The sum of 1 + 2 is _'
        |> print // "The sum of 1 + 2 is 3"

let { fold } = StdLib.List
naturalNumbers
    |> map( _, _ ** 2 )
    |> fold( _, 0, _ + _ )
    |> print

let largerNumber = if (two > three) two else three
print( largerNumber ) // "3"

// Block expressions evaluate to their last line. This includes if-expressions, as well as loops:
let i = 1
let newI = while (i < largerNumber) {
    let doubledI = i * 2
    i = doubledI
}
print( newI ) // "6"

// Longer functions can be decalred using blocks, where the last line of the block will implicitly be returned
let longFormSubtract = \(a, b) -> {
    let result = a - b
    result
}
print( longFormSubtract( three, two ) ) // "1"