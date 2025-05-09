package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.convertors.WebSeriesConvertor;
import com.driver.model.ProductionHouse;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        WebSeries webSeries = WebSeriesConvertor.webSeriesDTOtowebSeries(webSeriesEntryDto);

        if(webSeriesRepository.findBySeriesName(webSeries.getSeriesName())!=null){
            throw new RuntimeException("Series is already present");
        }

        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        webSeries.setProductionHouse(productionHouse);


        //Production house Rating update
        productionHouse.getWebSeriesList().add(webSeries);

        int noOfWebSeriesByProductionHouse = productionHouse.getWebSeriesList().size();

        double newProductionHouseRating;
        if (noOfWebSeriesByProductionHouse == 1) {
            // If this is the first web series, set the rating directly
            newProductionHouseRating = webSeries.getRating();
        } else {
            newProductionHouseRating =
                    ((productionHouse.getRatings() * (noOfWebSeriesByProductionHouse - 1)) + webSeries.getRating()) / noOfWebSeriesByProductionHouse;
        }

        productionHouse.setRatings(newProductionHouseRating);

        WebSeries savedWebSeries = webSeriesRepository.save(webSeries);
        productionHouseRepository.save(productionHouse);
        if(savedWebSeries!=null && savedWebSeries.getId() > 0)return savedWebSeries.getId();
        return null;
    }

}
