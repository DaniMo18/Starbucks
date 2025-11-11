public class Drink {
    private String name;
    private String size;
    private double price;

    public Drink(String name, String size, double price) {
        this.name = name;
        this.size = size;
        this.price = price;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name + " (" + ", " + size + ") - $" + price;
    }
}

