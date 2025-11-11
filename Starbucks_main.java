import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Starbucks_main {

    
    public static void main(String[] args) {
        String filePath = "menu.csv"; 
        Map<String, Drink> menu = new HashMap<>();

        // 1. Read CSV into menu
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; 
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String name = parts[0].trim();
                String type = parts[1].trim();
                String size = parts[2].trim();
                double price = Double.parseDouble(parts[3].trim());

                Drink drink;
                switch (type.toLowerCase()) {
                    case "1":
                        drink = new Coffee(name, size, price);
                        break;
                    case "2":
                        drink = new Tea(name, size, price);
                        break;
                    case "3":
                        drink = new Refresher(name, size, price);
                        break;
                    case "4":
                        drink = new Frappuccino(name, size, price);
                        break;
                    case "5":
                        drink = new Seasonal(name, size, price);
                        break;
                    default:
                        continue;
                }

             
                menu.put(name + "-" + size, drink);
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        double totalPrice = 0.0; // Running total for the order
        boolean keepOrdering = true;

        System.out.println("=== Welcome to Starbucks Please Select an Option ===");

        while (keepOrdering) {
            // ask user for drink type
           
            System.out.println("1. Show all Available Drinks");
            System.out.println("2. Search Drinks by Type");
            System.out.println("3. Place an Order(one or more)");
            System.out.println("4. View Todays Sales Summary");
            System.out.println("5. Quit");

            int typeChoice = scanner.nextInt();
            scanner.nextLine(); // Clear newline so it doest skip

            String chosenType = "";
            switch (typeChoice) {
                case 1: chosenType = "coffee"; break;
                case 2: chosenType = "tea"; break;
                case 3: chosenType = "refresher"; break;
                case 4: chosenType = "frappuccino"; break;
                case 5: chosenType = "seasonal"; break;
                default:
                    System.out.println("Invalid choice. Try again.");
                    continue;
            }

            // 3. Display numbered list of drinks for that type
            List<String> drinkNames = new ArrayList<>();
            System.out.println("\nAvailable " + chosenType + " drinks:");
            for (Drink d : menu.values()) {
                if (d.getClass().getSimpleName().equalsIgnoreCase(chosenType) && !drinkNames.contains(d.getName())) {
                    drinkNames.add(d.getName());
                }
            }

            // If no drinks found for this type
            if (drinkNames.isEmpty()) {
                System.out.println("No drinks found for this type.");
                continue;
            }

            for (int i = 0; i < drinkNames.size(); i++) {
                System.out.println((i + 1) + ". " + drinkNames.get(i));
            }

            // 4. Ask for drink by number
            System.out.print("\nSelect a drink by number: ");
            int drinkChoice = scanner.nextInt();
            scanner.nextLine(); // Clear newline

            if (drinkChoice < 1 || drinkChoice > drinkNames.size()) {
                System.out.println("Invalid drink selection. Try again.");
                continue;
            }

            String chosenDrinkName = drinkNames.get(drinkChoice - 1);

            // 5. Display numbered list of sizes for that drink
            List<String> sizes = new ArrayList<>();
            for (Drink d : menu.values()) {
                if (d.getName().equalsIgnoreCase(chosenDrinkName) && !sizes.contains(d.getSize())) {
                    sizes.add(d.getSize());
                }
            }

            System.out.println("\nAvailable sizes for " + chosenDrinkName + ":");
            for (int i = 0; i < sizes.size(); i++) {
                System.out.println((i + 1) + ". " + sizes.get(i));
            }

            System.out.print("\nSelect a size by number: ");
            int sizeChoice = scanner.nextInt();
            scanner.nextLine(); // Clear newline

            if (sizeChoice < 1 || sizeChoice > sizes.size()) {
                System.out.println("Invalid size selection. Try again.");
                continue;
            }

            String chosenSize = sizes.get(sizeChoice - 1);

            // 6. Find drink and check if it's valid
            Drink selected = menu.get(chosenDrinkName + "-" + chosenSize);
            if (selected == null) {
                System.out.println("Invalid selection. Please try again.");
                continue;
            }

            // 7. Ask for quantity
            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Clear newline

            if (quantity <= 0) {
                System.out.println("Quantity must be at least 1.");
                continue;
            }

            // 8. Add to total
            double itemTotal = selected.getPrice() * quantity;
            totalPrice += itemTotal;

            System.out.println("\nAdded to order: " + quantity + " x " + selected.getName() + " (" + selected.getSize() + ") = $" + String.format("%.2f", itemTotal));

            // 9. Ask if they want to order another drink
            System.out.print("\nWould you like to order another drink? (yes(1)/no(2)): ");
            int more = scanner.nextInt();

            if (more == 2) {
                keepOrdering = false;
            }
        }
        public static void displayDrinks(String category){
            String csvFile = menu.csv;
            String line;
            String split = ",";
            try(BufferedReader bf = new BufferedReader(menu.csv)){
                bf.readLine();
                
                while((line = br.readLine()) != null){
                    String[] drinkData = line.split(split);

                    //Assuming category is second column
                    if(drinkData.length > 1 && drinkData[1].trim().equalsIgnoreCase(category)){
                        System.out.println("Drink " + drinkData[0].trim() + "Price "+ drinkData[1].trim());
                    }
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }

        // 10. Final total
        System.out.println("\n=== Final Receipt ===");
        System.out.println("Total amount due: $" + String.format("%.2f", totalPrice));
        System.out.println("Thank you for visiting Starbucks!");

        scanner.close();
    }
}



