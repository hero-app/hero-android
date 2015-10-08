package com.hero.model.response.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse
{
    @JsonProperty("_id")
    public String id;

    @JsonProperty("key")
    public String key;

    @JsonProperty("name")
    public String name;

    @JsonProperty("image")
    public String image;

    @JsonProperty("fbid")
    public String fbid;


}
