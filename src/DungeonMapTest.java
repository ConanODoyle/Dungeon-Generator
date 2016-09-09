import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Samuel on 9/7/2016.
 */
public class DungeonMapTest {
    @Test
    public void printDungeon() throws Exception {
        DungeonMap map = new DungeonMap();
        Random rand = new Random();
        int x; int y; int z;
        for (int i = 0; i < 6; i++) {
            x = rand.nextInt(20);
            y = rand.nextInt(20);
            z = rand.nextInt(7) + 1;
            DungeonRoom r1 = new BasicRoom(new Triple(x, y, z), 4 + rand.nextInt(20), map);
            r1.generateRoom();
            map.rooms.add(r1);
        }
        map.pickExits();
        //serves as relative position tile so its easier to figure out what is where.
        map.tileLoc.put(Triple.ORIGIN, new Tile(Triple.ORIGIN, map));
        map.tiles.add(map.tileLoc.get(Triple.ORIGIN));
        map.tileLoc.get(Triple.ORIGIN).character = 'o';

        map.printDungeon();

        //generate paths between exits??
        System.out.println("\n");
        Triple start;
        Triple end;
        int[] options = {0, 1, 2, 3, 4, 5};
        ArrayList<Integer> roomOptions;
        DungeonRoom r;
        for (int i = 0; i < 6; i++) {
            roomOptions = new ArrayList(Arrays.asList(options));
            roomOptions.remove(new Integer(i));
            r = map.rooms.get(i);

            for (int j = 0; j < r.exits.size(); j++) {
                System.out.println("Room " + i + " exit " + j);
                Tile startTile = r.exits.get(j);
                Tile endTile = map.getRandomExit(i);
                int maxDist = 20;
                while (startTile.position.distanceFrom(endTile.position) > maxDist) {
                    endTile = map.getRandomExit(i);
                    maxDist++;
                }
                ArrayList<Triple> startPosOptions = map.getExitXYAdjacentOpenPositions(startTile.position);
                //ArrayList<Triple> endPosOptions = map.getExitXYAdjacentOpenPositions(endTile.position);

                start = startPosOptions.get(map.rand.nextInt(startPosOptions.size()));
                end = endTile.position;//endPosOptions.get(map.rand.nextInt(endPosOptions.size()));
                System.out.println("   Path Distance: " + (start.distanceFrom(end) + 10));
                if (!map.generatePath(start, end, start.distanceFrom(end) + 20, true, true)) {
                    j--;
                    continue;
                }
                startTile.connect(map.tileLoc.get(start));
                //map.printDungeon();
            }
        }

        map.printDungeon();
    }

}