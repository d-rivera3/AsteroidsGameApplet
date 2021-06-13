/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroidsgame;

import java.applet.*;       //java applet
import java.awt.*;          //to draw graphics
import java.awt.event.*;    //event handling


public class AsteroidsGame extends Applet implements Runnable, KeyListener{
    
    int x,y, xVelocity, yVelocity;
    Thread thread;
    long startTime, endTime, framePeriod;   //long for milliseconds
    Dimension dim;  //stores size of back buffer
    Image img;      //back buffer object
    Graphics g;     //used to draw on back buffer
    Ship ship;      //ship object
    boolean paused; //game pause
    Shot[] shots;   //array of Shots
    int numShots;   //number of shots limit
    boolean shooting;   //is ship shooting
    Asteroid[] asteroids;
    int numAsteroids;
    double astRadius, minAstVel, maxAstVel;
    int astNumHits, astNumSplit;
    int level;
    
    public void init(){
        resize(500,500);    //applet size
        
        shots = new Shot[41];
        
        numAsteroids = 0;
        level = 0;
        astRadius = 60;
        minAstVel = .5;
        maxAstVel = 5;
        astNumHits = 3;
        astNumSplit = 2;
        
        endTime = 0;
        startTime = 0;
        framePeriod = 25;
        addKeyListener(this);
        dim = getSize();
        img = createImage(dim.width, dim.height);
        g = img.getGraphics();
        thread = new Thread(this);
        thread.start();
    }
    
    public void setUpNextLevel(){
        level++;
        
        ship = new Ship(250,250,0,.35,.98,.1,12);   //create ship
        ship.setActive(true);
        numShots = 0;   //starting with 0 shots
        paused = false;
        shooting = false;   //ship not firing
        
        asteroids = new Asteroid[level * 
                (int)Math.pow(astNumSplit,astNumHits-1) + 1];
        
        numAsteroids = level;
        
        for(int i=0; i<numAsteroids; i++){
            asteroids[i] = new Asteroid(Math.random()*dim.width, 
                    Math.random()*dim.height, astRadius, minAstVel, maxAstVel,
                    astNumHits, astNumSplit);
        }
        
    }
    
    public void paint(Graphics gfx){
        g.setColor(Color.black);  //all done in back buffer g
        g.fillRect(0,0,500,500);  //draw black rect 
        
        for(int i=0; i<numShots; i++) //call draw for all shots
            shots[i].draw(g);
        
        for(int i=0; i<numAsteroids; i++) //call draw for all asteroids
            asteroids[i].draw(g);
        
        ship.draw(g);
            
        //draw level at top left corner
        g.setColor(Color.cyan);
        g.drawString("Level " + level, 20, 20);
        
        gfx.drawImage(img, 0, 0, this); //copies back buffer to screen
    }
    
    public void update(Graphics gfx){
        paint(gfx); //call paint without clearing screen
    }
    
    public void run(){
        for(;;){    //infinite loop till webpage closed
            //mark start time
            startTime = System.currentTimeMillis();
            
            //new level if asteroids destroyed
            if(numAsteroids <= 0)
                setUpNextLevel();
                    
            if(!paused){
                ship.move(dim.width, dim.height);   //move ship
                
                //move shots
                for(int i=0;i<numShots;i++){
                    shots[i].move(dim.width, dim.height);
                    
                    //delete shot if lasted too long and shift array spot to account it
                    if(shots[i].getLifeLeft() <= 0){
                        deleteShot(i);
                        i--;
                    }
                }
                
                //move asteroids and check collisions
                updateAsteroids();
                
                if(shooting && ship.canShoot()){
                    shots[numShots] = ship.shoot();
                    numShots++;
                }
            }
            
            repaint();

            /*  try-catch pause thread execution for 25 minus time to 
                move circle and repaint */

            try{
                //mark end time
                endTime = System.currentTimeMillis();
                //avoid negative sleep time, sleep if framePeriod not 25sec
                if(framePeriod - (endTime - startTime) > 0)
                    Thread.sleep(framePeriod - (endTime - startTime));
            }catch(InterruptedException e){
            }
            
        }//end of infinite loop
    }
    
    public void deleteShot(int index){
        //delete shot and shift other shots up array
        numShots--;
        for(int i = index; i < numShots; i++)
            shots[i] = shots[i+1];
        shots[numShots] = null;
    }
    
    public void deleteAsteroid(int index){
        //delete asteroid and shift others up array
        numAsteroids--;
        for(int i = index; i < numAsteroids; i++)
            asteroids[i] = asteroids[i+1];
        asteroids[numAsteroids] = null;
    }
    
    public void addAsteroids(Asteroid ast){
        asteroids[numAsteroids] = ast;
        numAsteroids++;
    }
    
    public void updateAsteroids(){
        for(int i=0; i<numAsteroids; i++){
            asteroids[i].move(dim.width, dim.height);
            
            if(asteroids[i].shipCollision(ship)){
                level--;
                numAsteroids = 0;
                return;
            }
            
            for(int j=0; j<numShots; j++){
                deleteShot(j);
                
                if(asteroids[i].getHitsLeft() > 1){
                    for(int k=0; k<asteroids[i].getNumSplit(); k++)
                        addAsteroids(asteroids[i].createSplitAsteroid(minAstVel, maxAstVel));
                }
                
                deleteAsteroid(i);
                j = numShots;
                i--;
            }
        }
    }
    
    
    /*  when a key is pressed */
    public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            if(ship.isActive() && !paused)
                ship.setActive(true);
            else{
                paused = !paused;   //enter is pause button
                if(paused)          //grays ship if paused
                    ship.setActive(false);
                else
                    ship.setActive(true);
            }
        }
        else if(paused || !ship.isActive()) //game paused or ship inactive
            return;     //ignore key events except for ENTER
        
        //ship moves 
        else if(e.getKeyCode() == KeyEvent.VK_UP)
            ship.setAccelerating(true);
        else if(e.getKeyCode() == KeyEvent.VK_LEFT)
            ship.setTurningLeft(true);
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            ship.setTurningRight(true);
        
        else if(e.getKeyCode() == KeyEvent.VK_CONTROL)
            shooting = true;

    }
    
    /*  when key is released */
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_UP)
            ship.setAccelerating(false);
        else if(e.getKeyCode() == KeyEvent.VK_LEFT)
            ship.setTurningLeft(false);
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            ship.setTurningRight(false);
        else if(e.getKeyCode() == KeyEvent.VK_CONTROL)
            shooting = false;

    }
    
    // empty method needed to implement keylistener interface
    public void keyTyped(KeyEvent e){
    }
    
    //bottom of game class
}
