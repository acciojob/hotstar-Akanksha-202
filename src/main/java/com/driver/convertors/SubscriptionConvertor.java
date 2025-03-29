package com.driver.convertors;

import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.User;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class SubscriptionConvertor {

    public static Subscription subscriptionReqToSubscription(SubscriptionEntryDto subscriptionEntryDto){

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        return subscription;
    }
}
