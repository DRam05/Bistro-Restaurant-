package restaurant;

public class Ingredient {

    private final String name;
    private final String unit;

    public Ingredient(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }

    public String getName() { return name; }
    public String getUnit() { return unit; }
}