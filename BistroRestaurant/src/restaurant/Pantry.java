package restaurant;

// import Java API packages
import java.util.*;
import menu.*;

public class Pantry {

    // create empty hash table of ingredient_name : ingredient_object
    private HashMap<String, IngredientAmount> pantry_items;

    // default constructor
    public Pantry() {
        pantry_items = new HashMap<>();
    }

    public ArrayList<IngredientAmount> asList() {
        return new ArrayList<>(pantry_items.values());
    }

    // copy constructor
    public Pantry(Pantry other) {
        pantry_items = new HashMap<>(other.pantry_items);
    }

    // getter method
    public Ingredient getIngredient(String name) { 
    // Returns ingredient object with provided ingredient name
    // Returns null if name does not exist
    if (name == null) return null;

    // try exact match first
    IngredientAmount amt = pantry_items.get(name);
    if (amt != null) return amt.getIngredient();

    // try case-insensitive, trimmed match
    for (String key : pantry_items.keySet()) {
        if (key.trim().equalsIgnoreCase(name.trim())) {
            return pantry_items.get(key).getIngredient();
        }
    }
    return null;
    }

    // other operators
    public void add(IngredientAmount ingredientAmt) {
        pantry_items.put(ingredientAmt.getIngredient().getName(), ingredientAmt);
    }

    public void reduce(ArrayList<IngredientAmount> recipe, int quantity) {
        for (IngredientAmount req : recipe) {
            IngredientAmount inPantry = pantry_items.get(req.getIngredient().getName());
            if (inPantry != null) {
                double used = req.getRequiredAmount() * quantity;
                inPantry.deduct(used);
            }
        }
    }

    

    public int numItemsInPantry() { 
        return pantry_items.size(); 
    }

    public boolean hasAll(ArrayList<IngredientAmount> recipe, int quantity) {
    // Returns true if pantry has enough ingredients for quantity of
    // recipe provided.
    // TODO
    boolean hasIngridents = false;
    for(IngredientAmount required : recipe){
        String ingredientName = required.getIngredient().getName();
        IngredientAmount inPantry = pantry_items.get(ingredientName);

        if (inPantry == null) {
            // Ingredient not found in pantry
            hasIngridents = false;
        }

        double totalNeeded = required.getRequiredAmount() * quantity;
        if (!inPantry.hasAtLeast(totalNeeded)) {
            hasIngridents = false; // Not enough in stock
        }
        else{
            hasIngridents = true;
        }
    }
    

        // replace this line
        return hasIngridents;

        
    }
}