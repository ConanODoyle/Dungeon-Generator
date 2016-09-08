import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by Samuel on 9/5/2016.
 */
public class TripleTest {
    @Test
    public void hashCodeTest() throws Exception {
        Triple a = new Triple(1, 2, 3);
        Triple b = new Triple(2, 1, 3);
        HashMap<Triple, Integer> map = new HashMap<>();

        map.put(a, 100);
        map.put(b, 0);
        assertTrue(map.get(a).equals(100));
        assertTrue(map.get(b).equals(0));
        System.out.println(map.containsKey(a));
        System.out.println(map.containsKey(b));
        Triple c = new Triple(3, 2, 1);
        System.out.println(map.containsKey(c));
        System.out.println(map.get(c));
    }

}