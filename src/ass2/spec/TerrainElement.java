package ass2.spec;

import com.jogamp.opengl.GLAutoDrawable;

public abstract class TerrainElement {

    protected Terrain terrain;
    protected double[] position;
    
    protected TerrainElement() {
        position = new double[3];
    }
    
    /**
     * Set the terrain which this element is a member of 
     * @param terrain which this is a member of 
     */
    public void setTerrain(Terrain terrain) 
    {
        this.terrain = terrain;
    }
    
    /**
     * @return The terrain which this tree is located on
     */
    public Terrain getTerrain() 
    {
        return this.terrain;
    }
    
    /**
     * @return The position of this element
     */
    public double[] getPosition()
    {
        return this.position;
    }
    
    /**
     * Setup the object to be ready to be drawn
     * @param drawable
     */
    public abstract void init(GLAutoDrawable drawable);
    
    /**
     * Draw the object to the world
     * @param drawable
     */
    public abstract void display(GLAutoDrawable drawable);
}
