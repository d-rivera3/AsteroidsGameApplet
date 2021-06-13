/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroidsgame;

import java.awt.*;

public class Asteroid {
    double x, y, xVelocity, yVelocity, radius;
    int hitsLeft, numSplit;
    
    public Asteroid(double x, double y, double radius, double minVelocity,
            double maxVelocity, int hitsLeft, int numSplit){
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.hitsLeft = hitsLeft;
        this.numSplit = numSplit;
        
        double vel = minVelocity + Math.random() * (maxVelocity - minVelocity);
        double dir = 2 * Math.PI * Math.random();
        
        xVelocity = vel * Math.cos(dir);
        yVelocity = vel * Math.sin(dir);
    }
    
    public void move(int scrnWidth, int scrnHeight){
        x += xVelocity;
        y += yVelocity;
        
        if(x < 0 - radius)
            x += scrnWidth + 2 * radius;
        else if(x > scrnWidth + radius)
            x -= scrnWidth + 2 *radius;
        if(y < 0 - radius)
            y += scrnHeight + 2 *radius;
        else if(y > scrnHeight + 2 *radius)
            y -= scrnHeight + 2 *radius;
                    
    }
    
    public void draw(Graphics g){
        g.setColor(Color.MAGENTA);
        g.fillOval((int)(x-radius+.5), (int)(y-radius+.5), (int)(2*radius), 
                (int)(2*radius));
    }
    
    public int getHitsLeft(){
        return hitsLeft;
    }
    
    public int getNumSplit(){
        return numSplit;
    }
    
    public boolean shipCollision(Ship ship){
        if(Math.pow(radius+ship.getRadius(),2) >  Math.pow(ship.getX()-x,2) + 
                Math.pow(ship.getY()-y,2) && ship.isActive())
            return true;
        return false;
    }
    
    public boolean shotCollision(Shot shot){
        if(Math.pow(radius,2) >  Math.pow(shot.getX()-x,2) + 
                Math.pow(shot.getY()-y,2))
            return true;
        return false;
    }
    
    public Asteroid createSplitAsteroid(double minVelocity, double maxVelocity){
        return new Asteroid(x, y, radius/Math.sqrt(numSplit), minVelocity,
                maxVelocity, hitsLeft-1, numSplit);
    }
}
