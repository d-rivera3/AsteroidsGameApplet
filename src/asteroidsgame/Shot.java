/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroidsgame;

import java.awt.*;

public class Shot {
    
    final double shotSpeed = 12; //pixels per frame
    double x,y,xVelocity,yVelocity; //movement vars
    int lifeLeft;   //to disaappear shot
    
    public Shot(double x, double y, double angle, double shipXVel, 
            double shipYVel, int lifeLeft){
        //shot position
        this.x = x;
        this.y = y;
        
        //add ship velocity to shot velocity (relativity)
        xVelocity = shotSpeed * Math.cos(angle) + shipXVel;
        yVelocity = shotSpeed * Math.sin(angle) + shipYVel;
        
        this.lifeLeft = lifeLeft;   //remaining shot frames on screen
    }
    
    public void move(int scrnWidth, int scrnHeight){
        lifeLeft--; //used to determine shot to disappear
        
        x+= xVelocity;  //move shot by x/y
        y+= yVelocity;
        
        //wrap shot around by horizontal/vertical scrn limits
        if(x<0)
            x+= scrnWidth;
        else if(x>scrnWidth)
            x-= scrnWidth;
        if(y<0)
            y+= scrnHeight;
        else if(y>scrnHeight)
            y-= scrnHeight;
        
    }
    
    public void draw(Graphics g){
        g.setColor(Color.YELLOW);   //shot color 
        g.fillOval((int)(x-0.5), (int)(y-0.5), 5,5);    //shot size&shape
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public int getLifeLeft(){
        return lifeLeft;
    }
}
