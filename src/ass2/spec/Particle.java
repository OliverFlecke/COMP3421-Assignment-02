package ass2.spec;

import java.util.Random;

/**
 * A class representing a particle, which simulates rain
 * @author Oliver Fleckenstein
 */
public class Particle
{
    public static final String textureFileName = "src/textures/rain/white.png";
    public static final String textureExt = ".png";
    public static Texture texture;

    private static Random rand = new Random();
    public static float speedZGlobal = -0.15f;
    boolean active; // always active in this program
    float x, y, z;  // position
    float speedX, speedY, speedZ; // speed in the direction

    // Constructor
    public Particle() {
        active = true;
        burst();
    }

    /**
     * Set the particles starting position and speed
     */
    public void burst() 
    {
        // Set the initial position
        reset();

        float maxSpeed = 0.05f;
        float speed = 0.02f + (rand.nextFloat() - 0.5f) * maxSpeed; 
        float angle = (float)Math.toRadians(rand.nextInt(10));
        
        speedX = speed * (float)Math.cos(angle);
        speedY = speed * (float)Math.sin(angle);
        speedZ = speedZGlobal;
    }
    
    private void reset()
    {
        z = rand.nextFloat() * 4 + (float) Game.avatar.getPosition()[2];
        x = rand.nextFloat() * 10 - 5;
        y = rand.nextFloat() * 10 - 5;
    }

    /**
     * Move the particle
     */
    public void move()
    {
        x += speedX;
        y += speedY;
        z += speedZ;
//        life -= 0.002;

        if (z < 0)
        {
            reset();
        }

    }
}