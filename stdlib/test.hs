let struct = { greeting: 'hello', b: 'world', nested: { a: 'Im nested!', nums: [3, '.', 1, 4, 1, 5, 9] } }
let { greeting, nested: { nums: [ one, two, three, four ] } } = struct
print(greeting @ '!')
print('🥧: ' @ one @ two @ three @ four)

let fastFib = \(n) -> {
    let mem = [ 0, 1 ]
    let m = 1
    while (m < n) {
        m = m + 1
        let [ pp, p ] = mem
        (mem = [ p, pp + p ])[1]
    }
}

print(fastFib(25))
