package com.example.assignmenttwo_starter.utilities;

import com.example.assignmenttwo_starter.model.Subscription;
import net.glxn.qrgen.core.vcard.VCard;

public class SubscriptionVCardUtility {
    /**
     * Creates a VCard from a subscription
     * @param subscription The subscription to create a VCard from
     * @return The VCard
     */
    public static VCard getVCard(Subscription subscription)
    {
        VCard vCard = new VCard();
        vCard.setName(subscription.getName());
        vCard.setCompany("Pettopia");
        vCard.setAddress("Limerick");
        vCard.setPhoneNumber("0871234567");
        vCard.setWebsite(subscription.getUrl());

        return vCard;
    }

    private SubscriptionVCardUtility() {
        throw new IllegalStateException("Utility class");
    }
}
