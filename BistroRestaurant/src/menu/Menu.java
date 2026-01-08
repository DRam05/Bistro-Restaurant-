package menu;

import java.util.ArrayList;

public class Menu {

    private ArrayList<MenuItem> menu_items = new ArrayList<>();

    public void addItem(MenuItem item) {
        menu_items.add(item);
    }

    public MenuItem findMenuItemByName(String name) {

        for (MenuItem item : menu_items) 
            if(item.getName().equals(name)) return item;

        // return if item not found
        return null;
    }

    public ArrayList<MenuItem> getMenuItems() { 
        return menu_items; 
    }
}