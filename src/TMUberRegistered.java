import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TMUberRegistered
{
    // These variables are used to generate user account and driver ids
    private static int firstUserAccountID = 900;
    private static int firstDriverId = 700;

    // Generate a new user account id
    public static String generateUserAccountId(ArrayList<User> current) {
        return "" + firstUserAccountID + current.size();
    }

    // Generate a new driver id
    public static String generateDriverId(ArrayList<Driver> current)
    {
        return "" + firstDriverId + current.size();
    }

    // Database of Preregistered users
    // In Assignment 2 these will be loaded from a file
    // The test scripts and test outputs included with the skeleton code use these
    // users and drivers below. You may want to work with these to test your code (i.e. check your output with the
    // sample output provided). 
    public static ArrayList<User> loadPreregisteredUsers(String filename) throws Exception {
		ArrayList<User> usersList = new ArrayList<User>();
		try {  
			//the file to be opened for reading  
			FileInputStream fis = new FileInputStream(filename);       
			Scanner sc = new Scanner(fis);    
			
			while(sc.hasNextLine()) {
				User user = new User(generateUserAccountId(usersList), sc.nextLine(), sc.nextLine(), sc.nextInt());
				if(sc.hasNextLine()) {
					sc.nextLine();
				}
				user.printInfo();
				System.out.println();
				usersList.add(user);
			}  
			sc.close();      
		} catch(FileNotFoundException e) {  
			throw e;  
		} catch(IOException ioe) {  
			throw ioe;  
		}
		return usersList;
    }


    // Database of Preregistered users
    // In Assignment 2 these will be loaded from a file
    public static ArrayList<Driver> loadPreregisteredDrivers(String filename) throws Exception {
    	ArrayList<Driver> driversList = new ArrayList<Driver>();
		try {  
			//the file to be opened for reading  
			FileInputStream fis = new FileInputStream(filename);       
			Scanner sc = new Scanner(fis);    
			
			while(sc.hasNextLine()) {
				Driver driver = new Driver(generateDriverId(driversList), sc.nextLine(), sc.nextLine(), sc.nextLine(), sc.nextLine());
//				if(sc.hasNextLine()) {
//					sc.nextLine();
//				}
				driver.printInfo();
				System.out.println();
				driversList.add(driver);
			}  
			sc.close();      
		} catch(FileNotFoundException e) {  
			throw e;  
		} catch(IOException ioe) {  
			throw ioe;  
		}
		return driversList;
    }
}

