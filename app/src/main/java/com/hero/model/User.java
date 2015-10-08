package com.hero.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User
{
    @JsonProperty("name")
    public String name;

    @JsonProperty("fbid")
    public String fbid;

    @JsonProperty("image")
    public String image;
}
