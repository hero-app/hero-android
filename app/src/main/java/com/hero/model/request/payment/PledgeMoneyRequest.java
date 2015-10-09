package com.hero.model.request.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hero.model.request.common.AuthenticatedRequest;

public class PledgeMoneyRequest extends AuthenticatedRequest
{
    public PledgeMoneyRequest(String token)
    {
        super(token);
    }

    @JsonProperty("nonce")
    public String nonce;

    @JsonProperty("amount")
    public double amount;

}
