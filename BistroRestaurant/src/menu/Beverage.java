package menu;

import java.util.ArrayList;

public class Beverage extends MenuItem {
    
    public Beverage(String name, ArrayList<IngredientAmount> recipe, double priceSuggested) {
        super(name, recipe, priceSuggested);
    }
}