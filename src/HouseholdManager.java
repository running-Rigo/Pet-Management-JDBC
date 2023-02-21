import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;


public class HouseholdManager {
    public static Scanner sn = Main.giveScanner();
    public static ArrayList <Household> householdsList = new ArrayList<>();

    public static void updateHouseholdsFromDB() throws SQLException{
        householdsList = DatabaseHandler.updateHouseholdsFromDB();
    }

    public static void getAllHouseholds() throws SQLException {
        //Update data from DB;
        householdsList = DatabaseHandler.updateHouseholdsFromDB();
        for( int i = 0; i < householdsList.size(); i++){
            Household household = householdsList.get(i);
            System.out.println(household);
        }
    }


    public static void createNewHousehold() throws SQLException {
        System.out.println("Gib eine Adresse für den Haushalt ein:");
        String address = sn.nextLine();
        DatabaseHandler.addHouseholdToDb(address);
        householdsList = DatabaseHandler.updateHouseholdsFromDB();
        System.out.println("Neuer Haushalt wurde angelegt.");
    }

    public static void createNewPerson() throws SQLException {
        System.out.println();
        System.out.println("Zunächst musst du einen Haushalt für die neue Person auswählen:");
        //in the method chooseHouseholdById() the user can choose a household-object by its id; this household-object is returned and saved as chosenHousehold;
        Household chosenHousehold = chooseHouseholdById();
        chosenHousehold.createPerson();
        }

    public static void createNewPet() throws SQLException {
        System.out.println();
        System.out.println("Zunächst musst du einen Besitzer für das Haustier auswählen:");
        //in the method choosePersonById() the user can choose a person-object by its id; this person-object is returned and saved as chosenPerson;
        Person chosenPerson = choosePersonById();
        chosenPerson.createPet();
    }

    public static void readHousehold() throws SQLException {
        Household chosenHousehold = chooseHouseholdById();
        System.out.println("Haushalts-ID: " + chosenHousehold.getHouseholdId());
        System.out.println("Adresse: " + chosenHousehold.address);
        System.out.println("Im Haushalt lebende Personen:");
        for(int i = 0; i < chosenHousehold.personsInHouseholdList.size(); i++){
            System.out.println(chosenHousehold.personsInHouseholdList.get(i));
        }
    }

    public static void readPerson() throws SQLException{
        Person chosenPerson = choosePersonById();
        System.out.println("Personen-ID: " + chosenPerson.getPersonId());
        System.out.println("Vorname: " + chosenPerson.firstName);
        System.out.println("Nachname: " + chosenPerson.secondName);
        System.out.println("Zugehörige Haustiere:");
        for( int i = 0; i < chosenPerson.petsList.size(); i++){
            System.out.println(chosenPerson.petsList.get(i));
        }
    }

    public static void readPet() throws SQLException{
        Pet chosenPet = choosePetById();
        System.out.println("Pet-ID: " + chosenPet.getPetId());
        System.out.println("Name: " + chosenPet.name);
        System.out.println("Tierart: " + chosenPet.species);
    }

    public static Household chooseHouseholdById() throws SQLException {
        boolean validInput = false;
        int householdChoice = 0;
        int householdIndex = 0;

        getAllHouseholds();
        do {
            do {
                System.out.println("Wähle einen Haushalt (Eingabe der ID)");
                String choiceString = sn.nextLine();
                try {
                    householdChoice = Integer.parseInt(choiceString);
                    validInput = true;
                } catch (NumberFormatException ex) {
                    System.out.println("Ungültige Eingabe!");
                }
            } while (!validInput);
        }while(getHousehold(householdChoice) ==  null);
        //The household with the chosen ID must be selected from the households-arraylist:
        return getHousehold(householdChoice);

        //return householdsList.get(householdIndex);
    }

    public static Household getHousehold(int id){
        for(int i = 0; i<householdsList.size(); i++){
            Household household = householdsList.get(i);
            if(household.getHouseholdId() == id)
                return household;
        }
        return null;
    }

    public static Person choosePersonById() throws SQLException {
        boolean validInput = false;
        int chosenPersonId = 0;
        int personIndex = 0;
        int householdIndex = 0;
        Person chosenPerson = null;
        for (int i = 0; i < householdsList.size(); i++) {
            Household household = householdsList.get(i);
            household.printPersons();
        }
        //user has to choose an existing person-id for his input;
        do {
            //user has to choose an integer for his/her input;
            do {
                System.out.println("Bitte wähle eine Person aus (Eingabe der Person_ID)");
                String choiceString = sn.nextLine();
                try {
                    chosenPersonId = Integer.parseInt(choiceString);
                    validInput = true;
                } catch (NumberFormatException ex) {
                    System.out.println("Ungültige Eingabe.");
                }
            } while (!validInput);
            //The person with the chosen ID must be selected from the households-persons-arraylist:
            for (int i = 0; i < householdsList.size(); i++) {
                Household household = householdsList.get(i);
                for (int j = 0; j < household.personsInHouseholdList.size(); j++) {
                    Person person = household.personsInHouseholdList.get(j);
                    if (person.getPersonId() == chosenPersonId) {
                        personIndex = j;
                        householdIndex = i;
                        //for-loops are stopped when person is found;
                        j = household.personsInHouseholdList.size();
                        i = householdsList.size();
                        //the chosenPerson-object is re-assigned:
                        Household chosenHousehold = householdsList.get(householdIndex);
                        chosenPerson = chosenHousehold.personsInHouseholdList.get(personIndex);
                    }
                    //if chosen Person id doesn't exist:
                    else{
                        chosenPerson = null;
                    }
                }
            }
        }while(chosenPerson == null);
        return chosenPerson;
    }

    public static Person getPersonById(int chosenPersonId) throws SQLException {
        Person chosenPerson = null;
        int personIndex = 0;
        int householdIndex = 0;
        for (int i = 0; i < householdsList.size(); i++) {
            Household household = householdsList.get(i);
            for (int j = 0; j < household.personsInHouseholdList.size(); j++) {
                Person person = household.personsInHouseholdList.get(j);
                if (person.getPersonId() == chosenPersonId) {
                    personIndex = j;
                    householdIndex = i;
                    //for-loops are stopped when person is found;
                    j = household.personsInHouseholdList.size();
                    i = householdsList.size();
                    //the chosenPerson-object is re-assigned:
                    Household chosenHousehold = householdsList.get(householdIndex);
                    chosenPerson = chosenHousehold.personsInHouseholdList.get(personIndex);
                }
            }
        }
        return chosenPerson;
    }

    public static Pet choosePetById() throws SQLException {
        boolean validInput = false;
        int chosenPetId = 0;
        int petIndex;
        int personIndex ;
        int householdIndex;
        Pet chosenPet = null;

        //Printing all existing Pets:
        for (int i = 0; i < householdsList.size(); i++) {
            Household household = householdsList.get(i);
            for (int j = 0; j < household.personsInHouseholdList.size(); j++) {
                Person person = household.personsInHouseholdList.get(j);
                person.printPets();
            }
        }
        do{
            do {
                System.out.println("Bitte wähle ein Haustier aus (Eingabe der Pet_ID)");
                String choiceString = sn.nextLine();
                try {
                    chosenPetId = Integer.parseInt(choiceString);
                    validInput = true;
                } catch (NumberFormatException ex) {
                    System.out.println("Ungültige Eingabe.");
                }
            } while (!validInput);
            //The pet with the chosen ID must be selected from the households-persons-pets-arraylist:
            for (int i = 0; i < householdsList.size(); i++) {
                Household household = householdsList.get(i);
                for (int j = 0; j < household.personsInHouseholdList.size(); j++) {
                    Person person = household.personsInHouseholdList.get(j);
                    if (person.petsList.size() > 0) { //searching for the chosen pet_id in all person-objects that have a petslist with entries
                        for (int z = 0; z < person.petsList.size(); z++) {
                            if (person.petsList.get(z).getPetId() == chosenPetId) {
                                petIndex = z;
                                personIndex = j;
                                householdIndex = i;
                                j = household.personsInHouseholdList.size();
                                i = householdsList.size();
                                z = person.petsList.size();

                                Household chosenHousehold = householdsList.get(householdIndex);
                                Person chosenPerson = chosenHousehold.personsInHouseholdList.get(personIndex);
                                chosenPet = chosenPerson.petsList.get(petIndex);
                            }
                            else chosenPet = null;
                        }
                    }
                }
            }

        }while(chosenPet == null);
        return chosenPet;
    }

    public static boolean updateHousehold (int householdId, String attribute) throws SQLException {
        if(!attribute.equals("address")) {
            System.out.println("Du musst ein gültiges Attribut von Haushalt eingeben (Addresse)!");
            return false;
        }
        Household chosenHousehold = getHousehold(householdId);
        if(chosenHousehold == null){
            System.out.println("Es existiert kein Haushalt mit dieser ID.");
            return false;
        }
        System.out.println("Gib nun den neuen Wert ein:");
        String inputString = sn.nextLine();
        DatabaseHandler.changeHousehold(chosenHousehold.getHouseholdId(),inputString);
        chosenHousehold.address = inputString;
        System.out.println("Haushalt erfolgreich geändert!");
        return true;
    }

    public static void deleteHousehold(int householdId) throws SQLException{
       //if there are no persons for this household, the household is just deleted
        if(getHousehold(householdId).personsInHouseholdList.size() == 0){
            DatabaseHandler.deleteHousehold(householdId);
            for (int i = 0; i < householdsList.size(); i++){
                if(householdsList.get(i).getHouseholdId() == householdId){
                    householdsList.remove(i);
                    System.out.println("Der Haushalt wurde aus der Liste entfernt.");
                    i = householdsList.size();
                }
            }
        }else { //if there are persons for the chosen household
            System.out.println("Zu diesem Haushalt gehören noch " + getHousehold(householdId).personsInHouseholdList.size() + " Personen. Möchtest du den Haushalt mitsamt ihrer Personen löschen? (ja/nein");
            String userChoiceString = sn.nextLine();
            if (!userChoiceString.equals("ja")) {
                return;
            }
            //DB Handler deletes Persons from personsInHouseholdList of the household
            for (int i = 0; i < getHousehold(householdId).personsInHouseholdList.size(); i++) {
                getHousehold(householdId).deletePerson(getHousehold(householdId).personsInHouseholdList.get(i).getPersonId());
            }
            //eventually the person has pets and you don't want to delete a person so in the end you can't delete the household
            if(getHousehold(householdId).personsInHouseholdList.size() != 0){
                System.out.println("Da du zum Haushalt gehörende Personen/Haustiere nicht löschen wolltest, konnte der Haushalt nicht gelöscht werden.");
                return;
            }else{
                //if all persons were deleted, finally the houshold can be deleted
                DatabaseHandler.deleteHousehold(householdId);
                for(int i = 0; i < householdsList.size(); i++){
                    if(householdsList.get(i).getHouseholdId() == householdId){
                        householdsList.remove(i);
                        System.out.println("Der Haushalt wurde aus der Liste entfernt");
                        i = householdsList.size();
                    }
                }
            }
        }

    }


}








