package orders;

// import Java API packages
import java.util.ArrayList;
import menu.*;
import restaurant.*;

public class Orders {
    private ArrayList<OrderLine> lines;
    
    public Orders() {
        lines = new ArrayList<>();
    }

    public void addItem(MenuItem item, int qty) {
        lines.add(new OrderLine(item, qty));
    }

    public ArrayList<OrderLine> getLines() 
    { 
        return lines; 
    }

    public boolean validateStock(Pantry pantry) {
        // TODO: simulate total recipe usage per line and check with pantry.hasAll(...)
        Pantry tempPantry = new Pantry(pantry);
        boolean valid = false;

        for (OrderLine line : lines){
            MenuItem item = line.getMenuItem();
            int qty = line.getQuantity();
            if (!tempPantry.hasAll(item.getRecipe(), qty)) {
                valid = false; 
            } else {
                tempPantry.reduce(item.getRecipe(), qty);
                valid = true;
            }
        }
        // replace the following statement
        return valid;
       
    }

    public double computeSubtotal(double entreeDiscountPct, double beverageDiscountPct, double dessertDiscountPct) {
        double subtotal = 0.0;
    
        for (OrderLine line : lines) {
            MenuItem item = line.getMenuItem();
            double price = item.getSuggestedPrice();
            int quantity = line.getQuantity();
    
            switch (item.getName().toLowerCase()) {
                case "entree":
                    price = price * (1 - entreeDiscountPct / 100.0);
                    break;
                case "beverage":
                    price = price * (1 - beverageDiscountPct / 100.0);
                    break;
                case "dessert":
                    price = price * (1 - dessertDiscountPct / 100.0);
                    break;
            }
    
            subtotal += price * quantity;
        }
    
        return subtotal;
    }

    public double computeTax(double subtotal) {

        // replace the following statement
        return subtotal * 0.06;
    }

public String buildReceiptSection(restaurant.Restaurant restaurant,
                                  double entreeDiscountPct,
                                  double beverageDiscountPct,
                                  double dessertDiscountPct) {

    StringBuilder receipt = new StringBuilder();

    // ENTREE section
    receipt.append("-- ENTREES --\n");
    for (OrderLine line : lines) {
        if (line.getMenuItem() instanceof menu.Entree) {
            double price = line.getMenuItem().getSuggestedPrice();
            if (entreeDiscountPct > 0) {
                price = price * (1 - entreeDiscountPct / 100.0);
            }
            receipt.append(String.format("%s x%d @ %.2f = $%.2f%s\n",
                    line.getMenuItem().getName(),
                    line.getQuantity(),
                    line.getMenuItem().getSuggestedPrice(),
                    price * line.getQuantity(),
                    (entreeDiscountPct > 0 ? String.format(" (after %.0f%% DCD)", entreeDiscountPct) : "")));
        }
    }

    // BEVERAGE section
    receipt.append("-- BEVERAGES --\n");
    for (OrderLine line : lines) {
        if (line.getMenuItem() instanceof menu.Beverage) {
            double price = line.getMenuItem().getSuggestedPrice();
            if (beverageDiscountPct > 0) {
                price = price * (1 - beverageDiscountPct / 100.0);
            }
            receipt.append(String.format("%s x%d @ %.2f = $%.2f%s\n",
                    line.getMenuItem().getName(),
                    line.getQuantity(),
                    line.getMenuItem().getSuggestedPrice(),
                    price * line.getQuantity(),
                    (beverageDiscountPct > 0 ? String.format(" (after %.0f%% DCD)", beverageDiscountPct) : "")));
        }
    }

    // DESSERT section
    receipt.append("-- DESSERTS --\n");
    for (OrderLine line : lines) {
        if (line.getMenuItem() instanceof menu.Dessert) {
            double price = line.getMenuItem().getSuggestedPrice();
            if (dessertDiscountPct > 0) {
                price = price * (1 - dessertDiscountPct / 100.0);
            }
            receipt.append(String.format("%s x%d @ %.2f = $%.2f%s\n",
                    line.getMenuItem().getName(),
                    line.getQuantity(),
                    line.getMenuItem().getSuggestedPrice(),
                    price * line.getQuantity(),
                    (dessertDiscountPct > 0 ? String.format(" (after %.0f%% DCD)", dessertDiscountPct) : "")));
        }
    }

    // Totals section
    double subtotal = computeSubtotal(entreeDiscountPct, beverageDiscountPct, dessertDiscountPct);
    double tax = computeTax(subtotal);
    double finalTotal = subtotal + tax;

    receipt.append(String.format("Subtotal: $%.2f\n", subtotal));
    receipt.append(String.format("Tax (6%%): $%.2f\n", tax));
    receipt.append(String.format("TOTAL: $%.2f\n", finalTotal));

    return receipt.toString();
}


    public double totalUnitsUsedAllIngredients() {
        // TODO: compute sum of all units used (for checksum)
        double totalUnits = 0.0;

        for(OrderLine line : lines){
            MenuItem item = line.getMenuItem();
            int qty = line.getQuantity();

            for(IngredientAmount ingredientAmt : item.getRecipe()){
                totalUnits = totalUnits + (ingredientAmt.getRequiredAmount() * qty);
            }

        }
        // replace the following statement
        return totalUnits;
    }
}