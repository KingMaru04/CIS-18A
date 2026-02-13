import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.io.FileWriter;
import java.io.IOException;

/* ---------------- INTERFACES ---------------- */

interface Billable{
    double calculateTotal();
}

interface Schedulable{
    boolean isWithinBusinessHours(LocalTime time);
}

/* ---------------- BASE CLASS ---------------- */

class Business{
    protected String businessName;
    protected int openingHour;
    protected int closingHour;

    public Business(String businessName, int openingHour, int closingHour){
        this.businessName = businessName;
        this.openingHour = openingHour;
        this.closingHour = closingHour;
    }

    public String getBusinessName(){
        return businessName;
    }
}

/* ---------------- CHILD CLASS (INHERITANCE) ---------------- */

class MobileCarDetailer extends Business implements Billable, Schedulable{
    // Encapsulated data
    private String[] services;
    private double[] prices;

    private int selectedService;
    private int quantity;

    public MobileCarDetailer(){
        super("Sparkle Mobile Car Detailer", 8, 18);

        services = new String[]{"Exterior Wash",
                                "Interior Deep Clean",
                                "Full Detail Package"};

        prices = new double[]{25.00,
                              45.00,
                              70.00};
    }

    public void displayMenu(){
        System.out.println("\n--- Service Menu ---");

        for(int i = 0; i < services.length; i++)
        {
            System.out.println((i + 1) + ". " + services[i] + " - $" + prices[i]);
        }
    }

    public boolean setSelection(int option, int quantity){
        if(option >= 1 && option <= services.length && quantity > 0)
        {
            this.selectedService = option - 1;
            this.quantity = quantity;
            return true;
        }
        return false;
    }

    public String getSelectedService(){
        return services[selectedService];
    }

    public int getQuantity(){
        return quantity;
    }

    public double getUnitPrice(){
        return prices[selectedService];
    }

    // Interface implementation
    public double calculateTotal(){
        return prices[selectedService] * quantity;
    }

    // Interface implementation
    public boolean isWithinBusinessHours(LocalTime time){
        int hour = time.getHour();
        return hour >= openingHour && hour < closingHour;
    }
}

/* ---------------- MAIN APPLICATION CLASS ---------------- */

public class OrderSystem{
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);

        MobileCarDetailer shop = new MobileCarDetailer();

        System.out.println("Welcome to " + shop.getBusinessName());

        shop.displayMenu();

        int option;
        int qty;

        boolean validSelection = false;

        // Controlled statement (while loop)
        while(!validSelection){
            System.out.print("\nSelect a service (1-3): ");
            option = input.nextInt();

            System.out.print("Enter quantity of vehicles: ");
            qty = input.nextInt();

            // If statement
            if(shop.setSelection(option, qty)){
                validSelection = true;
            }
            else{
                System.out.println("Invalid selection. Try again.");
            }
        }

        input.nextLine(); // clear buffer

        System.out.print("\nEnter appointment date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(input.nextLine());

        LocalTime time;
        boolean validTime = false;

        // Do-while loop
        do{
            System.out.print("Enter appointment time (HH:MM): ");
            time = LocalTime.parse(input.nextLine());

            if(shop.isWithinBusinessHours(time)){
                validTime = true;
            }
            else{
                System.out.println("Outside business hours (08:00 - 18:00).");
            }

        } while(!validTime);

        double total = shop.calculateTotal();

        System.out.println("\n----- Order Summary -----");
        System.out.println("Service: " + shop.getSelectedService());
        System.out.println("Quantity: " + shop.getQuantity());
        System.out.println("Price per service: $" + shop.getUnitPrice());
        System.out.println("Total: $" + total);
        System.out.println("Appointment: " + date + " at " + time);

        // Switch statement for feedback message
        switch(shop.getQuantity()){
            case 1:
                System.out.println("Single vehicle service scheduled.");
                break;
            case 2:
            case 3:
                System.out.println("Multi-vehicle service scheduled.");
                break;
            default:
                System.out.println("Fleet service scheduled.");
        }

        // Output to file
        writeToFile(shop, date, time, total);

        input.close();
    }

    private static void writeToFile(MobileCarDetailer shop,
                                    LocalDate date,
                                    LocalTime time,
                                    double total)
    {
        try
        {
            FileWriter writer = new FileWriter("order_summary.txt");

            writer.write("Business: " + shop.getBusinessName() + "\n");
            writer.write("Service: " + shop.getSelectedService() + "\n");
            writer.write("Quantity: " + shop.getQuantity() + "\n");
            writer.write("Unit price: $" + shop.getUnitPrice() + "\n");
            writer.write("Total: $" + total + "\n");
            writer.write("Appointment date: " + date + "\n");
            writer.write("Appointment time: " + time + "\n");

            writer.close();

            System.out.println("\nOrder summary saved to order_summary.txt");
        }
        catch(IOException e){
            System.out.println("File error: " + e.getMessage());
        }
    }
}
