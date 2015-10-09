package com.hero.model.response.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientTokenResponse
{
    @JsonProperty("client_token")
    public String token;
}
