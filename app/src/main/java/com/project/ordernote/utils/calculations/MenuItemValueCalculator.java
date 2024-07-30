package com.project.ordernote.utils.calculations;

import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.utils.Constants;

public class MenuItemValueCalculator {


    public static double calculateItemPrice(MenuItems_Model menuItem) {
        double totalPrice = 0.0;
        try{

            if (menuItem.getItemtype().equals(Constants.priceperkg_pricetype)) {

                double weight     = menuItem.getGrossweight();
                double priceperkg = menuItem.getPriceperkg();
                double quantity   = menuItem.getQuantity();

                double priceBasedOnWeight = (weight * priceperkg);

                totalPrice = priceBasedOnWeight * quantity;


            }
            else if (menuItem.getItemtype().equals(Constants.unitprice_pricetype)) {
                double priceperUnit = menuItem.getUnitprice();
                double quantity   = menuItem.getQuantity();


                totalPrice = priceperUnit * quantity;

            }
            else {
                totalPrice = 0;
            }



        }
        catch (Exception e){
            e.printStackTrace();
        }

        return totalPrice;
    }


}
