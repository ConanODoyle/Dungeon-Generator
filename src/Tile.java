import java.util.HashMap;

/**
 * Created by Samuel on 9/3/2016.
 */
@SuppressWarnings("WeakerAccess")
public class Tile {
    public int x;
    public int y;
    public int z;
    public Triple position;
    public String type;
    public HashMap<Integer, Tile> connections = new HashMap<>(); //0-3 forward right back left; 4 up, 5 down
    public DungeonMap d;
    public char character;

    public static final String[][][] TILETYPES = {{
            {"─", "│"}, //room
            {"┌", "┐","└","┘"},
            {"├", "┤", "┬", "┴"},
            {"┼"}},
            {{"═", "║"}, //hallway
            {"╔", "╗","╚","╝"},
            {"╠", "╣", "╦", "╩"},
            {"╬"}},
            { {"╴", "╵", "╶", "╷"} }
    };

    public Tile(Triple t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
        position = t;
    }

    public Tile(Triple t, DungeonMap d) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
        position = t;
        this.d = d;
    }

    public Tile(Triple t, String type, DungeonMap d) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
        position = t;
        this.d = d;
        this.type = type;
    }

    public void connect(Tile t) {
        if (connections.containsValue(t))
            return;

        int dir = position.isAdjacent(t.position);
        if (dir < 0) {
            //System.out.println("Tiles must be adjacent to be connected!");
            return;
        }
        this.connections.put(dir, t);
        t.connect(this);
    }

    /**Tileset Information:
     * height = tile height;
     * width = tile width;
     * length = tile length; (let players make non-square tiles maybe??)
     * hasWalkways;     (If true, there are walkway floor tiles                             - _Modifier=Walkway)
     * hasBalconies;    (If true, there are balcony floor tiles                             - _Modifier=Balcony)
     * hasPits;         (If true, there are pit floor tiles and pit walled, floored tiles   - _Modifier=Pit)
     * hasWindows;      (If true, there are tiles with windowed walls, ceiling, or floor    - _Modifier=Window[FLRBUD])
     * hasGrates;       (If true, there are tiles with windowed walls, ceiling, or floor    - _Modifier=Grate[FLRBUD])
     *      sidenote:   grates are equivalent to windows, but are able to attack through. Windows will be made of indestructible glass?
     *                  in addition, grated/windowed tiles must be stacked on top/next to other grated/windowed tiles to see through
     *                  so you don't put a floor grate tile on top of a ceilingless tile, but rather a tile with a ceiling grate.
     * hasTorches;      (If true, the tile has torches and is lit by a single light source  - _Modifier=Light)
     * multiple modifiers come after each other (ex: _Modifier=WalkwayLight) (could comma delineate them) why does this say _Modifier= when this says
     * tiles may not have modifiers
     * hasLip;          (If true, has lip tiles that go along the upper edge of ceilingless tiles, place last   - _Modifier=Lip)
     * hasArches;       (If true, has 3 arch types that go at the top of tile edges         - _Modifier=Arch[BLRN]) (both left right none)
     * Rooms will look for certain custom tiles when generating before defaulting to using the base tileset tiles.
     * 
     *
     *
     * Tile Types
     * openings: S / C / T / X / E / N (none; fully walled in)
     *      Tiles oriented so that the opening orientation preference is Back-Left-Right-Forwards
     *      Ex: corner tile will always have openings facing back and left by default
     *      Ex: a T tile will always have openings facing back, left, and right by default
     * Ce (no ceil) / Fl(no floor)
     * no pillar: FL / FR / BL / BR (based on default orientation) (can be multiple)
     * door: F / L / R / B / M (S only) // doubledoor (corner door): FLF / FLL /FRF / FRR / BLB / BLL / BRB / BRR
     *      doors must be attached to wall (except for S_M)
     *      ex: S tiles cannot have F doors, only L / R
     *      double door tiles have doors next to a corner of the tile. FL indicates corner, F indicates which wall its on
     *
     * format:
     * [tileset]_[SCTXE]_NoPillar?([CF]+?)_No?(FL)?(FR)?(BL)?(BR)_(Door?([FLRBM]+?)|DDoor?(FLF)?(FLL)?(FRF)?(FRR)?(BLB)?(BLL)?(BRB)?(BRR)|NoDoor)?(_Modifier=[modifier]+)
     * example:
     * T1_S_NoCe_NoPillar_DoorLR
     *      A straight tile with no ceiling, and doors on the left/right walls.
     * T1_T_NoCeFl_NoPillarBLBR_DoorF
     *      A T tile with no ceiling or floor, no pillars, and has a door on the wall.
     * T1_N_NoCe_NoPillar_NoDoor
     *      A "bucket" tile with no ceiling.
     * T1_X_NoCe_NoPillarFLFRBLBR_NoDoor
     *      A floor tile with no ceiling or pillars.
     *
     * secondary tile type:
     * Stair_[FLRB]{1}
     * example:
     * T1_Stair_F
     *      A stair tile that the player exits in the direction he enters.
     */
}
