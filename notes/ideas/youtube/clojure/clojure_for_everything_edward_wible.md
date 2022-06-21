# Clojure For Everything - Edward Wible

## Growing Nubank

[Applause]

[Music]

Good morning it's really exciting to be here. I had a good day yesterday and hope all of you had too. Thank you to all the nubankers that made this event happen.

I have to admit I had nothing to do with it. I didn't lift a finger and it just happened with lots of courage and we're learning how to do this

I'd like to talk to you this morning about nubank and some experiences within nubank but I'm going to try to tie it back to things that you might get it to apply in your day to day so hopefully  
they'll be able to make this relevant for you.

[0):00:46] The title is “Clojure for Everything” or maybe “Clojure Everywhere” and hopefully it be
Clear by the end.

[00:00:56] nubank tests run very very quickly and for those on the inside
that's been plenty profoundly stressful experience but also very rewarding  
to see people using the products we built and reacting positively to the change
that we are trying to bring to the market ..um first in Brazil but now we're talking  
about going to Mexico, to Argentina. So we're starting the \n international expansion/

< CUSTOMER BASE GROWTH - NUBANK>
https://www.businessofapps.com/data/nubank-statistics/
https://www.reuters.com/business/finance/buffett-backed-nubank-swings-profit-strong-client-base-loan-volumes-2022-05-16/

Year Users
2016 1.3 million
2017 4 million
2018 8 million
2019 15 million
2020 30 million
2021 48 million
2022 (May) 59 million

So this is our customer base growth which is very intense.

— TODO

<CHART ENGINEERING GROWTH>

In addition we've been growing quickly in terms of head count.
So this is what it feels like inside with your colleagues are exploding and this  
is an engineering of course so engineering is about 300 something like that

But this is overall hectic and there are certain \n problems that scale war with headcount than
with customer growth and I want to talk a bit about that.

To give you a sense for some high-level numbers \n

- 330 engineers
- 315 microservices pretty much every single one is Clojure
- about 400 deploys a week

We went yesterday counted all of the lines of Clojure and nubank is 1.7 million lines of Clojure.

[00:02:13] I don't know if that sounds like a lot to me for a bank that actually sounds like very very
little and I think that speaks to the compact and efficiency way that clojure reads and the power that you can do with the few keystrokes of Clojure. So we really like it.

- 3,000 lines of code for service
-

This is an approximation. I think there's probably some test code in there. Usually we think of our services as less than 2000 lines of Clojure - something that you could re-write in a couple of weeks.

We have over

- 2,100 datomic transactors running in production.

There are lots of scale, there's lots of services and lots of Clojure happening and we really really like it. That leads me to the next thing - I wanna talk about which is:

We like Clojure so much that we find ourselves wondering: how can use it more and where are we not using Closure and how can we leverage what over 300 people normally love and

When you come from other languages to Clojure and most people do - we almost hire nobody that already knew Clojure. This is something we teach, we grow - people learn it . It works really well. It is the easiest language to learn even though it doesn’t feel like that initially. So you may have to trust me on that one.

We want to find ways to leverage that because we've invested - we've got 300 people very comfortable with Clojure and when you take that away from people, who have become used to Clojure, they don't like it.

And you can see some of the things happen when we don’t get to work in Clojure in this talk

I'm not going to talk about the production stack today. I’m not \n going to talk about microservice architecture. Kafka, Immutability.. all that stuff, I think it's well covered.

I want to talk about something that I don’t think we have talked about yet - which is basically this iceberg in a sense, the starting from the app right - which is the visible thing. All of our competitors think that competing with nubank means copying the app. 😂

As engineers we know that there's actually a lot of other \n things happening under the waterline. The app is just the tip of the iceberg. But I don’t want to talk about the app, I don’t even want to talk about backends for frontends, the graphQL, pathom. Those excellent API’s. I don’t want to talk about micro services. I don’t want to talk about infra. I don’t want to talk about ETL. I don’t even want to talk about CICD.

I want to talk about something that is super deep which is what we call “glue”. Actually we don’t call it glue. We don’t even talk about it, so thats why I actually want to talk about it.

I want to talk about the glue code, the connective tissue that holds nubank together as we scale to lots more employees. As we scale across countries, there is code that is generally sort of thought of as less important than production code and I’d like that to change.

[00:05:20] So why is it hurting? Why is this relevant?

I think that certain things scale badly in ways that you don’t expect.

In fact our customers is scaling better I think than most people would expect. I don’t want to say that’s the easy part because there are people working really hard on the sharding and making sure that as we add millions of customers a month, everything stays up, everything still runs. But customers in terms of their load profile, in the way customers work in our services actually looked fairly similar. You don’t get to 13 million customers having unique code running for each one of them.

[00:06:00] Unfortunately for employees it's a bit harder. Employees have different functions, have different permissions and different roles. So employee headcount growth actually leads to stress and pressure in the system in ways that is non-obvious especially if you haven't operated at the scale which I assume its not happening

To get there I want to talk a little bit about stories of users and nubank

## Example 1: Marketing Analyst

So let's imagine we have a young marketing analyst and this very passionate, very excited just joined nubank. a very very special moment. They are full of hope, they have a yes they want those ideas to impact customers and make \n customers lives better in this case:

"I have an idea for an email. Customers are confused about their credit card bills...."

true story.. um

"this email will solve it".

less true but lets assume that it is true

"this email will solve that customer confusion and it's just an email. Let's do my idea all right. So let's create something"

That's a great moment. What can happen?

### Chapter 1: The Bright Idea

So first of all young marketing analyst arrives in a team and says:

marketer: "yo!! engineers, let's do my idea".

engineers: "you know cool (sarcastically). We like ideas but you know everybody's got an idea - it's usually easier to think of those ideas than it is to implementthem in production, at scale with a high level of quality. So we tend to have list of ideas that we aren't doing or can't do and that can be disappointing to people but that's been obstruction that's why the game the game works. 😎

But this marketing analyst says

marketing analyst: "they look busy. you know I can I can emphathise with that. The engineering team is busy. I'll do it myself? 😎 why not right? 🤔 YOLO 🧌... "

### Chapter 2: Where to Start

Where to start so this is a repository called "notification", which we created to notifications. back when I used to name services, they were named exactly what they were. Now people name crazy things but this one is notification.

It's a Clojure service. It's 98 percent Closjure. We like it so this is a good circus right. Our marketing analyst can have a good time here

207 contributors

```md
README.md

## Creating new notification

- [] Add Topic to config
- [] Add consumer/consuemr test
- [] Create event at `controller/event.clj`
- [] Add event trigger config to `models/event_trigger.clj`
- [] Add template config to `models/email.clj`
- [] Create postman flow to test new event
```

marketing analyst: "There's a `README.md`. How do I get started? How do I how do I do this?.. Okay it's just a checklist. So all I have to do is....wait....topic, consumer 😅, trigger 😓, template 😨, postman 😰.. wtf is that even mean. I'm confused. On noes 😭."

And suddenly that hope and that optimism is slowly bleeding away and youung marketing analyst says

marketing analyst: "😞 this may not be as easy as I thought, but I'm gonna go for it anyway 💪."

### Chapter 3: The Codebase

`resources/templates/email/activation_reminder/default.html`

marketing analyst 🤓: "so I'm gonna go into this project and I and I'm gonna find, based on other pull requests that I've seen the `resources` folder and I'm going to go in there and I found an `HTML5`. And HTML is NOT CODE so I can do this and lets say I can do do that. And then I notice in other `Pull Request` that there is another `folder` that I have to change and this is called `src` (source). So that sounds a little bit riskier right. We're talking about the source code of a critical infrastructure project, the stoppage of which would be very harmful for our business and our customer's experience.😞 But Yolo right 😂 💀. let's do it anyway."

### Chapter 4: The Pull Request

marketing analst: "I've opened the pull request 💪. I've powered through. I don't know about balancing parenthesis but I did it 💪. I opened it. The engineers don't seem exciting even to review my pull request what is happening and someone told me that I broke an integration test. What is that?"

### Chapter 5: Engineering Timeout

So the time up here is that - what is really happening?

It's not that the engineering team is hostile to marketing analysts or even that the engineering team is unmotivated to work good ideas that will help our customers.

It's an engineering team and their customers are locked into a iteration loop that is unproductive and coupled in an unproductive way.

What have we learned here?

We say that Clojure code is data and data is code but I think for honest this is more code than data and this COULD have been data.

In addition the locality is poor - you have to go and change these completely different branches in different areas of this project to do the incremental thing which is the next email or the next variant of that email. I didn't even get into the guidance of for every new email right a new integration tests right. We actually did that too which is not a good idea.

Third, we've coupled the inner loop of iterating and experimenting this communication right, which is superfast with the deployment of a core infrastructure project.

And lastly, and we didnt even get to this - we have centralized the adaptation of business events from everywhere in the system into notification payloads that could used to interpolate emails to customers. Which means any business event that requires notification also requires a pull request on our notification service and this isnt a bad service. The code isn't bad. Ihe people that wrote it werent.... you know ... not trying to do the job in fact you know I'm largely responsible for these mistakes. It's just that we've got the `domain model` in the dividing line between those different abstractions slightly wrong and that makes all the difference.

## Example 2: Engineering Example

One more one more example. So this is an engineering and I think most of the folks in the audience are software engineers and they would be able to connect with this one.

"I have an idea" it always starts the same right and it's always a new microservice because you're an engineer and now you let's say you already wrote it. You just need to deploy it and how hard could that be

Where to start?

### `nubank/deploy`

- Automated resource provisioning and deploying for AWS

This is a project called deploy and one thing you might be able to see here is that it's 95 percent `Ruby`. Remember when I said that people that get used to Clojure don't like it when you take it away from them. Engineers did not like this project and this is another thing that I did so 😅 I can, I can own that.

When we started `nubank`, `Ruby` was the only language that was comfortable with. I knew we didn't want to use `Ruby` at `nubank` even when we were buildng and I knew we wanted to use Clojure but I didnt know Clojure yet. So I have had these crutches and these fallbacks where if I need to make it work at least I can do it in the language that I know and that I should think, you know, is very well known very well understood that affects everybody.

So I did this and it lived for many years longer than I expected and people still will come up to me and insult me sometimes over this.

So 218 contributors. 15,000 commits in Ruby. Alright you know Yolo!! I can do Ruby! Ownership mentality! I am trying to get the service out, its important. There's a `README.md` but you really can't read that. But in that `README.md` is all kinds of crazy stuff with exclamation points. At nubank we put put an exclamation point at the end of something that has **side effects**.

So there's a bunch of stuff about creating infrastructure and putting in stack IDS and doing crazy stuff. Like maybe you can actually follow along \n with like that usage guide, because you're an engineer, but should you? Should you have to? Let's see what happens:

```groovy

class StackService {
}
```

Here is the source code for file called `stack-service` that sounds like what I'm doing but it's actually a total red herring this has nothing to do with deploying service and

```clj
;; large map file
{}
```

finally we found it again looking at other pull requests that other people did and we have this map and it's kind of declarative no this is kind of nice but my goodness seems large and hard to maintain and kind of unwieldy and at the high-water mark before we killed this thing they were over 2,000 lines of code in this one file. Again the intent wasn't bad. It is kind of declarative but it wasn't the right split and that makes a big difference.

### Pull Request

So the engineer opens Pull Request. Again other engineers don't seem that excited to review my PR. Why is that? Of course, you've broken integration test.

So this is probably an exaggeration of thinking what's worse than a dumpster fire this

< DUMPSTER FIRE PIC>

was the only thing on the internet that was worse than a dumpster fire. And it started at the greatest of intentions, right, the best of intentions and these projects ran nubank for years and it worked but we can do better.

## What have we learned?

What we have what we learned here?

- Teams are correctly protective of critical infrastructure. We don't want critical infrastructure projects being the kind of self-service into large crashes. Also what we were doing that this infrastructure was provisioning CloudFormation template. Basically a declarative template like CloudFormation is defined by the following: you render into the template what you want, NOT how to get it not which steps to take right so it's very different from imperative programming. But we managed to wrap an imperative command line interface around. It's not a beautiful declarative DSL but it works and most importantly AWS has to worry about all of those race conditions and interdependencies between the infrastructures. So that's actually quite impressive and that we managed to do that
- Context switching: 300 nubank engineers, every time you're asking them to have friction in their in their work flow - they go out of their comfort zone and lose efficiency and speed.
- It's kind of hard to visualize the state here right like I ran the command with the exclamation point. Did it work? Is it there? The incentive is for you to go use the AWS console and check and we really don't really want people doing that, no offence to the AWS Console.

<LAUGHS>

## The Problem

The overarching problem of these two examples is the following:

- we've mixed unintentionally data with logic and
- we've mixed platform, that the people should be building on top of with the implementation of that platform and we have this subtle coupling

And the way to know this is happening is that **you feel like people are working too hard** on both sides - so people that are trying to build on the platform are struggling - there's a lot of friction right, that hope is gradually leaving away, that optimism. - In the team that seems unresponsive to floor requests they're not unresponsive at all - they're just getting hammered from all sides because there's too much friction in the inner loop and they are being set up to be gatekeepers for this critical infrastructure and that is a problem.

## Big and Growing

```md
## Big and Growing (opportunity)

- These arent the only examples
- Problem scales (badly) with headcount
  - Employees can be more complex than customers
- Problem scales (badly) with org structure
  - Multi country
  - Multi account
  - Multi Cloud etc
- Friction limits demand

- **With sufficient domain understanding, most problems have a context vs. platform shape**
```

So these are not isolated examples, I had five or six more so I see this a lot. This is the sort of problem that scales with head count not customer growth. It's hard to solve this problem without writing some custom code which is hard to prioritize, when your customer growth is rocket ship. So this is actually a tricky problem to unpack and to prioritize.

And the other thing that this feels badly with is work structure so as you have multiple offices, multiple countries you need stronger connective tissue to make the complexity of operating in that environment easier to deal with. And so at the moment when our connected tissue needs an upgrade we're actually lagging the company in that respect. So this is what I'm refering to as "glue" code. This is a code that we almost think of it as a second class citizen. - its in production, it's in tooling and tooling everybody knows you write in `bash` and unfortunately, thats what we do most of the time thousands of lines of bash and I think only recently that we have stopped to ask ourselves

> "Why, why aren't we using the thing that we know how to do at high quality with good tests, in production? Why are we using that everywhere? Why don't we leveraging that thing that everybody already knows and likes into the other areas that are deeper in that iceberg and its the stuff that we typically, historically havent thought about? "

Lhe last thing that I want to talk about is is that friction right so - sometimes as engineers we can get a misleading demand signal from the amount of people trying to use our platform specifically because our platform is too hard to use. So that friction involved in iterating actually suppresses the demand that you would see if the cost of iterating on top of the platform was lowered. And so we tend to even underestimate these problems when we see them because there are a lot of people who aren't like the young marketing analysts aren't willing to punch through that they'll say - "Ah, lifes too short"

[00:19:31] And I see many patterns are emerging in nubank whereby with a sufficient understanding of the domain, we've been redefined what's the data thing was the platform and kind of refactor that to make for self-service and I see this in other domains. Our position flow - getting your product into the store, our finance team getting your product into our financial accounting and our reporting from central bank.

[00:19:54] There are a lot of things whereby a small, declarative, data driven approach combined with platform automation would actually
decouple and release a bunch of stress in the organization. So maybe you have this sort of things in your companies

## Lets Solve this

[00:20:09] how do we solve this?

### Logic vs Code Churn

My proposal is that we split the high velocity content and churn: when you're talking about content, it's not bad. It's actually a good. Churn means you are moving fast. Churn on a codebase, right, churn turn on logic is generally thought of as bad and engineers will look at that and say that something's wrong with there's a smell here. We shouldnt have 12,000 commits on this repo, we shouldn't have 300 contributors. It's a core infrastructure project and what's going on there.

[00:20:42] So if you separate the content on the platform and the logic, then you can unlock customers to flow at their pace a nd we have a lot of examples of this working at nubank and some of the examples that did not.

[00:20:54] In addition, let robots do the validation and the gatekeeping. You have a very very high leverage moment before merging pull requests whereby you can actually enforce the rules and behaviors and the incentives that will speak louder than words. You could go and give talks to everybody in the company and still, people would understand 40% of what you said. You change an integration test that is the gatekeeper to merging a pull request, and you change behavior, immediately. So let the robots what they do and let the robots synchronize that state to wherever it needs to go.

[00:21:29] Finally let engineers build the next platform versus operating the current platform, thats a clear anti pattern. And the way that you need to break these things apart tends to result in leading to glue them back together, somewhere. So reading the declarative data, parsing it, validating it, synchronizing id and synchronizing that state somewhere, all of that stuff we consider to be "glue code" and all of that was happening in the crazy mix and python, bash and ruby. That clarity and then consistency that we've had on our production stack, we somehow didn't have for everywhere else and I think that was a missed opportunity

[00:22:08] so let's revisit the \n example of notifications here is late \n work today so if you can read the top we \n have a path into every point github and \n the first feature here is that the path \n is a tree which humans already know how \n to navigate \n and so the first 10 minutes you know the \n template is the next portuguese i know \n that i'm working in Portuguese and not \n English or Spanish Brazil I know that \n I'm talking to Tom Brazil not Mexico \n Argentina acquisition this is super \n important there's a team called \n acquisition a new Bank which means we've \n not namespace everything in there based \n on that team so you leave that context \n looking local and then in flight this is \n about inviting customers to be living of \n customers so you've actually spoked it \n down to a feature or a domain and here \n you have tons of variants for a/b \n testing of different emails and the only \n thing that we need to do here is add at \n the end some metadata and add an mg/ml \n file which is an email optimize it \n software HTML if you don't have to edit \n anything else \n so we've achieved locality we've \n achieved the copy-paste that everybody \n wants to do you come here you copy \n something that works you paste it you \n name it get out so this is actually \n something that worked for my marketing \n and the addition that we've given Martha \n us more power as well we've actually \n given control over an a/b testing so you \n can actually set percentage \n distributions for different templates \n here because we don't like talking to \n engineers but this is that fast \n iteration you do not want to be able to \n look at that as engineers in addition we \n can actually compose other tools so we \n have the github UI which is great \n because it means we don't have to build \n a custom UI and so this is actually the \n mg/ml \n that I downloaded for Mac and I can \n actually just navigate that tree \n structure here and visualize a live way \n I'm talking about the rules of the game \n and the beautiful thing that happens \n when you get all this right is that pull \n request reviews happen locally somebody \n that has the context of one either duty \n is going to review and they need to \n submerge you're not waiting for the \n engineers are comfortable with this \n because we're enforcing all those rules \n right here but you can't see me this is \n a new mango CD pull request validation \n that runs and tells us that everything \n is back and you can validate 822 mates \n probably or anything so that way \n engineers are safe \n let's take the example if they deploy \n services production here's another thing \n that we've done this one's harder to \n read but here you have definition of \n services acetobacter that again which I \n thought was a full-service name and no \n idea what that service does but if you \n have a declarative template as well it's \n just an evening somebody has to find the \n rest of the reading for that in the end \n so if you put something wrong it will \n fail so you don't have to be worried \n about that \n you can copy from another one and paste \n and also and then you can send this but \n you only have to define the things here \n that differ from the default which means \n you only need to decide why is my \n service unique and different from the \n standard service as opposed to \n redefining on the with it so engineers \n actually like this they go they put \n their service there and it happens and \n emerges a master and somebody will make \n sure that that's where this happens for \n you so that's that's a delegate to \n again it's back for strong validation \n and it's not enough to know that it's \n valid Jason and they're validating them \n we need to make sure that those rules \n are enforced as if they're not you get \n the risk compensation thing happen \n whereby it's not safe to go fast and \n people slow down \n and that's actually a totally rational \n in the same way that those seat belts in \n cars means that you go slower so this is \n this is your seat belt if you get this \n really really tight people go really \n really fast in that sense of even \n constraints actually Spartan speed right \n in you this is an example smart defense \n so we don't leave an every engineer at \n UMaine to know about JVM options \n certainly don't want them to know about \n scaling and making sure that things \n running multiple availability zones also \n we can actually give them alert \n templates right there's a bunch of \n alerts here that probably are what you \n want and by default that the images \n here's an example of an alert template \n that is integrated with Prometheus which \n then escalates based on thresholds and \n triggers to opportunity which eventually \n calls you in that one at night on the \n weekend which sounds bad but like the \n seatbelt thing that's actually good is \n what that means is you don't have to \n check slack all weekend long you know \n that if something went wrong you're \n getting a call and so ironically by \n putting yourself in on-call rotation you \n have peace of mind it's not exactly what \n people expect so the magic that happens \n once we merge a declarative definition \n of infrastructure to mass spec and \n that's our T so we need enough there are \n other things out \n Tara formation we don't want that for \n our company I think that you probably \n don't want that for your company because \n you're not trying to expose everything \n that's possible on AWS to your internal \n customers you want explosive things that \n you want people to do you want that to \n be opinionated and minimal so that you \n get consistency if it's too wide open \n with you we'll do everything later on \n you can have to figure out you know how \n do I get some leverage here to clean \n things up very cool \n after the floor request validation \n passes and you emerge to master the \n orchestrator of what happens next \n so they're going to which is that we can \n also create a new service if you've \n created in that many and we haven't seen \n before \n we can already want provision that \n service fee \n crypto key sets we can make sure that \n the infrastructure is provisioned we \n controlled a lot of those things that \n tend to be tasks at frictions in the \n engineering day-to-day are often \n overlooked we can actually happen to the \n machines to do that versus having big \n checklists for you as an engineer to \n look for your news you can also write \n size nodes we can creative shards \n there's really no limit to power in this \n DSL has to converge or declarative to \n the value at receive mode so if we take \n a big step back here anything way too \n much to do too much affects you \n frustrated customers my somewhere it's \n take a look at what should be data and \n visit take a look at one of the triggers \n and how it had a completely factored \n those out and decode like that and take \n a look at where is the execution \n happened because it might be that you \n could have you know the language you \n actually like running in different \n places in different contexts and \n actually helping to glue all this \n together \n so for debate the theme that I see and \n then I keep going back to it for lots of \n different use cases not just the coupler \n they said is getting the right \n declarative DSL with the right \n enforcement that giving more ports so \n that our services can react to our food \n base not just our services to react to \n more types of events and I'm referring \n here that ports in adapters we'll talk a \n bit more about that later \n so this okay but that's not clear yet \n and in pleasure we're like why do we \n have to use thousands of lines of bash \n when we could actually make by the way I \n like - I do - but we don't test batch \n and that's the problem \n google recommends anything under 20 \n lines use batch go for it \n but we tend to make we have hundreds of \n lines \n that's not something you want to \n continue so closure will get that's that \n in the end for closure and my interfaces \n in clean attic I'm going to talk a \n little bit about using the same \n application code you might get running \n any different one time context instead \n of containerized customer right now the \n sort of work which is 18 years and \n doesn't have to for declared of DSL is \n the patterns that I'm seeing are and \n these are kind of questions you can ask \n yourself is it local can I make a change \n in one place and that can be the right \n change to me because if you have to go \n and make changes in various places it \n could be you could be the right thing to \n do but it tends to be spot um is it \n random right we don't want this 2,000 \n lines even if that's the clarity that's \n not much fun not least at which because \n that's the recipe for conflicts if \n you're exposing the github to business \n focus which I highly recommend like you \n have a lot of terrain just business \n folks at new thing who are not afraid of \n diplom they they will actually learn how \n that works and it's also two things one \n that's a UI that they can use they \n didn't have to build to business person \n was interacting with engineers you \n actually bringing this looks closer \n together and closer to understand one of \n them so I highly recommend it but if you \n set them up to fail by having huge files \n were buy a few append and someone else \n depends you've already failed which by \n the way is the most likely outcome \n you're not yourself a favor so you \n should be looking for very small files \n even's jason's that can serve regular it \n should also be minimal do not want a \n general purpose it to yourself for the \n unless you want the thing that makes \n sense for your company right and you \n want to expand that as it went a big \n sense but not before giving people way \n too much flats will election lead to \n problems nothing like in my experience \n you also want it to be canonical you \n want completing that again file to that \n repo when that hits master that is the \n truth the truth can't be somewhere else \n if you have two truths you're going to \n confuse people in the offense so the DSL \n has to be the canonical definition of \n the truth it's also audited we've \n convinced the central bank of Brazil to \n look at github as a source of truth \n they've also understood at home as a \n source of audits other benefits for \n having the source of truth we get as \n well you know declarative I think we \n talked a bit about that the other nice \n thing about declarative approaches is \n that they can pose so we can have \n multiple declarative approaches that we \n can actually converge those \n whereas as soon as you have an \n imperative wrapper that is no longer \n compose involved with with other \n portraits I'll give an example of \n declare that formats proposing native \n and also defaults so if there are things \n that you can have safety thoughts do \n that so that people don't have to write \n that the typical features that I see on \n top of these DSL is things like \n validating the equal representation of \n this valley the ansar they do expect \n write to validate inspect you can do \n that at the floor at less time so that \n you're pulling that feedback as close as \n possible to be using the other thing is \n see I see I see I see pipelines you can \n run the same code in here production to \n prod pipeline as you wanted to pour it \n less checks you can reuse them the next \n step is indexing and this is the one \n that we typically miss we leave match so \n - we tend to do fines and a couple the \n things that we're going to do next - the \n physical layout of those files on disk \n you're much more powerful if you haven't \n representation where by 10 units \n typically a closer money and we've have \n all of those files doing same data \n structure and Maori you can actually \n validate relationships between them like \n that a t-test the example it was \n referencing two other templates actually \n exist \n so this is actually you might actually \n just be able to do in terms of ports and \n adapters anyone here familiar with the \n ports of adapters architecture or the \n hexagonal architecture okay a few folks \n typically what this means is that you \n have pure logic in the center if you try \n to pull the side effects communicate \n either with the outside world or \n whatever to the edges and we consider \n ports to be things like HTTP you out \n Kappa message again message power the \n atomic writing reading and so those are \n all things that you want to make sure of \n well fact it to the edge you keep your \n logic middle really but and these really \n are reports that we use most common in \n why does that have to be the case we \n could do a lot more people get \n command-line interfaces generating \n almost like the same thing as he \n requests coming in you could generate \n that fluid command-line interface you \n could do that as a platform we don't do \n this part of the reason is we don't run \n closer to it this way yeah we'll be \n clear so any more words and any more \n power to what people can do as \n interesting as as a future picture in \n terms of closure everywhere so right now \n the only thing that exists in new bank \n is this container service a long-running \n service you want that to be you know \n highly performant latency-sensitive \n super stable auto-scaling \n that has that really good money but I \n think there are other possibilities for \n deploying for shipping that we could \n furnish engineers as a platform the one \n we've looked at first is lambda and this \n is a quick spike that we're doing and \n I'll share alike a little a little but \n it's working well and so you have to \n make a new project you can actually pick \n any function of any closer service that \n is on the outside but we can also think \n of exporting a closure function as a \n doctor eyes command-line interface \n that's available to all with any \n engineers you don't have to put the code \n the powers that lead into the repo that \n houses things in that case \n [Applause] \n but we're all experienced engineers \n everybody can have access this isn't how \n society works though we don't let \n everybody into the kitchen and there's \n actually good reasons for that health \n and sanitation regulation lawsuits there \n is something that we do in society these \n menus and this is good for two different \n reasons one when you order off the menu \n you're not too worried about the \n restaurant burning down as a result of \n you ordering their own dish and your \n expectations about what's available to \n you are aligned if you order something \n that we already make we know how to get \n it \n in contrast adding new items to the menu \n is different and you expect it to be \n different expected to take longer \n expected through all the infrastructure \n program and when people don't know the \n difference between those things you can \n get that coupling whereby expectations \n are not mad if I will be easy in this \n part so and user \n lambdas work like memories in this work \n like maybe a highly protected piece of \n source code that you have a team that \n has master branch protected in urban \n make sure that that code which is \n sensitive is never changed by Tamar \n communities and networks so if you want \n this you as your user necessary to \n [Music] \n modify other it's a bank for engineers \n to change that census because you do not \n have all the positions we need \n everything that they're actually there's \n a robotic efficient and we need the \n thing you're you're triggering is really \n enclosure in its testing did that for a \n long time we had automation which had \n all of our land and event for no \n apparent reason they're the reason why \n they should all have been \n there's Python there's JavaScript \n there's Java there's all kinds of stuff \n in there it's just very different from \n the consistency and clarity that we have \n the rest of the movie so we're gonna be \n visiting this mountain they were saying \n you can actually pick any function \n ownership we have a couple more minutes \n without time five minutes okay I'll try \n to finish quickly and then we feel \n better questions but this is a quick \n experience before it because as the big \n CTO I don't always get time to code but \n when I do this is closer to this project \n what I've seen from closure - is it \n works fine I've heard people say it \n doesn't work I've heard people say that \n there's a false start penalty you know \n that's not the right tool for the job \n in my experience that's my job but it \n can be a big tool I'm seeing we had this \n trip to our library so you couldn't \n learn a whole common library staff \n internet but if she wrote two new \n libraries one for kind of really really \n basically for stuff \n [Music] \n receiver ties it's not too big a \n footprint so we're near the ceiling we \n see millisecond things and the trick to \n that is deciding if it matters a lot of \n things that are more than \n whereby you know she won't care about \n the cool start as long as something \n happens right and you're happy not to \n pay for server for the other 20 days of \n a month that the thing didn't and if it \n is lazy sensitive if you want to be \n performing just put a contrary every \n five minutes it's not super clean but it \n does work and you get to solve on your \n millisecond latency which is the lowest \n it's brand-new the way that either this \n can build with so there's no reason \n either from one performance standpoint \n work developer ergonomics and there's \n some big benefits you can reuse book \n that already exists in that center part \n of your services just reuse that logic \n you don't have to extract it to a \n library you don't have to do together \n which is reuse it you can benefit from \n all of the concurrency benefits of \n closure in terms of lots of little \n regular files processing those things in \n parallel and seeking things in parallel \n you can do Java crypto and actually \n crypto keys can be hands-free you \n couldn't really do JavaScript and you \n can use public text excellent \n [Music] \n [Music] \n so that's a in so this is an example of \n definition and literally if you want \n high performance low latency you just \n add a keep warm crime every five minutes \n we can also slowly this is not example \n for the SL we have a small set of alec \n things that eventually we can do to \n declare a rainbow and that's actually \n compiling behind the scenes into it \n maybe as a service application model \n which is composing the top of a \n formation it's been like a macro expand \n on top of the nation and so what we can \n do is we can actually spec out the input \n type in the output type and then \n generate a bunch of our definition run \n them through all of our framework code \n for provision amendments and make sure \n that the result is a valid \n CloudFormation template a superpower \n find five like a bunch of bugs because \n it varies in your handler at the end of \n the day you have a little where's back \n which you do customized right and \n services that system the spikes \n components start in there started and \n then it stops so we're trying to \n converge and isolate to one place \n inclusion lamb doesn't want to just use \n a death because the things that are \n statically evaluated well the only \n evaluated on pull starts or the deploys \n they will not be evaluated inside the \n inner loop of every invocation and \n that's important for the plant and the \n union player that you have an input in \n context or by you can have you know \n clients that you don't want to be \n instantiate \n instantiate at the time you pull those \n into the context and then you usually \n would delegate to some logic that is \n outside this kinda musical so the very \n thin wrapper it just works so this is \n something that I'm excited about it I \n think it's gonna bring benefits to all \n of that glue that binds us together \n so hopefully that opens data trigger \n restitution I think about those things \n and type find places were like you may \n be coupling all those things into equal \n lines here that favorite has helped us a \n lot and if you're interested in building \n these with us do Bank is growing we're \n clearly growing theater or were also \n opening new offices around the world and \n we're starting to work with remote so if \n they're folks who wouldn't typically be \n able to work with that BBG \n

[Applause]

[Music]

Code

```
JSON.stringify(temp1
    .filter(event => event.segs)
    .map(event => event.segs)
    .flat()
    .map(str => str.utf8)
    .flat()
    .flat())
```
