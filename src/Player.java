public class Player {
    public int bootNum = 0; // number bootstrapped in total
    public int birthday = 0;
    public int agemod = 0; // age modifier for time travel shinanigins

    // current location
    public int col = 0;
    public int row = 0;
    // proposed move location
    public int mCol = 0;
    public int mRow = 0;
    public boolean willMove = false;

    // time machine coords that this player must fulfill (if not highest bootnum)
    public int burdenCol = 0;
    public int burdenRow = 0;

    //for time travel animations
    public boolean arriving = false;
    public boolean departing = false;

    private String[] keys = new String[Game.maxKeys];
    private String[] burdenKeys = new String[Game.maxKeys];

    public Player() {
        // populates keys (null otherwise...causes problems...)

        for (int k = 0; k < Game.maxKeys; k++) {
            keys[k] = "";
            burdenKeys[k] = "";
        }
    }

    public void resetKeys() {
        // resets all keys
        for (int k = 0; k < Game.maxKeys; k++) {
            setKey("", k);
        }
    }

    public void setKey(String key, int k) {
        keys[k] = key;
    }

    public void setAllKeys(String[] keys) {
        this.keys = keys;
    }

    public String getKey(int k) {
        return keys[k];
    }

    public String[] getKeyArray() {
        return keys;
    }

    public String getAge() {
        // returns age string
        String age = "";
        for (int i = 0; i < bootNum; i++) {
            age += "?";
        }
        if (bootNum > 0) {
            age += " + ";
        }
        age += "" + (Game.currentTimestep + agemod - birthday);
        return age;
    }

    public boolean hasBurdenKeys() {
        // figures out of the player has any keys
        for (int i = 0; i < Game.maxKeys; i++) {
            if (!burdenKeys[i].equals("")) {
                return true;
            }
        }
        return false;
    }

    public void move() {
        // moves
        row = mRow;
        col = mCol;
        willMove = false;
    }

    public String[] getBurdenKeyArray() {
        return burdenKeys;
    }

    public void setAllBurdenKeys(String[] keys) {
        burdenKeys = keys;
    }

    public String listBurdenKeys() {
        String k = "";
        for (int i = 0; i < Game.maxKeys; i++) {
            if (burdenKeys[i].equals("")) {
                break;
            } else if (i > 0) {
                // not first key!
                k += ", " + burdenKeys[i];
            } else {
                // first key
                k += burdenKeys[i];
            }
        }
        return k;
    }

    public String obliText() {
        //returns obligation text
        //should only be called if bootNum > 0!
        String gentext = "";
        if (bootNum == 1) {
            gentext = "primary generation";
        } else {
            gentext = "generation ";
            for (int i = 0; i < bootNum - 1; i++) {
                gentext += "?";
            }
        }
        String obli = "Player of " + gentext + " must travel to timestep " + birthday;
        if (!hasBurdenKeys()) {
            obli += ".";
        } else {
            obli += " holding keys: " + listBurdenKeys() + ".";
        }
        return obli;
    }
}
