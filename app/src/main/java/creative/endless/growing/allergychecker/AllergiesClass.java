package creative.endless.growing.allergychecker;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * Helper class for keeping all allergies
 *
 * @author Jonathan Alexander Norberg
 * @version 2017-12-01
 */

public class AllergiesClass  {
    public String motherAllergy;
    public Integer amount = 1;

    public AllergiesClass(String motherAllergy) {

        this.motherAllergy = motherAllergy;
    }
    public AllergiesClass(String motherAllergy,Integer amount) {

        this.motherAllergy = motherAllergy;
        this.amount = amount;
    }

}
