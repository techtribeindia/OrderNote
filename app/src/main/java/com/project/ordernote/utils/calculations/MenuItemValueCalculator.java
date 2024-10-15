package com.project.ordernote.utils.calculations;

import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.WeightConverter;

import java.text.DecimalFormat;

public class MenuItemValueCalculator {
    static DecimalFormat df = new DecimalFormat(Constants.twoDecimalPattern);


    public static double calculateItemtotalPrice(MenuItems_Model menuItem) {
        double totalPrice = 0.0;
        try{

            if (menuItem.getItemtype().equals(Constants.priceperkg_pricetype)) {

                double weightingrams     = menuItem.getGrossweight();
                double weightinKg = Double.parseDouble(WeightConverter.ConvertGramsToKilograms(String.valueOf(weightingrams)));
                double priceperkg = menuItem.getPriceperkg();
                double quantity   = menuItem.getQuantity();

                double priceBasedOnWeight = (weightinKg * priceperkg);

                totalPrice = priceBasedOnWeight * quantity;


            }
            else if (menuItem.getItemtype().equals(Constants.unitprice_itemtype)) {
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

        return Double.parseDouble(df.format(totalPrice));
    }

    public static double calculateItemPrice(MenuItems_Model menuItem) {
        double totalPrice = 0.0;
        try{

            if (menuItem.getItemtype().equals(Constants.priceperkg_pricetype)) {

                double weightingrams     = menuItem.getGrossweight();
                double weightinKg = Double.parseDouble(WeightConverter.ConvertGramsToKilograms(String.valueOf(weightingrams)));
                double priceperkg = menuItem.getPriceperkg();
                double quantity   = menuItem.getQuantity();

                totalPrice = (weightinKg * priceperkg);



            }
            else if (menuItem.getItemtype().equals(Constants.unitprice_pricetype)) {
                double priceperUnit = menuItem.getUnitprice();
                double quantity   = menuItem.getQuantity();


                totalPrice = priceperUnit ;

            }
            else {
                totalPrice = 0;
            }



        }
        catch (Exception e){
            e.printStackTrace();
        }

        return Double.parseDouble(df.format(totalPrice));
    }


}
