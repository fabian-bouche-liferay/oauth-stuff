package com.liferay.samples.fbo.oauth.token.exchange.internal;

import com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration;
import com.liferay.oauth2.provider.rest.internal.endpoint.access.token.grant.handler.BaseAccessTokenGrantHandler;
import com.liferay.oauth2.provider.rest.internal.endpoint.liferay.LiferayOAuthDataProvider;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration",
		service = {AccessTokenGrantHandler.class, LiferayTokenExchangeGrantHandler.class}
	)
public class LiferayTokenExchangeGrantHandler extends BaseAccessTokenGrantHandler {

	@Activate
	protected void activate(Map<String, Object> properties) {
		
		_liferayTokenExchangeGrantHandler = new LiferayTokenExchangeGrantHandler();


		_oAuth2ProviderConfiguration = ConfigurableUtil.createConfigurable(
			OAuth2ProviderConfiguration.class, properties);

	}

	@Override
	protected AccessTokenGrantHandler getAccessTokenGrantHandler() {
		return _liferayTokenExchangeGrantHandler;
	}

	@Override
	protected boolean hasPermission(Client client, MultivaluedMap<String, String> params) {
		return true;
	}

	@Override
	protected boolean isGrantHandlerEnabled() {
		return true;
	}

	private LiferayTokenExchangeGrantHandler _liferayTokenExchangeGrantHandler;
	
	@Reference
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;
	
	private OAuth2ProviderConfiguration _oAuth2ProviderConfiguration;
}
