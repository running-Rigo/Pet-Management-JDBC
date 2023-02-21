import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static Scanner sn = new Scanner(System.in);

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        boolean hasLeftProgram = false;
        DatabaseHandler.getConnection();
        DatabaseHandler.checkDbAndTables();
        HouseholdManager.updateHouseholdsFromDB();
        DatabaseHandler.checkForEntries();
        System.out.println("-".repeat(50));
        System.out.println("Willkommen in der Haushalte-Verwaltung! Was möchtest du tun?");
        do{
            showUserOptions();
            int userMenuChoice = getUserChoice();

            switch (userMenuChoice) {
                case 6:
                    hasLeftProgram = true;
                    break;
                case 1:
                    HouseholdManager.getAllHouseholds();
                    break;
                case 2:
                    deleteSomething();
                    break;
                case 3:
                    readSomething();
                    break;
                case 4:
                    updateSomething();
                    break;
                case 5:
                    createSomething();
            }
        }while(!hasLeftProgram);
        DatabaseHandler.connection.close();
    }
    public static void showUserOptions(){
        System.out.println();
        System.out.println("1. Alle bestehenden Haushalte anzeigen");
        System.out.println("2. Einen Datensatz (Haushalt/Person/Haustier) löschen");
        System.out.println("3. Einen Datensatz lesen (Haushalt/Person/Haustier)");
        System.out.println("4. Einen Datensatz ändern (Haushalt/Person/Haustier)");
        System.out.println("5. Einen Datensatz hinzufügen (Haushalt/Person/Haustier)");
        System.out.println("6. Das Programm beenden");
    }

    public static int getUserChoice(){
        int userInput = 0;
        boolean validInput = false;
        do{
            String userInputString = sn.nextLine();
            try{
                userInput = Integer.parseInt(userInputString);
                if(userInput == 1 || userInput == 2 || userInput == 3 || userInput == 4 || userInput == 5 || userInput == 6){
                    validInput = true;
                }
            }
            catch (NumberFormatException ex){
                System.out.println("Ungültige Eingabe!");
            }
        }while(!validInput);
        return userInput;
    }

    public static void deleteSomething() throws SQLException {
        int chosenDataSort = chooseDataSort();
        // 1 = a household will be deleted;
        if(chosenDataSort==1 && DatabaseHandler.hasHouseholds){
            boolean validInput = false;
            System.out.println();
            System.out.println("Gib die ID des Haushalts an, den du löschen möchtest:");
            Household chosenHousehold = HouseholdManager.chooseHouseholdById();
            int householdID = chosenHousehold.getHouseholdId();
            HouseholdManager.deleteHousehold(householdID);
        }
        // 2 = a person will be deleted;
        if(chosenDataSort == 2 && DatabaseHandler.hasPersons){
            Person chosenPerson = HouseholdManager.choosePersonById();
            int householdID = chosenPerson.householdID;
            int personID = chosenPerson.getPersonId();
            HouseholdManager.getHousehold(householdID).deletePerson(personID);
        }

        // 3 = a pet will be deleted;
        if(chosenDataSort == 3 && DatabaseHandler.hasPets){
            Pet chosenPet = HouseholdManager.choosePetById();
            int petId = chosenPet.getPetId();
            int ownerID = chosenPet.ownerId;
            int householdId = HouseholdManager.getPersonById(ownerID).householdID;
            HouseholdManager.getHousehold(householdId).getPerson(ownerID).deletePet(petId);
        }
        else{
            System.out.println("Keine Daten hierzu gefunden.");
        }
        DatabaseHandler.checkForEntries();
    }

    public static void readSomething() throws SQLException{
        int chosenDataSort = chooseDataSort();
        // 1 = a household will be read;
        if(chosenDataSort == 1 && DatabaseHandler.hasHouseholds){
            HouseholdManager.readHousehold();
        }
        else if (chosenDataSort == 2 && DatabaseHandler.hasPersons){
            HouseholdManager.readPerson();
        }
        else if (chosenDataSort == 3 && DatabaseHandler.hasPets){
            HouseholdManager.readPet();
        }
        else{
            System.out.println("Keine Daten hierzu gefunden.");
        }
    }

    public static void updateSomething() throws SQLException{
        int chosenDataSort = chooseDataSort();
        boolean validAttribute = false;
        // 1 = a household will be changed (eg. address)
        if(chosenDataSort == 1 && DatabaseHandler.hasHouseholds){
            Household household = HouseholdManager.chooseHouseholdById();
            System.out.println(household);
            System.out.println("Welches Attribut des Haushalts möchtest du ändern? (address)");
            do{
                String chosenAttribute = sn.nextLine();
                validAttribute = HouseholdManager.updateHousehold(household.getHouseholdId(), chosenAttribute);
            }while(!validAttribute);
        }
        // 2 = a person will be changed (eg firstName or secondName)
        if(chosenDataSort == 2 && DatabaseHandler.hasPersons){
            Person person = HouseholdManager.choosePersonById();
            System.out.println("Welches Attribut der Person möchtest du ändern? (firstName oder secondName)");
            do{
                String chosenAttribute = sn.nextLine();
                validAttribute = HouseholdManager.getHousehold(person.householdID).updatePerson(person.getPersonId(),chosenAttribute);
            }while(!validAttribute);
        }
        // 3 = a pet will be changed (eg name or species)
        if(chosenDataSort == 3 && DatabaseHandler.hasPets){
            Pet pet = HouseholdManager.choosePetById();
            int ownerId = pet.ownerId;
            Person owner = HouseholdManager.getPersonById(ownerId);
            System.out.println("Welches Attribut des Tieres möchtest du ändern? (species oder name)");
            do{
                String chosenAttribute = sn.nextLine();
                validAttribute = HouseholdManager.getHousehold(owner.householdID).getPerson(ownerId).updatePet(pet.getPetId(),chosenAttribute);
            }while(!validAttribute);
        }
        else{
            System.out.println("Keine Daten hierzu gefunden.");
        }
    }

    public static void createSomething() throws SQLException {
        int chosenDataSort = chooseDataSort();
        // 1 = a household will be created;
        if(chosenDataSort == 1){
            HouseholdManager.createNewHousehold();
        }
        if(chosenDataSort == 2){
            if(DatabaseHandler.hasHouseholds){
                HouseholdManager.createNewPerson();
            }
            else{
                System.out.println("Du musst zuerst einen Haushalt anlegen.");
            }
        }
        if(chosenDataSort == 3){
            if(DatabaseHandler.hasPersons){
                HouseholdManager.createNewPet();
            }
            else{
                System.out.println("Du musst zuerst eine Person (Besitzer) anlegen.");
            }
        }
        DatabaseHandler.checkForEntries();
    }

    public static int chooseDataSort(){
        System.out.println("Worauf bezieht sich deine Operation?");
        System.out.println("1. Haushalt");
        System.out.println("2. Person");
        System.out.println("3. Haustier");
        int userInput = 0;
        boolean validInput = false;
        do{
            String userInputString = sn.nextLine();
            try{
                userInput = Integer.parseInt(userInputString);
                if(userInput == 1 || userInput == 2 || userInput == 3){
                    validInput = true;
                }
            }
            catch (NumberFormatException ex){
                System.out.println("Ungültige Eingabe!");
            }
        }while(!validInput);
        return userInput;
    }

    public static Scanner giveScanner(){
       return sn;
    }
}