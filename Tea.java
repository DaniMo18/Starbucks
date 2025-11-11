public class Tea extends Drink {
    public Tea(String name, String size, double price) {
        super(name, size, price);
    }

    @Override
    public String toString() {
        return "[Tea] " + super.toString();
    }
}

