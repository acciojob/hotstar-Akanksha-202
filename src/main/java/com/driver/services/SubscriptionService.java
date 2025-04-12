package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.convertors.SubscriptionConvertor;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        Subscription subscription = SubscriptionConvertor.subscriptionReqToSubscription(subscriptionEntryDto);
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        subscription.setUser(user);


        if(subscription.getSubscriptionType()==SubscriptionType.BASIC){
            subscription.setTotalAmountPaid(500 + (200*(subscription.getNoOfScreensSubscribed())));
        }
        if(subscription.getSubscriptionType()==SubscriptionType.PRO){
            subscription.setTotalAmountPaid(800 + (250 * (subscription.getNoOfScreensSubscribed())));
        }
        if(subscription.getSubscriptionType()==SubscriptionType.ELITE){
            subscription.setTotalAmountPaid(1000 + (350 * (subscription.getNoOfScreensSubscribed())));
        }

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        if(savedSubscription != null && savedSubscription.getId() > 0) return subscription.getTotalAmountPaid();
        return null;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

            Optional<Subscription> subscriptionOptional = subscriptionRepository.findByUserId(userId);

            if(subscriptionOptional.isEmpty()) throw new RuntimeException("Invalid Id");

            Subscription savedSubscription = subscriptionOptional.get();
            SubscriptionType subscriptionType = savedSubscription.getSubscriptionType();

            int newTotalAmount = 0;

            if(subscriptionType == SubscriptionType.BASIC){
                savedSubscription.setSubscriptionType(SubscriptionType.PRO);
                newTotalAmount = 800 + (250 * (savedSubscription.getNoOfScreensSubscribed()));
                int differenceAmount = newTotalAmount - savedSubscription.getTotalAmountPaid();
                savedSubscription.setTotalAmountPaid(newTotalAmount);
                subscriptionRepository.save(savedSubscription);
                return differenceAmount;
            }
            else if(subscriptionType == SubscriptionType.PRO){
                savedSubscription.setSubscriptionType(SubscriptionType.ELITE);
                newTotalAmount =1000 + (350 * (savedSubscription.getNoOfScreensSubscribed()));
                int differenceAmount = newTotalAmount - savedSubscription.getTotalAmountPaid();
                savedSubscription.setTotalAmountPaid(newTotalAmount);
                subscriptionRepository.save(savedSubscription);
                return differenceAmount;
            }
            else if(subscriptionType == SubscriptionType.ELITE){
                throw new RuntimeException("Already the best Subscription");
            }

        return null;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription>subscriptionList = subscriptionRepository.findAll();
        int totalAmount = 0;
        for(Subscription subscription : subscriptionList){
            totalAmount+=subscription.getTotalAmountPaid();
        }
        if(totalAmount >= 0) return totalAmount;
        return null;
    }

}
