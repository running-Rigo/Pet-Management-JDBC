import java.sql.*;
import java.util.ArrayList;

public class DatabaseHandler {
    public static Connection connection;
    public static boolean hasHouseholds;
    public static boolean hasPersons;
    public static boolean hasPets;

    public static void getConnection() throws SQLException,ClassNotFoundException{
        Class.forName("org.mariadb.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306","root","");
    }

    public static void checkDbAndTables() throws SQLException {
        checkExistingDB();
        checkExistingTables();
    }

    public static void checkExistingDB() throws SQLException {
        boolean foundDb = false;
        ResultSet resultSet = connection.getMetaData().getCatalogs();
        while (resultSet.next() && !foundDb) {
            // Get the database name, which is at position 1
            String databaseName = resultSet.getString(1);
            if (databaseName.equals("household_management")){
                System.out.println("Datenbank wurde gefunden");
                foundDb =true;
            }
        }
        if (!foundDb){
            createDB();
        }
        // now specify connection to correct database
        connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/household_management","root","");
        resultSet.close();
    }

    public static void createDB() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("create database household_management");
        ps.execute();
        System.out.println("Datenbank household_management wurde erstellt!");
        ps.close();
    }

    public static void checkExistingTables() throws SQLException {
        PreparedStatement ps1= connection.prepareStatement("SELECT count(*) FROM information_schema.tables WHERE table_name = ?");
        ps1.setString(1, "Households");
        ResultSet resultSet = ps1.executeQuery();
        resultSet.next();
        if (resultSet.getInt(1) == 0){
            createHouseholdsTable();
        }
        else{
            System.out.println("Tabelle Households wurde gefunden");
        }
        ps1.setString(1,"Persons");
        resultSet = ps1.executeQuery();
        resultSet.next();
        if (resultSet.getInt(1) == 0){
            createPersonsTable();
        }
        else{
            System.out.println("Tabelle Persons");
        }
        ps1.setString(1,"Pets");
        resultSet = ps1.executeQuery();
        resultSet.next();
        if (resultSet.getInt(1) == 0){
            createPetsTable();
        }
        else{
            System.out.println("Tabelle Pets wurde gefunden");
        }
        ps1.close();
    }

    private static void createPetsTable() throws SQLException {
        System.out.println("Tabelle Pets muss erstellt werden...");
        PreparedStatement ps = connection.prepareStatement("create table pets(pet_id int not null auto_increment, species varChar(32), name varchar(32), owner_id int not null, primary key(pet_id), foreign key(owner_id) references persons(person_id))");
        ps.execute();
        ps.close();
    }

    private static void createPersonsTable() throws SQLException {
        System.out.println("Tabelle Persons muss erstellt werden...");
        PreparedStatement ps = connection.prepareStatement("create table persons(person_id int not null auto_increment, first_name varChar(32), second_name varChar(32), household_id int not null, primary key(person_id), FOREIGN KEY (household_id) REFERENCES households(household_id))");
        ps.execute();
        ps.close();
    }

    private static void createHouseholdsTable() throws SQLException {
        System.out.println("Tabelle Households muss erstellt werden...");
        PreparedStatement ps = connection.prepareStatement("create table households(household_id int not null auto_increment, address varChar(32), primary key(household_id))");
        ps.execute();
        ps.close();
    }

    public static ArrayList<Household> updateHouseholdsFromDB() throws SQLException {
        ArrayList<Household> dbHouseholdsList = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from households");
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            int householdId = rs.getInt(1);
            String address = rs.getString(2);
            //now query all Persons in this household and add them to arraylist:
            ArrayList<Person> personsInHousehold = updatePersonsInHousehold(householdId);
            //create household object and add to arraylist:
            Household household = new Household(householdId,address,personsInHousehold);
            dbHouseholdsList.add(household);
        }
        return dbHouseholdsList;
    }

    public static ArrayList<Person> updatePersonsInHousehold(int householdId) throws SQLException {
        ArrayList<Person> personsInHouseholdList = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from persons where household_id=?");
        ps.setInt(1, householdId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int personId = rs.getInt(1);
            String firstName = rs.getString(2);
            String secondName = rs.getString(3);
            //now query all pets for this person and add them to arraylist:
            ArrayList<Pet> petsList = updatePetsForPerson(personId);
            Person newPerson = new Person(personId,firstName,secondName,petsList,householdId);
            personsInHouseholdList.add(newPerson);
        }
        return personsInHouseholdList;
    }

    public static ArrayList<Pet> updatePetsForPerson(int personId) throws SQLException {
        ArrayList<Pet> petsOfPersonList = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from pets where owner_id=?");
        ps.setInt(1,personId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int petId = rs.getInt(1);
            String species = rs.getString(2);
            String name = rs.getString(3);
            Pet newPet = new Pet(petId,name,species,personId);
            petsOfPersonList.add(newPet);
        }
        return petsOfPersonList;
    }

    public static void addHouseholdToDb(String address) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("insert into households (address) values(?)");
        ps.setString(1,address);
        ps.execute();
        ps.close();
    }

    public static int addPersonToDB(int householdId, String firstName, String secondName) throws SQLException {
        int generatedId = 0;
        PreparedStatement ps = connection.prepareStatement("insert into persons (first_name,second_name,household_id) values(?,?,?)",Statement.RETURN_GENERATED_KEYS);
        ps.setString(1,firstName);
        ps.setString(2,secondName);
        ps.setInt(3,householdId);
        ps.execute();
        System.out.println("Person wurde in DB angelegt!");
        ResultSet rs = ps.getGeneratedKeys();
        ps.close();
        if (rs.next()) {
            generatedId = rs.getInt(1);
        }
        return generatedId;
    }

    public static int addPetToDb(int ownerId, String petName, String species) throws SQLException {
        int petId = 0;
        PreparedStatement ps = connection.prepareStatement("insert into pets (species,name,owner_id) values (?,?,?)", Statement.RETURN_GENERATED_KEYS);
        ps.setString(1,species);
        ps.setString(2,petName);
        ps.setInt(3,ownerId);
        ps.execute();
        System.out.println("Das Tier wurde in der DB angelegt!");
        ResultSet rs = ps.getGeneratedKeys();
        ps.close();
        if(rs.next()){
            petId = rs.getInt(1);
        }
        return petId;
    }

    public static void changeHousehold(int householdId, String userInput) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update households set address=? where household_id=?");
        ps.setString(1,userInput);
        ps.setInt(2,householdId);
        ps.execute();
        ps.close();
    }

    public static void changePerson(int personId, String attributeDesignation, String value) throws SQLException {
        PreparedStatement ps;
        if (attributeDesignation.equals("first_name")) {
            ps = connection.prepareStatement("update persons set first_name=? where person_id=?");
        } else{
            ps = connection.prepareStatement("update persons set second_name=? where person_id=?");
        }
        ps.setInt(2,personId);
        ps.setString(1,value);
        ps.execute();
        ps.close();
    }

    public static void changePet(int petId , String attributeDesignation, String value) throws SQLException{
        PreparedStatement ps;
        if(attributeDesignation.equals("species")){
            ps = connection.prepareStatement("update pets set species=? where pet_id=?");
        } else{
            ps = connection.prepareStatement("update pets set name=? where pet_id=?");
        }
        ps.setInt(2,petId);
        ps.setString(1,value);
        ps.execute();
        ps.close();
    }

    public static void deletePet(int petID) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("delete from pets where pet_id = ?");
        ps.setInt(1,petID);
        ps.execute();
        ps.close();
    }

    public static void deletePerson(int personId) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("delete from persons where person_id = ?");
        ps.setInt(1,personId);
        ps.execute();
        ps.close();
    }

    public static void deleteHousehold(int householdId) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("delete from households where household_id = ?");
        ps.setInt(1,householdId);
        ps.execute();
        ps.close();
    }

    public static void checkForEntries() throws SQLException{
        PreparedStatement ps1 = connection.prepareStatement("select * from households");
        ResultSet rs1 = ps1.executeQuery();
        int i = 0;
        while(rs1.next()){
            i++;
        }
        hasHouseholds = i != 0;
        ps1.close();
        PreparedStatement ps2 = connection.prepareStatement("select * from persons");
        ResultSet rs2 = ps2.executeQuery();
        i = 0;
        while(rs2.next()){
            i++;
        }
        hasPersons = i != 0;
        ps2.close();
        PreparedStatement ps3 = connection.prepareStatement("select * from pets");
        ResultSet rs3 = ps3.executeQuery();
        i = 0;
        while(rs3.next()){
            i++;
        }
        hasPets = i != 0;
    }

}

