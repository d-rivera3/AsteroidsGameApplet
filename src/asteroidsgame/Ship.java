/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroidsgame;

import java.awt.*;
import java.util.Arrays;

public class Ship {
    
    //shape of ship and thruster
    final double[] origXPts = {14,-10,-6,-10}, origYPts = {0,-8,0,8}, 
        origFlameXPts = {-6,-23,-6}, origFlameYPts = {-3,0,3};
    
    //radius of circle to approx ship
    final int radius = 6;
    
    //movement variables
    double x, y, angle, xVelocity, yVelocity, acceleration,
            velocityDecay, rotationalSpeed;      
    boolean turningLeft, turningRight, accelerating, active;
    
    //store current locations of points used to draw ship and thrust
    int[] xPts, yPts, flameXPts, flameYPts;
    //determine rate of firing
    int shotDelay, shotDelayLeft;
    
    public Ship(double x, double y, double angle, double acceleration, 
            double velocityDecay, double rotationalSpeed, int shotDelay){
        //this = ship object, setting parameters to ship vars
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.acceleration = acceleration;
        this.velocityDecay = velocityDecay;
        this.rotationalSpeed = rotationalSpeed;
        
        xVelocity = 0;  //not moving
        yVelocity = 0;
        turningLeft = false;    //not turning
        turningRight = false;
        accelerating = false;   //not accelerating
        active = false; //start off paused
        xPts = new int[4];  //alloc space for arrays
        yPts = new int[4];
        flameXPts = new int[3];
        flameYPts = new int[3];
        this.shotDelay = shotDelay; //# of frames between steps
        shotDelay = 0;  //shot ready
    }
    
    public void draw(Graphics g){
        if(accelerating && active){ //draw flame if accelerating
            for(int i=0; i<3;i++){
                flameXPts[i] = Math.abs((int)(origFlameXPts[i]*Math.cos(angle) -
                        origFlameYPts[i]*Math.sin(angle) + x + .5))%575;
                flameYPts[i] = Math.abs((int) (origFlameXPts[i]*Math.sin(angle) +
                        origFlameYPts[i]*Math.cos(angle) + y + .5))%575;
            }
            g.setColor(Color.red);  //flame color
            g.fillPolygon(flameXPts,flameYPts,3); //3 is # of points
        }
        
        //calc polygon for ship
        for(int i=0; i<4;i++){
            xPts[i] = Math.abs((int) (origXPts[i]*Math.cos(angle)-origYPts[i]*Math.sin(angle) + x + .5))%575;
            yPts[i] = Math.abs((int) (origXPts[i]*Math.sin(angle)+origYPts[i]*Math.cos(angle) + y + .5))%575;
        }
        
        //ship color
        if(active)
            g.setColor(Color.GREEN);  //running game ship color
        else
            g.setColor(Color.PINK); //paused game ship color 
        
        //draw ship
        g.fillPolygon(xPts,yPts,4); //4 is # of points
    }
    
    //move is called every frame
    public void move(int scrnWidth, int scrnHeight){
        if(shotDelay>0)
            shotDelay--;
        if(turningLeft)
            angle -= rotationalSpeed;
        if(turningRight)
            angle += rotationalSpeed;
        if(angle > (2*Math.PI))
            angle -= (2*Math.PI);
        else if(angle < (2*Math.PI))
            angle += (2*Math.PI);
        
        //compute accel to velocity
        if(accelerating){
            xVelocity += acceleration * Math.cos(angle);
            yVelocity += acceleration * Math.sin(angle);
        }
        
        //move ship by adding velocity to position
        x+= xVelocity;
        y+= yVelocity;
        xVelocity*= velocityDecay;  //decay slow ship by percentages
        yVelocity*= velocityDecay;
        
        //wrap ship around left<->right screen
        if(x<0)
            x += scrnWidth;
        else if(x > scrnWidth);
            x -= scrnWidth;
        
        //wrap ship around top<->bottom screen
        if(y<0)
            y += scrnHeight;
        else if(y > scrnHeight);
            y -= scrnHeight;
        
    }
    
    public void setAccelerating(boolean accelerating){
        this.accelerating = accelerating;   //start or stop accel of ship
    }
    
    /*  start/stop turning ship in either direction */
    public void setTurningLeft(boolean turningLeft){
        this.turningLeft = turningLeft;
    }
    
    public void setTurningRight(boolean turningRight){
        this.turningRight = turningRight;
    }
    
    /*  get x, y of ship's location */
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    //get circle radius to approx ship location
    public double getRadius(){
        return radius;
    }
    
    //set if game is/not paused
    public void setActive(boolean active){
        this.active = active;
    }
    
    //game is/not paused
    public boolean isActive(){
        return active;
    }
    
    //checks to see if ship ready to shoot or wait more
    public boolean canShoot(){
        if(shotDelayLeft > 0)  
            return false;
        else
            return true;
    }
    
    public Shot shoot(){
        shotDelayLeft = shotDelay;  //fire rate ready
        return new Shot(x,y,angle,xVelocity,yVelocity,500);
    }
    
    
}
