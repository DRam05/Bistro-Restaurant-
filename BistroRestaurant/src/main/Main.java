package main;

// import Java API packages
import exceptions.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import menu.*;
import orders.*;
import restaurant.*;

public class Main {
    public static void main(String[] args) {
        try {

            // USER INPUT
            // Create input Scanner
            Scanner sc = new Scanner(System.in);

            // Get UserID
            System.out.print("Enter netID: ");
            String netID = sc.nextLine().trim();

            // CREATE FILE OBJECTS
            File ingredientsFile = new File("H:/COSC 237/BistroRestaurant/BistroRestaurant/lib/ingredients.csv");
            File recipeFile = new File("H:/COSC 237/BistroRestaurant/BistroRestaurant/lib/recipes.csv");
            File ordersFile = new File("H:/COSC 237/BistroRestaurant/BistroRestaurant/lib/orders.txt");

            // OPEN AND READ FILES
            // Read current pantry items
            Pantry current_pantry = loadPantry(ingredientsFile);

            // Keep version of pantry (before processing orders)
            Pantry initial_pantry = new Pantry(current_pantry);

            // Read recipes file
            Menu menu = loadMenu(recipeFile, current_pantry);

            // Read menu orders
            Orders orders = loadOrders(ordersFile, menu);

            // CREATE RESTAURANT
            // Constructs a restaurant with provided pantry and menu.
            // Determines daily discount based on the provided netID.
            // Randomly determines pantry spoilage based on netID.
            Restaurant restaurant = new Restaurant(current_pantry, menu, netID);

            // PROCESS FOOD ORDERS
            // Throws InsufficientInventoryException if not enough ingredients
            // in current pantry to fulfill all menu item orders.
            restaurant.processOrders(orders);

            // CREATE EMPTY RESTAURANT TRANSCRIPT
            ArrayList<String> receipt_transcript = new ArrayList<String>();
            
            // Add daily category discount
            receipt_transcript.add("TODAY'S CATEGORY DISCOUNT");
            receipt_transcript.add(restaurant.getDiscountCategory() + "   " +
                                   restaurant.getDiscountPercent() + "%");
                                   
            // Add initial pantry inventory to receipt transcript
            // before orders processed
            receipt_transcript.add("PANTRY INGREDIENTS REPORT (Initial)");
            addPantry(initial_pantry, receipt_transcript);

            // Add receipt header to transcript
            addReceiptHeader(restaurant, receipt_transcript);

            // Add receipt orders (and totals) to transcript
            addReceiptOrders(restaurant, orders, receipt_transcript);

            // Add spoliage report
            addSpoilage(restaurant.getSpoilage(), receipt_transcript);

            // Add final pantry ingredients to receipt transcript 
            // after orders processed
            receipt_transcript.add("PANTRY INGREDIENTS REPORT (Ending)");
            addPantry(restaurant.getPantry(), receipt_transcript);

            // Add checksum to transcript
            // TODO

            double subtotal = orders.computeSubtotal(
            restaurant.getDiscountCategory().equals("Entree") ? restaurant.getDiscountPercent() : 0,
            restaurant.getDiscountCategory().equals("Beverage") ? restaurant.getDiscountPercent() : 0,
            restaurant.getDiscountCategory().equals("Dessert") ? restaurant.getDiscountPercent() : 0);

            double totalUnits = orders.totalUnitsUsedAllIngredients();
            int checksum = (int) restaurant.computeChecksum(subtotal, totalUnits);
            receipt_transcript.add("CHECKSUM " + checksum);

            // OUTPUT RECEIPT SCRIPT
            // write receipt transcript to receipt_script.txt file
            // TODO
            try (java.io.PrintWriter writer = new java.io.PrintWriter("H:/COSC 237/BistroRestaurant/BistroRestaurant/lib/receipt_transcript.txt")) {
                for (String line : receipt_transcript) {
                writer.println(line);
            }
            System.out.println("Receipt transcript successfully written to receipt_transcript.txt");
}           catch (IOException e) {
                System.out.println("Error writing receipt transcript: " + e.getMessage());
            }

            // Close scanner
            sc.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("File " + e.getMessage() + " not found");

            // terminate program
            System.exit(1);
        }
        catch (NumberFormatException e) {
            System.out.println("Number format exception found in file " + 
                               e.getMessage());
            System.exit(1);
        }
        catch (IOException ex) {
            System.out.println("Error while reading file " + ex.getMessage());
            System.exit(1);
        }
        catch (InvalidMenuItemCategoryException e) {
            System.out.println("Invalid menu item category " + e.getMessage() + 
                               " found in recipes.csv file");
            System.exit(1);
        }
        catch (ImproperIngredientsFormatException e) {
            System.out.println("Improperly formatted ingredients found in ingredients.csv file");
            System.exit(1);
        }
        catch (InvalidOrdersFormatException e) {
            System.out.println("Invalid formatted order found in orders.txt file");
            System.exit(1);
        }
        catch (InsufficientInventoryException e) {
            System.out.println("Insufficient ingredients inventory");
            System.exit(1);
        }
    }


    // SUPPORTING METHODS
    public static Pantry loadPantry(File ingredientsCsv) throws
                                        NumberFormatException, 
                                        FileNotFoundException, IOException {
    //-----------------------------------------------------------------------
    // Reads the ingredients of the pantry from a comma separated file of
    // the form:
    //             name, unit, starting_quantity
    //
    // Returns object of type Pantry initialized with ingredients read.
    //
    // Throws FileNotFoundException if file in ingredientsCsv not found.
    // Throws IOException if error occurs during reading of ingredients file.
    //-----------------------------------------------------------------------
        
        // create pantry object
        Pantry pantry = new Pantry();

        // read and store comma-separated ingredients file
        ArrayList<String[]> rows = readSimpleCSV(ingredientsCsv);

        // create ingredient objects and add to pantry
        try {
            for (int i = 0; i < rows.size(); i++) {
                String[] r = rows.get(i);
                String name = r[0].replace("\uFEFF", "").trim();
                String unit = r[1];

                double qty = Double.parseDouble(r[2]);
                pantry.add(new IngredientAmount(new Ingredient(name, unit), qty));


            }
        }
        catch (NumberFormatException e) {
            throw new NumberFormatException("ingredients.csv");
        }

        return pantry;
    }

    public static Menu loadMenu(File recipesCsv, Pantry pantry) throws 
                                        InvalidMenuItemCategoryException,
                                        FileNotFoundException, IOException {
    //-----------------------------------------------------------------------
    // Reads the recipes of menu from a comma separated file of the form:
    //   type, name, suggested_price, ingredient1:qty; ingredient2:qty; . . .
    //
    // Returns object of type Menu initialized with the recipes read.
    //
    // Throws InvalidMenuItemCategoryException if an invalid category read.
    // Throws FileNotFoundException if file recipesCsv not found.
    // Throws IOException if error occurs during reading.
    //-----------------------------------------------------------------------
        Menu menu = new Menu();
        ArrayList<String[]> rows = readSimpleCSV(recipesCsv);

        try {

            // iterate over each array of strings in ArrayList rows
            for (int i = 0; i < rows.size(); i++) {

                // read complete line of recipe
                String[] r = rows.get(i);

                // type one of Entree, Beverage or Dessert
                String type = r[0].trim();

                // name (e.g., Pancakes)
                String name = r[1].trim();

                // price of menu item
                double price = Double.parseDouble(r[2].trim());

                // parse aribitray number of ingredients for menu item,
                // each ingredient of the form: ingredient_name:quantity
                // (e.g., Flour:150), and store in hash table
                LinkedHashMap<String,String> portions = parsePortions(r[3].trim());

                // create empty list of ingredients for recipe
                ArrayList<IngredientAmount> recipe = new ArrayList<>();

                // for each ingredient_name:quantity pair, create ingredient
                // object and add to ingredients list for recipe
                for (Map.Entry<String,String> e : portions.entrySet()) {

                    Ingredient ingredient = pantry.getIngredient(e.getKey());


                    double amt = Double.parseDouble(e.getValue());
                    recipe.add(new IngredientAmount(ingredient, amt));
                }

                switch (type) {
                    case "Entree": menu.addItem(new Entree(name, recipe, price)); break;
                    case "Beverage": menu.addItem(new Beverage(name, recipe, price)); break;
                    case "Dessert": menu.addItem(new Dessert(name, recipe, price)); break;
                    default: throw new InvalidMenuItemCategoryException(type);
                }
            }
        }
        // rethrow NumberFormatException with name of file
        catch (NumberFormatException e) {
            throw new NumberFormatException("recipes.csv");
        }

        return menu;
    }

    public static Orders loadOrders(File ordersfile, Menu menu) throws 
                                        InvalidOrdersFormatException,
                                        FileNotFoundException, IOException {
    //-----------------------------------------------------------------------
    // Reads a list of orders from a text file of the form:
    // <menu item type>, <menu item name>, <quantity ordered>, e.g.,
    //
    //      Entree, Pancakes, 2
    //      Beverage, Latte, 3
    //      Dessert, ChocolateMousse, 1
    //
    // Returns object of type Orders initialized with the orders read.
    //
    // Throws FileNotFoundException if orders_file not found.
    // Throws IOException if error occurs during reading of file.
    //
    // NOTE: Because Order objects contain a collection of MenuItem objects,
    //       the Menu is passed as a parameter to this method to retrieve
    //       MenuItem objects from.
    //-----------------------------------------------------------------------
        Orders orders = new Orders();
        MenuItem menu_item;

        BufferedReader br = new BufferedReader(new FileReader(ordersfile));
        String line;

        // read line-by-line until end of file
        while ((line = br.readLine()) != null) {

            // remove any leading or trailing blanks
            String t = line.trim();

            // parse if not empty line and not a comment line
            if(!(t.isEmpty()) && !(t.startsWith("#"))) {

                // parse line around comma delimiter
                String[] parts = t.split(",");

                for(int i = 0; i < parts.length; i++){
                    parts[i] = parts[i].trim();
                }

                // check if parsed into three strings
                if (parts.length == 3) {

                    // get ordered menu item type, name and quantity
                    // String type = parts[0]; // not needed
                    String name = parts[1];
                    int quantity = Integer.parseInt(parts[2]);

                    // get corresponding MenuItem object
                    menu_item = menu.findMenuItemByName(name);

                    // add MenuItem object (and quantity) to orders
                    if (menu_item == null) {
                        System.out.println("Unknown menu item in orders file: " + name);
                        br.close();
                        throw new InvalidOrdersFormatException(); 
                        }

                orders.addItem(menu_item, quantity);
                }
                // otherwise, thrown exception
                else {
                    br.close();
                    throw new InvalidOrdersFormatException();
                } 
            }
        }

        br.close();
        return orders;
    }

    public static ArrayList<String[]> readSimpleCSV(File f) throws 
                                        FileNotFoundException, IOException {
    //-----------------------------------------------------------------------
    // Reads provided comma separated file.
    //
    // Returns an ArrayList in which each element is of type String[], where
    // each comma-separate item in lines read stored as a separate string.
    //-----------------------------------------------------------------------
        
        // Creates an intially empty resizeable ArrayList object that
        // stores a list of String[] type (i.e., a list of arrays of strings)
        ArrayList<String[]> rows = new ArrayList<>();

        // Reads each line of a comma separated file, and stores each
        // comma-separated item as a String
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {

            String line;

            // Continue reading each line until end of file
            while ((line = br.readLine()) != null) {

                // Parses items separated by a comma delimiter, converts
                // items into an array of strings, and adds as new element
                // in the ArrayList rows (skips any empty lines read)
                if (!(line.trim().isEmpty()))
                    rows.add(line.split(","));
            }
        }
         // Catch and rethrow exception to include file name
        catch (FileNotFoundException e) {
            throw new FileNotFoundException(f.getName());
        }

        // Catch and rethrow exception to include file name
        catch (IOException e) {
            throw new IOException(f.getName());
        }
        
        // Returns populated ArrayList
        return rows;
    }

    public static LinkedHashMap<String, String> parsePortions(String semicolonSeparated) 
                                throws ImproperIngredientsFormatException {
    //-----------------------------------------------------------------------
    // Returns a hash table from the provided semicolon separated string with
    // the following format:  "Flour:150;Milk:200;Egg:1"
    //-----------------------------------------------------------------------

        LinkedHashMap<String,String> map = new LinkedHashMap<>();

        // Return empty hash table has if provided null value or empty string
        if (semicolonSeparated == null || semicolonSeparated.isEmpty()) return map;

        // Split into array of strings (using semicolon as delimiter)
        String[] parts = semicolonSeparated.split(";");

        // Split each ingredient into name and quantity
        for (String p : parts) {

            String[] kv = p.split(":");

            // If properly parsed into two parts, add key-value pair to hash table
            if (kv.length == 2) 
                map.put(kv[0].trim(), kv[1].trim());
            else
            // otherwise, throw exception
                throw new ImproperIngredientsFormatException();
        }

        return map;
    }

    public static void addPantry(Pantry pantry, ArrayList<String> transcript) {
        //-----------------------------------------------------------------------
        // Add pantry ingredients to transcript as lines: "Name, unit, amount"
        //-----------------------------------------------------------------------
        for (menu.IngredientAmount ia : pantry.asList()) {
            restaurant.Ingredient ing = ia.getIngredient();
            double amt = ia.getRequiredAmount();
            transcript.add(String.format("%s, %s, %s",ing.getName(),ing.getUnit(), amt));
        }
    }

    public static void addReceiptHeader(Restaurant restaurant, ArrayList<String> transcript) {
    //-----------------------------------------------------------------------
    // Adds receipt header to transcript.
    //-----------------------------------------------------------------------
            transcript.add("BYTE BISTRO — Towson");
            transcript.add("netID: " + restaurant.getNetID());
            transcript.add("Daily Discount:" + restaurant.getDiscountCategory() +
                           " at " + restaurant.getDiscountPercent() + "%");
            transcript.add("Date: " + 
               LocalDateTime.now().format(DateTimeFormatter.
                                   ofPattern("yyyy-MM-dd HH:mm")));
            transcript.add(" ");
    }

    public static void addReceiptOrders(Restaurant restaurant, Orders orders, ArrayList<String> transcript) {
        double e=0,b=0,d=0;
        switch (restaurant.getDiscountCategory()) {
            case "Entree":   e = restaurant.getDiscountPercent(); break;
            case "Beverage": b = restaurant.getDiscountPercent(); break;
            case "Dessert":  d = restaurant.getDiscountPercent(); break;
        }
        String section = orders.buildReceiptSection(restaurant, e, b, d);
        for (String line : section.split("\\R")) transcript.add(line);
    }

    public static void addSpoilage(ArrayList<IngredientAmount> spoilage,
                                   ArrayList<String> transcript) {
    //-----------------------------------------------------------------------
    // Adds to transcript the list of pantry item ingredients that have
    // spoiled, along with the amount of ingredient spoiled.
    //-----------------------------------------------------------------------
    // TODO
    transcript.add("SPOILAGE REPORT");

if(spoilage == null || spoilage.isEmpty()){
    transcript.add("No Spoilage Occured Today");
    return;
}

for(menu.IngredientAmount ia : spoilage){
    restaurant.Ingredient ing = ia.getIngredient();
    double amt = ia.getRequiredAmount();

    transcript.add(String.format("%s: %.0f %s", ing.getName(), amt, ing.getUnit()));
}

    }
}