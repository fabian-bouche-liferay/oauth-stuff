package com.liferay.samples.fbo.oauth.token.exchange;

import com.google.gson.annotations.SerializedName;

public class TokenExchangeResponse {

	@SerializedName("access_token")
	private String accessToken;

	@SerializedName("issued_token_type")
	private String issuedTokenType;
	
	@SerializedName("token_type")
	private String tokenType;

	@SerializedName("expires_in")
	private long expiresIn;

	@SerializedName("scope")
	private String scope;

	@SerializedName("refresh_token")
	private String refreshToken;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getIssuedTokenType() {
		return issuedTokenType;
	}

	public void setIssuedTokenType(String issuedTokenType) {
		this.issuedTokenType = issuedTokenType;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
}
