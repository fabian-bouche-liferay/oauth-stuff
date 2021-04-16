package com.liferay.samples.fbo.oauth.token.exchange.internal;

import com.liferay.samples.fbo.oauth.token.exchange.TokenExchangeConstants;
import com.liferay.samples.fbo.oauth.token.exchange.TokenExchangeResponse;
import com.liferay.samples.fbo.oauth.token.exchange.TokenExchangeService;

import org.osgi.service.component.annotations.Component;

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
		TokenExchangeResponse tokenExchange = new TokenExchangeResponse();
		return tokenExchange;
	}

}
