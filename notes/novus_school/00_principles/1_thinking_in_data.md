# Thinking in Data

# Levels

01 - Basic
02 - Intermediate
03 - Advanced
RL - Real Life Production Codebase Simulation (FUTURE WORK)

## What is thinking in data?

Its being able to use the application and think how the data, that powers the application is structured and how it changes when the user interact with application


## But whats the point?

There are many benefits of thinking in data but I will tell you think being able to this: 

1. thinking in data gives a new language which can then be used to study software and find patterns. finding patterns means you are getting familiar with how the software works - you then move to the next step - you can create a mental picture of how a part of software works. For example you can understand how the duolingo quiz works by studying the user actions affects data (using network tab). This mental picture becomes a model to quickly create a mental picture of a situation/software feature, which becomes the model with you can apply in similar situations.

2. thinking in data is a fantastic tool for reverse engineering a software.

3. thinking in data is also language agnostic since data is language agnostic - every programming language has primitive data types, which means it is not just limited to java gang or ruby gang or javascript gang. Its a skill any developer can learn and apply in their work of field

4. thinking in data can be used to debug programs and identify the root cause of the issue - what is a bug? if you think about bug is nothing but bad data - and by indentifying and replacing bad-data with good data, you are essentially "fixing the bug".

5. thinking in data means you are saving lots of resources in your brain - less things to think about. which means that i have more brain energy spent on things that really matters and not on ceremonial stuff


## How do you think in data: Cause / Effect Law

One easy way to thinking in data is thinking in terms of cause and effect. for example in twitter if you dont type anything, then the tweet button is disabled. but once you start typing, then the software nore that there is something written, and if you click on button, it creates a new tweet. 

In this example typing is the cause and button being enabled is the effect and click on a submit is the cause and new tweet is the effect.

Now once you have identified cause and effect - next step is to identify the data related to each cause and effect - this is more of an open ended problem - you can use inspector for example to inspect where the data is coming. This step allows us to build relationship between data and understand how things work.


Lets look at a more concrete example

### Mental Model: Example 1 Duolingo

So Duoligo is an app that lets you learn any language you want. I have personally used it in the past to learn French, Spanish, Portugese, Esperanto, German, Mandarin and Korean. Note that except for French and Spanish, I have forgotten almost all of what I have learned.

One of the nice feature of duolingo is the "skill" feature - where you are given a set of questions - in various formats - and you have to guess the right answer. Now instead of looking from the users eye - lets look at the application from the eye of the software engineer

As a software engineer - I have learned that the best way to get to the heart of the problem is the ask honest intellectual questions - and each question will lead to a clue and by consequently answering the questions, we get closer to the truth - the solution.

So whats the first question we are going to ask

#### Question 1: How is duolingo built?

Duolingo is built using React + Redux

#### Question : Ummm... How did you know that?

Well, if you go `http://www.duolingo.com` and open the inspector tab you will notice that you can click on the `Components` tab.
`Components` tabs gets activated iff it is a react app

### Question 3: I open the tab, but I dont see "Component"? Am I missing something
Ah yes, you have install google chrome exteion - react dev tools

If you use chrome please install [react dev-tools](https://chrome.google.com/webstore/detail/react-developer-tools/fmkadmapgofadopljbjfkapdkoienihi?hl=en), a chrome extestion that allows you to detect if the web application is written in react

You might have to re-start the browser.

### Question 4: Okay I can see the "Components". How did you know that it uses redux?

Simple. You must have noticed that if you inspect the component, you can view the prop of that component. You notice that the top level component is called "l" followed by "Context.Provider". If you click on either of them and look at their prop, you will notice that they have a propery passed called "store". You can right click on it (as of April 7, 2022 it works) and store as a global variable. Lets go ahead and do that.

#### Question 5: Whats next? How do I acess this new global variable

When you clicked on "Store as Global variable" button, the brower created a global variable called $reactTemp0. So if you go to console and type `$reactTemp0`, an redux store gets retured.

Redux store is a javascript `object` containing four properties
```
{ dispatch: ƒ (o)
  getState: ƒ h()
  replaceReducer: ƒ y(e)
  subscribe: ƒ m(e)
}
```

Now if you invoke `$reactTemp0.getState()` - it returns the Duolingo state tree, which is a concept from redux

Let's talk a little bit about what we just did - we just saw what the state tree for duolingo. I am talking about a billion dollar company - where hundred of engineers work on this tree directly or indirectly - every single day

This is what powers duolingo

If you manage to understand this tree of data - and how it changes over time - then you have understood Duolingo

We can even go next step further, and see where all these data are populating from (hint: go to`Network` tab and find out where it is)

Now lets study this redux tree:
