let { print } = StdLib
let { append, map, fold, size } = StdLib.List
let { length, toCharacters, parseInteger } = StdLib.String

let path = '/Users/yacynug/SideProjects/GryphonScript/aoc/day1/part1.txt'

let characterToDigitReducer = \(acc, cur) -> {
   let maybeNumber = parseInteger(cur)
   if (maybeNumber != nil) append(acc, maybeNumber)
   else acc
}

let digitListToTwoDigitNumber = \(digits) -> digits[0] * 10 + digits[size(digits) - 1]

path
    |> StdLib.File.readLines
    |> map(_, \(line) -> line |> toCharacters |> fold(_, [], characterToDigitReducer))
    |> map(_, digitListToTwoDigitNumber)
    |> fold(_, 0, _ + _)
    |> print