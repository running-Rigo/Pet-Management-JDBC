public class Pet {
    private int petId;
    public String name;
    public String species;
    public int ownerId;

    public Pet(int petId, String name, String species, int ownerId) {
        this.petId = petId;
        this.name = name;
        this.species = species;
        this.ownerId = ownerId;
    }

    public int getPetId() {
        return petId;
    }


    @Override
    public String toString() {
        return "Pet{" +
                "petId=" + petId +
                ", name='" + name + '\'' +
                ", species='" + species + '\'' +
                ", ownerId=" + ownerId +
                '}';
    }


}
