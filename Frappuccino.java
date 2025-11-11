public class Frappuccino extends Drink {
    public Frappuccino(String name, String size, double price) {
        super(name, size, price);
    }

    @Override
    public String toString() {
        return "[Frappuccino] " + super.toString();
    }
}
