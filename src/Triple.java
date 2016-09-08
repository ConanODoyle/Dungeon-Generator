/**
 * Created by Samuel on 9/5/2016.
 */
public class Triple {
    public final int x, y, z; //so hashmaps arent broken in interesting ways
    protected static final int[] xShift = {0, 1, 0, -1, 0, 0};
    protected static final int[] yShift = {1, 0, -1, 0, 0, 0};
    protected static final int[] zShift = {0, 0, 0, 0, 1, -1};
    public static final Triple ORIGIN = new Triple(0, 0, 0);

    public Triple(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Triple copy() {
        return new Triple(this.x, this.y, this.z);
    }

    public int isAdjacent(Triple t) { //0-3 forward right back left; 4 up, 5 down

        for (int i = 0; i < 6; i++) {
            if (t.x + xShift[i] == x &&
                    t.y + yShift[i] == y &&
                    t.z + zShift[i] == z)
                return i;
        }
        return -1;
    }

    public Triple transform(int a, int b, int c) {
        return new Triple(this.x + a, this.y + b, this.z + c);
    }

    public Triple transform(Triple t) {
        return new Triple(this.x + t.x, this.y + t.y, this.z + t.z);
    }

    public boolean equals(Object o) {
        if (o instanceof Triple) {
            Triple t = (Triple) o;
            return t.x == x && t.y == y && t.z == z;
        } else {
            return false;
        }
    }

    public Triple shiftPos(int dir) {
        return this.transform(xShift[dir], yShift[dir], zShift[dir]);
    }

    public int hashCode() {
        return x+y+z;
    }

    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }
}
