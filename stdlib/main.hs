let add = \(a, b) -> a + b
let add5 = add(_, 5)
let modelNumber = add(600, 70) |> add5

'The best motorcycle is the Daytona ' @ modelNumber @ 'r' |> print

let list = [[1, 2, 3], [4, 5, 6]]
list[1][1] |> print

list[1][1] = 100
list[1][1] |> print

list |> print

let set = \(l) -> l[0] = 0
set(list) |> print
list |> print