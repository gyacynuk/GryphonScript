let add = \(a, b) -> a + b
let add5 = add(_, 5)
let modelNumber = add(600, 70) |> add5

'The best motorcycle is the Daytona ' @ modelNumber @ 'r' |> print
