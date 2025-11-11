import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Starbucks_main {

    // Track ordered items for the session
    static class OrderItem {
        final String name;
        final String size;
        final int qty;
        final double unitPrice;

        OrderItem(String name, String size, int qty, double unitPrice) {
            this.name = name;
            this.size = size;
            this.qty = qty;
            this.unitPrice = unitPrice;
        }

        double total() { return unitPrice * qty; }
    }

    public static void main(String[] args) {
        String filePath = "menu.csv";
        Map<String, Drink> menu = new HashMap<>();
        List<OrderItem> sales = new ArrayList<>();
        double totalPrice = 0.0;

        // Load CSV -> menu
        if (!loadMenu(filePath, menu)) return;

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=== Welcome to Starbucks ===");
        while (running) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Show all Available Drinks");
            System.out.println("2. Search Drinks by Type");
            System.out.println("3. Place an Order (one or more)");
            System.out.println("4. View Today's Sales Summary");
            System.out.println("5. Quit");
            System.out.print("Enter choice: ");

            int choice = readInt(scanner);
            switch (choice) {
                case 1:
                    displayAllDrinks(menu);
                    break;

                case 2:
                    String type = promptType(scanner);
                    displayDrinksByType(menu, type);
                    break;

                case 3:
                    double added = handleOrderFlow(scanner, menu, sales);
                    totalPrice += added;
                    break;

                case 4:
                    showSalesSummary(sales, totalPrice);
                    break;

                case 5:
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }

        // Final receipt
        System.out.println("\n=== Final Receipt ===");
        System.out.printf("Total amount due: $%.2f%n", totalPrice);
        System.out.println("Thank you for visiting Starbucks!");

        scanner.close();
    }

    //Menu loading 
    private static boolean loadMenu(String csvPath, Map<String, Drink> menu) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String name  = parts[0].trim();
                String type  = parts[1].trim();  // "1".."5" or words
                String size  = parts[2].trim();
                double price = Double.parseDouble(parts[3].trim());

                String normalizedType = normalizeType(type);
                Drink drink = createDrink(normalizedType, name, size, price);
                if (drink == null) continue;

                menu.put(key(name, size), drink);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
            return false;
        }
    }

    private static String normalizeType(String t) {
        String s = t.toLowerCase();
        switch (s) {
            case "1": case "coffee": return "coffee";
            case "2": case "tea": return "tea";
            case "3": case "refresher": return "refresher";
            case "4": case "frappuccino": return "frappuccino";
            case "5": case "seasonal": return "seasonal";
            default: return "";
        }
    }

    private static Drink createDrink(String type, String name, String size, double price) {
        switch (type) {
            case "coffee": return new Coffee(name, size, price);
            case "tea": return new Tea(name, size, price);
            case "refresher": return new Refresher(name, size, price);
            case "frappuccino": return new Frappuccino(name, size, price);
            case "seasonal": return new Seasonal(name, size, price);
            default: return null;
        }
    }

    private static String key(String name, String size) { return name + "-" + size; }

    private static String typeOf(Drink d) { return d.getClass().getSimpleName().toLowerCase(); }

    //Option 1 show all
    private static void displayAllDrinks(Map<String, Drink> menu) {
        Map<String, List<Drink>> byType = new TreeMap<>();
        for (Drink d : menu.values()) {
            byType.computeIfAbsent(typeOf(d), k -> new ArrayList<>()).add(d);
        }
        for (Map.Entry<String, List<Drink>> e : byType.entrySet()) {
            System.out.println("\n" + capitalize(e.getKey()) + ":");
            e.getValue().sort(Comparator.comparing(Drink::getName).thenComparing(Drink::getSize));
            for (Drink d : e.getValue()) {
                System.out.printf("  %-25s %-8s $%.2f%n", d.getName(), d.getSize(), d.getPrice());
            }
        }
    }

    //  Option 2 show by type 
    private static void displayDrinksByType(Map<String, Drink> menu, String type) {
        List<Drink> list = new ArrayList<>();
        for (Drink d : menu.values()) {
            if (typeOf(d).equalsIgnoreCase(type)) list.add(d);
        }
        if (list.isEmpty()) {
            System.out.println("No drinks found for type: " + type);
            return;
        }
        list.sort(Comparator.comparing(Drink::getName).thenComparing(Drink::getSize));
        System.out.println("\n" + capitalize(type) + " drinks:");
        for (Drink d : list) {
            System.out.printf("  %-25s %-8s $%.2f%n", d.getName(), d.getSize(), d.getPrice());
        }
    }

    private static String promptType(Scanner sc) {
        System.out.println("\nChoose a drink type:");
        System.out.println("1. Coffee");
        System.out.println("2. Tea");
        System.out.println("3. Refresher");
        System.out.println("4. Frappuccino");
        System.out.println("5. Seasonal");
        System.out.print("Enter choice: ");
        int c = readInt(sc);
        switch (c) {
            case 1: return "coffee";
            case 2: return "tea";
            case 3: return "refresher";
            case 4: return "frappuccino";
            case 5: return "seasonal";
            default: return "Wrong input Try again";
        }
    }

    // Option 3 ordering flow
    private static double handleOrderFlow(Scanner scanner, Map<String, Drink> menu, List<OrderItem> sales) {
        double addedTotal = 0.0;
        boolean keepOrdering = true;

        while (keepOrdering) {
            String chosenType = promptType(scanner);

            // gather unique drink names for that type
            List<String> drinkNames = new ArrayList<>();
            for (Drink d : menu.values()) {
                if (typeOf(d).equalsIgnoreCase(chosenType) && !drinkNames.contains(d.getName())) {
                    drinkNames.add(d.getName());
                }
            }
            if (drinkNames.isEmpty()) {
                System.out.println("No drinks found for this type.");
                break;
            }
            Collections.sort(drinkNames);

            System.out.println("\nAvailable " + chosenType + " drinks:");
            for (int i = 0; i < drinkNames.size(); i++) {
                System.out.println((i + 1) + ". " + drinkNames.get(i));
            }
            System.out.print("\nSelect a drink by number: ");
            int drinkChoice = readInt(scanner);
            if (drinkChoice < 1 || drinkChoice > drinkNames.size()) {
                System.out.println("Invalid drink selection.");
                continue;
            }
            String chosenDrinkName = drinkNames.get(drinkChoice - 1);

            // sizes for that drink
            List<String> sizes = new ArrayList<>();
            for (Drink d : menu.values()) {
                if (d.getName().equalsIgnoreCase(chosenDrinkName) && !sizes.contains(d.getSize())) {
                    sizes.add(d.getSize());
                }
            }
            if (sizes.isEmpty()) {
                System.out.println("No sizes found for that drink.");
                continue;
            }
            sizes.sort(String::compareTo);
            System.out.println("\nAvailable sizes for " + chosenDrinkName + ":");
            for (int i = 0; i < sizes.size(); i++) {
                System.out.println((i + 1) + ". " + sizes.get(i));
            }
            System.out.print("\nSelect a size by number: ");
            int sizeChoice = readInt(scanner);
            if (sizeChoice < 1 || sizeChoice > sizes.size()) {
                System.out.println("Invalid size selection.");
                continue;
            }
            String chosenSize = sizes.get(sizeChoice - 1);

            Drink selected = menu.get(key(chosenDrinkName, chosenSize));
            if (selected == null) {
                System.out.println("Invalid selection.");
                continue;
            }

            System.out.print("Enter quantity: ");
            int quantity = readInt(scanner);
            if (quantity <= 0) {
                System.out.println("Quantity must be at least 1.");
                continue;
            }

            double itemTotal = selected.getPrice() * quantity;
            addedTotal += itemTotal;
            sales.add(new OrderItem(selected.getName(), selected.getSize(), quantity, selected.getPrice()));

            System.out.printf("\nAdded: %d x %s (%s) = $%.2f%n",
                    quantity, selected.getName(), selected.getSize(), itemTotal);

            System.out.print("\nOrder another? (1 = yes, 2 = no): ");
            int more = readInt(scanner);
            if (more == 2) keepOrdering = false;
        }
        return addedTotal;
    }

    // Option 4 sales summary 
    private static void showSalesSummary(List<OrderItem> sales, double totalPrice) {
        System.out.println("\n=== Today's Sales Summary ===");
        if (sales.isEmpty()) {
            System.out.println("No sales yet.");
            return;
        }
        int totalDrinks = 0;
        Map<String, Integer> byDrink = new TreeMap<>();
        for (OrderItem oi : sales) {
            totalDrinks += oi.qty;
            String k = oi.name + " (" + oi.size + ")";
            byDrink.put(k, byDrink.getOrDefault(k, 0) + oi.qty);
        }
        for (Map.Entry<String, Integer> e : byDrink.entrySet()) {
            System.out.printf("  %-30s x %d%n", e.getKey(), e.getValue());
        }
        System.out.printf("Total drinks sold: %d%n", totalDrinks);
        System.out.printf("Revenue: $%.2f%n", totalPrice);
    }

    // Helpers 
    private static int readInt(Scanner sc) {
        while (true) {
            try {
                String s = sc.nextLine().trim();
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // Optional: show drinks from CSV by a text category 
    public static void displayDrinks(String category) {
        String csvFile = "menu.csv";
        String line;
        String split = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // skip header
            boolean found = false;
            while ((line = br.readLine()) != null) {
                String[] drinkData = line.split(split);
                if (drinkData.length >= 4 && normalizeType(drinkData[1].trim()).equalsIgnoreCase(category)) {
                    String name = drinkData[0].trim();
                    String size = drinkData[2].trim();
                    String price = drinkData[3].trim();
                    System.out.println(name + " " + size + " $" + price);
                    found = true;
                }
            }
            if (!found) System.out.println("No drinks for category: " + category);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
