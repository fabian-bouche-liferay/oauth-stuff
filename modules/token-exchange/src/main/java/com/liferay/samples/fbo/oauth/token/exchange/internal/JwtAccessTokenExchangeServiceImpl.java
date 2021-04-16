package com.liferay.samples.fbo.oauth.token.exchange.internal;

import com.liferay.samples.fbo.oauth.token.exchange.TokenExchangeConstants;
import com.liferay.samples.fbo.oauth.token.exchange.TokenExchangeResponse;
import com.liferay.samples.fbo.oauth.token.exchange.TokenExchangeService;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;
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
	public TokenExchangeResponse getTokenExchangeResponse(String token) {
		TokenExchangeResponse tokenExchangeResponse = new TokenExchangeResponse();


		Client client = new Client();
		client.setClientId("id-c23ec8b7-7799-8cc8-ec73-3859d4ae74a1");
		
		MultivaluedMap<String, String> paramsMap = new MultivaluedHashMap<String, String>();
		paramsMap.add("access_token", token);
		
		_accessTokenGrantHandler.createAccessToken(client, paramsMap);
		
		return tokenExchangeResponse;
	}
	
	@Reference
	private LiferayTokenExchangeGrantHandler _accessTokenGrantHandler;
	
}
