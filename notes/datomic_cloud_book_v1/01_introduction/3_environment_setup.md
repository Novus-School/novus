## Environment Setup


In many of the subsequent lessons, we'll be directly interacting with the datomic client api. To do this, we'll need to set up our environment. First step is to install datomic `dev-local`

# How to get started with datomic`dev-local` in 5 steps

**Requirements: Before you start the tutorial you should have following installed**
- [Java](https://www.oracle.com/java/technologies/downloads/)
- [JDK v17](https://jdk.java.net/17/)
- maven: `brew install maven`
- Clojure: `brew install clojure/tools/clojure`


## Step 1: Download dev-local

- Download cognitect [dev-local](https://cognitect.com/dev-tools) software.
- You will need to provide a valid email address.

## Step 2: Run `./install.sh` from the downloaded folder

- Check your inbox/junk folder for an email titled "Cognitect dev-tools download" - click on the download link provided in the email. Head over to `downloads` directory
- Unzip the downloaded file and cd into the directory and run the following script `./install.sh`. This script will install following clojure libraries:
    - [rebl](https://docs.datomic.com/cloud/other-tools/REBL.html) - REBL is a graphical, interactive tool for browsing Clojure data
    - datomic/dev-local - allows you to develop datomic cloud applications locally

```zsh
Installing: com.cognitect/rebl {:mvn/version "0.9.244"}
Installing: com.datomic/dev-local {:mvn/version "1.0.242"}
```

## Step 3: Specify storage location for datomic for local development

- Next step is to specify the storage location for datomic.
- Head to home directory and create a directory called `.datomic`avigate to `.datomic` directory and create a file called `dev-local.edn`. Inside the `edn` file create a map and specify the storage directory `:storage-dir`.  
- In my case the value is `/Users/vishalgautam/.datomic/storage`.
```clj
{:storage-dir "/Users/vishalgautam/.datomic/storage"}
```
Please note that it needs to be a full path. With this done we are done with the dev local. Next step is to configure your secret `settings.yml`

## Step 4: Configure `settings.yml`

- First go to the `home` directory and navigate to `.m2`.
- Inside `.m2` directory should contain a single directory called `repository`.
- This directory contains all the cached folders from maven. Lets create a file called `settings.xml`

```sh
➜  .m2 touch settings.xml
```

Next paste the configuration from the email into `settings.xml`

![Install Finish](settingsxml.png)

## Step 5: Configure `~/.clojure/deps.edn` file

- Add an entry under the `:mvn/repos` key in your ~/.clojure/deps.edn file.
- You only need to do this once, nothing else needs to be done per-project to specify maven information.

```
{:mvn/repos {"cognitect-dev-tools"
              {:url "https://dev-tools.cognitect.com/maven/releases/"}}}

```

Once this step is done we are ready to use datomic dev local in our clojure app
