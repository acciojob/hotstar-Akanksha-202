package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User savedUser = userRepository.save(user);
        if (userRepository.findById(savedUser.getId()).isPresent()) {
            return savedUser.getId();
        }

        return null;
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        int noOfSeriesViewable = 0;

        for(WebSeries webSeries : webSeriesList){
            // Ensure there are no null values before proceeding
            if (user.getSubscription() == null || user.getSubscription().getSubscriptionType() == null ||
                    webSeries.getSubscriptionType() == null) {
                continue;
            }

            if (user.getAge() >= webSeries.getAgeLimit() && user.getSubscription().getSubscriptionType().ordinal() >= webSeries.getSubscriptionType().ordinal()) {
                noOfSeriesViewable++;
            }
        }

        if(noOfSeriesViewable > 0) return noOfSeriesViewable;

        return null;
    }


}
