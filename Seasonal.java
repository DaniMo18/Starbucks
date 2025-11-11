public class Seasonal extends Drink {
    public Seasonal(String name, String size, double price) {
        super(name, size, price);
    }

    @Override
    public String toString() {
        return "[Seasonal] " + super.toString();
    }
}
