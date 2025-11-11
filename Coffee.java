/**
* This is the class COffee extending Drink
*/
public class Coffee extends Drink {
    public Coffee(String name, String size, double price) {
        super(name, size, price);
    }

    @Override
    public String toString() {
        return "[Coffee] " + super.toString();
    }
}
