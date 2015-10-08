package com.hero.model.request.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticatedRequest
{
    @JsonProperty("key")
    public String key;

    public AuthenticatedRequest(String key)
    {
        this.key = key;
    }
}
