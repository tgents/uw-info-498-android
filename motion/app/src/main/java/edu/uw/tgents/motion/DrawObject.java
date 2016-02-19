package edu.uw.tgents.motion;

/**
 * Created by tgents on 2/17/2016.
 * Class for objects being drawn
 */
public class DrawObject {
    public int x;
    public int y;
    public int radius;

    public DrawObject(){
        this.x = 0;
        this.y = 0;
        this.radius = 10;
    }

    public DrawObject(int xpos, int ypos, int r){
        this.x = xpos;
        this.y = ypos;
        this.radius = r;
    }
}
