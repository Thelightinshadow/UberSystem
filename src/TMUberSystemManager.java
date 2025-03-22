import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/*
 * 
 * This class contains the main logic of the system.
 * 
 *  It keeps track of all users, drivers and service requests (RIDE or DELIVERY)
 * 
 */
public class TMUberSystemManager {
	private Map<String, User> users;
	private Map<String, Driver> drivers;
	
	private ArrayList<User> usersList;
	private ArrayList<Driver> driversList;
	private ArrayList<TMUberService> serviceRequests;
			
	@SuppressWarnings("unchecked")
	private Queue<TMUberService>[] queues = null;

	public double totalRevenue; // Total revenues accumulated via rides and deliveries

	// Rates per city block
	private static final double DELIVERYRATE = 1.2;
	private static final double RIDERATE = 1.5;

	// Portion of a ride/delivery cost paid to the driver
	private static final double PAYRATE = 0.1;

	// These variables are used to generate user account and driver ids
	int userAccountId = 900;
	int driverId = 700;

	public TMUberSystemManager() {
		users = new HashMap<String, User>();
		drivers = new HashMap<String, Driver>();
		usersList = new ArrayList<User>();
		driversList = new ArrayList<Driver>();
		initializeQueues();
		totalRevenue = 0;
	}

	@SuppressWarnings("unchecked")
	private void initializeQueues() {
		queues = new LinkedList[4];
		for (int i = 0; i < queues.length; i++) {
			this.queues[i] = new LinkedList<TMUberService>();
		}
	}
	// Generate a new user account id
	private String generateUserAccountId() {
		return "" + userAccountId + users.size();
	}

	// Generate a new driver id
	private String generateDriverId() {
		return "" + driverId + drivers.size();
	}

	public ArrayList<User> loadPreregisteredUsers(String filename) throws Exception {
		ArrayList<User> usersLst = TMUberRegistered.loadPreregisteredUsers(filename);
		setUsers(usersLst);
		return usersLst;
	}
	
	
	public ArrayList<Driver> loadPreregisteredDrivers(String filename) throws Exception {
		ArrayList<Driver> driversLst = TMUberRegistered.loadPreregisteredDrivers(filename);
		setDrivers(driversLst);
		return driversLst;
	}
	
	public void setUsers(ArrayList<User> userList) {
		for (int i = 0; i < userList.size(); i++) {
			users.put(userList.get(i).getAccountId(), userList.get(i));
		}
		this.usersList.addAll(userList);
	}
	
	public void setDrivers(ArrayList<Driver> driverList) {
		for (int i = 0; i < driverList.size(); i++) {
			drivers.put(driverList.get(i).getId(), driverList.get(i));
		}
		this.driversList.addAll(driverList);
	}

	public void populateTMUberServicesArray() {
		this.serviceRequests = new ArrayList<TMUberService>();
		for (int i = 0; i < queues.length; i++) {	
			Queue<TMUberService> queue = queues[i];
			if(queue != null && !queue.isEmpty()) {
				Iterator<TMUberService> qIterator = queue.iterator(); 
				while (qIterator.hasNext()) { 
					this.serviceRequests.add(qIterator.next());
				} 
			}
		}		
	}
	
	// Given user account id, find user in list of users
	public User getUser(String accountId) {
		for (Map.Entry<String, User> e : users.entrySet()) {
			if (e.getKey().equals(accountId)) {
				System.out.println(e.getKey() + " " + e.getValue());
				return e.getValue();
			}
		}    
		return null;
	}

	// Check for duplicate user
	private boolean userExists(User user) {
		for (Map.Entry<String, User> e : users.entrySet()) {
			if (e.getKey().equals(user.getAccountId())) {
				return true;
			}
		}    
		return false;
	}

	// Check for duplicate driver
	private boolean driverExists(Driver driver) {
		for (Map.Entry<String, Driver> e : drivers.entrySet()) {
			if (e.getKey().equals(driver.getId())) {
				return true;
			}
		}    		
		return false;
	}

	// Given a user, check if user ride/delivery request already exists in service
	// requests
	private boolean existingRequest(TMUberService req) {
		int zone = CityMap.getCityZone(req.getFrom());		
		
		if(zone != -1) {
			Queue<TMUberService> queue = queues[zone];
			if(queue != null) {
				Iterator<TMUberService> qIterator = queue.iterator(); 
			       
				while (qIterator.hasNext()) { 
					if (qIterator.next().equals(req))
						return true;
				} 					
			}
		}

		return false;
	}

	// Calculate the cost of a ride or of a delivery based on distance
	private double getDeliveryCost(int distance) {
		return distance * DELIVERYRATE;
	}

	private double getRideCost(int distance) {
		return distance * RIDERATE;
	}

	// Go through all drivers and see if one is available
	// Choose the first available driver
	private Driver getAvailableDriver() {
		for (Map.Entry<String, Driver> e : drivers.entrySet()) {
			if (e.getValue().getStatus() == Driver.Status.AVAILABLE) {
				return e.getValue();
			}
		}    		
		return null;
	}

	// Print Information (printInfo()) about all registered users in the system
	public void listAllUsers() {
		System.out.println();

		for (int i = 0; i < usersList.size(); i++) {
			int index = i + 1;
			System.out.printf("%-2s. ", index);
			usersList.get(i).printInfo();
			System.out.println();
		}
//		int index = 1;
//		for (Map.Entry<String, User> e : users.entrySet()) {
//			System.out.printf("%-2s. ", index);
//			e.getValue().printInfo();
//			System.out.println();	
//			index++;
//		}    
	}

	// Print Information (printInfo()) about all registered drivers in the system
	public void listAllDrivers() {
		System.out.println();
		
		for (int i = 0; i < driversList.size(); i++) {
			int index = i + 1;
			System.out.printf("%-2s. ", index);
			driversList.get(i).printInfo();
			System.out.println();
		}
//		int index = 1;
//		for (Map.Entry<String, Driver> e : drivers.entrySet()) {
//			System.out.printf("%-2s. ", index);
//			e.getValue().printInfo();
//			System.out.println();
//			index++;
//		}    		

	}

	// Print Information (printInfo()) about all current service requests
	public void listAllServiceRequests() {
		populateTMUberServicesArray();
		for (int i = 0; i < serviceRequests.size(); i++) {
			int index = i + 1;
			System.out.println();
			System.out.print(index + ". ");
			for (int j = 0; j < 60; j++)
				System.out.print("-");
			serviceRequests.get(i).printInfo();
			System.out.println();
		}
//		for (int i = 0; i < queues.length; i++) {
//			int index = i + 1;
//		
//			Queue<TMUberService> queue = queues[i];
//			if(queue != null && !queue.isEmpty()) {
//				Iterator<TMUberService> qIterator = queue.iterator(); 
//			       
//				while (qIterator.hasNext()) { 
//					System.out.println();
//					System.out.print(index + ". ");
//					for (int j = 0; j < 60; j++)
//						System.out.print("-");
//					qIterator.next().printInfo();
//					System.out.println();
//				} 				
//			}
//		}
	}

	// Add a new user to the system
	public void registerNewUser(String name, String address, double wallet) throws RuntimeException {
		// Check to ensure name is valid
		if (name == null || name.equals("")) {
			throw new InvalidUserNameException("Invalid User Name " + name);
		}
		// Check to ensure address is valid
		if (!CityMap.validAddress(address)) {
			throw new InvalidAddressException("Invalid User Address " + address);
		}
		// Check to ensure wallet amount is valid
		if (wallet < 0) {
			throw new InvalidMoneyInWalletException("Invalid Money in Wallet");
		}
		// Check for duplicate user
		User user = new User(generateUserAccountId(), name, address, wallet);
		if (userExists(user)) {
			throw new UserExistException("User Already Exists in System");
		}
		users.put(user.getAccountId(), user);
		this.usersList.add(user);
		return;
	}

	// Add a new driver to the system
	public void registerNewDriver(String name, String carModel, String carLicencePlate, String address) throws RuntimeException{
		// Check to ensure name is valid
		if (name == null || name.equals("")) {
			throw new InvalidDriverNameException("Invalid Driver Name " + name);
		}
		// Check to ensure car models is valid
		if (carModel == null || carModel.equals("")) {
			throw new InvalidCarModelException("Invalid Car Model " + carModel);
		}
		// Check to ensure car licence plate is valid
		// i.e. not null or empty string
		if (carLicencePlate == null || carLicencePlate.equals("")) {
			throw new InvalidLicencePlateException("Invalid Car Licence Plate " + carLicencePlate);
		}
		// Check for duplicate driver. If not a duplicate, add the driver to the drivers
		// list
		Driver driver = new Driver(generateDriverId(), name, carModel, carLicencePlate, address);
		if (driverExists(driver)) {
			throw new DriverExistException("Driver Already Exists in System");
		}
		//get the zone from the parameter address
		int zone = CityMap.getCityZone(address);
		//Set the driver zone
		driver.setZone(zone);
		//add the new driver to the drivers map
		drivers.put(driver.getId(), driver);
		//add the driver to the driversList
		this.driversList.add(driver);
		return;
	}

	// Request a ride. User wallet will be reduced when drop off happens
	public void requestRide(String accountId, String from, String to) throws RuntimeException {
		// Check valid user account
		User user = getUser(accountId);
		if (user == null) {
			throw new UserAccountNoFoundException("User Account Not Found " + accountId);
		}
		// Check for a valid from and to addresses
		if (!CityMap.validAddress(from)) {
			throw new InvalidAddressException("Invalid address " + from);
		}
		if (!CityMap.validAddress(to)) {
			throw new InvalidAddressException("Invalid address " + to);
		}
		// Get the distance for this ride
		int distance = CityMap.getDistance(from, to); // city blocks
		// Distance == 0 or == 1 is not accepted - walk!
		if (!(distance > 1)) {
			throw new InsufficientTravelDistanceException("Insufficient Travel Distance");
		}
		// Check if user has enough money in wallet for this trip
		double cost = getRideCost(distance);
		if (user.getWallet() < cost) {
			throw new InsufficientFundsException("Insufficient Funds");
		}
		// Get an available driver
		Driver driver = getAvailableDriver();
		if (driver == null) {
			throw new NoDriversAvailableException("No Drivers Available");
		}
		// Create the request
		TMUberRide req = new TMUberRide(from, to, user, distance, cost);

		// Check if existing ride request for this user - only one ride request per user
		// at a time
		if (existingRequest(req)) {
			throw new UserHasRideRequestException("User Already Has Ride Request");
		}
		//Set the driver status to DRIVING
		driver.setStatus(Driver.Status.DRIVING);
		//Set the driver service to requested service
		driver.setTmuberService(req);
		//get the zone and then add the request to the proper queue using the zone
		int zone = CityMap.getCityZone(from);
		if(zone == -1) {
			//cannot find the zone based on the "from" address
			throw new InvalidZoneNumberException("Invalid Zone Number: " + zone + " for address: " + from);			
		}		
		queues[zone].add(req);
		user.addRide();
		return;
	}

	// Request a food delivery. User wallet will be reduced when drop off happens
	public void requestDelivery(String accountId, String from, String to, String restaurant, String foodOrderId) throws RuntimeException {

		// Check for valid user account
		User user = getUser(accountId);
		if (user == null) {
			throw new UserAccountNoFoundException("User Account Not Found " + accountId);
		}
		// Check for valid from and to address
		if (!CityMap.validAddress(from)) {
			throw new InvalidAddressException("Invalid address " + from);
		}
		if (!CityMap.validAddress(to)) {
			throw new InvalidAddressException("Invalid address " + to);
		}
		// Get the distance to travel
		int distance = CityMap.getDistance(from, to); // city blocks
		// Distance must be at least 1 city block
		if (distance == 0) {
			throw new InsufficientTravelDistanceException("Insufficient Travel Distance");
		}
		// Check if user has enough money in wallet for this delivery
		double cost = getDeliveryCost(distance);
		if (user.getWallet() < cost) {
			throw new InsufficientFundsException("Insufficient Funds");
		}
		// Find an available driver, if any
		Driver driver = getAvailableDriver();
		if (driver == null) {
			throw new NoDriversAvailableException("No Drivers Available");
		}
		TMUberDelivery delivery = new TMUberDelivery(from, to, user, distance, cost, restaurant, foodOrderId);
		// Check if existing delivery request for this user for this restaurant and food
		// order #
		if (existingRequest(delivery)) {
			throw new UserHasDeliveryException("User Already Has Delivery Request at Restaurant with this Food Order");
		}
		driver.setStatus(Driver.Status.DRIVING);
		
		int zone = CityMap.getCityZone(from);
		if(zone == -1) {
			//cannot find the zone based on the "from" address
			throw new InvalidZoneNumberException("Invalid Zone Number: " + zone + " for address: " + from);			
		}		
		queues[zone].add(delivery);
		user.addDelivery();	
		return;
	}

	// cancel an existing service request.
	// parameter request is the index in the serviceRequests array list
	public void cancelServiceRequest(int zoneNum, int request) throws RuntimeException {
		//check if valid zone #
		if (zoneNum < 0 || zoneNum > 3) {
			throw new InvalidZoneNumberException("Invalid Zone # " + zoneNum);
		}
		//check if valid request #
		if (request < 1 || queues[zoneNum] == null || queues[zoneNum].isEmpty()) {
			throw new InvalidRequestNumberException("Invalid Request # " + request);
		}
		TMUberService tmUberService = findRequestByRequestId(queues[zoneNum], request - 1);
		if(tmUberService != null)
			queues[zoneNum].remove(tmUberService);
		return;
	}
	//this method is to find the request id in the 4 queues
	private TMUberService findRequestByRequestId(Queue<TMUberService> queue, int request) {
		//return null if queue is null
        if (queue == null) {
            return null;
        }

        int numberOfQueues = queue.size();
        //return null if the request is greater than the numberOfQueues
        if (request < 0 || numberOfQueues < request + 1) {
            return null;
        }
        //find the service based on the request id
        TMUberService element = null;
        for (int i = 0; i < numberOfQueues; i++) {
            if (i == request) {
                element = queue.remove();
            } else {
                queue.add(queue.remove());
            }
        }
        return element;  		
	}
	
	// Find driver by driverId
	private Driver findDriverByDriverId(String driverId, Driver.Status status) {
//		for (int i = 0; i < driversList.size(); i++) {
//			if (driversList.get(i).getId().equals(driverId) && driversList.get(i).getStatus() == status)
//				return driversList.get(i);
//		}
		//Iterate through the drivers map and find the driver
		for (Map.Entry<String, Driver> e : drivers.entrySet()) {
			if (e.getValue().getId().equals(driverId) && e.getValue().getStatus() == status) {
				return e.getValue();
			}
		}    		
		
		return null;
	}

	// Drop off a ride or a delivery. This completes a service.
	// parameter request is the index in the serviceRequests array list
	public void dropOff(String driverId) throws RuntimeException {
		//check if driverId is blank or null
		if (driverId == null || "".equals(driverId)) {
			throw new InvalidDriverIdException("Invalid Driver ID " + driverId);
		}
		//find the driver by Id and has Driving Status
		Driver driver = findDriverByDriverId(driverId, Driver.Status.DRIVING);
		if(driver == null) {
			//cannot find the driver with the given driver id and status driving
			throw new DriverNotFoundException("Driver not found with driver ID " + driverId + " and driver status driving");
		}

		//get the uber service from the driver object
		TMUberService service = driver.getTmuberService();			
		totalRevenue += service.getCost(); // add service cost to revenues
		driver.pay(service.getCost()*PAYRATE);      // pay the driver
		totalRevenue -= service.getCost() * PAYRATE; // deduct driver fee from total revenues
		driver.setStatus(Driver.Status.AVAILABLE);  // driver is now available again
		User user = service.getUser();
		user.payForService(service.getCost()); // user pays for ride or delivery
		//set the driver address to the service To address
		driver.setAddress(service.getTo());
		//remove the tmuberservice object from the driver
		driver.setTmuberService(null);
		//set the driver new zone.
		driver.setZone(CityMap.getCityZone(driver.getAddress()));
		return;
	}

	public void pickup(String driverId) throws RuntimeException {
		//check if driverId is blank or null
		if (driverId == null || "".equals(driverId)) {
			throw new InvalidDriverIdException("Invalid Driver ID " + driverId);
		}
		//find the driver by Id and has Available Status
		Driver driver = findDriverByDriverId(driverId, Driver.Status.AVAILABLE);
		if(driver == null) {
			//cannot find the driver with the given driver id and status available
			throw new DriverNotFoundException("Driver not found with driver ID " + driverId + " and driver status available");			
		}

		int zone = CityMap.getCityZone(driver.getAddress());
		if(zone == -1) {
			//cannot find the zone for the driver address
			throw new InvalidZoneNumberException("Invalid Zone Number: " + zone + " for address: " + driver.getAddress());			
		}
		
		driver.setZone(zone);		
		//check if valid request #
		if (queues[zone] == null || queues[zone].isEmpty()) {
			throw new NoServiceRequestInQueueException("No service request in the queue at zone " + zone);
		}
		//remove the service from the queue
		TMUberService service = queues[zone].remove();		
		//set the driver service to the request service
		driver.setTmuberService(service);
		//set driver status to driving
		driver.setStatus(Driver.Status.DRIVING);  // driver is now driving
		//set the driver new address
		driver.setAddress(driver.getTmuberService().getFrom());				
		return;
	}
	
	public void driveTo(String driverId, String address) throws RuntimeException {
		//check if driverid is null or blank
		if (driverId == null || "".equals(driverId)) {
			throw new InvalidDriverIdException("Invalid Driver ID " + driverId);
		}
		//check if address is null or blank
		if (address == null || "".equals(address)) {
			throw new InvalidAddressException("Invalid address " + address);
		}		
		//check if address is valid
		if (!CityMap.validAddress(address)) {
			throw new InvalidAddressException("Invalid address " + address);
		}
	
		Driver driver = findDriverByDriverId(driverId, Driver.Status.AVAILABLE);
		if(driver == null) {
			//cannot find the driver with the given driver id and status available
			throw new DriverNotFoundException("Driver not found with driver ID " + driverId + " and driver status available");
		}
		driver.setAddress(address);
		driver.setZone(CityMap.getCityZone(driver.getAddress()));
		driver.setStatus(Driver.Status.DRIVING); 
		return;
	}
	
	// Sort users by name
	public void sortByUserName() {
		Collections.sort(usersList, new NameComparator());
		listAllUsers();
	}

	private class NameComparator implements Comparator<User> {
		public int compare(User a, User b) {
			return a.getName().compareTo(b.getName());
		}
	}

	// Sort users by number amount in wallet
	public void sortByWallet() {
		Collections.sort(usersList, new UserWalletComparator());
		listAllUsers();
	}

	private class UserWalletComparator implements Comparator<User> {
		public int compare(User a, User b) {
			if (a.getWallet() > b.getWallet())
				return 1;
			if (a.getWallet() < b.getWallet())
				return -1;
			return 0;
		}
	}

	// Sort trips (rides or deliveries) by distance
	// class TMUberService must implement Comparable
	public void sortByDistance() {
		populateTMUberServicesArray();
		Collections.sort(serviceRequests);
		listAllServiceRequests();
	}
}

class InvalidRequestNumberException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public InvalidRequestNumberException(String errorMessage) {
        super(errorMessage);
    }
}
class DriverNotFoundException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public DriverNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
class InvalidDriverIdException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public InvalidDriverIdException(String errorMessage) {
        super(errorMessage);
    }
}
class InvalidAddressException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public InvalidAddressException(String errorMessage) {
        super(errorMessage);
    }
}
class AccountNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    public AccountNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
class InsufficientFundsException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public InsufficientFundsException(String errorMessage) {
        super(errorMessage);
    }
}
class InsufficientTravelDistanceException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    public InsufficientTravelDistanceException(String errorMessage) {
        super(errorMessage);
    }
}
class NoDriversAvailableException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
	public NoDriversAvailableException(String errorMessage) {
        super(errorMessage);
    }
}
class InvalidUserNameException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public InvalidUserNameException(String errorMessage) {
        super(errorMessage);
    }
}
class InvalidMoneyInWalletException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public InvalidMoneyInWalletException(String errorMessage) {
        super(errorMessage);
    }
}
class UserAlreadyExistException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public UserAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }
}
class InvalidDriverNameException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    public InvalidDriverNameException(String errorMessage) {
        super(errorMessage);
    }
}
class InvalidCarModelException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public InvalidCarModelException(String errorMessage) {
        super(errorMessage);
    }
}
class InvalidLicencePlateException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public InvalidLicencePlateException(String errorMessage) {
        super(errorMessage);
    }
}
class DriverExistException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public DriverExistException(String errorMessage) {
        super(errorMessage);
    }
}
class UserExistException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public UserExistException(String errorMessage) {
        super(errorMessage);
    }
}
class UserAccountNoFoundException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public UserAccountNoFoundException(String errorMessage) {
        super(errorMessage);
    }
}
class UserHasDeliveryException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public UserHasDeliveryException(String errorMessage) {
        super(errorMessage);
    }
}
class UserHasRideRequestException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public UserHasRideRequestException(String errorMessage) {
        super(errorMessage);
    }
}
class NoServiceRequestInQueueException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public NoServiceRequestInQueueException(String errorMessage) {
        super(errorMessage);
    }
}
class InvalidZoneNumberException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
    public InvalidZoneNumberException(String errorMessage) {
        super(errorMessage);
    }
}