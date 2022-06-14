# AWS 101

## Objectives

- [x] Setup AWS development environment (Create User and assign Ad)
- [x] Learn how to create AWS IAM User + Assign Permissions

## Configure AWS development environment

### Step 1: Create a new AWS IAM User

If you are new to AWS, and you haven't setup your AWS development environment before,
then the first thing you do is create a IAM User and give it Admin Access, so that you have all the
permissions you possibly need for everything we need to do in the workshop including deploying lots of
different resources to AWS env.

First lets search for "IAM" service using the search bar. Once there, go to users section and
click on the "Add User" button. Its should be a blue button

Give your new user a new name. Next we need to enable programmatic access for this user. This enables an access key ID and
secret access key for the AWS Resources

Next, we need to set permissions for this user. Click on the "Attach existing policy directly" and select "AdministratorAccess"
to give the user Admin Access (aka access to all the AWS resources)

Now finally skip all the other steps and create the new user

Once the user has been created, note down the Access key ID and Secret access key. Because once the user is created
and we leave this page, then we won't have access to the secret access key (FYI: we can generate a new one if we forget)


### Step 2: Install AWS CLI

The AWS Command Line Interface (AWS CLI) is a unified tool to manage your AWS services.
With just one tool to download and configure, you can control multiple AWS services from the command line and automate them through scripts.

If you haven't installed aws cli then follow this tutorial - https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html


### Step 3: Configuring the AWS CLI with `aws configure`

For general use, the aws configure command is the fastest way to set up your AWS CLI installation.
When you enter this command, the AWS CLI prompts you for four pieces of information:
- Access key ID
- Secret access key
- AWS Region
- Output format


### Step 4: Optional - Create Profiles with `--profile flag`

If you already have default profile setup, but you want to keep it because you are using it for work then, then you may want to create a new aws profile
on your machine. To create a profile type type `aws configure` then give it a profile flag and a value. For example
```
aww configure --profile dev
```

And then do the same thing - copy access and secret key and so on

Thats how you can create a new profile. After you can use `AWS_PROFILE=dev` - it is saying given this terminal session I want to
always execute everything using this profile.

So thats about it, thats what you need to create a new IAM User
