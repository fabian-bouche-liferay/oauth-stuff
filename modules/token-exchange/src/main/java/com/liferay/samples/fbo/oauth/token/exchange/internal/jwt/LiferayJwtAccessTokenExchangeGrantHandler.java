package com.liferay.samples.fbo.oauth.token.exchange.internal.jwt;

import com.liferay.oauth2.provider.rest.internal.endpoint.access.token.grant.handler.BaseAccessTokenGrantHandler;
import com.liferay.oauth2.provider.rest.internal.endpoint.liferay.LiferayOAuthDataProvider;

import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration",
		service = {AccessTokenGrantHandler.class, LiferayJwtAccessTokenExchangeGrantHandler.class}
	)
public class LiferayJwtAccessTokenExchangeGrantHandler extends BaseAccessTokenGrantHandler {

	@Activate
	protected void activate(Map<String, Object> properties) {
		
		_tokenExchangeGrantHandler = new JwtAccessTokenExchangeGrantHandler(_liferayOAuthDataProvider);

	}

	@Override
	protected AccessTokenGrantHandler getAccessTokenGrantHandler() {
		return _tokenExchangeGrantHandler;
	}

	@Override
	protected boolean hasPermission(Client client, MultivaluedMap<String, String> params) {
		return true;
	}

	@Override
	protected boolean isGrantHandlerEnabled() {
		return true;
	}

	private JwtAccessTokenExchangeGrantHandler _tokenExchangeGrantHandler;
	
	@Reference
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

}
