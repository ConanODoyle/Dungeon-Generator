import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 */
public class RectRoom extends DungeonRoom {
    private int maxVolume;
    private int maxHeight;

    public RectRoom(Triple pos, DungeonMap d) {
        super(pos);
        maxVolume = rand.nextInt(20);
        this.d = d;
    }

    public RectRoom(Triple pos, int volume, int height, DungeonMap d) {
        super(pos);
        maxVolume = volume;
        maxHeight = height;
        this.d = d;
    }
    @Override
    public ArrayList<Tile> generateRoom() throws InterruptedException {
        int width = rand.nextInt(maxVolume/2)+2;
        int length = maxVolume/width;
        int height = rand.nextInt(maxHeight) + 1;

        tiles.clear();
        tileLoc.clear();

        //get shifts from the initial room position
        int xShift = rand.nextInt(width);
        int yShift = rand.nextInt(length);

        int x; int y;
        Triple currPos;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                x = i + xShift + pos.x;
                y = j + yShift + pos.y;
                currPos = new Triple(x, y, pos.z);
                if (d.isPositionOccupied(currPos)) {
                    break;
                }
                for (int k = 0; k < height; k++) {
                    currPos = new Triple(x, y, pos.z + k);
                    if (!d.isPositionOccupied(currPos)) {
                        addTile(currPos, new Tile(currPos));
                    } else {
                        break;
                    }
                }
            }
        }
        if (tiles.size() < maxVolume/2) {
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < width; j++) {
                    x = j + xShift + pos.x;
                    y = i + yShift + pos.y;
                    currPos = new Triple(x, y, pos.z);
                    if (d.isPositionOccupied(currPos)) {
                        break;
                    }
                    for (int k = 0; k < height; k++) {
                        currPos = new Triple(x, y, pos.z + k);
                        if (!d.isPositionOccupied(currPos)) {
                            addTile(currPos, new Tile(currPos));
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        updateRoomInformation();
        if (dim.x <= 0 || dim.y <= 0) { //redo if the room is too thin
            generateRoom();
        }

        return tiles;
    }

    @Override
    public void pickExits() {
        Collections.shuffle(tiles);
        int numExits = 1;
        numExits += (int) (Math.pow(dim.x * dim.y, 1.0/3));

        Triple currPos;
        int[] open;
        for (Tile t : tiles) {
            currPos = t.position;
            if (currPos.z != pos.z) {
                continue;
            }
            if (getNumXYAdjacentTiles(currPos) < 4) {
                open = getOpenDirections(currPos);
                boolean hasOpenExit = false;

                for (int i = 0; i < 4; i++) {
                    hasOpenExit = hasOpenExit || open[i] == 1;
                }
                if (hasOpenExit) {
                    exits.add(t);
                    numExits--;
                }
            }

            if (numExits <= 0) {
                break;
            }
        }
    }
}
