package com.hero.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParticipantVideo
{
    @JsonProperty("preview")
    public String preview;

    @JsonProperty("url")
    public String url;

    @JsonProperty("file")
    public String file;
}
