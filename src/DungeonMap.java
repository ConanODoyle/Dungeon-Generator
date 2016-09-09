import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public Tile getRandomExit(int ignore) {
        ArrayList<Integer> roomNums = new ArrayList(IntStream
                .iterate(0, n -> n + 1)
                .limit(rooms.size())
                .boxed()
                .collect(Collectors.toList()));
        roomNums.remove(new Integer(ignore));

        int roomNum;
        DungeonRoom room;
        Tile result = null;
        while (roomNums.size() > 0) {
            roomNum = roomNums.get(rand.nextInt(roomNums.size()));
            room = rooms.get(roomNum);
            if (room.exits.size() > 0) {
                result = room.exits.get(rand.nextInt(room.exits.size()));
                break;
            } else {
                roomNums.remove(new Integer(roomNum));
            }
        }
        return result;
    }

    protected void addTile(Triple pos, Tile t) {
        tiles.add(t);
        if (tileLoc.containsKey(pos)) {
            System.out.println("Warning: room contains tile at position already, overwriting...");
        }
        tileLoc.put(pos, t);
    }

    public void pickExits() {
        rooms.forEach(DungeonRoom::pickExits);
    }

    private int pathCount = 0;
    private HashSet<Triple> pathPos = new HashSet<>();

    public boolean generatePath(Triple st, Triple en, int length, boolean vertical, boolean prematureFinish) {
        pathPos.clear();
        pathCount = 0;
        return _generatePath(st, en, length, vertical, prematureFinish) != null;
    }

    private Tile _generatePath(Triple st, Triple en, int length, boolean vertical, boolean prematureFinish) {
        int dist = Math.abs(st.x - en.x) + Math.abs(st.y - en.y) + Math.abs(st.z - en.z);
        if (st.equals(en) && (length == 0 || prematureFinish)) {
            System.out.println("Found path!!!");
            if (getTileAt(st) == null) {
                addTile(st, new Tile(st, "path", this));
            }
            return getTileAt(st);
        } else if (length <= 0 || length < dist) {
            //System.out.println("Invalid distance to end! " + length + " < " + dist);
            return null;
        } else if (isPositionOccupiedByRoom(st)) {
            //System.out.println("Position already in use!!");
            return null;
        } else if (getTileAt(st) != null && pathPos.contains(getTileAt(st).position)) {
            //System.out.println("Position has been used by path before!!");
            return null;
        }
        //create a tile here if needed
        boolean hadTile = true;
        if (getTileAt(st) == null) {
            addTile(st, new Tile(st, "path", this));
            tileLoc.get(st).character = (char) ('0' + (pathCount++) % 10);
            hadTile = false;
        } else {
            getTileAt(st).character = '+';
        }
        pathPos.add(st);

        //pick a random direction and go with it
        Integer[] o = {0, 1, 2, 3, 4, 5};
        ArrayList<Integer> options = new ArrayList<>(Arrays.asList(o));
        while (options.size() > 0) {
            int dir = options.get(rand.nextInt(options.size()));
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
                getTileAt(st).connect(nextTile);
                return getTileAt(st);
            } else {
                options.remove(new Integer(dir));
            }
        }
        //could not find path, so remove tile as necessary.
        //System.out.println("Could not find valid path!");
        if (!hadTile) {
            tiles.remove(tileLoc.get(st));
            //pathPos.remove(tileLoc.get(st));
            tileLoc.remove(st);
            pathCount--;
        }
        return null;
    }

    private int fileNum = -1;
    public boolean doCustomLetters = false;
    public void printDungeon() {
        fileNum++;
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
                xMin = Math.min(t.x, xMin); xMax = Math.max(t.x + 1, xMax);
                yMin = Math.min(t.y, yMin); yMax = Math.max(t.y + 1, yMax);
                zMin = Math.min(t.z, zMin); zMax = Math.max(t.z + 1, zMax);
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
            if (t.character == 0 || !doCustomLetters) {
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
                if (e.character <= 0 || !doCustomLetters) {
                    printArray[e.x - xMin][e.y - yMin][e.z - zMin] = '*';
                } else {
                    printArray[e.x - xMin][e.y - yMin][e.z - zMin] = e.character;
                }
            }
        }

        //print layer by layer
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("" + fileNum + ".map"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < printArray[0][0].length; i++) {
            try {
                assert writer != null;
                writer.write("Layer " + i);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("\nLayer " + i);
            for (int j = 0; j < printArray[0].length; j++) {
                for (int k = 0; k < printArray.length; k++) {
                    try {
                        writer.write(printArray[k][j][i]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.print(printArray[k][j][i]);
                }
                try {
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println();
            }
        }
        try {
            assert writer != null;
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    public ArrayList<Triple> getExitXYAdjacentOpenPositions(Triple pos) {
        ArrayList<Triple> result = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Triple shift = pos.transform(Triple.xShift[i], Triple.yShift[i], 0);
            if (!isPositionOccupied(shift) || getTileAt(shift).type.equals("path")) {
                result.add(shift);
            }
        }
        return result;
    }
}