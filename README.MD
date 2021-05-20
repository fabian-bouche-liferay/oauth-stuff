# OAuth 2.0 Proof of concepts

## Liferay Token Exchange RFC 8693

### Concept

https://datatracker.ietf.org/doc/html/rfc8693 is a specification that describes the contract for
a token exchange procedure.

The idea is to allow an OAuth 2.0 Authorization Server to deliver a token in exchange for another
existing token (usually a token provided by another authority).
 
The specification leaves a lot of freedom. The input token for the exchange could be anything.

The proposition of this proof of concept is to make Liferay expose a Token Exchange endpoint.

This way, we would allow Liferay to trust tokens emitted by third party IAM Authorities.

I wrote an earlier PoC https://github.com/fabian-bouche-liferay/external-oauth where the idea
was to trust those tokens directly on the API endpoints. The con of this approach is that it is
a bit intrusive.

This new approach allows us to have a mechanism for the generation of a **native** Liferay   
Access Token. The con is that you need the API client to make an additional HTTP request
to that new endpoint before being able to make calls to Liferay APIs.

But there are advantages:
 - External token validation is made once for all
 - My opinion is that it is less intrusive in the Liferay core code
 
### Managing multiple token exchange procedures

The token exchange feature is likely not to be **natively implemented** in Liferay because
the **algorithm** for token exchange is very contextual. How to map claims and scopes is likely
to vary a lot from an external authority to the other. There are just too many combinations.

As a consequence, my proposition is that we have a `TokenExchangeService` interface that can
be implemented through as many specific implementations as external authorities one needs
to integrate with.

I've provided an example here: `JwtAccessTokenEExchangeServiceImpl` which I have tested against
a Keycloak server that delivers Access Tokens in the JWT format with specific claims.

In that particular example, I'm mapping the JWT token to a Liferay user by checking an `email`
claim in the JWT token and doing a 1 to 1 mapping of scopes (meaning that I've defined the
exact same scope names in Keycloak than in Liferay).

As said earlier, there could be many other possibilities and as many implementations.

So the idea is to have a `ServiceTracker` in the `TokenExchangeServlet` that fetches
the right implementation for the token using a property in the TokenExchangeService
component's registration.

### Testing!

Setup an external Authorization Server that delivers JWT OAuth 2.0 Access tokens.
Configure it so that it delivers `Liferay.Headless.Delivery.everything.read` among the scopes of
the JWT token's `scope` claim.
 
I've used Keycloak for my PoC.

You shall now create a user with the same email address both in Keycloak and Liferay.
And you shall create an *OAuth 2 Application* in Liferay and a *Client* in Keycloak
with **matching client ids**.

Make an authorization code flow against Keycloak and extract the JWT access token.

Now, it the Liferay http://localhost:8080/o/token-exchange endpoint with 
`Content-Type: application/x-www-form-urlencoded` form `POST` with the following fields:

```
grant_type=urn:ietf:params:oauth:grant-type:token-exchange
subject_token=The_JWT_Token_You_Got_From_Keycloak
subject_token_type=urn:ietf:params:oauth:token-type:access_token
```

The Liferay endpoint will reply with something like that:

```
{
	"access_token":"e13ffaae6d2670433cd39b75f1511728fa29a5649422a8b249c65aa6d450b6",
	"issued_token_type":"urn:ietf:params:oauth:token-type:access_token",
	"token_type":"Bearer",
	"expires_in":600,
	"scope":"openid,email,profile,Liferay.Headless.Delivery.everything.read,refreshToken",
	"refresh_token":"baae994565f83629b83efc93d28e4f60b9efd16fcc5243dff2df4e452384052"
}
```

You can now use that access token against the Liferay APIs!