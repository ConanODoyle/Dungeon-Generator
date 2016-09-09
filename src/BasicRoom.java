import java.util.ArrayList;
import java.util.Collections;


/**A basic flat room with doors and single-height ceiling
 * Has a morphous shape - flows to fill the available area.
 * Ceiling height ranges from 1 to 2
 */
@SuppressWarnings({"ForLoopReplaceableByForEach", "WeakerAccess"})
public class BasicRoom extends DungeonRoom{
    private int maxVolume;
    private int volume = 0;

    public BasicRoom(Triple pos, DungeonMap d) {
        super(pos);
        maxVolume = rand.nextInt(20);
        this.d = d;
    }

    public BasicRoom(Triple pos, int volume, DungeonMap d) {
        super(pos);
        maxVolume = volume;
        this.d = d;
    }

    @Override
    public ArrayList<Tile> generateRoom() throws InterruptedException {
        System.out.println("Beginning BasicRoom generation...");
        dim = dim.transform(0, 0, rand.nextInt(2)+1);
        System.out.println("    Height: " + dim.z);

        Tile currTile = new Tile(startPos);
        tiles.clear();
        tileLoc.clear();
        tiles.add(currTile);
        tileLoc.put(startPos, currTile);
        volume++;

        //start flow fill
        takenPos.add(currTile.position);
        Triple currPos;
        Triple shift;
        while (volume < maxVolume && !takenPos.isEmpty()) {
            int mod = rand.nextInt(4); //randomly pick an adjacent tile to add
            int finMod = mod;
            currPos = takenPos.get(rand.nextInt(takenPos.size()));
            if (getNumXYAdjacentTiles(currPos) >= 4){
                System.out.println("    Tile surrounded, canceling flow fill...");
                takenPos.remove(currPos);
                continue;
            }

            int occupiedCount = 0;
            int adjCount = 0;
            for (int i = 0; i < 4; i++) {
                shift = shiftPos(currPos, (mod + i) % 4);
                int numAdj = getNumXYAdjacentTiles(shift);
                if (!isPositionOccupied(shift) && !d.isPositionOccupied(shift)) {
                    if (numAdj > adjCount && !tileLoc.containsKey(shift)) { //prefer tiles with more adjacent tiles
                        finMod = (mod + i) % 4;                             //to prevent stringy rooms
                        adjCount = numAdj;
                    }
                } else {
                    occupiedCount++;
                }
            }
            if (occupiedCount >= 4) {
                System.out.println("    Tile surrounded, canceling flow fill...");
                takenPos.remove(currPos);
                continue;
            }
            //valid position found; claim tile and add position to queue
            currPos = shiftPos(currPos, finMod);
            currTile = new Tile(currPos);
            addTile(currPos, currTile);
            takenPos.add(currPos);

            volume++;
        }

        //raise room ceiling as necessary
        if (dim.z > 1) {
            Triple up;
            int size = tiles.size();
            for (int i = 0; i < size; i++) {
                up = tiles.get(i).position.transform(0, 0, 1);
                if (!d.isPositionOccupied(up)) {
                    addTile(up, new Tile(up));
                }
            }
        }

        //connect all the tiles together - data used to pick tile types and determine exit dir
        //slow but works; optimize later
        for (Tile t : tiles) {
            tiles.forEach(t::connect);
        }
        //also reset pos and dimensions to correct values
        updateRoomInformation();
        return tiles;
    }

    @Override
    public void pickExits() {
        Collections.shuffle(takenPos);
        int index = 0;
        int modifier = Math.max(1, (int) Math.pow(volume, 1.0/4));
        System.out.println("Starting exit search...");
        Triple currPos;
        for (int i = 0; i < rand.nextInt(2) + modifier && index < tiles.size(); i++) {
            currPos = takenPos.get(index);
            if (getNumXYAdjacentTiles(currPos) >= 4 || currPos.z != pos.z) {
                i--;
                index++;
                continue;
            }
            int[] openDir = getOpenDirections(currPos);
            boolean hasOpenEnd = false;
            for (int j = 0; j < 4; j++) {
                hasOpenEnd = hasOpenEnd || openDir[j] == 1;
            }
            if (hasOpenEnd) {
                index++;
                exits.add(tileLoc.get(currPos));
            } else {
                index++;
                i--;
            }
        }
        System.out.println("Finished exit search...");
    }
}
