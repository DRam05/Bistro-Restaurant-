package menu;

import restaurant.Ingredient;

public class IngredientAmount {

    private final Ingredient ingredient;
    private double requiredAmount;

    public IngredientAmount(Ingredient ingredient, double requiredAmount) {
        this.ingredient = ingredient;
        this.requiredAmount = requiredAmount;
    }

    public Ingredient getIngredient() { return ingredient; }
    public double getRequiredAmount() { return requiredAmount; }

    public void setRequiredAmount(double amt) {
        requiredAmount = Math.max(0, amt);
    }

    public boolean hasAtLeast(double amount) {
    // Returns true if there exists are least "amount" units
    // of ingredient
            return requiredAmount >= amount;
    }

     public void deduct(double amount) {
        // TODO: implement (should never go below zero)
        requiredAmount = Math.max(0, requiredAmount - amount);
    }
}