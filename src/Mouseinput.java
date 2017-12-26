import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;

import javax.swing.*;

public class Mouseinput implements MouseListener {

    private int x = 0;
    private int y = 0;

    public static boolean pauseMouse = false;

    @Override
    public void mouseClicked(MouseEvent e) {

        // left click to select cells
        if (e.getButton() == 1 && !pauseMouse) {
            // PAUSES MOUSE TO AVOID DOUBLE CLICKING NONSENSE
            pauseMouse = true;
            // selects given cell, if possible
            // first finds what unit we are in
            float unitsWide = Renderer.unitsWide;
            float unitsTall = Renderer.getUnitsTall(Renderer.unitsWide);
            // computes which unit we are in based on position of mouse relative
            // to screen size
            // also adjusts for offset
            // "from center" is from true center

            float clickUnitXfromCenter = unitsWide * ((float) e.getX() / Renderer.getWindowWidth() - 0.5f)
                    + Renderer.centerOffX;
            float clickUnitYfromCenter = unitsTall * ((float) e.getY() / Renderer.getWindowHeight() - 0.5f)
                    - Renderer.centerOffY;

            // now figures out which hexagon that is, shifts a bit
            // "parity" determined by Y

            float a = 0;
            // must flip the Y (around -1...it works...)
            // this is just because the way the hexagons are drawn vs "indexed"
            int clickHexRow = -1 - Math.round((clickUnitYfromCenter - 1.75f) / 1.75f);
            if (clickHexRow % 2 != 0) {
                // must shift
                a = 1f;
            }
            int clickHexCol = Math.round((clickUnitXfromCenter - 1 + a) / 2);

            // checks to see if in bounds
            if (-Game.gridRows / 2 <= clickHexRow && clickHexRow < (float) Game.gridRows / 2
                    && -Game.gridCols / 2 <= clickHexCol && clickHexCol < (float) Game.gridCols / 2) {
                // depending on gamemode, highlights cell and does action, or just changes cell
                switch (Game.gameMode) {
                    case 0:
                        Cell targ = Game.getCell(clickHexRow, clickHexCol);
                        // depending on command, will try to move or interact
                        boolean noSelect = false;
                        Cell s = Game.selectedCell;
                        switch (Game.command) {
                            case 0:
                                //move
                                // checks to see if cell is passable and no movement is planned
                                // also if the selected player can actually move there! (must be adjacent)
                                // finally, makes sure not too many keys coming together
                                if (s == null) {
                                    // no selected cell yet, don't try any movements!
                                    break;
                                }
                                if (targ.getPassable() && !targ.hasMovePlanned
                                        && Game.checkAdjacent(targ, s) && s.player != null) {
                                    // possible move!
                                    // checks for too many keys
                                    if (s.numKeys() + targ.numKeys() > Game.maxKeys) {
                                        // too many!
                                        JOptionPane.showMessageDialog(null,
                                                "Too many keys in one spot!",
                                                "Cannot move!",
                                                JOptionPane.WARNING_MESSAGE);
                                        // will not select new cell, in fact, deselect
                                        Game.deselect();
                                        noSelect = true;
                                    } else {
                                        // finds cell that it was going to move to, and unsets moveplanned
                                        if(s.player.willMove) {
                                            Game.getCell(s.player.mRow, s.player.mCol).hasMovePlanned = false;
                                        }
                                        s.player.willMove = true;
                                        s.player.mCol = targ.col;
                                        s.player.mRow = targ.row;
                                        targ.hasMovePlanned = true;
                                        // will not select new cell, in fact, deselect
                                        Game.deselect();
                                        noSelect = true;
                                    }
                                } else if (s.equals(targ) && s.player != null) {
                                    // clicked same cell to move, cancels and deselects
                                    if(s.player.willMove) {
                                        Game.getCell(s.player.mRow, s.player.mCol).hasMovePlanned = false;
                                    }
                                    // doesn't matter if mCol and mRow are still set to something lol
                                    s.player.willMove = false;
                                }
                                break;
                            case 1:
                                //interact
                                // depending on what player clicks on, may be trying to give/use key or use time machine
                                // empty space/other player = give key, locked door = open door, time machine = use
                                // only ambiguity is if another player is standing on a time machine, but in this case
                                // it is impossible to use the time machine, so in fact there is none
                                // first checks to see if trying to interact with adjacent empty cell or player

                                if (targ.getType() == 8 && targ.player == null) {
                                    // trying to summon (only thing possible)
                                    // opens dialogue to ask user what to summon
                                    Eventlistener.pauseRendering = true;
                                    int bootstrapChoice = JOptionPane.showConfirmDialog(
                                            null,
                                            "Bootstrap player from future?",
                                            "Time Machine",
                                            JOptionPane.YES_NO_OPTION);
                                    if (bootstrapChoice == 0) {
                                        // adds new player
                                        // animates
                                        Eventlistener.ttAnimation = true;
                                        Player p = new Player();
                                        p.bootNum = Game.highestBootnum + 1;
                                        p.birthday = Game.currentTimestep;
                                        p.arriving = true;
                                        p.col = targ.col;
                                        p.row = targ.row;
                                        Game.players.add(p);
                                        targ.player = p;
                                        // asks if the bootstrapped player should have any keys
                                        int addedKeys = 0;
                                        String messageText1 = "Bootstrap with key? (No commas or spaces!)";
                                        String messageText2 = "Bootstrap with another key?";
                                        while (addedKeys < Game.maxKeys) {
                                            String messageText = "";
                                            if (addedKeys == 0) {
                                                messageText = messageText1;
                                            } else {
                                                messageText = messageText2;
                                            }
                                            String proposedKey = JOptionPane.showInputDialog(messageText);
                                            if (proposedKey == null) {
                                                //breaks out of loop
                                                break;
                                            } else if (proposedKey.equals("")) {
                                                //also breaks
                                                break;
                                            }
                                            //checks for commas
                                            if (proposedKey.contains(",") || proposedKey.contains(" ")) {
                                                JOptionPane.showMessageDialog(null, "Key label cannot have commas " +
                                                                "or spaces!",
                                                        "Cannot bootstrap with proposed key.",
                                                        JOptionPane.WARNING_MESSAGE);
                                            } else {
                                                targ.addKey(proposedKey);
                                                addedKeys++;
                                            }
                                        }
                                        //sets original keys
                                        p.setAllBurdenKeys(targ.getKeyArray().clone());
                                        //emburdens highest bootnum player
                                        Game.emburdenHighest(targ.row,targ.col,targ.getKeyArray().clone());
                                        Game.highestBootnum++; //now advances bootnum
                                        //unpauses renderer and does time travel animation
                                        Game.deselect();
                                        noSelect = true;
                                    }
                                    Eventlistener.pauseRendering = false;
                                } else if (s != null) {
                                    // checks adjacency and ability to receive key
                                    if (s.player != null && Game.checkAdjacent(s, targ) &&
                                            (targ.canHaveKey || targ.player != null || targ.getType() == 6)) {
                                        // must be player adjacent to selection
                                        // either target cell has a player on it, can have a key, or is a locked door
                                        // also checks if there is one to give!
                                        if (targ.getType() != 6) {
                                            // trying to give key
                                            if (targ.numKeys() < Game.maxKeys && s.hasKeys()) {
                                                // can receive and has one to give!
                                                // asks player which key he wants to give
                                                String[] keysToGive = s.getShortKeyArray();
                                                String giveKey = (String) JOptionPane.showInputDialog(
                                                        null,
                                                        "Which key would you like to give?",
                                                        "Give Key",
                                                        JOptionPane.PLAIN_MESSAGE,
                                                        null,
                                                        keysToGive,
                                                        keysToGive[0]);
                                                if (giveKey != null) {
                                                    // user made a choice
                                                    targ.addKey(giveKey);
                                                    s.removeKey(giveKey);
                                                }
                                                noSelect = true;
                                            }
                                        } else {
                                            // trying to unlock door
                                            if (s.hasThisKey(targ.label)) {
                                                // unlocks door! (sets to open door)
                                                Game.setCell(targ, 4);
                                                noSelect = true;
                                            } else {
                                                JOptionPane.showMessageDialog(null,
                                                        "You need the key: " + targ.label,
                                                        "Cannot unlock!",
                                                        JOptionPane.WARNING_MESSAGE);
                                                noSelect = true;
                                            }

                                        }
                                    } else if (s.equals(targ) && s.getType() == 8) {
                                        // only other action possible is a player clicking on themself on a time machine
                                        // this is to fulfill a bootstrap
                                        // checks to make sure there is actually a bootstrap to fulfil
                                        if (Game.checkOpenBootstraps()) {
                                            // checks to see if this player is capable of closing a bootstrap
                                            // must be of correct generation and have all require keys
                                            // also, cannot have any extraneous keys!
                                            // if this fails, the called method will tell user why
                                            if (Game.checkCapableFulfill(s.player)) {
                                                // asks if the user is sure they want to close a bootstrap
                                                Eventlistener.pauseRendering = true;
                                                int bootstrapChoice = JOptionPane.showConfirmDialog(
                                                        null,
                                                        "Send this player back in time to close bootstrap?",
                                                        "Time Machine",
                                                        JOptionPane.YES_NO_OPTION);
                                                if (bootstrapChoice == 0) {
                                                    // fulfills bootstrap!
                                                    s.player.departing = true;
                                                    Eventlistener.ttAnimation = true;
                                                    Game.deselect();
                                                    noSelect = true;
                                                }
                                                Eventlistener.pauseRendering = false;
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(null,
                                                    "No open bootstraps! Vacate cell to bootstrap.",
                                                    "Time Machine",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                        }


                                        Game.deselect();
                                        noSelect = true;
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                        // selects new cell
                        if (!noSelect) {
                            Game.selectCell(clickHexRow, clickHexCol);
                        }
                        break;
                    case 1:
                        // will set a new cell type
                        // if this is setting a unique cell, sets old to empty space
                        int t = Game.makeCellType;
                        if (Cell.isUnique(t)) {
                            // sets to empty space the (hopefully unique) last cell of this type
                            Game.clearCellOfType(t);
                        }
                        // if trying to give a key (t = 7) make sure this is possible (only way this can fail)
                        Cell c = Game.getCell(clickHexRow, clickHexCol);
                        if (t != 7 || c.canHaveKey) {
                            Game.setCell(c, t);
                        }
                        break;
                    case 2:
                        Game.selectCell(clickHexRow, clickHexCol);
                        break;
                    default:
                        break;
                }


            } else {
                // deselects
                Game.deselect();
            }
            //unpauses mouse
            pauseMouse = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (e.getButton() == 3) {
            // holding down right click to drag screen
            // speed is a function of what the zoom is at
            float speed = Renderer.unitsWide / 1375;
            // check bounds
            float newOffX = Renderer.centerOffX + speed * (x - e.getX());
            float newOffY = Renderer.centerOffY + speed * (e.getY() - y);
            // checks to see if in bounds, or at least the change is in the
            // right direction

            if (Math.abs(newOffX) < Renderer.maxOffX
                    || (Renderer.maxOffX - newOffX) * Math.signum((speed * (x - e.getX()))) > 0) {
                // in bounds OR travelling back towards bounds
                Renderer.centerOffX = newOffX;
            }
            if (Math.abs(newOffY) < Renderer.maxOffY
                    || (Renderer.maxOffY - newOffY) * Math.signum((speed * (y - e.getY()))) < 0) {
                // in bounds OR travelling back towards bounds
                Renderer.centerOffY = newOffY;
            }

        }
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {

    }

    @Override
    public void mouseExited(MouseEvent arg0) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // update coordinates
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {

    }

    @Override
    public void mouseWheelMoved(MouseEvent e) {

        float r = Renderer.unitsWide;
        if (e.getRotation()[1] > 0) {
            Renderer.unitsWide = Math.min(Renderer.maxUnitsWide, r + 5);
        } else {
            Renderer.unitsWide = Math.max(Renderer.minUnitsWide, r - 5);
        }
        // updates bounds too
        getBounds();


    }

    public static void getBounds() {

        float unitsWide = Renderer.unitsWide;
        float unitsTall = Renderer.getUnitsTall(Renderer.unitsWide);

        // updates bounds based on screen size
        // scale by 2 for size of hexagon in X, 1.9 for size of hexagon in Y
        // plus slight fudge to see edges

        Renderer.maxOffX = Math.max(0, (float) (Game.gridRows - unitsWide / 2) + 2);
        Renderer.maxOffY = (float) Math.max(0, (float) (Game.gridCols - unitsTall / 1.75f) + 2);
    }

}
