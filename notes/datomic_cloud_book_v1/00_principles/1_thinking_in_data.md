# Thinking in Data

# Levels

01 - Basic
02 - Intermediate
03 - Advanced
RL - Real Life Production Codebase Simulation (FUTURE WORK)

## What is thinking in data?

Its being able to use the application and think how the data, that powers the application is structured and how it changes when the user interact with application

Lets look at an example

### Example 1: Duolingo

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

