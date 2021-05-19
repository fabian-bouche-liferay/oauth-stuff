package com.liferay.samples.fbo.oauth.token.exchange.internal;

import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.oauth2.provider.rest.internal.endpoint.liferay.LiferayOAuthDataProvider;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

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
		
		String emailAddress = params.get("email").get(0);
		String companyIdString = params.get("companyId").get(0);
		long companyId = Long.parseLong(companyIdString);
		
		System.out.println("Email: " + emailAddress);
		User user = UserLocalServiceUtil.fetchUserByEmailAddress(companyId, emailAddress);
		
		UserSubject subject = new UserSubject(user.getScreenName());
		subject.setId(String.valueOf(user.getUserId()));
		
		List<String> scopes = params.get("approvedScopes"); 

		System.out.println("User subject: " + subject.getId());
		System.out.println("Client ID: " + client.getClientId());
		
		client.getProperties().put(OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID, companyIdString);
		
        return doCreateAccessToken(client, subject, scopes);
	}

}
