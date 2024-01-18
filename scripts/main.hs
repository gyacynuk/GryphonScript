let { print } = StdLib
let { size } = StdLib.List

let add = \(a, b) -> a + b
let add5 = add(_, 5)
let doSomething = _(1, 2)
let modelNumber = add(600, 70) |> add5

print('Doing something ' @ add(
    doSomething(add),
    doSomething(\(a, b) -> a - b)))

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

print('') print('-----') print('')

let add10 = _ + 10
print('100 + 10 = ' @ add10(100))

print('Or in other words...')
print(100 |> _ + 10)

print('') print('-----') print('')

let pt = \() -> {
    print('this\nis
    a
    \'test\'')
}()

print('String _ is now a _'('interpolation', 'thing'))

print(size(simpleList))

let printListElements = \(list) -> {
    let helper = \(l, i) -> {
        if (i < size(l)) {
            print(l[i])
            helper(l, i+1)
        }
    }
    helper(list, 0)
}

listGenerator(100, 300) |> printListElements

let s = { a: 'hello', b: 123, c: { add, a: 'I\'m nested!' } }
print(s)
s.foo = 'bar'
print(s)
print(s['foo'])
print(s.c.add(100, 1000))

print(simpleList ++ simpleList)

let { b } = s
print(b)

let s = { a: 'hello', b: 'world', c: { a: 'Im nested!', nums: [3, 1, 4, 1, 5, 9] } }
let { b, c: { a } } = s
print(b)
print(a)

let fastFib = \(n) -> {
    let mem = { pp:0, p:1 }
    let aux = \(n) -> {
        if (n > 2) aux(n-1)
        let { pp, p } = mem
        let cur = pp + p
        (mem = { pp: p, p: cur }).p
    }
    n |> aux
}

print('5:_, 7:_'(fastFib(5), fastFib(7)))

