package com.liferay.samples.fbo.oauth.token.exchange.internal;

import com.liferay.oauth2.provider.rest.internal.endpoint.liferay.LiferayOAuthDataProvider;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.rs.security.oauth2.common.AccessTokenRegistration;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.AbstractGrantHandler;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;

public class TokenExchangeGrantHandler extends AbstractGrantHandler {

	public static final String TOKEN_EXCHANGE_GRANT = "token-exchange";
	
	protected TokenExchangeGrantHandler(LiferayOAuthDataProvider _liferayOAuthDataProvider) {
		super(TOKEN_EXCHANGE_GRANT);
		setDataProvider(_liferayOAuthDataProvider);
	}

	@Override
	public ServerAccessToken createAccessToken(Client client, MultivaluedMap<String, String> params)
			throws OAuthServiceException {
		
		UserSubject subject = new UserSubject(params.get("userSubject").get(0));
		List<String> clientCodeVerifier = null;
		List<String> scopes = params.get("approvedScopes"); 
		
		AccessTokenRegistration atr = new AccessTokenRegistration();
        atr.setClient(client);
        atr.setApprovedScope(scopes);
        atr.setSubject(subject);
        return getDataProvider().createAccessToken(atr);
		
		//return doCreateAccessToken(client, grant, getSingleGrantType(), clientCodeVerifier, audiences);
	}

}
