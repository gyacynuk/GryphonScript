let name = 'Scout'
let message = 'My dog\'s name is _ and he is _ years old'
print(message(name, 15))

let subtract = \(a, b) -> a - b
print( 'subtract result: ' @ subtract( 10, 3 ) )

let negative = subtract( 0, _ )
print( 'negative result: ' @ negative( 5 ) )

let negative2 = 0 - _
print( 'negative2 result: ' @ negative2( 5 ) )

let list = [ 1, 2, 3, 4, 5  ]
let isEven = _ % 2 == 0
let isTrue = _ == true

let result = size( filter( map(list, isEven), isTrue ) )
print( 'composed list lambda result: ' @ result )

let pipedResult = list
    |> map(_, isEven)
    |> filter(_, isTrue)
    |> size
print( 'piped list lambda result: ' @ pipedResult )