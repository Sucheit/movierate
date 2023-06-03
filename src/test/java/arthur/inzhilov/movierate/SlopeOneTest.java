package arthur.inzhilov.movierate;

import arthur.inzhilov.movierate.utility.Item;
import arthur.inzhilov.movierate.utility.SlopeOne;
import arthur.inzhilov.movierate.utility.User;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SlopeOneTest {

    @Test
    public void slopeOneTest() {
        Map<User, HashMap<Item, Double>> inputData = new HashMap<>();
        User user1 = new User(1L);
        User user2 = new User(2L);
        User user3 = new User(3L);
        Item item1 = new Item(1L);
        Item item2 = new Item(2L);
        Item item3 = new Item(3L);
        HashMap<Item, Double> itemsUser1 = new HashMap<>();
        itemsUser1.put(item1, 5D);
        itemsUser1.put(item2, 3D);
        itemsUser1.put(item3, 2D);
        HashMap<Item, Double> itemsUser2 = new HashMap<>();
        itemsUser2.put(item1, 3D);
        itemsUser2.put(item2, 4D);
        itemsUser2.put(item3, 0D);
        HashMap<Item, Double> itemsUser3 = new HashMap<>();
        itemsUser3.put(item2, 2D);
        itemsUser3.put(item3, 5D);
        inputData.put(user1, itemsUser1);
        inputData.put(user2, itemsUser2);
        inputData.put(user3, itemsUser3);
        Set<Item> items = Set.of(item1, item2, item3);
        Map<User, HashMap<Item, Double>> outputData = SlopeOne.slopeOne(inputData, items);
        double epsilon = 0.1d;
        double expected = 4.33d;
        Double result = outputData.get(user3).get(item1);
        assertEquals(expected, result, epsilon);
    }
}
