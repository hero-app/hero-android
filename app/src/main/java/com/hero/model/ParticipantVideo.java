package com.hero.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParticipantVideo
{
    @JsonProperty("preview")
    public String preview;

    @JsonProperty("data")
    public String data;

    @JsonProperty("file")
    public String file;
}
