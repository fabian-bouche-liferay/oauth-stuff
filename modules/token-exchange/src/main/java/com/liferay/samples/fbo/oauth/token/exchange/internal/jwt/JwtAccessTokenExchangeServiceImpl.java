package com.liferay.samples.fbo.oauth.token.exchange.internal.jwt;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.liferay.samples.fbo.oauth.token.exchange.InvalidTokenException;
import com.liferay.samples.fbo.oauth.token.exchange.TokenExchangeConstants;
import com.liferay.samples.fbo.oauth.token.exchange.TokenExchangeResponse;
import com.liferay.samples.fbo.oauth.token.exchange.TokenExchangeService;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate = true,
		property = (
				TokenExchangeConstants.TOKEN_SUBJECT_TYPE + "=" + TokenExchangeConstants.JWT_ACCESS_TOKEN_SUBJECT_TYPE
				),
		service = TokenExchangeService.class
		)
public class JwtAccessTokenExchangeServiceImpl implements TokenExchangeService {

	@Override
	public TokenExchangeResponse getTokenExchangeResponse(String token, long companyId) throws InvalidTokenException {
		TokenExchangeResponse tokenExchangeResponse = new TokenExchangeResponse();

		DecodedJWT jwt = JWT.decode(token);
		String issuer = jwt.getIssuer();
		String kid = jwt.getKeyId();
		
		String jwks = issuer + "/protocol/openid-connect/certs";
		
		try {
		
			JwkProvider provider = new UrlJwkProvider(new URL(jwks));
		
			Jwk jwk = provider.get(kid);

			Client client = new Client();
			client.setClientId(jwt.getClaim("azp").asString());
			
			Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
		    JWTVerifier verifier = JWT.require(algorithm)
		        .withIssuer(issuer)
		        .build();
		    
		    verifier.verify(token);
		    
		    String approvedScopes = jwt.getClaim("scope").asString();
		    StringTokenizer approvedScopesStringTokenizer = new StringTokenizer(approvedScopes, " ");
			
		    List<String> approvedScopesList = new ArrayList<>();        
		    while (approvedScopesStringTokenizer.hasMoreTokens()){
		    	approvedScopesList.add(approvedScopesStringTokenizer.nextToken());
		    }
		    
			MultivaluedMap<String, String> paramsMap = new MultivaluedHashMap<String, String>();
			paramsMap.add("access_token", token);
			paramsMap.add("email", jwt.getClaim("email").asString());
			paramsMap.add("companyId", String.valueOf(companyId));
			paramsMap.addAll("approvedScopes", approvedScopesList);
			
			ServerAccessToken serverAccessToken = _accessTokenGrantHandler.createAccessToken(client, paramsMap);
			
			tokenExchangeResponse.setAccessToken(serverAccessToken.getTokenKey());
			tokenExchangeResponse.setExpiresIn(serverAccessToken.getExpiresIn());
			tokenExchangeResponse.setIssuedTokenType("urn:ietf:params:oauth:token-type:access_token");
			tokenExchangeResponse.setRefreshToken(serverAccessToken.getRefreshToken());
			tokenExchangeResponse.setScope(serverAccessToken.getScopes().stream().map(scope -> scope.getPermission()).collect(Collectors.joining(",")));
			tokenExchangeResponse.setTokenType(serverAccessToken.getTokenType());
			
			return tokenExchangeResponse;
			
		} catch (JwkException | MalformedURLException e) {
			throw new InvalidTokenException(e);
		}
		
	}
	
	@Reference
	private LiferayJwtAccessTokenExchangeGrantHandler _accessTokenGrantHandler;
	
}
