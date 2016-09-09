import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**Used to test dungeon generation
 * Really the major dungeon generation logic
 * Should move to DungeonMap as a "generateDungeon" function
 */
public class DungeonMapTest {

    public int maxRooms = 22; //max 26 since the bls generator assumes the rooms use letters
    public int approxWidth = 10; //max will not apply later when proper BLS generation is added.
    public int approxLength = 40;
    public int approxHeight = 8;

    @Test
    public void printDungeon() throws Exception {
        DungeonMap map = new DungeonMap();
        Random rand = new Random();
        int x; int y; int z;
        for (int i = 0; i < maxRooms; i++) {
            x = rand.nextInt(approxWidth);
            y = rand.nextInt(approxLength);
            z = rand.nextInt(approxHeight) + 1;
            DungeonRoom r1 = new RectRoom(new Triple(x, y, z), 8 + rand.nextInt(15), 3, map);
            r1.generateRoom();
            map.rooms.add(r1);
        }
        map.pickExits();
        //serves as relative position tile so its easier to figure out what is where.
        //map.doCustomLetters = true;
        //map.tileLoc.put(Triple.ORIGIN, new Tile(Triple.ORIGIN, map));
        //map.tiles.add(map.tileLoc.get(Triple.ORIGIN));
        //map.tileLoc.get(Triple.ORIGIN).character = 'o';

        map.printDungeon();

        //generate paths between exits??
        System.out.println("\n");
        Triple start;
        Triple end;
        int[] options = {0, 1, 2, 3, 4, 5};
        ArrayList<Integer> roomOptions;
        DungeonRoom r;
        for (int i = 0; i < maxRooms; i++) {
            roomOptions = new ArrayList(Arrays.asList(options));
            roomOptions.remove(new Integer(i));
            r = map.rooms.get(i);

            for (int j = 0; j < r.exits.size(); j++) {
                System.out.println("Room " + i + " exit " + j);
                Tile startTile = r.exits.get(j);
                Tile endTile = map.getRandomExit(i);
                int maxDist = 10;
                while (startTile.position.distanceFrom(endTile.position) > maxDist) {
                    endTile = map.getRandomExit(i);
                    maxDist++;
                    maxDist %= 20;
                }
                ArrayList<Triple> startPosOptions = map.getExitXYAdjacentOpenPositions(startTile.position);
                ArrayList<Triple> endPosOptions = map.getExitXYAdjacentOpenPositions(endTile.position);

                start = startPosOptions.get(map.rand.nextInt(startPosOptions.size()));
                end = endPosOptions.get(map.rand.nextInt(endPosOptions.size()));
                System.out.println("   Path Distance: " + start.distanceFrom(end) + " + " + (int) (Math.pow(approxHeight*approxLength*approxWidth, 1.0/3) / 3 + 1));
                if (!map.generatePath(start, end, start.distanceFrom(end) + (int) Math.pow(approxHeight*approxLength*approxWidth, 1.0/3) / 3 + 1, true, true)) {
                    System.out.println("      Could not find path! Restarting...");
                    j--;
                    continue;
                }
                startTile.connect(map.tileLoc.get(start));
                endTile.connect(map.tileLoc.get(end));
                //map.printDungeon();
            }
            MapToBLS.generateSimpleBLS("1.map");
        }

        map.printDungeon();
    }

}