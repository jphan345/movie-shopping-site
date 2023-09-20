package com.github.klefstad_teaching.cs122b.billing.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public final class Validate
{
    public void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new ResultError(BillingResults.INVALID_QUANTITY);
        }
        if (quantity > 10) {
            throw new ResultError(BillingResults.MAX_QUANTITY);
        }
    }

    public boolean isPremiumUser(JWTClaimsSet claimsSet) throws ParseException {
        boolean isPremium = false;
        for (String role : claimsSet.getStringListClaim(JWTManager.CLAIM_ROLES)) {
            if (role.equalsIgnoreCase("premium")) {
                isPremium = true;
                break;
            }
        }
        return isPremium;
    }
}
