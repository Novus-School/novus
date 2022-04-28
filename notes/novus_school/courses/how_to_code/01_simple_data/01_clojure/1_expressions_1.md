
# Expressions - Part 01

## words of interest:
- building blocks
- expression
- values
- number
- parenthesis
- comments
- primitive operator
-


## Learning Objectives
1. Understand expression and values
2. Understand how commenting works in clojure

Vishal Gautam: So as I said in the introduction,
we're going to design some great programs in this course--
some games, some animations, some information visualization,
some web programs.

Really great stuff.

But the thing is, you got to walk before you can run.
And in the same way, we've got to learn some basic building blocks before we
can build these great programs.
So that's what we're going to do for the next few videos is
learn some basic building blocks out of which we're
going to build bigger programs.
We're going to try to do it quickly because it isn't that exciting.
But we're going to try not to do it too quickly because we want everybody
to be able to stay with us.
So if maybe you've programmed before and this video seems a little slow,
then feel free to speed it up.
But most of the people who take this class
haven't programmed before, so we're going
to try to go a good speed for that.
And if the video's a little too fast, then you
can replay the part of the video that you need to replay.

# Clojure + Atom

As we go through it, I'd encourage you to have DrRacket open and follow along
with the examples that I'm going to do.
So here we go.
When we start Racket for the first time, we
have to tell it what language we're going to use.
Your Racket may have started up already saying, Beginning student language,
down in this lower left corner.
And if it did, then you're all set.
You don't need to do anything.
But if it didn't, then go to the Language menu.
Say Choose Language.
Make sure the How To Design Program part is opened up
and choose Beginning Student.
The way you'll get to this menu will be slightly different
in Windows, of course, but you're going to want to go to the Language menu
to do that.
So go to the Language menu and select Beginning Student Language,
and then you'll be all set to go.
Now that that's done, we can get started.

# Expressions and Values

The top part of Racket here is called the Definitions Area,
and the bottom part is called the Interaction Area.
We're going to start by working up in the Definitions Area.
And we're going to start by writing a simple arithmetic expression.
I'll just put plus 3 4.

```
(+ 3 4)
;; => 7
```

This is how we're going to say to add three to four
in the beginning student language.
And if I ask DrRacket to run that short program, it will.
And down here, it tells me that the result of that program
is 7, which isn't so surprising.
Adding 3 to 4 should produce 7.
This is what's called an expression.
And in the bottom window, we have what's called a value.
And the way Racket is working is we give it expressions,
and it evaluates the expression to produce the value.
Expressions can be more complicated.
For example, we could say plus 3 times 2 3.

```
(+ 3 (* 2 3))
;; => 9
```
And we can run that program, and no surprise, it produces 9.
We can make expressions that are even more complicated than that
or use other primitive operators.
Here we'll just divide 12 by times 2 3.

```
(+ 3 4)
(+ 3 (* 2 3))
(/ 12 (* 2 3))
;; => 2
```
And unsurprisingly, that will give us 2.

## Rules of Expression

Two types of expression:
1. Simple Expression - Values
2. Compound Expressions

### Simple Expression
- number
- keyword
- string
- boolean

### Compound Expression

```
`(<primitive-operator> <expression>...)`
```

- So what we've seen so far is that the rule for the way we form an expression
is open parenthesis, the name of a primitive operator, like plus
or times or slash, and then any number of other expressions followed
by a close parenthesis.
- And there's another rule that says expressions can be actual values.

So numbers themselves can be expressions.

There are two ways to form an expression

1. Value
2. `(<primitive-operator> <expression>...)`


## Comments
Let me show you another thing we can do.
We could take all of this, and if we say to Racket Comment That Out
with Semicolons, then it'll put a semicolon
in front of each of those lines.
And what that tells Racket is, for now, it should ignore everything on a line
after the first semicolon.
So if I would run this program now, as far as Racket's concerned,
there's no expressions in there at all.
And so there's no values that come out.

```
; (+ 3 4)
; (+ 3 (* 2 3))
; (/ 12 (* 2 3))

```

## More Primitive Operations

### rem

Let me tell you about two more primitive operations on numbers,
and then I'll ask you to do an exercise.
The first one is rem.
We call it remainder.

But we dont know how to use it. We have two options
1. google
2. use doc macro

Lets check out the documentation using `doc`

To check the documentation for `doc` we can apply doc on it self like so

```
user=> (doc doc)
-------------------------
clojure.repl/doc
([name])
Macro
  Prints documentation for a var or special form given its name,
   or for a spec if given a keyword
nil
user=>
```

Lets see what we can learn from rem

```
user=> (doc rem)
-------------------------
clojure.core/rem
([num div])
  remainder of dividing numerator by denominator.
nil
user=>
```

Okay so `rem` expects two arguments: `num` and `div` - where num is numerator and div is denominator

Lets see its implementation using `source`
```
user=> (doc source)
-------------------------
clojure.repl/source
([n])
Macro
  Prints the source code for the given symbol, if it can find it.
  This requires that the symbol resolve to a Var defined in a
  namespace for which the .clj is in the classpath.

  Example: (source filter)
nil
user=>
```

```
user=> (rem 10 4)
2
```

If you divide, 10 by 4, the remainder is `2` as we just saw


another one here at the same time-- square root of 16,

```
user=> (Math/sqrt 16)
4

```
and I'll run these two.
And what you're seeing is that `rem` returns the remainder of two numbers, and square root
produces the square root of a number.
OK.
We've seen how to form expressions.
And we've seen a number of `primitives` that
`operate` on numbers, primitives like `plus`, and `times`, and `divide`, and `minus`,
and square, and square root.

## Exercise Time

What I'd like to do now is give you an exercise
that you can use to help reinforce what we've learned so far.
We'll have exercises like this throughout the videos in this course.
I'd like to encourage you to take the time
to do them just to test your understanding of what's come so far.
So here's the exercise.

### Problem - Implement Pythogarus theorm

Assume that the short sides of a right triangle have lengths 3 and 4.
What is the length of the long side? Recall that the Pythagorean Theorem tells us
that:

```
x^2 = y^2 + z^2
```

Write a Clojure `expression` that produces the value of `x` for this triangle
where other two sides have lengths `3` (y) and `4` (z). Your expression shouldn't contain any
numbers other than 3 and 4 (or in other words, all the math should be done in your
expression, not in your head).
