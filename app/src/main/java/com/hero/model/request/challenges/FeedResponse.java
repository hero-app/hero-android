package com.hero.model.request.challenges;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hero.model.Challenge;

import java.util.List;

public class FeedResponse
{
    @JsonProperty("challenges")
    public List<Challenge> challenges;
}
