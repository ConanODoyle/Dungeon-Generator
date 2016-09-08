import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**Generates the map layout
 * Generic class that randomly selects rooms and places them in random positions.
 * Connects them afterward with set-length paths.
 */
@SuppressWarnings({"ForLoopReplaceableByForEach", "WeakerAccess"})
public class DungeonMap {
    public ArrayList<Tile> tiles = new ArrayList<>();
    public HashMap<Triple, Tile> tileLoc = new HashMap<>();
    public ArrayList<DungeonRoom> rooms = new ArrayList<>();
    public static final Random rand = new Random();

    public boolean isPositionOccupied(Triple pos) {
        for (DungeonRoom d : rooms) {
            if (d.isPositionOccupied(pos))
                return true;
        }
        return tileLoc.containsKey(pos);
    }

    public boolean isPositionOccupiedByRoom(Triple pos) {
        for (DungeonRoom d : rooms) {
            if (d.isPositionOccupied(pos))
                return true;
        }
        return false;
    }

    public void pickExits() {
        for (DungeonRoom r : rooms) {
            r.pickExits();
        }
    }
    private int pathCount = 0;
    private ArrayList<Tile> pathTiles = new ArrayList<>();

    public void generatePath(Triple st, Triple en, int length, boolean vertical, boolean prematureFinish) {
        pathTiles = new ArrayList<>();
        _generatePath(st, en, length, vertical, prematureFinish);
    }

    private Tile _generatePath(Triple st, Triple en, int length, boolean vertical, boolean prematureFinish) {
        int dist = Math.abs(st.x - en.x) + Math.abs(st.y - en.y) + Math.abs(st.z - en.z);
        if (st.equals(en) && (length == 0 || prematureFinish)) {
            System.out.println("Found path!!!");
            if (getTileAt(st) == null) {
                tileLoc.put(st, new Tile(st, "path_end", this));
            }
            return getTileAt(st);
        } else if (length <= 0 || length < dist) {
            System.out.println("Invalid distance to end! " + length + " < " + dist);
            return null;
        } else if (isPositionOccupiedByRoom(st) || pathTiles.contains(getTileAt(st))) {
            System.out.println("Position already in use!!");
            return null;
        }
        //create a tile here if needed
        boolean hadTile = true;
        if (getTileAt(st) == null) {
            tileLoc.put(st, new Tile(st, "path", this));
            tiles.add(tileLoc.get(st));
            tileLoc.get(st).character = (char) ('0' + pathCount++);
            hadTile = false;
        } else {
            getTileAt(st).character = '+';
        }
        pathTiles.add(getTileAt(st));

        //pick a random direction and go with it
        Integer[] o = {0, 1, 2, 3, 4, 5};
        ArrayList<Integer> options = new ArrayList<>(Arrays.asList(o));
        System.out.println("Picking a direction...");
        while (options.size() > 0) {
            int dir = options.get(rand.nextInt(options.size()));
            System.out.println("    dir: " + dir);
            if (!vertical && dir > 3) { //if we just came from a vertical movement we can't do it again
                options.remove(new Integer(dir));
                continue;
            }
            Triple nextPos = st.shiftPos(dir);
            Tile nextTile;
            if (dir > 3) {
                nextTile = _generatePath(nextPos, en, length - 1, false, prematureFinish);
            } else {
                nextTile = _generatePath(nextPos, en, length - 1, true, prematureFinish);
            }
            if (nextTile != null) {
                System.out.println("Found path, collapsing...");
                getTileAt(st).connect(nextTile);
                nextTile.connect(getTileAt(st));
                return getTileAt(st);
            } else {
                options.remove(new Integer(dir));
            }
        }
        //could not find path, so remove tile as necessary.
        System.out.println("Could not find valid path!");
        if (!hadTile) {
            tiles.remove(tileLoc.get(st));
            pathTiles.remove(tileLoc.get(st));
            tileLoc.remove(st);
            pathCount--;
        }
        return null;
    }

    public void printDungeon() {
        //get edge points
        int xMin; int xMax;
        int yMin; int yMax;
        int zMin; int zMax;
        if (!rooms.isEmpty()) {
            DungeonRoom room = rooms.get(0);
            xMin = room.pos.x; xMax = room.pos.x + room.dim.x;
            yMin = room.pos.y; yMax = room.pos.y + room.dim.y;
            zMin = room.pos.z; zMax = room.pos.z + room.dim.z;
            for (DungeonRoom r : rooms) {
                xMin = Math.min(r.pos.x, xMin); xMax = Math.max(r.pos.x + r.dim.x, xMax);
                yMin = Math.min(r.pos.y, yMin); yMax = Math.max(r.pos.y + r.dim.y, yMax);
                zMin = Math.min(r.pos.z, zMin); zMax = Math.max(r.pos.z + r.dim.z, zMax);
            }

            for (Tile t : tiles) {
                xMin = Math.min(t.x, xMin); xMax = Math.max(t.x, xMax);
                yMin = Math.min(t.y, yMin); yMax = Math.max(t.y, yMax);
                zMin = Math.min(t.z, zMin); zMax = Math.max(t.z, zMax);
            }
        } else if (tiles.isEmpty()) {
            System.out.println("Cannot print; nothing to print!");
            return;
        } else {
            xMin = xMax = tiles.get(0).x;
            yMin = yMax = tiles.get(0).y;
            zMin = zMax = tiles.get(0).z;

            for (Tile t : tiles) {
                xMin = Math.min(t.x, xMin); xMax = Math.max(t.x, xMax);
                yMin = Math.min(t.y, yMin); yMax = Math.max(t.y, yMax);
                zMin = Math.min(t.z, zMin); zMax = Math.max(t.z, zMax);
            }
            xMax++;
            yMax++;
            zMax++;
        }
        //create array and place strings into there based on the offset from xMin/yMin/zMin
        char[][][] printArray = new char[xMax - xMin][yMax - yMin][zMax - zMin];
        System.out.println("PrintArray Dimensions: " + (xMax - xMin) + "," + (yMax - yMin) + "," + (zMax - zMin));
        for (int i = 0; i < printArray.length; i++) {
            for (int j = 0; j < printArray[i].length; j++) {
                for (int k = 0; k < printArray[i][j].length; k++) {
                    printArray[i][j][k] = ' ';
                }
            }
        }
        for (Tile t : tiles) {
            if (t.character == 0) {
                printArray[t.x - xMin][t.y - yMin][t.z - zMin] = '.';
            } else {
                printArray[t.x - xMin][t.y - yMin][t.z - zMin] = t.character;
            }
        }
        for (int i = 0; i < rooms.size(); i++) {
            DungeonRoom r = rooms.get(i);
            for (Tile t : r.tiles) {
                printArray[t.x - xMin][t.y - yMin][t.z - zMin] = (char) ('A' + i);
            }
            for (Tile e : r.exits) {
                printArray[e.x - xMin][e.y - yMin][e.z - zMin] = '*';
            }
        }

        //print layer by layer
        for (int i = 0; i < printArray[0][0].length; i++) {
            System.out.println("Layer " + i);
            for (int j = 0; j < printArray[0].length; j++) {
                for (int k = 0; k < printArray.length; k++) {
                    System.out.print(printArray[k][j][i]);
                }
                System.out.println();
            }
        }
    }

    public Tile getTileAt(Triple pos) {
        for (DungeonRoom d : rooms) {
            if (d.getTile(pos) != null)
                return d.getTile(pos);
        }
        if (tileLoc.containsKey(pos)) {
            return tileLoc.get(pos);
        } else {
            return null;
        }
    }
}