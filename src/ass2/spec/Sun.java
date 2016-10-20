package ass2.spec;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

public class Sun 
{
    private String sunTextureFileName = "src/textures/sun/black-and-yellow.jpg";
    private String sunTextureExt = "jpg";
    private Texture texture;
    
    private final float[] daySky = { 0, 0, 0.8f, 1.0f };
    private final float[] nightSky = { 0, 0, 0, 0 };
    private final float[] dayLight = { 1.0f, 0.75f, 0.0f, 1.0f };
    private final float[] nightLight = { 0.0f, 0.0f, 0.5f, 1.0f };
    private final float[] ambientLight = { 0.25f, 0.5f, 0.0f, 1.0f };
    private float timeInterval = -0.001f;
    private float timeOfDay = 1;
    
    private boolean isTimeDynamic = false;
    
    // Variables for lightning
    private float[] dynamic_sunlight;
    private int light_slices = 250;
    private int light_step = 0;
    private float light_radius = 10f;
    private float[] staticPosition;
    private boolean dynamicPosition = true;
    private boolean isDay = true;
    
    Terrain terrain;
    
    public Sun(Terrain terrain)
    {
        this.staticPosition = new float[3];
        this.dynamic_sunlight = new float[3];
        this.terrain = terrain;
    }
    
    public void init(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        texture = new Texture(gl, sunTextureFileName, sunTextureExt, true);
    }
    
    /**
     * Draw the sun
     * @param gl
     */
    public void drawSun(GL2 gl)
    {
        calculateDynamicSunlightPosition();
        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
            gl.glTranslatef(getPosition()[0], getPosition()[1], getPosition()[2]);
            float[] matAmbAndDif = new float[] {1f, 0.2f, 0.0f, 1.0f};
            float[] matSpec = new float[] { 0.2f, 0.2f, 0.0f, 1.0f };
            float[] matShine = new float[] { 10.0f };
            float[] emm = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
            
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDif,0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpec,0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShine,0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emm,0);
            
            gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureId());
            Game.glut.glutSolidSphere(1, 100, 100);
        gl.glPopAttrib();
        gl.glPopMatrix();
    }
    
    /**
     * Calculates the dynamic position of the sun 
     */
    private void calculateDynamicSunlightPosition()
    {
        double light_angle = light_step * (2 * Math.PI / light_slices);
        dynamic_sunlight[0] = (float) (terrain.getSize().getWidth() / 2 + light_radius * Math.cos(light_angle));
        dynamic_sunlight[1] = (float) (terrain.getSize().getHeight() / 2 + light_radius * Math.sin(light_angle));
        dynamic_sunlight[2] = 5;
        light_step++;
    }
    
    /**
     * Tick the time of day
     */
    public void tickTimeOfDay() 
    {
        timeOfDay += timeInterval;
        if (timeOfDay >= 1 || timeOfDay <= 0) 
        {
            timeInterval = -timeInterval;
        }
    }
    
    /**
     * @return The current position of the sun
     */
    public float[] getPosition() {
        if (dynamicPosition)
        {
            return this.dynamic_sunlight;
        }
        else 
        {
            return this.staticPosition;
        }
    }
    
    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setPosition(float dx, float dy, float dz) {
        this.staticPosition[0] = dx;
        this.staticPosition[2] = dy;
        this.staticPosition[1] = dz;        
    }
    
    
    /**
     * Switch the lightning between dynamic and static
     */
    public void switchLightning() 
    {
        this.dynamicPosition = !this.dynamicPosition;
    }
    
    /**
     * Switch between day and night mode
     */
    public void switchDay()
    {
        this.isDay = !this.isDay;
    }
    
    /**
     * @return True if it is day time
     */
    public boolean isDay() 
    {
        return this.isDay;
    }

    /**
     * @return The ambient light of the sun
     */
    public float[] getAmbientLight() {
        return this.ambientLight;
    }

    /**
     * @return The light currently coming from the sun 
     */
    public float[] getLight() {
        float[] light = { 
                timeOfDay * dayLight[0] + (1 - timeOfDay) * nightLight[0], 
                timeOfDay * dayLight[1] + (1 - timeOfDay) * nightLight[1],
                timeOfDay * dayLight[2] + (1 - timeOfDay) * nightLight[2],
                timeOfDay * dayLight[3] + (1 - timeOfDay) * nightLight[3]
                };
        return light;
    }

    /**
     * @return The color of the sky
     */
    public float[] getSkyColor() {
        float[] sky = {
                timeOfDay * daySky[0] + (1 - timeOfDay) * nightSky[0],
                timeOfDay * daySky[1] + (1 - timeOfDay) * nightSky[1],
                timeOfDay * daySky[2] + (1 - timeOfDay) * nightSky[2],
                timeOfDay * daySky[3] + (1 - timeOfDay) * nightSky[3]
        };
        return sky;
    }

    /**
     * @return If the is running or not
     */
    public boolean getIsTimeDynamic() {
        return this.isTimeDynamic;
    }
    
    /**
     * Switch between dynamic and stacit time mode
     */
    public void switchTimeDynamic() 
    {
        this.isTimeDynamic = !this.isTimeDynamic;
    }
}
