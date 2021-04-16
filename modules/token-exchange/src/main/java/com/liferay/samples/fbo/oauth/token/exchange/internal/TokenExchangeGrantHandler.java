package com.liferay.samples.fbo.oauth.token.exchange.internal;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.AbstractGrantHandler;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;

public class TokenExchangeGrantHandler extends AbstractGrantHandler {

	public static final String TOKEN_EXCHANGE_GRANT = "token-exchange";
	
	protected TokenExchangeGrantHandler() {
		super(TOKEN_EXCHANGE_GRANT);
	}

	@Override
	public ServerAccessToken createAccessToken(Client client, MultivaluedMap<String, String> arg1)
			throws OAuthServiceException {
		
		UserSubject grant = null;
		List<String> clientCodeVerifier = null;
		List<String> audiences = null;
		return doCreateAccessToken(client, grant, getSingleGrantType(), clientCodeVerifier, audiences);
	}

}
