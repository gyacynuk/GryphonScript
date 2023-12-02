let { print } = StdLib
let { typeOf } = StdLib.Type
let { append, take, sublist, map, filter, fold, size } = StdLib.List
let { length, toCharacters, parseInteger } = StdLib.String

// Create an array of all lower-case characters
let chars = 'abcdefghijklmnopqrstuvwxyz' |> toCharacters

// Initialize a new trie node, associated with a given character
let initTrieNode = \(char) -> ({ next:{}, char: char })

// Given a trie node, insert a word to rooted at this trie
let insertIntoTrie = \(root, fullWord) -> {
    let aux = \(trie, chars) -> {
        let curChar = chars[0]
        let curTrie = trie[curChar]
        if (curTrie == nil) {
            curTrie = initTrieNode(curChar)
            trie[curChar] = curTrie
        }

        if (size(chars) == 1) {
            curTrie.fullWord = fullWord
            curTrie.isTerminal = true
        } else aux(curTrie, sublist(chars, 1, size(chars)))
    }
    aux(root, toCharacters(fullWord))
    root
}

// Given a list of words, build a trie which contains these words
let buildTrie = \(words) -> {
    let root = initTrieNode('root')
    root.link = root

    words |> map(_, insertIntoTrie(root, _))
    chars |> map(_, \(char) ->
            if (root[char]) {
                root[char].link = root
                root.next[char] = root[char]
            } else root.next[char] = root)
    root
}

// Given a canonical trie, convert it into an Aho-Corasick automaton
let convertToAhoTrie = \(root) -> {
    let getChildren = \(node) -> chars
        |> map(_, \(char) -> node[char])
        |> filter(_, _ != nil)

    let queue = getChildren(root)

    let setNextAndLink = \(u, char, maybeV) -> {
        if (maybeV) {
            let v = maybeV
            v.link = u.link.next[char]
            u.next[char] = v
            append(queue, v)
        } else u.next[char] = u.link.next[char]
    }

    // BFS search trie nodes to ensure we process in level-order traversal. This is needed to maintain the invariant that
    // node u always has a computed suffix link
    while (size(queue) > 0) {
        let [ u ] = queue
        queue = sublist(queue, 1, size(queue))

        chars
            |> map(_, \(char) -> {
                let v = u[char]
                setNextAndLink(u, char, v)
                v
            })
            |> filter(_, _ != nil)
            |> map(_, append(queue, _))
    }

    root
}

// Map of string numbers to their integer representation
let numbersToValuesMap = {
    zero: 0, one: 1, two: 2, three: 3, four: 4, five: 5, six: 6, seven: 7, eight: 8, nine: 9
}

// Build an Aho-Corasick automaton, and return the root of the backing trie
let ahoRoot = StdLib.Type.Struct.keys(numbersToValuesMap)
    |> buildTrie
    |> convertToAhoTrie

// Given a string character, return an integer if the char corresponds to a single digit integer, otherwise return the
// character as-is
let charToDigitOrString = \(char) -> {
   let maybeNumber = parseInteger(char)
   if (maybeNumber != nil) maybeNumber
   else char
}

// A reducer which accumulates a list of lists, where each sublist contains elements only of the same type (eg: all ints,
// or all characters)
let coalesceWithSameType = \(lists, cur) -> {
    let latestList = lists[size(lists)-1]
    if (size(latestList) == 0) append(latestList, cur)
    else {
        let latestType = typeOf(latestList[0])
        if (latestType == typeOf(cur)) append(latestList, cur)
        else append(lists, [cur])
    }
    lists
}

// A reducer which converts lists of the same type (eg: all ints or all characters) into lists of integers. Char lists
// are parsed to integers using the Aho-Corasick algorithm to match text representations of numbers, such as 'one'
let parseNumberFromListUsingAhoCorasick = \(numbers, listOfSameType, ahoRoot) -> {
    if (typeOf(listOfSameType[0]) == 'string') {
        listOfSameType
            |> fold(_, ahoRoot, \(root, char) -> {
                let nextNode = root.next[char]
                if (nextNode.isTerminal) {
                    append(numbers, numbersToValuesMap[nextNode.fullWord])
                }
                nextNode
            })
    } else {
        listOfSameType |> map(_, append(numbers, _))
    }
    numbers
}

// Given a list of digits, return a two digit number where the tens column corresponds to the first element of the list,
// and the ones column corresponds to the last item in the list
let digitListToTwoDigitNumber = \(digits) -> digits[0] * 10 + digits[size(digits) - 1]

// Read the input file, and transform to a single output number
let path = '/Users/yacynug/SideProjects/GryphonScript/aoc/day1/part1.txt'
path
    |> StdLib.File.readLines
    |> map(_, \(line) -> line
        |> toCharacters
        |> map(_, charToDigitOrString)
        |> fold(_, [[]], coalesceWithSameType)
        |> fold(_, [], parseNumberFromListUsingAhoCorasick(_, _, ahoRoot)))
    |> map(_, digitListToTwoDigitNumber)
    |> fold(_, 0, _ + _)