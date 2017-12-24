import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

import javax.swing.*;

public class Keyinput implements KeyListener {


    @Override
    public void keyPressed(KeyEvent e) {
        // depends on game mode for what keys do
        switch (e.getKeyCode()) {
            case 27:
                //escape key
                //opens escape menu

                switch (Game.gameMode) {
                    case 0:
                        //escape menu for normal play
                        String[] opts = {"Restart Level",
                                "Load New Level",
                                "Edit This Level",
                                "How to Play"};
                        int a = JOptionPane.showOptionDialog(null,
                                "DO NOT SCROLL MOUSE IN THIS MENU! WILL CRASH GAME! Hit ESC again to close.",
                                "Boostrap",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                opts,
                                opts[0]);
                        switch (a) {
                            case 0:
                                // restart level (loads current)
                                Game.loadLevel(true);
                                break;
                            case 1:
                                // load new level
                                Game.loadLevel(false);
                                break;
                            case 2:
                                // edit this level
                                Game.gameMode = 1;
                                Game.loadLevel(true);
                                break;
                            case 3:
                                // show help
                                Game.showGameHelp();
                                break;
                            default:
                                break;
                        }
                        break;
                    case 1:
                        //escape menu for level editor
                        String[] opts2 = {"Save and Play Level",
                                "Save Level",
                                "Rename",
                                "Load Level to Edit",
                                "How to Use Editor"};
                        int b = JOptionPane.showOptionDialog(null,
                                "DO NOT SCROLL MOUSE IN THIS MENU! WILL CRASH GAME! Hit ESC again to close.",
                                "Boostrap Level Editor",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                opts2,
                                opts2[0]);
                        switch (b) {
                            case 0:
                                // save and play
                                Game.newCellsFromString(Game.saveLevel());
                                Game.gameMode = 0;
                                Game.loadLevel(true);
                                break;
                            case 1:
                                // save
                                Game.saveLevel();
                                break;
                            case 2:
                                // rename
                                Game.levelName = Game.askLevelName();
                                Renderer.setWindowTitle();
                                break;
                            case 3:
                                // load to edit
                                // if it doesn't work, doesn't matter - just returns user to editing
                                Game.loadLevel(false);
                                break;
                            case 4:
                                // show help
                                Game.showEditorHelp();
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }


                break;
            case 87:
                //W key
                switch (Game.gameMode) {
                    case 0:
                        break;
                    case 1:
                        // wall
                        Game.makeCellType = 1;
                        break;
                    default:
                        break;
                }
                break;
            case 69:
                //E key
                switch (Game.gameMode) {
                    case 0:
                        break;
                    case 1:
                        // empty space
                        Game.makeCellType = 0;
                        break;
                    default:
                        break;
                }
                break;
            case 66:
                //B key
                switch (Game.gameMode) {
                    case 0:
                        break;
                    case 1:
                        // button
                        Game.makeCellType = 2;
                        break;
                    default:
                        break;
                }
                break;
            case 80:
                //P key
                switch (Game.gameMode) {
                    case 0:
                        break;
                    case 1:
                        // player
                        Game.makeCellType = 3;
                        break;
                    default:
                        break;
                }
                break;
            case 79:
                //O key
                switch (Game.gameMode) {
                    case 0:
                        break;
                    case 1:
                        // open door
                        Game.makeCellType = 4;
                        break;
                    default:
                        break;
                }
                break;
            case 67:
                //C key
                switch (Game.gameMode) {
                    case 0:
                        break;
                    case 1:
                        // closed door
                        Game.makeCellType = 5;
                        break;
                    default:
                        break;
                }
                break;
            case 76:
                //L key
                switch (Game.gameMode) {
                    case 0:
                        break;
                    case 1:
                        // closed door
                        Game.makeCellType = 6;
                        break;
                    default:
                        break;
                }
                break;
            case 75:
                //K key
                switch (Game.gameMode) {
                    case 0:
                        break;
                    case 1:
                        // gives key (must be empty cell or player for this to do anything)
                        Game.makeCellType = 7;
                        break;
                    default:
                        break;
                }
                break;
            case 84:
                //T key
                switch (Game.gameMode) {
                    case 0:
                        break;
                    case 1:
                        // time machine!
                        Game.makeCellType = 8;
                        break;
                    default:
                        break;
                }
                break;
            case 88:
                //X key
                switch (Game.gameMode) {
                    case 0:
                        break;
                    case 1:
                        // exit
                        Game.makeCellType = 9;
                        break;
                    default:
                        break;
                }
                break;
            case 73:
                //I key
                switch (Game.gameMode) {
                    case 0:
                        // interact
                        Game.command = 1;
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
                break;
            case 77:
                //M key
                switch (Game.gameMode) {
                    case 0:
                        // move
                        Game.command = 0;
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
                break;
            case 17:
                //CTRL key
                switch (Game.gameMode) {
                    case 0:
                        // switch move and interact command
                        Game.command = 1 - Game.command; //huehuehuehue
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
                break;
            case 32:
                //SPACE key
                switch (Game.gameMode) {
                    case 0:
                        // advance timestep (or at least tries to)
                        Game.advanceTimestep();
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
                break;
            default:
                //other key
                break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
