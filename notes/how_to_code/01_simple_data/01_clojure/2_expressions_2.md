# Expressions - Part 02

Solution:

- Let me just quickly show you how I think about it.
- Given the formula, I know that I need to square 3 ```(* 3 3)```, and I
  need to square 4 ```(* 4 4)```, and I need to add those together ```(+ (* 3 3) (* 4 4))```,
  and I need to take the square root of that whole thing

```clj
(Math/sqrt (* 3 3) (* 4 4))
=> 5
```

- And if I run that, I get 5. Sure enough. Let me say a word here about math.

## A Word About Math

- The Pythagorean theorem here is pretty much the hardest math we're going to
  do in this whole course. And that's important because you need to know that
  to design a lot of programs, you don't need to know a lot of math.
- You can be a very `good program designer without knowing a lot of math`.
- There are certain kinds of programs, programs in graphics or vision or
  machine learning, where you do need to know a lot of math. That's because you
  need to understand that **domain**.
- We won't do a lot of math in this course, though.
- The Pythagorean theorem here is about the hardest we'll do.
- There is, however, one little detail I need to tell you.
- A little bit more math, and then we'll go on.


OK.

- Now you've seen how to write expressions that operate on numbers.
- At this point you should have a pretty good sense of how to write such
  expressions, and an intuitive understanding of what they're going to evaluate to.

## Next Lesson:

- In the next video, we're going to look at the more precise rules that
  Racket uses for evaluating them.
- You may wonder at this point are there more primitives that operate
  on numbers?
- And the answer is that there are lots, lots, and lots and lots. Far more
  than you could hope to learn at this point. But if you want to
  discover some of those primitives, I suggest you jump ahead to the video on
  discovering primitives that comes later this week.
- Of course, it's also fine to just stick with the primitives we learn now--
  plus and times and minus and square and square root.

---
Questions

4

1. It will help you learn how to design programs if you learn to identify key technical terms we use, especially when talking about the underlying programming language. This question for example wants you to identify which of the following are `expressions` (check all that apply):
- [x] sqr
- [x] (+ 2 3)
- []  +
- [x] (sqrt 2)
- [x] 1
- [] )

2. Which of the following are `values`:
- sqr
- (+ 2 3)
- +
- (sqrt 2)
- 1
- )


3. What would be the result of evaluating the following `expression`:
```
(/ (* 2 3) (- 3 1))
```


Note that questions 4 and 5 go together; you may want to read both before answering either.
Recall that the average of a set of numbers is the sum of the numbers divided by how many numbers there are.

4. Which of these expressions produces the average of the numbers 4, 6.2 and -12? Check ALL that are correct.

- (/ 3 (+ 6.2 -12 4))
- [x] (/ (+ -8 6.2) 3)
- [x] (/ (+ 4 6.2 -12) 3)
- [x] -0.6

5. Which of these expressions most clearly expresses the idea that it computes the produces the average of the numbers 4, 6.2 and -12?

- (/ 3 (+ 6.2 -12 4))
- (/ (+ -8 6.2) 3)
- [x] (/ (+ 4 6.2 -12) 3)
- -0.6
