package creative.endless.growing.allergychecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class AnalyzeText {
    private final ScannerFragment scannerFragment;
    private boolean allergiesSelected = true;
    private ArrayList<AllergyList.E_Numbers> allFoundENumbers = new ArrayList<>();
    private HashMap<String, Integer> allFoundAllergies = new HashMap<>();
    private TreeSet<String> allStringsOrdered = new TreeSet<>();
    private AlgorithmAllergies algorithmAllergies;
    private HashSet<String> personalAllergies;
    private TreeMap<Integer, TreeSet<String>> treeMapSplittedFullText;
    private long start;

    public AnalyzeText(ScannerFragment scannerFragment) {
        this.scannerFragment = scannerFragment;
        algorithmAllergies = new AlgorithmAllergies();
        personalAllergies = CollectAllAllergies.getAllergies(scannerFragment.getContext());

    }

    private void setup(String[] fullTextArray) {

        treeMapSplittedFullText = algorithmAllergies.FixStringAllStrings(fullTextArray);

        algorithmAllergies.bkTree(treeMapSplittedFullText, personalAllergies, allFoundAllergies);

        ArrayList<AllergyList.E_Numbers> eNumbersArrayList = new AllergyList(scannerFragment.getContext()).getArrayListE_Numbers();

        for (int j = 0; j < fullTextArray.length; j++) {
            if (j + 1 < fullTextArray.length && fullTextArray.length != 1) {
                String number = fullTextArray[j] + fullTextArray[j + 1].replaceAll("\\D+", "");
                if (number.length() > 2 && fullTextArray[j].compareToIgnoreCase("e") == 0) {

                    algorithmAllergies.checkFullStringEnumbers(fullTextArray[j] + fullTextArray[j + 1], eNumbersArrayList, allFoundENumbers);
                }
            }
            String number = fullTextArray[j].replaceAll("\\D+", "");
            if (number.length() > 2) {
                algorithmAllergies.checkFullStringEnumbers(fullTextArray[j], eNumbersArrayList, allFoundENumbers);
            }
        }
        for (String s : algorithmAllergies.FixStringAllStringsToCheckLast(fullTextArray)) {
            algorithmAllergies.checkFullString(s, personalAllergies, allFoundAllergies);
        }

        Collections.sort(allFoundENumbers);
        for (TreeSet<String> stringTreeSet : treeMapSplittedFullText.values()) {
            allStringsOrdered.addAll(stringTreeSet);
        }
        allFoundAllergies = (HashMap<String, Integer>) sortByComparator(allFoundAllergies, false);
        if (!allFoundAllergies.isEmpty() || !allFoundENumbers.isEmpty()) {
            scannerFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scannerFragment.insertData(allFoundAllergies, allFoundENumbers);
            }
        });
        }
    }

    public void analyzeString(final String string) {
        start = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] fullTextArray = string.split("\\s+");
                setup(fullTextArray);
            }
        }).start();

    }

    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order) {

        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public void clear() {
        allFoundAllergies = new HashMap<>();
        allFoundENumbers = new ArrayList<>();
    }

}

