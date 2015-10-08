package com.hero.model.request.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest
{
    @JsonProperty("facebook_access_token")
    public String facebookAccessToken;

    public LoginRequest(String facebookAccessToken)
    {
        this.facebookAccessToken = facebookAccessToken;
    }
}
