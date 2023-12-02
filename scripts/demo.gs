// Ideas:
// Numbers can contain _ for spacing
let a = 1_000_000;

// use _ for partial function application, since it allows for us to decide which argument we are supplying, making
// functions which reverse args, or even support named args, less needed:
  let subtract = (a, b) => a - b;
  let subtract5 = subtract(_, 5);
  let negative = subtract(0, _);

// |> infix/pipe operator
  let negativeTenLiteral = 10 |> subtract(0, _);

// All access to fields is nil-safe by default
  let o = { a: "" };
  print(o.b.c); // nil
  let s = "string";
  print(s.a); // nil
// But we can force nil checks by using !.
  print(o!.b!.c); // Error: 'nil' has no field 'c'

// Everything is an expression. Even blocks, which evaluate to their last field. This also means there are NO return statements
  let a = {
    let a = 1;
    let b = 2;
    a + b;
  };
  print(a); // 3
  // equivalent
  print(if (a == 1) { "foo" } else { "bar" });
  print(if (a == 1) "foo" else "bar");

  // Also works for loops
  let l = [ 1, 2, 3 ]
  let calcMax = (list) => {
    let tmpMax = l[0];
    for (i = 0; i < len(l); i += 1) {
      tmpMax = Math::max(l[i], tmpMax);
    }
  }

// List literal:
let list = [ 1, 2, 3 ];

// Object literals, which allow for field punning:
let a = "1";
let keyForB = 'b';
let o = { a, b: "2", c: { d: "3"} };
print(o); // { a: 1, b: 2, c: { d: "3"} }
print(o[keyForB]); // 2
print(o['b']); // 2
print(o['dne']['dne']); // nil (also nil safe field access)

// function example
let fib = (n) ->
    if (n == 1) 0
    elif (n == 2) 1
    else fib(n-1) + fib(n-2);

// Need to think on:
  // Modules (::) module operator - Or, can just treat modules like objects, which simplifies a lot
  // isInstance() function, maybe: isInstance("hello", Types::String)


// Nice to have:
// List and object destructuring:
let { a, c: { d } } = o;
print a; // 1
print d; // 3

let [ a, b ] = [ 'foo', 'bar', 'baz' ];
print(a); // foo