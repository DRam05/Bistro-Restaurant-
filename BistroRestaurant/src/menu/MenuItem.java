package menu;

import java.util.ArrayList;

public class MenuItem {
    
    private String name;
    private ArrayList<IngredientAmount> recipe;
    private double suggestedPrice;

    public MenuItem(String name, ArrayList<IngredientAmount> recipe, double suggestedPrice) {
        this.name = name;
        this.recipe = recipe;
        this.suggestedPrice = suggestedPrice;
    }

    public String getName() { return name; }
    public ArrayList<IngredientAmount> getRecipe() { return recipe; }
    public double getSuggestedPrice() { return suggestedPrice; }
}