package com.project.ordernote.data.local;


import android.view.MenuItem;

import com.project.ordernote.data.model.AppData_Model;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.MenuItems_Model;

import java.util.ArrayList;
import java.util.List;


public class LocalDataManager {

    // Static instance of the singleton class
    private static LocalDataManager instance;

    // Lists to hold the data
    private List<MenuItems_Model> menuItems;
    private List<Buyers_Model> buyers;
    private AppData_Model appData_model;
    // Private constructor to prevent instantiation
    private LocalDataManager() {
        menuItems = new ArrayList<>();
        buyers = new ArrayList<>();
        appData_model = new AppData_Model();
    }

    // Method to get the singleton instance
    public static synchronized LocalDataManager getInstance() {
        if (instance == null) {
            instance = new LocalDataManager();
        }
        return instance;
    }

    // Method to get the list of menu items
    public List<MenuItems_Model> getMenuItem() {
        if(menuItems==null){
            menuItems = new ArrayList<>();
        }
        return menuItems;
    }

    public AppData_Model getAppData_model() {
        if(appData_model == null ){
            appData_model = new AppData_Model();
        }
        return appData_model;
    }

    public void setAppData_model(AppData_Model appData_model) {
        this.appData_model = appData_model;
    }

    // Method to set the list of menu items
    public void setMenuItems(List<MenuItems_Model> menuItems) {

        this.menuItems = menuItems;
    }

    // Method to add a menu item to the list
    public void addMenuItem(MenuItems_Model menuItem) {
        this.menuItems.add(menuItem);
    }

    // Method to clear the list of menu items
    public void clearMenuItems() {
        this.menuItems.clear();
    }

    // Method to get the list of buyers
    public List<Buyers_Model> getBuyers() {
        if(buyers==null){
            buyers = new ArrayList<>();
        }
        return buyers;
    }

    // Method to set the list of buyers
    public void setBuyers(List<Buyers_Model> buyers) {
        this.buyers = buyers;
    }

    // Method to add a buyer to the list
    public void addBuyer(Buyers_Model buyer) {
        this.buyers.add(buyer);
    }

    // Method to clear the list of buyers
    public void clearBuyers() {
        this.buyers.clear();
    }
}

