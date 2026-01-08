package restaurant;

// import Java API packages
import java.util.Random;

// import project packages
import menu.Menu;
import menu.IngredientAmount;
import orders.Orders;
import java.util.ArrayList;
import exceptions.*;
import orders.OrderLine;

public class Restaurant {

    private Pantry pantry;
    private Menu menu;
    private ArrayList<IngredientAmount> spoilage;

    // netID related fields
    private String netID;
    private int seed;

    // computed discount values based on seed
    private int discountCategoryNum; // 0=Entree, 1=Beverage, 2=Dessert
    private int discountPercent;  // 3%...7%
   
    // constructor
    public Restaurant(Pantry pantry, Menu menu, String netID) {
        this.pantry = pantry;
        this.menu = menu;
        this.netID = netID;
        spoilage = new ArrayList<IngredientAmount>();

         // computes and stores seed value (based on netID)
        int s = 0;
        for (char c : netID.toCharArray()){
            s = s + (int)c;
        }
        seed = s;

        // determine category with discount and discount percentage
        this.discountCategoryNum = seed % 3;
        this.discountPercent = 3 + (seed % 5);
    }

    // getters and setters
    public String getNetID() {
        return netID;
    }

    public String getDiscountCategory() {
        String[] categories = {"Entree", "Beverage", "Dessert"};
        return categories[discountCategoryNum];
    }

    public int getDiscountPercent() { return discountPercent; }
    
    public int getSeed() { return seed; }
    public Menu getMenu() { return menu; }
    public Pantry getPantry() { return pantry; }
    public ArrayList<IngredientAmount> getSpoilage() { return spoilage; }

    public void processOrders(Orders orders) throws InsufficientInventoryException {

        ArrayList<IngredientAmount> pantryList = pantry.asList();

    if (!orders.validateStock(pantry)) {
        throw new InsufficientInventoryException("Not enough ingredients to fulfill all orders.");
    }

    for (OrderLine line : orders.getLines()) {
        menu.MenuItem item = line.getMenuItem();
        int qty = line.getQuantity();

        pantry.reduce(item.getRecipe(), qty);
    }

    Random rand = new Random(seed);

    int m = 1 + (seed % 2);



    spoilage.clear(); // reset daily spoilage

        for (int i = 0; i < m && !pantryList.isEmpty(); i++) {
            int index = rand.nextInt(pantryList.size());
            IngredientAmount ingAmt = pantryList.get(index);

            double spoilQty = 1 + rand.nextInt(3);

            double available = ingAmt.getRequiredAmount();
            double actualSpoiled = Math.min(spoilQty, available);

            ingAmt.setRequiredAmount(available - actualSpoiled);
            spoilage.add(new IngredientAmount(ingAmt.getIngredient(), actualSpoiled));
        }


        int rand_num = rand.nextInt(100);
       System.out.println(rand_num);

       

    }

    public double computeChecksum(double orderSubtotal, 
                                  double totalUnitsUsedAllIngredients) {
    //-----------------------------------------------------------------------
    // Returns computed checksum (as specified in the assignment).
    //-----------------------------------------------------------------------
        return Math.round(orderSubtotal*100 + seed +
                               totalUnitsUsedAllIngredients) % 9973;
    }
}
