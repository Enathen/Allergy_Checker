package creative.endless.growing.allergychecker;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * Helper class for keeping all allergies
 *
 * @author Jonathan Alexander Norberg
 * @version 2017-12-01
 */

public class AllergiesClass implements Comparable<AllergiesClass> {
    private final String language;
    private final String nameOfIngredient;
    private final int id;
    private String motherAllergy;
    private String nameOfWordFound;
    private int distance;
    private int foundAllergies;
    private ArrayList<AllergiesClass> allergiesClasses = new ArrayList<>();

    public AllergiesClass(String language, String nameOfIngredient, int ID, String motherAllergy) {

        this.language = language;
        this.nameOfIngredient = nameOfIngredient;
        id = ID;
        this.motherAllergy = motherAllergy;
        increaseFoundAllergies(this);
    }

    public AllergiesClass(String language, String nameOfIngredient, int ID, String motherAllergy, int distance) {

        this.language = language;
        this.nameOfIngredient = nameOfIngredient;
        id = ID;
        this.motherAllergy = motherAllergy;
        this.distance = distance;
        increaseFoundAllergies(this);
    }

    public int getFoundAllergies() {
        return foundAllergies;
    }

    public void increaseFoundAllergies(AllergiesClass allergiesClass) {
        allergiesClasses.add(allergiesClass);
        foundAllergies++;
    }

    public ArrayList<AllergiesClass> getAllergiesClasses() {
        return allergiesClasses;
    }

    public int getDistance() {
        return distance;
    }

    public String getNameOfWordFound() {
        return nameOfWordFound;
    }

    public void setNameOfWordFound(String nameOfWordFound) {
        this.nameOfWordFound = nameOfWordFound;
    }

    public String getLanguage() {
        return language;
    }

    public String getNameOfIngredient() {
        return nameOfIngredient;
    }

    public int getId() {
        return id;
    }

    public String getMotherAllergy() {
        return motherAllergy;
    }


    @Override
    public int compareTo(@NonNull AllergiesClass o) {
        return motherAllergy.compareToIgnoreCase(o.motherAllergy);
    }
}
