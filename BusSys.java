import java.util.*;

class Cust {
    String name;
    String phone;
    String email;
    String city;
    int age;

    public Cust(String name, String phone, String email, String city, int age) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.city = city;
        this.age = age;
    }
}

class Bus {
    String number; 
    int seats;
    String from;
    String to;
    String time;
    double fare;
    List<Integer> availableSeats;

    public Bus(String number, int seats, String from, String to, String time, double fare) {
        this.number = number; 
        this.seats = seats;
        this.from = from;
        this.to = to;
        this.time = time;
        this.fare = fare;
        this.availableSeats = new ArrayList<>();
        for (int i = 1; i <= seats; i++) {
            availableSeats.add(i);
        }
    }
}

class Res {
    Cust cust;
    Bus bus;
    int seat;

    public Res(Cust cust, Bus bus, int seat) {
        this.cust = cust;
        this.bus = bus;
        this.seat = seat;
    }
}

public class BusSys {
    private static List<Cust> customers = new ArrayList<>();
    private static List<Bus> buses = new ArrayList<>();
    private static List<Res> reservations = new ArrayList<>();
    private static Queue<Cust> waitQueue = new LinkedList<>();
    private static Stack<Cust> custStack = new Stack<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("___________________________");
            System.out.println("\n1. Register Customer");
            System.out.println("2. Register Bus");
            System.out.println("3. Search Buses");
            System.out.println("4. Reserve Seat");
            System.out.println("5. Cancel Reservation");
            System.out.println("6. Display All Reservations");
            System.out.println("7. Display Customers (Newest to Oldest)");
            System.out.println("8. Request New Seat"); 
            System.out.println("9. Exit");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {
                case 1:
                    registerCust(sc);
                    break;
                case 2:
                    registerBus(sc);
                    break;
                case 3:
                    searchBuses(sc);
                    break;
                case 4:
                    reserveSeat(sc);
                    break;
                case 5:
                    cancelRes(sc);
                    break;
                case 6:
                    displayRes();
                    break;
                case 7:
                    displayCust();
                    break;
                case 8:
                    requestNewSeat(sc); 
                    break;
                case 9:
                    System.out.println("Exiting. Goodbye!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void registerCust(Scanner sc) {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Mobile: ");
        String phone = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.nextLine();
        System.out.print("Enter City: ");
        String city = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = sc.nextInt();
        sc.nextLine(); 

        Cust cust = new Cust(name, phone, email, city, age);
        customers.add(cust);
        custStack.push(cust);
        System.out.println("Customer registered!");
    }

    private static void registerBus(Scanner sc) {
        System.out.print("Enter Bus Number: "); 
        String number = sc.nextLine(); 
        System.out.print("Enter Total Seats: ");
        int seats = sc.nextInt();
        sc.nextLine(); 
        System.out.print("Enter From: ");
        String from = sc.nextLine();
        System.out.print("Enter To: ");
        String to = sc.nextLine();
        System.out.print("Enter Time: ");
        String time = sc.nextLine();
        System.out.print("Enter Fare: ");
        double fare = sc.nextDouble();
        sc.nextLine(); 

        Bus bus = new Bus(number, seats, from, to, time, fare); 
        buses.add(bus); 
        System.out.println("Bus registered!");
    }

    private static void searchBuses(Scanner sc) {
        System.out.print("Enter From: ");
        String start = sc.nextLine();
        System.out.print("Enter To: ");
        String end = sc.nextLine();

        boolean found = false;
        for (Bus bus : buses) {
            if (bus.from.equalsIgnoreCase(start) && bus.to.equalsIgnoreCase(end)) {
                System.out.println("Bus Number: " + bus.number + ", Time: " + bus.time +
                        ", Available Seats: " + bus.availableSeats.size() + ", Fare: " + bus.fare);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No buses found for this route.");
        }
    }

    private static void reserveSeat(Scanner sc) {
        System.out.print("Enter Name: "); 
        String name = sc.nextLine();
        System.out.print("Enter Bus Number: "); 
        String busNumber = sc.nextLine(); 

        Cust cust = findCustByName(name); 
        Bus bus = findBusByNumber(busNumber); 

        if (cust == null || bus == null) {
            System.out.println("Customer or Bus not found.");
            return;
        }

        if (bus.availableSeats.isEmpty()) {
            waitQueue.add(cust);
            System.out.println("No seats available. You are added to the waiting list.");
        } else {
            int seat = bus.availableSeats.remove(0);
            Res res = new Res(cust, bus, seat);
            reservations.add(res);
            System.out.println("Seat " + seat + " reserved!"); 
        }
    }

    private static void cancelRes(Scanner sc) {
        System.out.print("Enter Name: "); 
        String name = sc.nextLine();
        System.out.print("Enter Bus Number: "); 
        String busNumber = sc.nextLine(); 

        Res res = findResByName(name, busNumber); 
        if (res == null) {
            System.out.println("Reservation not found.");
            return;
        }

        reservations.remove(res);
        res.bus.availableSeats.add(res.seat);
        System.out.println("Reservation cancelled.");

        if (!waitQueue.isEmpty()) {
            Cust nextCust = waitQueue.poll();
            int seat = res.bus.availableSeats.remove(0);
            Res newRes = new Res(nextCust, res.bus, seat);
            reservations.add(newRes);
            System.out.println("Seat " + seat + " assigned to " + nextCust.name + " from the waitlist.");
        }
    }

    private static void displayRes() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations.");
            return;
        }

        for (Res res : reservations) {
            System.out.println("Customer: " + res.cust.name + ", Bus Number: " + res.bus.number +
                    ", Seat: " + res.seat); 
        }
    }

    private static void displayCust() {
        if (custStack.isEmpty()) {
            System.out.println("No customers.");
            return;
        }
    
        System.out.println("Customers (Newest to Oldest):");
    
        Stack<Cust> tempStack = new Stack<>();
    
        while (!custStack.isEmpty()) {
            tempStack.push(custStack.pop());
        }
    
        while (!tempStack.isEmpty()) {
            Cust cust = tempStack.pop();
            System.out.println("Name: " + cust.name + ", Email: " + cust.email + ", Mobile: " + cust.phone);
            custStack.push(cust); 
        }
    }
    
    private static void requestNewSeat(Scanner sc) {
        System.out.print("Enter Name: "); 
        String name = sc.nextLine();
        System.out.print("Enter Bus Number: "); 
        String busNumber = sc.nextLine(); 

        Cust cust = findCustByName(name); 
        Bus bus = findBusByNumber(busNumber); 

        if (cust == null || bus == null) {
            System.out.println("Customer or Bus not found.");
            return;
        }

        Res res = findResByName(name, busNumber);
        if (res == null) {
            System.out.println("No reservation found for this customer and bus.");
            return;
        }

        waitQueue.add(cust);
        System.out.println("You are added to the waiting list for a new seat.");
    }

    private static Cust findCustByName(String name) { 
        for (Cust cust : customers) {
            if (cust.name.equalsIgnoreCase(name)) { 
                return cust;
            }
        }
        return null;
    }

    private static Bus findBusByNumber(String number) {
        for (Bus bus : buses) {
            if (bus.number.equalsIgnoreCase(number)) { 
                return bus;
            }
        }
        return null;
    }

    private static Res findResByName(String name, String busNumber) { 
        for (Res res : reservations) {
            if (res.cust.name.equalsIgnoreCase(name) && res.bus.number.equalsIgnoreCase(busNumber)) { 
                return res;
            }
        }
        return null;
    }
}