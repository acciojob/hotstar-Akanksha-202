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

        // Validation: subscriptionType should not be null
        if(subscriptionEntryDto.getSubscriptionType() == null){
            return null;
        }

        // Validation: noOfScreensSubscribed should be positive
        if(subscriptionEntryDto.getNoOfScreensRequired() <= 0){
            return null;
        }

        //Validation: User already has a subscription
        if(subscriptionRepository.findByUserId(subscriptionEntryDto.getUserId()).isPresent()) return null;

        Subscription subscription = SubscriptionConvertor.subscriptionReqToSubscription(subscriptionEntryDto);
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).orElse(null);
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
        if(savedSubscription != null && savedSubscription.getId() > 0) return savedSubscription.getTotalAmountPaid();
        return null;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
            System.out.println("Starting upgradeSubscription for userId: " + userId);

            Optional<Subscription> subscriptionOptional = subscriptionRepository.findByUserId(userId);

            if(subscriptionOptional.isEmpty()) return null;

            Subscription savedSubscription = subscriptionOptional.get();
            SubscriptionType subscriptionType = savedSubscription.getSubscriptionType();

            System.out.println("Current subscriptionType: " + subscriptionType);
            System.out.println("No. of screens subscribed: " + savedSubscription.getNoOfScreensSubscribed());
            System.out.println("Old total amount paid: " + savedSubscription.getTotalAmountPaid());

            int newTotalAmount = 0;

            if(subscriptionType == SubscriptionType.BASIC){
                savedSubscription.setSubscriptionType(SubscriptionType.PRO);
                newTotalAmount = 800 + (250 * (savedSubscription.getNoOfScreensSubscribed()));
                int differenceAmount = newTotalAmount - savedSubscription.getTotalAmountPaid();
                System.out.println("Upgraded from BASIC to PRO. New Total: " + newTotalAmount + ", Difference to Pay: " + differenceAmount);

                savedSubscription.setTotalAmountPaid(newTotalAmount);
                subscriptionRepository.save(savedSubscription);
                return differenceAmount;
            }
            else if(subscriptionType == SubscriptionType.PRO){
                savedSubscription.setSubscriptionType(SubscriptionType.ELITE);
                newTotalAmount =1000 + (350 * (savedSubscription.getNoOfScreensSubscribed()));
                int differenceAmount = newTotalAmount - savedSubscription.getTotalAmountPaid();
                System.out.println("Upgraded from PRO to ELITE. New Total: " + newTotalAmount + ", Difference to Pay: " + differenceAmount);

                savedSubscription.setTotalAmountPaid(newTotalAmount);
                subscriptionRepository.save(savedSubscription);
                return differenceAmount;
            }
            else if(subscriptionType == SubscriptionType.ELITE){
                throw new Exception("Already the best Subscription");
            }

            System.out.println("Reached end of upgradeSubscription method without upgrading.");
            return null;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription>subscriptionList = subscriptionRepository.findAll();
        if (subscriptionList.isEmpty()) {
            return null;  // return null if no subscriptions
        }
        int totalAmount = 0;
        for(Subscription subscription : subscriptionList){
            totalAmount+=subscription.getTotalAmountPaid();
        }
        if(totalAmount >= 0) return totalAmount;
        return null;
    }

}
