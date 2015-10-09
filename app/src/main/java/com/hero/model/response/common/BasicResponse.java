package com.hero.model.response.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BasicResponse
{
    @JsonProperty("success")
    public boolean success;
}
