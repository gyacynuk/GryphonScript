let struct = { greeting: 'hello', b: 'world', nested: { a: 'Im nested!', nums: [3, '.', 1, 4, 1, 5, 9] } }
let { greeting, nested: { nums: [ one, two, three, four ] } } = struct
print(greeting @ '!')
print('🥧: ' @ one @ two @ three @ four)

let performance = \(func, numRuns) -> {
    let i = 0
    let totalTime = 0
    while (i < numRuns) {
        i = i + 1

        let startTime = milliTime()
        func()
        let endTime = milliTime()

        totalTime = totalTime + (endTime - startTime)
    }

    totalTime / numRuns
}

let slowFib = \(n) -> {
    if (n == 0) 0
    else if (n == 1) 1
    else slowFib(n-1) + slowFib(n-2)
}

let fastFib = \(n) -> {
    let mem = [ 0, 1 ]
    let m = 1
    while (m < n) {
        m = m + 1
        let [ pp, p ] = mem
        (mem = [ p, pp + p ])[1]
    }
}

let n = 25

print(fastFib(n))
print(slowFib(n))

performance(\() -> slowFib(n), 5)
    |> 'slowFib took an average of _ms to compute the _th fib number'(_, n)
    |> print

performance(\() -> fastFib(n), 5)
    |> 'fastFib took an average of _ms to compute the _th fib number'(_, n)
    |> print

1 @ 2 @ 'three' |> print