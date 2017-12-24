import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.awt.TextRenderer;

import java.awt.Font;

public class Eventlistener implements GLEventListener {

    public static GL2 gl = null;
    private double rot = 0;
    private double rad = 0;
    private double moveGhost = 0; // move ghosting, from 0 to 1. Always incrementing
    private double ttRad = 0; // time travel radius modifier

    public static boolean ttAnimation = false;
    public static boolean ttAnimationBegun = false;

    public static boolean moveAnimation = false; // will do one opaque move animation if true
    public static boolean moveAnimationBegun = false; // start from beginning

    public static boolean pauseRendering = false;

    public static boolean introAnimation = false;
    public static boolean introAnimationBegun = false;

    public static boolean outroAnimation = false;
    public static boolean outroAnimationBegun = false;

    // after display finishes outro animation, should finish the loading process in Game method
    public static boolean finishLoad = false;


    @Override
    public void display(GLAutoDrawable drawable) {

        // will pause rendering when game is loading a new level or something like that
        if (!pauseRendering) {

            // must prepare for intro animation if this is the case
            if (introAnimation && !introAnimationBegun) {
                rad = 0;
                rot = 0;
                introAnimationBegun = true;
            }
            if (outroAnimation && !outroAnimationBegun) {
                rad = 1;
                rot = 0;
                outroAnimationBegun = true;
            }
            if (moveAnimation && !moveAnimationBegun) {
                //resets moveGhost
                moveGhost = 0;
                moveAnimationBegun = true;
                //stops clikcing
                Mouseinput.pauseMouse = true;
            }
            if (ttAnimation && !ttAnimationBegun) {
                //resets
                ttRad = 0;
                ttAnimationBegun = true;
                //stops clikcing
                Mouseinput.pauseMouse = true;
            }

            gl = drawable.getGL().getGL2();
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

            gl.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();

            float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);

            //shifts slightly to account for hexagon sizing
            gl.glOrtho(-Renderer.unitsWide / 2 + Renderer.centerOffX, Renderer.unitsWide / 2 + Renderer.centerOffX,
                    -unitsTall / 2 + Renderer.centerOffY, unitsTall / 2 + Renderer.centerOffY, -1, 1);
            gl.glMatrixMode(GL2.GL_MODELVIEW);


            int a = 0;
            int b = 0;

            // will draw hexagons as far as you can zoom, (taking maximum dimension)
            int totalSize = (int) Math.max(Renderer.maxUnitsWide, Renderer.getUnitsTall(Renderer.maxUnitsWide));

            for (int i = -totalSize; i < totalSize; i++) {
                for (int j = -totalSize; j < totalSize; j++) {
                    // parity for grid offset
                    if (i % 2 == 0) {
                        a = 1;
                    } else {
                        a = 0;
                    }
                    // adds colored cell if it can, otherwise sets it to black
                    float[] RGBA = new float[4];
                    String label = null;
                    String key = null;
                    String age = null;
                    boolean drawOld = false;
                    Cell c = null;
                    if (Game.hasCell(i, j)) {
                        c = Game.getCell(i, j);
                        RGBA = c.getRGBA(false);
                        if (c.labelled && !introAnimation && !outroAnimation) {
                            label = c.label;
                        }
                        if (c.hasKeys() && !introAnimation && !outroAnimation) {
                            for (int k = 0; k < Game.maxKeys; k++) {
                                if (k == 0) {
                                    key = "";
                                }
                                if (k > 0 && !c.getKey(k).equals("")) {
                                    key += ",";
                                }
                                key += c.getKey(k);
                            }
                        }
                        if (c.player != null && !introAnimation && !outroAnimation
                                && Game.gameMode == 0) {
                            age = c.player.getAge(); // does not show in editor
                            // if it gets this far, also checks for ghost
                            if (c.hasMovingPlayer()) {
                                // oh boy! draws a spooky ghost.
                                double mi = c.player.mRow;
                                double mj = c.player.mCol;
                                if (mi % 2 == 0) {
                                    b = 1;
                                } else {
                                    b = 0;
                                }
                                // sets alpha to new thing really quick if not doing moveAnimation
                                if (!moveAnimation) {
                                    RGBA[3] = (float) (1 - moveGhost);
                                    Graphics.drawCells(((2 * j + a) * (1 - moveGhost)) + ((2 * mj + b) * (moveGhost)),
                                            ((1.75 * i) * (1 - moveGhost)) + ((1.75 * mi) * (moveGhost)), rad, rot, RGBA,
                                            null, null, null);
                                } else {
                                    // does same but with age and key (if holding) - still no label, that's old cell
                                    // player cannot leave key behind, automatically holding
                                    RGBA[3] = 1;
                                    Graphics.drawCells(((2 * j + a) * (1 - moveGhost)) + ((2 * mj + b) * (moveGhost)),
                                            ((1.75 * i) * (1 - moveGhost)) + ((1.75 * mi) * (moveGhost)), rad, rot, RGBA,
                                            null, key, age);
                                    drawOld = true; // draws old cell where player used to be
                                }
                                // sets alpha back
                                RGBA[3] = c.getRGBA(false)[3];
                            }
                        }

                        // time travel animation
                        if(c.player != null) {
                            if(c.player.arriving) {
                                // draws player growing in radius
                                Graphics.drawCells(2 * j + a, 1.75 * i, rad*ttRad, rot, RGBA, null, null, null);
                                drawOld = true;
                            } else if(c.player.departing) {
                                //draws player shrinking in radius
                                Graphics.drawCells(2 * j + a, 1.75 * i, rad*(1-ttRad), rot, RGBA, null, null, null);
                                drawOld = true;
                            }
                        }

                    } else {
                        RGBA[0] = 0;
                        RGBA[1] = 0;
                        RGBA[2] = 0;
                        RGBA[3] = 1;
                    }
                    //if this was a player moving or time travelling, does not render where the player was
                    if (drawOld) {
                        //draws empty tile in the player's place
                        //assumes c has been defined above
                        //player cannot leave keys behind in old cell, so removes
                        RGBA = c.getRGBA(true);
                        Graphics.drawCells(2 * j + a, 1.75 * i, rad, rot, RGBA, label, null, null);
                    } else {
                        Graphics.drawCells(2 * j + a, 1.75 * i, rad, rot, RGBA, label, key, age);
                    }
                }
            }


            // intro animation
            if (rad <= 0.98 && introAnimation) {
                rot += (double) 360 / 50; //360 degrees divided by 50 radius increases of size 0.02 up to 1
                rad += 0.02;
            } else if (rad > 0 && outroAnimation) {
                rot -= (double) 360 / 50;
                rad -= 0.02;
            } else {
                rot = 0;
                rad = 1;
                // resets booleans for next animation
                introAnimation = false;
                introAnimationBegun = false;

                if (finishLoad) {
                    // this means it just completed its outro animation
                    // now can finish loading new level
                    // THIS MOST LIKELY HAS BUGS OR AT LEAST INCOMPATIBLE WITH FUTURE FEATURES!!!
                    // the only reason this goes here is because I want the new level to be rendered only after
                    // the animation here has finished, and only eventlistener has the thread to know when this is done
                    Game.cells.clear();
                    Game.cells.addAll(Game.newCells);
                    Game.newCells.clear();
                    //sets up renderer again
                    Renderer.setUnitsWide(Math.max(3 * Game.gridRows, 3 * Game.gridCols));
                    Renderer.setWindowTitle();
                    //does intro animation
                    introAnimation = true;
                    introAnimationBegun = false;
                    finishLoad = false;
                }

                outroAnimation = false;
                outroAnimationBegun = false;
            }

            // now renders the top and bottom text
            TextRenderer botRend = new TextRenderer(new Font("Ariel", Font.BOLD, 18));
            botRend.beginRendering(Renderer.getWindowWidth(), Renderer.getWindowHeight());
            botRend.setColor(1, 1, 1, 0.8f);
            botRend.draw(Game.getBotText(), 5, 7);
            botRend.draw("ESC for Menu", 5, Renderer.getWindowHeight() - 15);
            botRend.endRendering();


            //move ghost

            if (moveGhost < 1) {
                moveGhost += 0.02;
            } else {
                moveGhost = 0;
                // finished with animation
                // this also finishes the timestep advance if any players needed to move
                if (moveAnimation) {
                    moveAnimation = false;
                    moveAnimationBegun = false;
                    // has game class finalize motions
                    Game.finishTimestep();
                }
                //lets user left click again
                Mouseinput.pauseMouse = false;
            }

            // time travel animation
            if(ttRad < 1) {
                ttRad += 0.02;
            } else {
                ttRad = 0;
                // finished animation
                ttAnimation = false;
                ttAnimationBegun = false;
                // tells game that arrivals are done
                // first removes time traveller if there was a departure
                Game.removeDeparted();
                Game.noTimeTravellers();
                Mouseinput.pauseMouse = false;
            }

        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClearColor(0, 0, 0, 1);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        float unitsTall = Renderer.getUnitsTall(Renderer.unitsWide);

        gl.glOrtho(-Renderer.unitsWide / 2, Renderer.unitsWide / 2, -unitsTall / 2, unitsTall / 2, -1, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }
}
