package com.hero.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Participant extends User
{
    @JsonProperty("video")
    public String rawVideo;
}
