package com.driver.services;


import com.driver.EntryDto.ProductionHouseEntryDto;
import com.driver.convertors.ProductionHouseConvertor;
import com.driver.model.ProductionHouse;
import com.driver.repository.ProductionHouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductionHouseService {

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addProductionHouseToDb(ProductionHouseEntryDto productionHouseEntryDto){

        ProductionHouse productionHouse = ProductionHouseConvertor.productionHouseDTOtoProductionHouse(productionHouseEntryDto);
        productionHouse.setRatings(0.0);

        ProductionHouse savedProductionHouse = productionHouseRepository.save(productionHouse);
        if(savedProductionHouse != null && savedProductionHouse.getId() > 0) return savedProductionHouse.getId();

        return  null;
    }



}
