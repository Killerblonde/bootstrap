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

    //for time travel animations
    public boolean arriving = false;
    public boolean departing = false;

    private String[] keys = new String[Game.maxKeys];
    private String[] originalKeys = new String[Game.maxKeys];

    public Player() {
        // populates keys (causing problems?)

        for(int k = 0; k < Game.maxKeys; k++) {
            keys[k] = "";
        }
    }

    public void resetKeys() {
        // resets all keys
        for(int k = 0; k < Game.maxKeys; k++) {
            setKey("",k);
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
        if(bootNum > 0) {
            age += " + ";
        }
        age += "" + (Game.currentTimestep + agemod - birthday);
        return age;
    }

    public boolean hasOriginalKeys() {
        // figures out of the player has any keys
        for(int i = 0; i < Game.maxKeys; i++) {
            if(!originalKeys[i].equals("")) {
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

    public String[] getOriginalKeys() {
        return originalKeys;
    }

    public void setAllOriginalKeys(String[] keys) {
        originalKeys = keys;
    }

    public String listOriginalKeys() {
        String k = "";
        for(int i = 0; i < Game.maxKeys; i++) {
            if(originalKeys[i].equals("")) {
                break;
            } else if (i > 0) {
                // not first key!
                k += ", " + originalKeys[i];
            } else {
                // first key
                k += originalKeys[i];
            }
        }
        return k;
    }

    public String obliText() {
        //returns obligation text
        String gentext = "";
        if(bootNum == 1) {
            gentext = "primary generation";
        } else {
            gentext = "generation ";
            for(int i = 0; i < bootNum; i++) {
                gentext += "?";
            }
        }
        String obli = "Player of " + gentext + " must travel to timestep " + birthday;
        if(!hasOriginalKeys()) {
            obli += ".";
        } else {
            obli += " holding keys: " + listOriginalKeys() + ".";
        }
        return obli;
    }
}
