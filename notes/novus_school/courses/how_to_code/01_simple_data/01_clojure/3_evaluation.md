# Evaluation

This section presents the detailed rules that DrRacket uses to evaluate the kinds of BSL expressions we have seen so far. Understanding these rules is key to understanding how your programs really work, and will help you figure out what is wrong if your program does not work as expected.


In the last video, we learned how to write expressions that operate on numbers. That
video should have given you a pretty good sense of how to write those expressions.

In this video, we're going to look in more detail at the rules Clojure
uses to evaluate those expressions. Now, you may find it a little pedantic
to talk about the precise rules for evaluating an expression like plus 1 2.

And if all programs were that short, you'd be right. But here's something to consider.
The Chevy Volt has 10 million lines of source code in it, 10 million lines of source
code in a car.

So programs get quite big. If that was a program written in the
clojure programming language, it would have many more than 10 million expressions
in it.

And it turns out that in order to be able to understand big programs,
we really need to understand the detailed rules by which expressions
get evaluated.

We don't always need to think in terms of those rules, but we do need to
have them to fall back on.

So that's what we're going to look at in this video.

Here's the expression that I'd like to work with.

And if I just run this, I'll get the value 14.

What I want to do, though, is look at the detailed step-by-step process
that Racket uses when it evaluates this expression that results in 14.

Before I do that, I want to introduce an additional bit of terminology here.

We're going to say that an expression, like this one, because it starts
with an open parenthesis and then the name of a primitive operation,
is called a primitive call, or a call to a primitive.

And in a primitive call, we're going to say that this is the operator.

And in a primitive call, we're going to say
that all of these expressions that follow the operator-- in this case,
there's three of them-- are the operands.

And of course, we could do the same thing
because there's another primitive call sitting right here.
And inside that primitive call, that's the operator
and those are the operands.

And I could do the same thing over here.
So that's a bit of terminology.

Now let's look at the step-by-step process that takes that expression
and ends up producing the value 14.

Well, the first thing that happens is Racket
is asked to evaluate this entire expression.

And it sees that it's a primitive call because it
starts with an open parenthesis and a primitive operator.

`So the rule for evaluating a primitive call is that first all of the operands
need to be reduced to values.`

Well, this operand, the first one, already is a value.
So no evaluation work's going to be required there.
It already is a value.

But this operand is an expression. It's not a value.

So some work is going to be required there.

Let's look at that.

Well, this whole thing is a primitive call
because it starts with a parenthesis and a primitive operator.

So let's look at its operands.
Well, that first operand is already a value.

So no work required there.

And that second operand is already a value, so no work required there.
So at this point, the two operands in this primitive call
have been reduced to values.

And so this multiplication can happen.

And this whole thing is just going to become 12.

So let's say that in the first step of the evaluation,
this whole expression becomes this.

Now, I've left some extra space in here that you wouldn't normally
leave here just to make things continue to line up.

But here we see that in the first evaluation step,
that operand gets reduced to the value 12.

Well, now we reduced the first two operands to plus to values.
We need to work on this third one.

It's an expression now.
It's not a value.

So we need to work on it.
Let's see, open paren, the name of a primitive operation.

This first operand here is not a value.
So we need to reduce it.

Let's see.

Open paren, the name a primitive operation.

This operand is a value.
This operand is a value.

So at this point, this thing here, we can do the plus to get three.
So the next step of the evaluation is this.
So that plus 1 2 has now become 3.
So again, we were working on these operands to minus.
This one's a value now.
This one already is a value.
So this minus can happen to get zero.
At this point, all the operands for that plus
have been reduced to values so that plus itself can happen.
And the whole thing, of course, becomes 14.

We'll learn more about the detailed step-by-step evaluation
rules in a couple more videos.
But for now, the intuition to take away from this
is that evaluation of an expression in general proceeds from left
to right and from inside to outside.
So as we evaluate the outer plus, this times three four
happens first because we go left to right.
But then when we try to do this whole expression, this plus 1 2
has to happen first because of the inside to outside behavior.
Because all of the operands to that minus have to become values
before the minus can proceed.

That left to right inside to outside intuition
is an important way to understand the evaluation of expressions
in the beginning student language.
Now you've seen the first evaluation rule, the primitive call rule.
And you've seen how repeated application of that rule
can evaluate numerical expressions of arbitrary complexity.
You've also seen how repeated application of that rule
leads to a left to right inside to outside evaluation order.
One of the great things about our beginning student language
is that it actually turns out not to have very many evaluation rules.
We're going to learn three more in the first week of the course,
and then just three more the entire rest of the course.
And that's going to let us write programs of arbitrary complexity.
That's one of the reasons why we're using the BSL language.
It lets us spend not very much time explaining the language
so we can focus more of our time on how to design programs.
And that, the ability to design programs in any language,
is really the most important thing to learn.

## Questions ===
1. Consider the following expression:
```
(* (- 4 2) 3)
```

1. Select all calls to primitives.
- [x] (* (- 4 2) 3)
- *
- [x] (- 4 2)
- 3
- -
- 4
- 2


2. Select all operators.
- (* (- 4 2) 3)
- [x] *
- (- 4 2)
- 3
- [x] -
- 4
- 2

3. Select all operands.
- (* (- 4 2) 3)
- *
- [x] (- 4 2)
- [x] 3
- -
- [x] 4
- [x] 2

4. How many call to primitives do you see:
- 2

5. How many operators do you see:
- 2

6. How many operands do you see:
- 4


Notice how # call to primitives is same as # operators.
Which means if there are 'n' number of operators means there will be 'n' number of primitive calls
