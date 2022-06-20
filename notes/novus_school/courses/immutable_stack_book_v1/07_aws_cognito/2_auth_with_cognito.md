# Cognito Authentication For Datomic Cloud Apps

Operation Prerequisites

- Terraform installed

Data Prerequisites

1. `aws_region` - `us-east-1`

Why is this needed?
- provider
- output

2. `gateway_id`
  - `IonApiGatewayId`
  - `datomic system describe-groups app-prod` -> "http-direct" -> "api-gateway-id"

Why is this needed?
- We needed gateway_id to create: 3 gateway routes and 1 gateway auhorizer

3. `lb_integration`
  - `IonApiIntegrationId`

Why is this needed?
- `lg_integration` gets used when creating public and auth routes


## Using Cognito Authentication with Datomic Cloud

When we deploy an ion application, AWS Gateway is one of of many resources that gets created. It contains a single catch-call route called $default, which simply proxies every request to the deployed ion. Which means currently every route is a public route. In order to implementation authentication, we need to create three API Gateway routes

1. /api/v1/public - public ex: login/register
2. /api/v1/authed - authenticated: ex: courses/lessons
3. /{proxy+} - Creating a catch all OPTIONS route. (Only required for request from a browser)

Our budding new startup is going to need publicly accessible routes for prospective users to ask questions and create accounts. It will also need authenticated routes so the users can… well, let’s keep this part quiet until the multi-billion dollar IPO. For now, it is good enough to say that it will need authenticated routes.

Also for authenticated routes, we will need to create an authorizer.

Q. What is an authorizer?

A Lambda authorizer (formerly known as a custom authorizer) is an API Gateway feature that uses a Lambda function to control access to your API.

A Lambda authorizer is useful if you want to implement a custom authorization scheme that uses a bearer token authentication strategy such as OAuth or SAML, or that uses request parameters to determine the caller's identity.

When a client makes a request to one of your API's methods, API Gateway calls your Lambda authorizer, which takes the caller's identity as input and returns an IAM policy as output.

There are two types of Lambda authorizers:

- A token-based Lambda authorizer (also called a TOKEN authorizer) receives the caller's identity in a bearer token, such as a JSON Web Token (JWT) or an OAuth token.
- A request parameter-based Lambda authorizer (also called a REQUEST authorizer) receives the caller's identity in a combination of headers, query string parameters, stageVariables, and $context variables.


What makes an authorizer? [aws doc](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-apigatewayv2-authorizer.html)

In order to create an authorizer, you will need the following information

Mandatory
- ApiId (api_id): The API identifier.
- AuthorizerType (authorizer_type): REQUEST | JWT
- Name (name): Name of the authorizer

Optional
- IdentitySource (identity_sources): The identity source for which authorization is requested. For JWT, a single entry that specifies where to extract the JSON Web Token (JWT) from inbound requests. Currently only header-based and query parameter-based selections are supported, for example $request.header.Authorization.
- JwtConfiguration (jwt_configuration):


Q. How are we going to make authenticated routes authenticated

Thats a very good question, in addition to creating the routes, we will also have to create the following resources
- Cognito User Pool
- Cognito User Pool Client

Once we have User Pool, we will be able to implement sign up users. For this example we are going to create a default user

Once the user is created. We will then create API Gateway resources i.e

We are going to use terraform to deploy our resource.

### Step 1: Create routes  - Create `scripts/main.tf`


```tf
###########
# Variables
###########
variable "aws_region" {
  type = string
}
variable "gateway_id" {
  type = string
  description = "Physical ID of the `HttpDirectApiGateway`. Found in Cloudformation Outputs tab of Compute Stack"
}
variable "lb_integration" {
  type = string
  description = "Physical ID of the `HttpDirectApiIntegration` found in Cloudformation Outputs tab of Compute Stack"
}

###########
# Provider
###########
provider "aws" {
  region = var.aws_region
}

###########
# Cognito Resources
###########

# =========
# The Cognito user_pool
resource "aws_cognito_user_pool" "pool" {
  name = "ion-example-user-pool"
  password_policy {
    minimum_length    = 8
    require_uppercase = true
    require_lowercase = true
    require_numbers   = true
    require_symbols   = false
  }
  schema  {
    attribute_data_type = "String"
    name                = "email"
    required            = true
  }
  mfa_configuration        = "OFF"
  auto_verified_attributes = ["email"]
  alias_attributes         = ["email"]
  username_configuration  {
    case_sensitive = false
  }
}

# =========
# The Cognito client will be the bridge between the gateway and the user pool
resource "aws_cognito_user_pool_client" "pool_client" {
  name                = "ion-example-user-pool-client"
  user_pool_id        = aws_cognito_user_pool.pool.id
  generate_secret     = false
  explicit_auth_flows = ["ALLOW_ADMIN_USER_PASSWORD_AUTH",
                         "ALLOW_CUSTOM_AUTH",
                         "ALLOW_USER_PASSWORD_AUTH",
                         "ALLOW_USER_SRP_AUTH",
                         "ALLOW_REFRESH_TOKEN_AUTH"]
}

# =========
# There is no direct way to create Cognito users with terraform
# A null_resource allows shelling out to the aws cli for the duties of
# creating a new user and setting the user's password to allow retrieving a token
resource "null_resource" "cognito_user" {

  triggers = {
    user_pool_id = aws_cognito_user_pool.pool.id
  }

  provisioner "local-exec" {
    command = "aws cognito-idp admin-create-user --user-pool-id ${aws_cognito_user_pool.pool.id} --username exemplarUser"
  }

  provisioner "local-exec" {
    command = "aws cognito-idp admin-set-user-password --user-pool-id ${aws_cognito_user_pool.pool.id} --username exemplarUser --password Password1- --permanent"
  }
}

```

Now that the Congito Resources are created, next lets implement API Gatway resources: routes and authorizers

First lets start by creating the public route resource
[doc](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-apigatewayv2-route.html)

```tf
###########
# API-Gateway Resources
###########

# =========
# Creating the public ANY route with the appropriate load balancer integration
resource "aws_apigatewayv2_route" "public" {
  api_id    = var.gateway_id
  route_key = "ANY /api/v1/public/{proxy+}"
  target    = "integrations/${var.lb_integration}"
}
```

The `AWS::ApiGatewayV2::Route` (`aws_apigatewayv2_route`) resource creates a route for an API.

We need to provide three properties:
- ApiId (`api_id`)* - The API Identifier, in our case its the provided gateway ID
- RouteKey* (`route_key`) - The route key for the route. For HTTP APIs, the route key can be either $default, or a combination of an HTTP method and resource path, for example, in our case the public route has `ANY /api/v1/public/{proxy+}` as the route key value


Now that our public route is created, lets see how we can create an authorizer

```tf
# =========
# Creating the gateway authorizer with the above created user pool and client
resource "aws_apigatewayv2_authorizer" "gw_auth" {
  api_id           = var.gateway_id
  authorizer_type  = "JWT"
  identity_sources = ["$request.header.Authorization"]
  name             = "ion-example-authorizer"

  jwt_configuration {
    audience = [aws_cognito_user_pool_client.pool_client.id]
    issuer   = "https://${aws_cognito_user_pool.pool.endpoint}"
  }
}
```

Now that we have created our authorizer, finally lets add the authenticated route, cors routes and as well as outputs

```tf

# =========
# Creating the authenticated ANY route with the appropriate load balancer integration
# and the above created authorizer
resource "aws_apigatewayv2_route" "authed" {
  api_id = var.gateway_id
  route_key = "ANY /api/v1/authed/{proxy+}"
  target = "integrations/${var.lb_integration}"
  authorization_type = "JWT"
  authorizer_id = aws_apigatewayv2_authorizer.gw_auth.id
}

# =========
# Creating a catch all OPTIONS route. (Only required for request from a browser)
resource "aws_apigatewayv2_route" "cors" {
  api_id = var.gateway_id
  route_key = "OPTIONS /{proxy+}"
}

###########
# Outputs
###########
output "user_pool" {
  value = aws_cognito_user_pool.pool.id
}

output "user_pool_client" {
  value = aws_cognito_user_pool_client.pool_client.id
}

output "api_url" {
  value = "https://${var.gateway_id}.execute-api.${var.aws_region}.amazonaws.com"
}

```

### Step 2: Initialize terraform project


Now that we have authenticated and cors routes as well as outputs defined, next step is to initualize a new terraform project using the `init` command

```
> terraform init
```

This will create a new terraform project. Next lets prepare our terraform project using the `plan` command

### Step 3: Plan terraform project

```
> terraform plan
```

Next, let's enter the values for `aws_region`, `gateway_id`, `lb_integration`. (Note: Please look at the Prerequisites section to see how to retrieve these values)


### Step 4: Deploy terraform project

To deploy, we can use the `apply` command. Note: you may have you re-enter values from step 3

```
> terraform apply
```

In the final question answer "yes". Once you hit enter, terraform will start creating AWS Resources - Cognito Use and API Gateway resources. It will also create a sample user that we can test with


### Step 5: Test

Create `input.json` in the root directory. And enter the following values

```
{
    "UserPoolId": "<AWS_COGNITO_USER_POOL_ID>",
    "ClientId": "<AWS_COGNITO_USER_POOL_CLIENT_ID>",
    "AuthFlow": "ADMIN_NO_SRP_AUTH",
    "AuthParameters": {
        "USERNAME": "exemplarUser",
        "PASSWORD": "Password1-"
    }
}


```

Next to retrieve the auth token for this user, we can test it like so

```
aws cognito-idp admin-initiate-auth --region us-east-1 --cli-input-json file://input.json
```


### Step 6: Create auth routes


This will generate an output that contains the auth token. Next, lets actually implement authenticated routes

```clj

(ns novus.auth.routes
  (:require [novus.middleware :refer [token-auth-mw]]))


;;
(defn say-hello-response [{{:keys [username]} :identity}]
    {:status 200
     :body {:message (str "Hello, " username)}})

(def authed-routes
  ["/authed"
   ["/say-hello" {:name ::say-hello
                  :get {:middleware [token-auth-mw]
                        :handler say-hello-response}}]])


```

Next, lets define the token-auth-mw

```clj
(ns novus.middleware
  (:require [ring.util.response :as rr]
            [clojure.string :as str]
            [muuntaja.core :as m])
  (:import java.util.Base64))

(def wrap-env
 {:name ::env
  :description "Middleware for injecting env into request"
  ;; runs once - imporant for performance reasons
  :compile (fn [{:keys [env]} route-options]
             (fn [handler]
               (fn [request]
                 (handler (assoc request :env env)))))})


(defn decode-jwt [jwt]
  (let [[_ payload _] (str/split jwt #"\.")]
    (when payload
      (String. (.decode (Base64/getDecoder) ^String payload)))))

(def token-auth-mw
  {:name ::token-auth
   :summary "Inject a map containing `:username` and `:email` into the key `:identity` on the request.
             The application uses AWS Cognito for request authorization in front of the application.
             By the time we are at application router we are confident we have a valid token. This simply
             decodes the token and injects the user identity into the request."
   :wrap (fn [handler]
           (fn [request]
             (let [jwt (-> request :headers (get "authorization"))
                   decoded-token (when jwt
                                  (->> jwt decode-jwt (m/decode "application/json")))]
               (handler (assoc request :identity {:username (:cognito:username decoded-token)
                                                  :email (:email decoded-token)})))))})


```

Now that we have defined our middleware. lets use our route

```clj
(defn routes
  [env]
  (ring/ring-handler
    (ring/router
      [swagger-docs
       ["/api/v1"
        student/routes
        auth/authed-routes]]
      (router-config env))
    (ring/routes
      (swagger-ui/create-swagger-ui-handler {:path "/"}))))

```

Now that we have added our test auth data, its time to test it. Lets commit all of our changes and deploy our app using ion dev tools

Okay one the deployment is complete. Its time to test our route. And if you test it everthing should work just fine

## Summary

In this lesson you learned how to use Cognito Authentication in your Datomic Ion Apps. You learned the basics of terraform to create aws resources. You learned how to create AWS Cognito User Pool, User Pool Client as well as AWS API Gateway Resources i.e Routes and Authorizors

Finally you added auth routes to your app and as well as a middleware that extracts users email and username.
