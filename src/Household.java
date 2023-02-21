import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Household {
    public Scanner sn = Main.giveScanner();
    private final int householdId;
    public String address;
    public ArrayList<Person> personsInHouseholdList = new ArrayList<>();

    public Household(int householdId, String address, ArrayList<Person> personsInHouseholdList) {
        this.householdId = householdId;
        this.address = address;
        this.personsInHouseholdList = personsInHouseholdList;
    }

    public int getHouseholdId() {
        return householdId;
    }

    public void printPersons(){
        for(int i = 0; i<personsInHouseholdList.size(); i++){
            Person person = personsInHouseholdList.get(i);
            System.out.println(person);
            //person.printPets();
        }
    }

    public Person getPerson(int id){
        for(int i = 0; i<this.personsInHouseholdList.size(); i++){
            Person person = this.personsInHouseholdList.get(i);
            if(person.getPersonId() == id)
                return person;
        }
        return null;
    }

    public boolean updatePerson(int personId, String attributeDesignation) throws SQLException{
        if(!(attributeDesignation.equals("firstName") || attributeDesignation.equals("secondName"))){
            System.out.println("Du musst ein gültiges Attribut von Person eingeben (first_name oder second_name)!");
            return false;
        }
        Person chosenPerson = getPerson(personId);
        if(chosenPerson == null){
            System.out.println("Im gewählten Hauswahlt existiert keine Person mit dieser ID.");
            return false;
        }
        System.out.println("Gib nun den neuen Wert ein:");
        String inputString = sn.nextLine();
        DatabaseHandler.changePerson(chosenPerson.getPersonId(),attributeDesignation,inputString);
        if(attributeDesignation.equals("firstName")){
            chosenPerson.firstName = inputString;
        } else{
            chosenPerson.secondName = inputString;
        }
        System.out.println("Die Person wurde erfolgreich geändert!");
        return true;
    }

    public void createPerson() throws SQLException {
        System.out.println("Gib den Vornamen der Person ein:");
        String firstName = sn.nextLine();
        System.out.println("Gib den Nachnamen der Person ein:");
        String secondName = sn.nextLine();
        int personId = DatabaseHandler.addPersonToDB(householdId,firstName,secondName);
        Person newPerson = new Person(personId,firstName,secondName,householdId);
        personsInHouseholdList.add(newPerson);
    }

    public void deletePerson( int personID ) throws SQLException{
        //if there are no pets for this person, the person is just deleted
        if(getPerson(personID).petsList.size() == 0){
            DatabaseHandler.deletePerson(personID);
            for(int i = 0; i < personsInHouseholdList.size(); i++){
                if(personsInHouseholdList.get(i).getPersonId() == personID){
                    personsInHouseholdList.remove(i);
                    System.out.println("Die Person wurde aus der Personenliste des Haushalts gelöscht.");
                    i = personsInHouseholdList.size();
                }
            }
        } else{ //if there are pets for the chosen person, they have to be deleted first
            System.out.println("Zu dieser Person gehören noch "+ getPerson(personID).petsList.size() +" Haustiere. Möchtest du die Person mitsamt ihrer Haustiere löschen? (Eingabe ja/nein)");
            String userChoiceString = sn.nextLine();
            if(!userChoiceString.equals("ja")){
                return;
            }
            //DB-Handler deletes each pet from the petslist of the person
            for(int i = 0; i < getPerson(personID).petsList.size(); i++){
                DatabaseHandler.deletePet(getPerson(personID).petsList.get(i).getPetId());
            }
            getPerson(personID).petsList.clear();
            System.out.println("Alle zugehörigen Haustiere wurden gelöscht.");
            DatabaseHandler.deletePerson(personID);
            for(int i = 0; i < personsInHouseholdList.size(); i++){
                if(personsInHouseholdList.get(i).getPersonId() == personID){
                    personsInHouseholdList.remove(i);
                    System.out.println("Die Person wurde aus der Personenliste des Haushalts gelöscht.");
                    i = personsInHouseholdList.size();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Household{" +
                "householdId=" + householdId +
                ", address='" + address + '\'' +
                ", personsInHouseholdList=" + personsInHouseholdList +
                '}';
    }
}
