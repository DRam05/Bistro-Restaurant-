A mock restaurant ordering application written in Java. Wrote this program for a project in a class for university. The program focuses on class, subclass usage plus some linked list and math using ASCII character values to generate discounts and receipts. 

Technologies

- Java (Object-Oriented Programming)

- Java Collections Framework
    - ArrayList
    - HashMap

- File I/O
    - Reading CSV files
    - Reading plain text files
    - Writing output transcripts

- Random Number Generation
    - java.util.Random with seeded randomness

- Exception Handling
    - Custom exceptions for inventory validation

- Date & Time API
    - LocalDateTime for receipt timestamps

Features

- Seed Calculation
    - A deterministic seed is calculated using the sum of ASCII values of each character in the user’s NetID.

- This seed ensures that:
    - The daily discount
    - Ingredient spoilage
    - Checksum calculation are reproducible for the same NetID.

- Daily Discount Generation
    - The discounted category is determined by:
    dayIndex = seed % 3
        0 → Entree
        1 → Beverage
        2 → Dessert

    - The discount percentage is determined by:
    discountPercent = 3 + (seed % 5)
    - Only one category per day receives a discount.

- Inventory Validation
    - Before any order is processed:
    - The program verifies that the pantry has enough ingredients for all orders.
    - If inventory is insufficient:
        -An InsufficientInventoryException is thrown.
        -No inventory is deducted.

- Pantry Management
    - Pantry inventory is reduced:
        - When orders are fulfilled
        - When spoilage is applied
    - Ingredient quantities are never allowed to drop below zero.

- Spoilage Simulation
    - After all orders are processed A random number of ingredients spoil:
    m = 1 + (seed % 2)
    - Each spoiled ingredient loses a random amount between 1 and 3 units.
    - Spoilage is recorded and displayed in the receipt transcript.

- Receipt Generation
    - The program generates a detailed receipt transcript that includes:
    - Discount information
    - Itemized orders by category
    - Discounted prices where applicable
    - Subtotal
    - Tax (6%)
    - Final total
    - Spoilage report
    - Ending pantry inventory
    - Checksum value

The Process
- For this project I was given a UML Diagram showcasing all the files necessary to complete the program plus the methods needed in each file. With the UML Diagram come some pre-written files, some completed and other semi-completed/empty ready to be written and implemented. Methods in the Ingrdient, IngridientAmount, MenuItem, OrderLine, and Main files had methods to be completed. 
- First I started with the Ingridient File by creating the constructor method along with the get methods
- Then I began the IngridientAmount file, again creating constructor, get methods, plus setRequiredAmount used to help find the amount of a certain ingredient required to for an order, and deduct method used to take ingrdients out of the pantry for when they were used. 
- Similarly, I made constuctor and get methods in the MenuItem File and OrderLine Files
- After that I began finishing the Pantry file which took ingridents from a .txt file to load the pantry and be able to check if the pantry has reasonable amount of ingridents for the orders
- Next created all the files in the Orders File which took and read the files for orders from the .txt files, validated if they were in the pantry and if there was enough to be used using the methods created in the Pantry file, computed Subtotal and took into account the possible discounts available, Built the recipt and calculated the total amount of ingridients used. 
- After that, I began the Restauraunt file which was used to generate the seed based on useres NetID and converting it into ASCII values and using it to find out which item type would be discounted and how much would the discount be. *Seed and Checksum formulas highlighted below*
- Finally, I finished the main file by introducing the code to the .txt files and implementing them into a readable format. This includes the ingridients.csv, recipies.csv and order.txt file to be used by all the other files in the program. After asking for the users NetID, the program outputs a receipt.txt file to the libs folder, showcasing what was ordered, who ordered, an itemized reciept, what the discounts were, what ingridients went bad, and the remainder of ingridients of the day. 

What I learend
- How file Input/Output integrates real-world data into programs
    - Creating programs to read .txt/.csv files 
    - Creating programs to use data and store it in a .txt file
- How object-oriented design helps manage complex systems
- Writing code that follows a UML diagram and specification
- How seeded randomness allows reproducible simulations
- The importance of separating responsibilities across classes


Compute and print checksum Formulas

Seed = for (char c : netID.toCharArray()){s = s + (int)c}
    This takes every individual character in the user input and automatically assigns its ASCII value, 
    then adds them together
CheckSum = Math.round(orderSubtotal*100 + seed + totalUnitsUsedAllIngredients) % 9973;
    This takes the subtotal and multiplies it by 100 and rounds it to an integer since it was a double, 
    adds it to the seed generated by the user input, adds it to all the units in the recepies, and gives
    the remainder (or Mod) of that after dividing it by 9973. 
