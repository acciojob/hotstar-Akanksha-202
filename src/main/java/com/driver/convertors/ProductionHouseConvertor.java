package com.driver.convertors;

import com.driver.EntryDto.ProductionHouseEntryDto;
import com.driver.model.ProductionHouse;

public class ProductionHouseConvertor {

    public static ProductionHouse productionHouseDTOtoProductionHouse(ProductionHouseEntryDto productionHouseEntryDto){

        ProductionHouse productionHouse = new ProductionHouse();
        productionHouse.setName(productionHouseEntryDto.getName());

        return productionHouse;
    }
}
