package com.liferay.samples.fbo.oauth.token.exchange;

public interface TokenExchangeService {

	public TokenExchangeResponse getTokenExchangeResponse(String token, long companyId) throws InvalidTokenException;
	
}
