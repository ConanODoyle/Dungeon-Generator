import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**Converts the .map files generated by the generator into .bls
 * Simple assumes the .map files contain only *, A-Z, and .'s
 */
public class MapToBLS {
    private static final String SAVE_FILE_PREFIX =
        "This is a Blockland save file.  You probably shouldn't modify it cause you'll screw it up.\n" +
        "1\n" +
        "\n" +
        "0.729412 0.137255 0.137255 1.000000\n" +
        "0.874510 0.384314 0.121569 1.000000\n" +
        "1.000000 0.647059 0.113725 1.000000\n" +
        "0.411765 0.564706 0.266667 1.000000\n" +
        "0.200000 0.427451 0.258824 1.000000\n" +
        "0.356863 0.576471 0.658824 1.000000\n" +
        "0.203922 0.349020 0.458824 1.000000\n" +
        "0.031373 0.101961 0.176471 1.000000\n" +
        "0.603922 0.392157 0.847059 1.000000\n" +
        "1.000000 1.000000 1.000000 0.003922\n" +
        "0.803922 0.137255 0.137255 0.509804\n" +
        "0.874510 0.349020 0.121569 0.509804\n" +
        "1.000000 0.647059 0.113725 0.509804\n" +
        "0.411765 0.564706 0.266667 0.509804\n" +
        "0.294118 0.509804 0.486275 0.509804\n" +
        "0.486275 0.592157 0.603922 0.509804\n" +
        "0.301961 0.376471 0.556863 0.509804\n" +
        "0.286275 0.349020 0.356863 0.509804\n" +
        "0.439216 0.337255 0.556863 0.509804\n" +
        "0.886275 0.376471 0.376471 1.000000\n" +
        "0.949020 0.631373 0.474510 1.000000\n" +
        "1.000000 0.784314 0.466667 1.000000\n" +
        "0.603922 0.713726 0.501961 1.000000\n" +
        "0.411765 0.674510 0.482353 1.000000\n" +
        "0.658824 0.768627 0.803922 1.000000\n" +
        "0.541176 0.603922 0.874510 1.000000\n" +
        "0.674510 0.549020 0.886275 1.000000\n" +
        "0.894118 0.627451 0.866667 1.000000\n" +
        "0.337255 0.015686 0.015686 1.000000\n" +
        "0.564706 0.203922 0.054902 1.000000\n" +
        "0.803922 0.521569 0.090196 1.000000\n" +
        "0.313726 0.431373 0.203922 1.000000\n" +
        "0.129412 0.247059 0.156863 1.000000\n" +
        "0.301961 0.341176 0.474510 1.000000\n" +
        "0.203922 0.227451 0.309804 1.000000\n" +
        "0.101961 0.113725 0.156863 1.000000\n" +
        "0.329412 0.258824 0.419608 1.000000\n" +
        "0.411765 0.274510 0.200000 1.000000\n" +
        "0.529412 0.368627 0.231373 1.000000\n" +
        "0.627451 0.486275 0.301961 1.000000\n" +
        "0.349020 0.392157 0.227451 1.000000\n" +
        "0.090196 0.176471 0.113725 1.000000\n" +
        "0.368627 0.458824 0.466667 1.000000\n" +
        "0.211765 0.301961 0.313726 1.000000\n" +
        "0.121569 0.192157 0.200000 1.000000\n" +
        "0.858824 0.858824 0.858824 0.376471\n" +
        "0.984314 0.984314 0.984314 1.000000\n" +
        "0.858824 0.858824 0.839216 1.000000\n" +
        "0.674510 0.674510 0.647059 1.000000\n" +
        "0.494118 0.494118 0.458824 1.000000\n" +
        "0.329412 0.329412 0.294118 1.000000\n" +
        "0.200000 0.200000 0.184314 1.000000\n" +
        "0.078431 0.078431 0.066667 1.000000\n" +
        "0.000000 0.000000 0.000000 1.000000\n" +
        "0.376471 0.376471 0.376471 0.509804\n" +
        "0.819608 0.686275 0.576471 1.000000\n" +
        "0.749020 0.584314 0.431373 1.000000\n" +
        "0.686275 0.486275 0.337255 1.000000\n" +
        "0.564706 0.400000 0.266667 1.000000\n" +
        "0.419608 0.286275 0.184314 1.000000\n" +
        "0.282353 0.184314 0.121569 1.000000\n" +
        "0.164706 0.109804 0.070588 1.000000\n" +
        "0.309804 0.227451 0.129412 1.000000\n" +
        "0.494118 0.368627 0.227451 1.000000";

    private static final int COLOR_BLACK = 53;
    private static final int[] COLOR_ARRAY = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int COLOR_DARK_GREY = 6;

    public static void generateSimpleBLS(String fileName) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File("saves/" + fileName)));
        String line = br.readLine();
        int brickCount = 0;
        while (line != null) {
            if (line.contains("Layer")) {
                line = br.readLine();
                continue;
            }
            char[] lineContents = line.toCharArray();
            for (char c : lineContents) {
                if (c == '.' || c == '*' || (c >= 'A' && c <= 'Z')) {
                    brickCount++;
                }
            }

            line = br.readLine();
        }
        br = new BufferedReader(new FileReader(new File(fileName)));
        line = br.readLine();

        BufferedWriter bw = new BufferedWriter(new FileWriter("DUNGEON " + DUNGEON_NUMBER + ".bls"));
        bw.write(SAVE_FILE_PREFIX);
        bw.newLine();
        bw.write("Linecount " + brickCount);
        bw.newLine();


        String BRICK_NAME = "4x Cube\"";
        String position;
        String writeLine;

        int xPos = 2;
        int yPos = 2;
        int zPos = -1;
        //noinspection ConstantConditions
        while (line != null && !line.equals("")) {
            if (line.contains("Layer")) {
                zPos += 2;
                yPos = 2;
                line = br.readLine();
                continue;
            }

            char[] lineContents = line.toCharArray();

            for (int i = 0; i < lineContents.length; i++) {
                char c = lineContents[i];
                position = (xPos + (2 * i)) + " " + yPos + " " + zPos;
                if (c == '.') {
                    writeLine = makeSimpleBrickString(BRICK_NAME, position, COLOR_BLACK);
                } else if (c >= 'A' && c <= 'Z') {
                    writeLine = makeSimpleBrickString(BRICK_NAME, position, COLOR_ARRAY[(c - 'A') % 9]);
                } else if (c == '*') {
                    writeLine = makeSimpleBrickExitString(" " + BRICK_NAME, position, COLOR_DARK_GREY);
                } else {
                    writeLine = null;
                }

                if (writeLine != null) {
                    bw.write(writeLine);
                    bw.newLine();
                }
            }

            yPos += 2;
            line = br.readLine();
        }

        br.close();

        bw.flush();
        bw.close();
    }

    private static String makeSimpleBrickString(String brick, String position, int color) {
        return brick + " " + position + " 0 0 " + color + "  0 0 1 1 1";
    }

    private static String makeSimpleBrickExitString(String brick, String position, int color) {
        return brick + " " + position + " 0 0 " + color + " Letters/A 0 0 1 1 1";
    }

    private static final int DUNGEON_NUMBER = 10;

    public static void main(String[] args) throws Exception {
        generateSimpleBLS("1.map");
    }
}
