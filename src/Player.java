public class Player {
    public int bootNum = 0; // number bootstrapped in total
    public int birthday = 0;

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
        age += "" + (Game.currentTimestep - birthday);
        return age;
    }

    public boolean hasKeys() {
        // figures out of the player has any keys
        for(int i = 0; i < Game.maxKeys; i++) {
            if(!keys[i].equals("")) {
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
}
