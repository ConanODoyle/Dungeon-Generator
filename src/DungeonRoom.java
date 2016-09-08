import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**A generic DungeonRoom abstract class that defines the basic requirements of any room.
 * Various useful reusable variables are defined here for convenience.
 */
@SuppressWarnings("WeakerAccess")
public abstract class DungeonRoom {
    public Triple startPos; //start position
    public Triple pos; //bottom back left corner
    public Triple dim; //dimensions
    public ArrayList<Tile> tiles = new ArrayList<>();
    public ArrayList<Triple> takenPos = new ArrayList<>();
    public HashMap<Triple, Tile> tileLoc = new HashMap<>();
    public ArrayList<Tile> exits = new ArrayList<>();
    public DungeonMap d;

    protected static final int[] xShift = {0, 1, 0, -1, 0, 0};
    protected static final int[] yShift = {1, 0, -1, 0, 0, 0};
    protected static final int[] zShift = {0, 0, 0, 0, 1, -1};
    protected static final Random rand = new Random();

    public DungeonRoom(Triple pos) {
        this.startPos = pos;
        this.dim = Triple.ORIGIN;
    }

    /**Generates the dungeon room according to the implementation in each room class.
     * Must call this function before generating or placing other rooms, as rooms
     * take up 0 tiles (eg the starting tile) before generation.
     * @return A list of tiles that the dungeon room occupies.
     * @throws InterruptedException
     */
    public abstract ArrayList<Tile> generateRoom() throws InterruptedException;

    public boolean isPositionOccupied(Triple pos) {
        return tileLoc.containsKey(pos);
    }

    public Tile getTile(Triple pos) {
        if (isPositionOccupied(pos)) {
            return tileLoc.get(pos);
        } else {
            return null;
        }
    }

    protected void addTile(Triple pos, Tile t) {
        tiles.add(t);
        if (tileLoc.containsKey(pos)) {
            System.out.println("Warning: room contains tile at position already, overwriting...");
        }
        tileLoc.put(pos, t);
    }

    public abstract void pickExits();

    protected int getNumXYAdjacentTiles(Triple pos) {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            Triple shift = pos.transform(xShift[i], yShift[i], 0);
            if (tileLoc.containsKey(shift)) {
                count++;
            }
        }
        return count;
    }

    protected int[] getOpenDirections(Triple pos) {
        int[] result = new int[6];
        for (int i = 0; i < 6; i++) {
            Triple shift = pos.transform(xShift[i], yShift[i], 0);
            if (!tileLoc.containsKey(shift)) {
                if (!d.isPositionOccupied(shift)) {
                    result[i] = 1;
                }
            }
        }
        return result;
    }

    public void updateRoomInformation() {
        int xMin; int xMax;
        int yMin; int yMax;
        int zMin; int zMax;
        if (tiles.isEmpty()) {
            dim = new Triple(0, 0, 0);
            return;
        }
        xMin = xMax = tiles.get(0).x;
        yMin = yMax = tiles.get(0).y;
        zMin = zMax = tiles.get(0).z;

        for (Tile t : tiles) {
            xMin = Math.min(t.x, xMin); xMax = Math.max(t.x, xMax);
            yMin = Math.min(t.y, yMin); yMax = Math.max(t.y, yMax);
            zMin = Math.min(t.z, zMin); zMax = Math.max(t.z, zMax);
            if (!takenPos.contains(t.position)) {
                takenPos.add(t.position);
            }
        }
        pos = new Triple(xMin, yMin, zMin);
        dim = new Triple(xMax - xMin + 1, yMax - yMin + 1, zMax - zMin + 1);
    }

    /**Shifts the given position by the direction given.
     * @param pos - starting position
     * @param dir - 0 forward, 1 right, 2 left, 3 back, 4 up, 5 down
     * @return - shifted position
     */
    protected Triple shiftPos(Triple pos, int dir) {
        return pos.transform(xShift[dir], yShift[dir], zShift[dir]);
    }
}
