import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Person {
    private final int personId;
    public String firstName;
    public String secondName;
    public int householdID;

    public Scanner sn= Main.giveScanner();
    public ArrayList<Pet> petsList;

    public Person(int personId, String firstName, String secondName, ArrayList<Pet> petsList,int householdID) {
        this.personId = personId;
        this.firstName = firstName;
        this.secondName = secondName;
        this.petsList = petsList;
        this.householdID = householdID;
    }

    public Person(int personId, String firstName, String secondName, int householdID) {
        this.personId = personId;
        this.firstName = firstName;
        this.secondName = secondName;
        this.householdID = householdID;
        this.petsList = new ArrayList<>();
    }

    public Pet getPet(int id){
        for(int i = 0; i<this.petsList.size(); i++){
            Pet pet = this.petsList.get(i);
            if(pet.getPetId() == id)
                return pet;
        }
        return null;
    }

    public void createPet() throws SQLException{
        System.out.println("Wie soll das Haustier heißen?");
        String petName = sn.nextLine();
        System.out.println("Zu welcher Tierart gehört das Tier?");
        String species = sn.nextLine();
        int petId = DatabaseHandler.addPetToDb(personId,petName,species);
        Pet newPet = new Pet(petId,petName,species,personId);
        petsList.add(newPet);
    }

    public void printPets(){
        for (Pet pet : petsList) {
            System.out.println(pet);
        }
    }

    public boolean updatePet(int petId, String attributeDesignation) throws SQLException {
          if(!(attributeDesignation.equals("species") || attributeDesignation.equals("name"))){
            System.out.println("Du musst ein gültiges Attribut von Pet eingeben (species oder name)!");
            return false;
        }
        Pet chosenPet = getPet(petId);
        if(chosenPet == null){
            System.out.println("Zur gewählten Person existiert kein Haustier mit dieser ID.");
            return false;
        }
        System.out.println("Gib nun den neuen Wert ein:");
        String inputString = sn.nextLine();
        DatabaseHandler.changePet(chosenPet.getPetId(),attributeDesignation,inputString);
        if(attributeDesignation.equals("species")){
            chosenPet.species = inputString;
        } else{
            chosenPet.name = inputString;
        }
        System.out.println("Das Tier wurde erfolgreich geändert!");
        return true;
    }

    public void deletePet(int petID) throws SQLException {
        DatabaseHandler.deletePet(petID);
        for (int i = 0; i < petsList.size(); i++){
            if(petsList.get(i).getPetId() == petID){
                petsList.remove(i);
                System.out.println("Tier wurde aus der petslist gelöscht.");
                i = petsList.size();
            }
        }
    }

    public int getPersonId() {
        return personId;
    }
    @Override
    public String toString() {
        return "Person{" +
                "personId=" + personId +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", householdID=" + householdID +
                ", petsList=" + petsList +
                '}';
    }
}
