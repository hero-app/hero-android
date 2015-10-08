package com.hero.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Challenge
{
    @JsonProperty("_id")
    public String id;

    @JsonProperty("title")
    public String title;

    @JsonProperty("image")
    public String image;

    @JsonProperty("status")
    public String status;

    @JsonProperty("description")
    public String description;

    @JsonProperty("category")
    public String category;

    @JsonProperty("rules")
    public String rules;

    @JsonProperty("creator")
    public User creator;

    @JsonProperty("fund_goal")
    public int fundGoal;

    @JsonProperty("charity_percentage")
    public int charityPercent;

    @JsonProperty("participants")
    public List<Participant> participants;
}
