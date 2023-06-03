package arthur.inzhilov.movierate.utility;


import java.util.*;
import java.util.Map.Entry;


/**
 * Slope One algorithm implementation
 */
public class SlopeOne {

    private static final Map<Item, Map<Item, Double>> diff = new HashMap<>();
    private static final Map<Item, Map<Item, Integer>> freq = new HashMap<>();

    private static final Map<User, HashMap<Item, Double>> outputData = new HashMap<>();

    private static Set<Item> items = new HashSet<>();

    public static Map<User, HashMap<Item, Double>> slopeOne(Map<User, HashMap<Item, Double>> inputData, Set<Item> newItems) {
        items = newItems;
        buildDifferencesMatrix(inputData);
        predict(inputData);
        return outputData;
    }

    /**
     * Based on the available data, calculate the relationships between the
     * films and number of occurrences
     *
     * @param data existing user data and their items' ratings
     */
    private static void buildDifferencesMatrix(Map<User, HashMap<Item, Double>> data) {
        for (HashMap<Item, Double> user : data.values()) {
            for (Entry<Item, Double> e : user.entrySet()) {
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<>());
                    freq.put(e.getKey(), new HashMap<>());
                }
                for (Entry<Item, Double> e2 : user.entrySet()) {
                    int oldCount = 0;
                    if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freq.get(e.getKey()).get(e2.getKey());
                    }
                    double oldDiff = 0.0;
                    if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diff.get(e.getKey()).get(e2.getKey());
                    }
                    double observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        for (Item j : diff.keySet()) {
            for (Item i : diff.get(j).keySet()) {
                double oldValue = diff.get(j).get(i);
                int count = freq.get(j).get(i);
                diff.get(j).put(i, oldValue / count);
            }
        }
    }

    /**
     * Based on existing data predict all missing ratings. If prediction is not
     * possible, the value will be equal to -1
     *
     * @param data existing user data and their items' ratings
     */
    public static void predict(Map<User, HashMap<Item, Double>> data) {
        HashMap<Item, Double> uPred = new HashMap<>();
        HashMap<Item, Integer> uFreq = new HashMap<>();
        for (Item j : diff.keySet()) {
            uFreq.put(j, 0);
            uPred.put(j, 0.0);
        }
        for (Entry<User, HashMap<Item, Double>> e : data.entrySet()) {
            for (Item j : e.getValue().keySet()) {
                for (Item k : diff.keySet()) {
                    try {
                        double predictedValue = diff.get(k).get(j) + e.getValue().get(j);
                        double finalValue = predictedValue * freq.get(k).get(j);
                        uPred.put(k, uPred.get(k) + finalValue);
                        uFreq.put(k, uFreq.get(k) + freq.get(k).get(j));
                    } catch (NullPointerException e1) {
                    }
                }
            }
            HashMap<Item, Double> clean = new HashMap<>();
            for (Item j : uPred.keySet()) {
                if (uFreq.get(j) > 0) {
                    clean.put(j, uPred.get(j) / uFreq.get(j));
                }
            }
            for (Item j : items) {
                if (e.getValue().containsKey(j)) {
                    clean.put(j, e.getValue().get(j));
                } else if (!clean.containsKey(j)) {
                    clean.put(j, -1.0);
                }
            }
            outputData.put(e.getKey(), clean);
        }
    }
}
