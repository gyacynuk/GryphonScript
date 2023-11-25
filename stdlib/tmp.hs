let name = 'Scout'
let message = 'My dog\'s name is _ and he is _ years old'
print(message(name, 15))

print(type(message))

let sum = \(a, b) -> a + b
print( sum( 2, 3 ) )

let subtract = \(a, b) -> a - b
print( subtract( 10, 3 ) )

let negative = subtract( 0, _ )
print( negative( 5 ) )

let negative2 = 0 - _
print( negative2( 5 ) )

let list = [ 1, 2, 3, 4, 5  ]
print(list)

let double = \(number) -> number * 2
let isEven = \(number) -> number % 2 == 0
let isTrue = \(boolean) -> boolean == true

let result = size( filter( map(list, isEven), isTrue ) )
let result2 = list
    |> map(_, isEven)
    |> filter(_, isTrue)
    |> size

print( result2 )