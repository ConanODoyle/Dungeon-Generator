import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by Samuel on 9/7/2016.
 */
public class DungeonMapTest {
    @Test
    public void printDungeon() throws Exception {
        DungeonMap map = new DungeonMap();
        Random rand = new Random();
        int x; int y; int z;
        for (int i = 0; i < 0; i++) {
            x = rand.nextInt(10);
            y = rand.nextInt(10);
            z = rand.nextInt(5);
            DungeonRoom r1 = new BasicRoom(new Triple(x, y, z), 8 + rand.nextInt(20), map);
            r1.generateRoom();
            map.rooms.add(r1);
        }
        map.pickExits();
        Triple start = new Triple(0, 0, 1);
        map.tileLoc.put(start, new Tile(start));
        map.tiles.add(map.tileLoc.get(start));
        map.tileLoc.get(start).character = 'S';

        Triple end = new Triple(2, 5, 2);
        map.tileLoc.put(end, new Tile(end));
        map.tiles.add(map.tileLoc.get(end));
        map.tileLoc.get(end).character = 'E';

        System.out.println("\n");
        map.generatePath(start, end, 15, false, true);
        map.printDungeon();
    }

}