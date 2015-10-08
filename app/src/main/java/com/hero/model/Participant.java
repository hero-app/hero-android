package com.hero.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Participant
{
    @JsonProperty("user")
    public User user;

    @JsonProperty("video")
    public ParticipantVideo video;
}
