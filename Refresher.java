public class Refresher extends Drink {
    public Refresher(String name, String size, double price) {
        super(name, size, price);
    }

    @Override
    public String toString() {
        return "[Refresher] " + super.toString();
    }
}
