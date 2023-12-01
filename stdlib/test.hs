let { accumulate } = StdLib.List

let p = \([a, b],  c) -> StdLib.print((a+b)*5)

p([1, 2], 5)

(\([a, b]) -> StdLib.print(a+b))([4, 5])

[1, 2, 3, 4] |> accumulate(_, 0, _ + _) |>  StdLib.print