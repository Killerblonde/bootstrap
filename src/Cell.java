public class Cell {

    public float red = 0;
    public float green = 0;
    public float blue = 0;
    public float alpha = 0;

    // alpha values for selected and deselected
    public float selAlpha = 1;
    public float deselAlpha = 0.6f;

    public int row = 0;
    public int col = 0;

    public boolean selected = false;

    public boolean activated = false; //if this is a door, means button has changed it open/closed

    // some cells are labelled (e.g. doors link to buttons, players)
    public String label = "";
    public boolean labelled = false;
    public boolean customLabel = false; // prevent player from labelling (if can label at all)

    // unique cells cannot appear twice in a grid
    // for players, this is true but only per label (for editor usage)
    public boolean unique = false;

    private boolean passable = false; // player can move through entity
    public boolean hasMovePlanned = false; // a player is planning on moving to this cell!

    public boolean useable = false; // player can use

    private String[] keys = new String[Game.maxKeys];
    public boolean canHaveKey = false;

    private int cellType = 0; //cell type (for automatic color setting)

    public Player player = null; //cell has a reference to the player it represents, kept null if no player

    public void propFromString(String propString) {
        //gets properties from a string, (assumes well-formatted!)
        //first gets row
        int i1 = propString.indexOf('|');
        row = Integer.parseInt(propString.substring(1, i1));
        String rest1 = propString.substring(i1 + 1);
        int i2 = rest1.indexOf('|');
        col = Integer.parseInt(rest1.substring(0, i2));
        String rest2 = rest1.substring(i2 + 1);
        int i3 = rest2.indexOf('|');
        cellType = Integer.parseInt(rest2.substring(0, i3));
        // if 3, makes empty cell and gives player
        if (cellType == 3) {
            cellType = 0;
            player = Game.players.get(0);
        }
        String rest3 = rest2.substring(i3 + 1);
        int i4 = rest3.indexOf('|');
        label = rest3.substring(0, i4);
        String rest4 = rest3.substring(i4 + 1);
        for (int i = 0; i < Game.maxKeys; i++) {
            int i5 = rest4.indexOf(',');
            setKey(rest4.substring(0, i5), i);
            rest4 = rest4.substring(i5 + 1);
        }
        autoProp(false, false);
    }

    public String writePropString() {
        // writes all properties to string (for saving)
        // only writes properties that cannot be inferred (e.g. does not include RGBA)
        // will make cellType = 3 temporarily if this is player!
        int tempType = cellType;
        if (player != null) {
            cellType = 3;
        }
        String propString = "[" + row + "|" + col + "|" + cellType + "|" + label + "|";
        for (int i = 0; i < Game.maxKeys; i++) {
            propString += getKey(i) + ",";
        }
        propString += "]";
        cellType = tempType;
        return propString;
    }

    /**
     * Automatically sets cell colors and properties due to cell type
     */
    public void autoProp(boolean removeKeys, boolean removePlayer) {
        if (removeKeys) {
            for (int i = 0; i < Game.maxKeys; i++) {
                keys[i] = "";
                if (player != null) {
                    player.setKey("", i);
                }
            }
        }
        if (removePlayer) {
            player = null;
        }
        switch (cellType) {
            case 0:
                // empty space
                red = 0.9f;
                green = 0.9f;
                blue = 0.9f;
                labelled = false;
                customLabel = false;
                passable = true;
                canHaveKey = true;
                break;
            case 1:
                // wall
                red = 0.05f;
                green = 0.05f;
                blue = 0.05f;
                labelled = false;
                customLabel = false;
                passable = false;
                canHaveKey = false;
                break;
            case 2:
                // button
                red = 0.5f;
                green = 0;
                blue = 0;
                labelled = true;
                customLabel = true;
                passable = true;
                canHaveKey = false;
                break;
            //no 3, this is player (attribute a cell has)
//            case 3:
//                // player
//                // in level editor, sets to empty cell with player!
//                red = 0;
//                green = 1;
//                blue = 0;
//                labelled = true;
//                customLabel = false;
//                passable = false;
//                canHaveKey = true;
//                player = Game.players.get(0); // should be first in array if autopropping
//                player.col = col;
//                player.row = row;
//                break;
            case 4:
                // open door
                red = 0.6f;
                green = 0.5f;
                blue = 0.8f;
                labelled = true;
                customLabel = true;
                passable = true;
                canHaveKey = false;
                break;
            case 5:
                // closed door
                red = 0.2f;
                green = 0.1f;
                blue = 0.4f;
                labelled = true;
                customLabel = true;
                passable = false;
                canHaveKey = false;
                break;
            case 6:
                // locked (closed) door
                red = 0.4f;
                green = 0.3f;
                blue = 0.2f;
                labelled = true;
                customLabel = true;
                passable = false;
                canHaveKey = false;
                break;
            //no case 7, this is "key", which is not a cell type
            case 8:
                // time machine
                red = 0;
                green = 0.3f;
                blue = 0.9f;
                labelled = false;
                customLabel = false;
                passable = true;
                canHaveKey = false;
                break;
            case 9:
                // exit
                red = 0.7f;
                green = 0.8f;
                blue = 0.2f;
                labelled = true; // not in the usual way, so will not ask player
                customLabel = false;
                label = "EXIT";
                passable = true;
                canHaveKey = false;
                break;
            default:
                System.out.println("Unknown cell type! autoProp failed.");
                break;
        }
        // sets unique
        unique = isUnique(cellType);
        // makes sure doesn't have label from past cell type
        if (!labelled) {
            label = "";
        }
    }

    public static boolean isUnique(int cellType) {
        switch (cellType) {
            case 3:
                return true; //player
            case 9:
                return true; //exit
        }
        return false;
    }

    public void setType(int cellType) {
        // sets type and formats cell
        // will clear key if this is called (setCell in Game will not call if giving key or player)
        this.cellType = cellType;
        autoProp(true, true);
    }

    public boolean getPassable() {
        // returns of cell is passable, and has no player!
        if (passable && player == null) {
            return true;
        }
        return false;
    }

    public int getType() {
        return cellType;
    }

    public void setRGBA(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public float[] getRGBA(boolean noPlayer) {
        // gets color, including "selected color"
        // puts to green if has a player! (still standing on button or whatever)
        float[] RGBA = new float[4];
        if (player == null || noPlayer) {
            //real color
            RGBA[0] = red;
            RGBA[1] = green;
            RGBA[2] = blue;
        } else {
            //player color
            RGBA[0] = 0;
            RGBA[1] = 1;
            RGBA[2] = 0;
        }
        if (selected) {
            RGBA[3] = selAlpha;
        } else {
            RGBA[3] = deselAlpha;
        }
        return RGBA;
    }

    public boolean hasMovingPlayer() {
        if (player == null) {
            return false;
        } else if (player.willMove) {
            return true;
        }
        return false;
    }

    public static String getCellName(int type) {
        // gets name of cell with this type
        // may be passed a cell
        String r = "";
        switch (type) {
            case 0:
                r = "Empty Space";
                break;
            case 1:
                r = "Wall";
                break;
            case 2:
                r = "Button";
                break;
            case 3:
                r = "Player"; //not a cell type... hmm...
                break;
            case 4:
                r = "Open Door";
                break;
            case 5:
                r = "Closed Door";
                break;
            case 6:
                r = "Locked Door";
                break;
            case 7:
                r = "Key"; //not a cell type... set this for editor
                break;
            case 8:
                r = "Time Machine";
                break;
            case 9:
                r = "Exit";
                break;
            default:
                return "INVALID CELL TYPE";
        }
        return r;
    }

    public String getThisCellName() {
        // gets name of THIS cell (nonstatic)
        String r = "";
        switch (cellType) {
            case 0:
                r = "Empty Space";
                break;
            case 1:
                r = "Wall";
                break;
            case 2:
                r = "Button";
                break;
            case 3:
                r = "Player"; //not a cell type... hmm...
                break;
            case 4:
                r = "Open Door";
                break;
            case 5:
                r = "Closed Door";
                break;
            case 6:
                r = "Locked Door";
                break;
            case 7:
                r = "Key"; //not a cell type... set this for editor
                break;
            case 8:
                r = "Time Machine";
                break;
            case 9:
                r = "Exit";
                break;
            default:
                return "INVALID CELL TYPE";
        }
        if (player != null) {
            r += " with Player";
        }
        if (hasKeys()) {
            r += " with Key(s)";
        }
        return r;
    }

    public void setKey(String key, int i) {
        if (player != null) {
            player.setKey(key, i);
        } else {
            keys[i] = key;
        }
    }

    public String getKey(int i) {
        if (player != null) {
            return player.getKey(i);
        }
        return keys[i];
    }

    public String[] getKeyArray() {
        if (player != null) {
            return player.getKeyArray();
        }
        return keys;
    }

    public String[] getShortKeyArray() {
        // returns minimum array
        // assumes there are some keys to begin with! otherwise might get funky...
        String[] minkeys = new String[numKeys()];
        for (int i = 0; i < numKeys(); i++) {
            minkeys[i] = getKey(i);
        }
        return minkeys;
    }

    public void clearKeys() {
        // clears keys from cell, and player too if there is one
        for (int i = 0; i < Game.maxKeys; i++) {
            keys[i] = "";
            if (player != null) {
                player.resetKeys();
                break;
            }
        }
    }

    public void addKey(String key) {
        // tries to add key to first empty spot in array
        if (firstEmptyKeyIndex() >= 0) {
            for (int i = 0; i < Game.maxKeys; i++) {
                if (getKey(i).equals("")) {
                    setKey(key, i);
                    break;
                }
            }
        } else {
            System.out.println("Tried to add key to full array! :(");
        }

    }

    public void removeKey(String key) {
        // tries to remove key and then shifts all other keys down one slot
        boolean found = false;
        for (int i = 0; i < Game.maxKeys; i++) {
            if (!getKey(i).equals(key) && !found) {
                // not found yet
                continue;
            } else if (!found) {
                found = true;
            }
            //now that it has been found, start shifting down
            if (found && i < Game.maxKeys - 1) {
                setKey(getKey(i + 1), i);
            } else if (found) {
                // last index, if it has anything in it just delete (would have already been shifted down)
                setKey("", i);
            }

        }
    }

    public boolean hasThisKey(String key) {
        for (int i = 0; i < Game.maxKeys; i++) {
            if (getKey(i).equals(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasKeys() {
        // figures out of the cell (or player) has any keys
        for (int i = 0; i < Game.maxKeys; i++) {

            if (getKey(i) == null) {
                System.out.println("null key!!!");
                continue;
            }
            if (!getKey(i).equals("")) {
                return true;
            }
        }
        return false;
    }

    public void addAllKeys(String[] newKeys) {
        // adds all keys in newKeys to current array
        // assumes no overflow!
        int feki = firstEmptyKeyIndex();
        if (feki >= 0) {
            for (int k = 0; k < Game.maxKeys; k++) {
                if (newKeys[k].equals("")) {
                    // stop adding
                    break;
                }
                addKey(newKeys[k]);
            }
        }
    }

    public int firstEmptyKeyIndex() {
        // returns first empty index, or -1 if no empty spots
        for (int k = 0; k < Game.maxKeys; k++) {
            if (getKey(k).equals("")) {
                return k;
            }
        }
        return -1;
    }

    public int numKeys() {
        int r = 0;
        for (int i = 0; i < Game.maxKeys; i++) {
            if (!getKey(i).equals("")) {
                r++;
            }
        }
        return r;
    }

    public void flipOpenClosed() {
        // flips door open or closed
        if (cellType == 5) {
            //open
            cellType = 4;
        } else if (cellType == 4) {
            //close
            cellType = 5;
            //if there is a player under the door, ENDS GAME!!!
            if (player != null) {
                Game.playerCrushed();
            }
        }
        autoProp(false, false);
    }
}
