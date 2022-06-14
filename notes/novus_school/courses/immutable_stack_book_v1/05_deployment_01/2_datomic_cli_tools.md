# Datomic CLI Tools

## Step 1: Download

Go to this [link](https://docs.datomic.com/cloud/releases.html) and download the CLI Tools. Next go to the downloads folder and cd into the directory
```
> cd Downloads/datomic-cli
```

Next, what do we do? Lets see whats inside README.md

Now if we run `bat README.md` (Note: to install bat on mac use: `brew install bat`)

```
│ File: README.txt
───────┼──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
1   │ Datomic Cloud CLI Tools
2   │ =======================
3   │
4   │ Homepage: https://docs.datomic.com/cloud/index.html
5   │
6   │ Usage
7   │ =====
8   │
9   │ Make each of the scripts executable:
10   │
11   │     chmod +x datomic*
12   │
13   │ Each script accepts -h to describe usage. See the Datomic
14   │ Cloud Documentation for more details.
───────┴─────────────────────────────────────────
```

So lets make each of the datomic* scripts executable, we can run this command

```
chmod +x datomic*
```

In linux/UNIX how it is works is that when you download a file it only has two access types: READ and WRITE. `+x` indicates that we are making the files
executable

## Step 2: Run `./datomic`

Next lets run `./datomic` script. This script may download cli tools (if you are doing this for the first time) and complain saying you have passed too few comments.

This is great but it would be even nicer we can run this script from anywhere and not just from inside download folder. To do this we are first going to move the
directory `datomic-cli` to `usr/local` folder


```
mv datomic-cli /usr/local

```

Note: If you got Permission denied error then use `sudo`. Lets try that again using super user do (sudo)

```
sudo mv datomic-cli /usr/local
```

Now to see that the folder is moved, lets `cd` into `user/local`

Next we need to put `datomic-cli` into our path. Not a class path but the unix PATH

If you `echo $PATH`

What happens is the UNIX Operating System will search for `datomic` scriot and if it finds it then it will execute it. you: But how? Thats where
symbolic links come in. We can create a symbolic link to datomic script like so

```
local> ln -s /usr/local/datomic-cli/datomic /usr/local/bin/datomic
```

This is saying we would like to create a link to `usr/local/bin/datomic`, which calls the `/usr/local/datomic-cli/datomic` script.

If you go to `/usr/local/bin` you should see a script called `datomic`, which is pointing to `/usr/local/datomic-cli/datomic` script

```
drwxr-xr-x  42 root          wheel   1.3K  9 Jun 21:20 .
drwxr-xr-x   9 root          wheel   288B  9 Jun 21:17 ..
lrwxr-xr-x   1 root          wheel    30B  9 Jun 21:20 datomic -> /usr/local/datomic-cli/datomic

```

With our symbolic links in place, we are ready to test datomic script from `novus` directory

```
> novus

usage: datomic [options] <command> <subcommand> [parameters]

Command line tools for managing Datomic

For more help:

  datomic help
  datomic <command> help
  datomic <command> <subcommand> help

Options:
  -h, --help             This help message. Specify `<command> help` for help about a command
  -r, --region REGION    AWS Region of the Datomic system
  -p, --profile PROFILE  Named profile from AWS credentials file

Available Commands:
  analytics
  client
  cloud
  gateway
  log
  solo
  system

```

Lets see the list of all the systems on AWS, to do that we can run `datomic cloud list-systems`, after a while it returns something like this
```clj
[{"name":"app-prod", "storage-cft-version":"973", "topology":"Unknown"}]
```

For you probably returns an empty vector and thats normal, you haven't deployed anything yet

As you can see I have a single system running called "app-prod". Topology is unknown because it's a custom topology called the "Split Stack" which you will learn next.

## Summary
