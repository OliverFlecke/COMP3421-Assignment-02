package ass2.spec;

import java.util.Random;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

/**
 * Can simulate rain
 * @author Oliver Fleckenstein
 *
 */
public class Rain 
{
    private static final int MAX_DROPLETS = 10000;
    public static final int HEAVY_RAIN = 1000;
    public static final int LIGHT_RAIN = 500;
    public static final int NO_RAIN = 0;
    private int rainMode = NO_RAIN;
    private RainParticle[] droplets = new RainParticle[MAX_DROPLETS];
    
    private static float speedZGlobal = -0.15f;
    public static Texture texture;
    private static Random rand = new Random();
    
    private Terrain terrain;
    
    /**
     * Create a rain simulator 
     * @param terrain
     */
    public Rain(Terrain terrain)
    {
        this.terrain = terrain;
    }
    
    /**
     * Make it rain!
     * @param gl
     */
    public void run(GL2 gl)
    {
        if (Game.avatar.getViewMode() == ViewMode.SUN) return;
        
        gl.glPushMatrix();
        gl.glTranslated(Game.avatar.getPosition()[0], Game.avatar.getPosition()[1], Game.avatar.getPosition()[2]);
        gl.glRotated(-Game.avatar.getRotation()[0], 0, 0, 1);            
        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        
        float matAmbAndDifL[] = {0.0f, 0.0f, 1.0f, 0.75f};
        float matSpecL[] = { 0.0f, 0.0f, 1.0f, 0.5f };
        float matShineL[] = { 200.0f };
        
        // Material properties of sphere.
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifL,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecL,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineL,0);

        // Bind texture
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureId());
        float size_x = 0.001f;
        float size_z = 0.02f;
        
        for (int i = 0; i < rainMode; i++)
        {
            if (droplets[i].active)
            {
                gl.glBegin(GL2.GL_QUADS);
                {
                    float px = droplets[i].x;
                    float py = droplets[i].y;
                    float pz = droplets[i].z;

                    gl.glTexCoord2d(1, 1);
                    gl.glVertex3f(px + size_x, py, pz + size_z); // Top Right
                    gl.glTexCoord2d(0, 1);
                    gl.glVertex3f(px - size_x, py, pz + size_z); // Top Left
                    gl.glTexCoord2d(0, 0);
                    gl.glVertex3f(px - size_x, py, pz - size_z); // Bottom Left
                    gl.glTexCoord2d(1, 0);
                    gl.glVertex3f(px + size_x, py, pz - size_z); // Bottom Right
                }
                gl.glEnd();
                
                droplets[i].move();
            }
        }
        gl.glPopAttrib();
        gl.glPopMatrix();
    }
    
    /**
     * Setup the rain to be ready for simulation
     * @param drawable
     */
    public void init(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();
        texture = new Texture(gl, RainParticle.textureFileName, RainParticle.textureExt, false);
        
        for (int i = 0; i < droplets.length; i++) 
        {
            droplets[i] = new RainParticle();
        }
    }
    
    /**
     * @return True, if it is raining
     */
    public boolean isRaining()
    {
        if (rainMode == 0) 
            return false;
        else 
            return true;
    }
    
    /**
     * Switch the rain on and off
     */
    public void switchRain()
    {
        switch (rainMode)
        {
            case NO_RAIN:
                rainMode = LIGHT_RAIN;
                break;
            case LIGHT_RAIN:
                rainMode = HEAVY_RAIN;
                break;
            case HEAVY_RAIN:
                rainMode = MAX_DROPLETS;
                break;
            default:
                rainMode = NO_RAIN;
                break;
        }
    }
    
    /**
     * A class representing a particle, which simulates rain
     * @author Oliver Fleckenstein
     */
    class RainParticle
    {
        public static final String textureFileName = "src/textures/rain/white.png";
        public static final String textureExt = ".png";
        boolean active; // always active in this program
        float x, y, z;  // position
        float speedX, speedY, speedZ; // speed in the direction

        // Constructor
        public RainParticle() {
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
            x = (float) (rand.nextFloat() * terrain.getSize().getWidth() - terrain.getSize().getWidth() / 2);
            y = (float) (rand.nextFloat() * terrain.getSize().getHeight() - terrain.getSize().getHeight() / 2);
        }

        /**
         * Move the particle
         */
        public void move()
        {
            x += speedX;
            y += speedY;
            z += speedZ;

            if (z < -2)
            {
                reset();
            }
        }
    }
}
