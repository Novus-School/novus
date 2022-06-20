# Tutorial: https://learn.hashicorp.com/tutorials/terraform/aws-build?in=terraform/aws-get-started

###########
# 1. Variables
###########
variable "aws_region" {
  type = string
}
variable "gateway_id" {
  type        = string
  description = "Physical ID of the `HttpDirectApiGateway`. Found in Cloudformation Outputs tab of Compute Stack"
}
variable "lb_integration" {
  type        = string
  description = "Physical ID of the `HttpDirectApiIntegration` found in Cloudformation Outputs tab of Compute Stack"
}

###########
# 2. Provider
###########
provider "aws" {
  region = var.aws_region
}

###########
# 3. Cognito Resources
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
  schema {
    attribute_data_type = "String"
    name                = "email"
    required            = true
  }
  mfa_configuration        = "OFF"
  auto_verified_attributes = ["email"]
  alias_attributes         = ["email"]
  username_configuration {
    case_sensitive = false
  }
}

# =========
# The Cognito client will be the bridge between the gateway and the user pool
resource "aws_cognito_user_pool_client" "pool_client" {
  name            = "ion-example-user-pool-client"
  user_pool_id    = aws_cognito_user_pool.pool.id
  generate_secret = false
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

###########
# 5. API-Gateway Resources
###########

# =========
# Creating the public ANY route with the appropriate load balancer integration
resource "aws_apigatewayv2_route" "public" {
  api_id    = var.gateway_id
  route_key = "ANY /api/v1/public/{proxy+}"
  target    = "integrations/${var.lb_integration}"
}

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

# =========
# Creating the authenticated ANY route with the appropriate load balancer integration
# and the above created authorizer
resource "aws_apigatewayv2_route" "authed" {
  api_id             = var.gateway_id
  route_key          = "ANY /api/v1/authed/{proxy+}"
  target             = "integrations/${var.lb_integration}"
  authorization_type = "JWT"
  authorizer_id      = aws_apigatewayv2_authorizer.gw_auth.id
}

# =========
# Creating a catch all OPTIONS route. (Only required for request from a browser)
resource "aws_apigatewayv2_route" "cors" {
  api_id    = var.gateway_id
  route_key = "OPTIONS /{proxy+}"
}

###########
# 6. Outputs (optional)
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
