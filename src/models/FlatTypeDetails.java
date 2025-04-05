package models;

/**
 * The {@link FlatTypeDetails} class represents the details of a flat type in a BTO project,
 * including the number of units available and their selling price.
 */
public class FlatTypeDetails {
    private int units;
    private double price;

    /**
     * Constructs a new {@link FlatTypeDetails} instance with the specified units and price.
     *
     * @param units the number of units available for this flat type
     * @param price the selling price of this flat type
     */
    public FlatTypeDetails(int units, double price) {
        this.units = units;
        this.price = price;
    }

    /**
     * Gets the number of units available for this flat type.
     *
     * @return the number of units
     */
    public int getUnits() {
        return units;
    }

    /**
     * Sets the number of units available for this flat type.
     *
     * @param units the new number of units
     */
    public void setUnits(int units) {
        this.units = units;
    }

    /**
     * Gets the selling price of this flat type.
     *
     * @return the selling price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the selling price of this flat type.
     *
     * @param price the new selling price
     */
    public void setPrice(double price) {
        this.price = price;
    }
} 