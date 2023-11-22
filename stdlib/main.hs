let add = \(a, b) -> a + b
let add5 = add(_, 5)
let modelNumber = add(600, 70) |> add5

'The best motorcycle is the Daytona ' @ modelNumber @ 'r' |> print

let simpleList = [ 1, 2, 3 ]
print('Lists are now a thing: ' @ simpleList)
print('They can be indexed (zero-based): ' @ simpleList[0])
simpleList[0] = 100
print('And they can be mutated: ' @ simpleList)

let heterogeneousNestedList = [ [ 1, 2 ], [ 'three', 'four' ], [ print ] ]
print('Since this is a dynamic scripting language, heterogeneous lists of any type are allowed: ' @ heterogeneousNestedList )

let listGenerator = [ _, 2, _, 4 ]
print('But whats really cool is that argument holes can be used in a list literal to create a lambda:' @ listGenerator(1, 3))
print('One more time! ' @ listGenerator(100, 300))