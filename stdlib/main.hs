let add = \(a, b) -> a + b
let add5 = add(_, 5)
let modelNumber = add(600, 70) |> add5

'The best motorcycle is the Daytona ' @ modelNumber @ 'r' |> print

let list = [[1, 2, 3], [4, 5, 6]]


list[1][1] = 100

print(0 >= 0)


let reset = \(l, length) -> {
    let index = length-1
    print(0 >= 0)
    print(index >= 0)
    if (index >= 0) {
        l[index] = 0
        reset(l, index)
    } else 'done'
}

reset(list, 2) |> print
list |> print