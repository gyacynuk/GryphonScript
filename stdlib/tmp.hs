let startTime = milliTime()

let makeCounter = \() -> {
    let count = 0
    \() -> count = count + 1
}

let printWithCounterName = \(name, counter) -> print(name + ': '  + counter())

let counter1 = makeCounter()
let counter2 = makeCounter()

printWithCounterName('Counter1', counter1)
printWithCounterName('Counter1', counter1)
printWithCounterName('Counter1', counter1)
print('')
printWithCounterName('Counter2', counter2)
print('')
printWithCounterName('Counter1', counter1)
printWithCounterName('Counter1', counter1)

let endTime = milliTime()

print((endTime - startTime) / 1_000)

let a = \(n) -> if (n == 0) 0 else n + a(n-1)

print(a(3))

let const = 1

print(1 + if (const == 100) { 100 } else 50 + 2 / 2 * 4)

if (true) const = const + 1

if (!false) {
    const = const + 10
}

const |> print

const = 0
while (const < 10) {
    const = const + 1  |> print
}
const |> print