package ass2.spec;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road
{
//    private String textureFileName = "src/textures/road/tarmac.jpg";
//    private String textureExt = ".jpg";
    private String textureFileName = "src/textures/road/asphalt.png";
    private String textureExt = ".png";
    private Texture texture;
    private Terrain terrain;
    private List<Double> points;
    private double width;
    
    /** 
     * Create a new road starting at the specified point
     */
    public Road(double width, double x0, double y0) {
        this.width = width;
        points = new ArrayList<Double>();
        points.add(x0);
        points.add(y0);
    }

    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(double width, double[] spine) {
        this.width = width;
        points = new ArrayList<Double>();
        for (int i = 0; i < spine.length; i++) {
            points.add(spine[i]);
        }
    }

    /**
     * The width of the road.
     * 
     * @return
     */
    public double getWidth() {
        return width;
    }

    /**
     * Add a new segment of road, beginning at the last point added and ending at (x3, y3).
     * (x1, y1) and (x2, y2) are interpolated as bezier control points.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    public void addSegment(double x1, double y1, double x2, double y2, double x3, double y3) {
        points.add(x1);
        points.add(y1);
        points.add(x2);
        points.add(y2);
        points.add(x3);
        points.add(y3);        
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return points.size() / 6;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public double[] controlPoint(int i) {
        double[] p = new double[2];
        p[0] = points.get(i*2);
        p[1] = points.get(i*2+1);
        return p;
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public double[] point(double t) {
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 6;
        
        double x0 = points.get(i++);
        double y0 = points.get(i++);
        double x1 = points.get(i++);
        double y1 = points.get(i++);
        double x2 = points.get(i++);
        double y2 = points.get(i++);
        double x3 = points.get(i++);
        double y3 = points.get(i++);
        
        double[] p = new double[2];

        p[0] = b(0, t) * x0 + b(1, t) * x1 + b(2, t) * x2 + b(3, t) * x3;
        p[1] = b(0, t) * y0 + b(1, t) * y1 + b(2, t) * y2 + b(3, t) * y3;        
        
        return p;
    }
    
    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private double b(int i, double t) {
        
        switch(i)
        {
            case 0:
                return (1-t) * (1-t) * (1-t);
                
            case 1:
                return 3 * (1-t) * (1-t) * t;
                
            case 2:
                return 3 * (1-t) * t * t;
                
            case 3:
                return t * t * t;
                
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }

    /**
     * Draw and display the road
     * @param drawable
     */
    public void display(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        double z_offset = 0.01;
        double step_size = 0.005;
        gl.glBegin(GL2.GL_LINE_STRIP);
        {
            for (double t = 0; t < this.size(); t += step_size)
            {
                double[] point = point(t);
                double z = z_offset + 1 + this.terrain.getAltitude(point[0], point[1]);
                gl.glVertex3d(point[0], point[1], z);
            }
        }
        gl.glEnd();


        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureId());
        gl.glBegin(GL2.GL_QUAD_STRIP);
        {
            double[] point = point(0);
            double[] lastPoint = point;
            for (double t = 0; t < this.size(); t += step_size)
            {
                point = point(t);
                // Calculate the vector-prep between the last point and the current
                double[] v = { -(lastPoint[1] - point[1]), lastPoint[0] - point[0] };
                double length = Math.sqrt(Math.pow(v[0], 2) + Math.pow(v[1], 2));
                v[0] = (v[0] / length) * (getWidth() / 2);
                v[1] = (v[1] / length) * (getWidth() / 2);
                
                
                double z0 = z_offset + this.terrain.getAltitude(point[0] - v[0], point[1] - v[1]);
                double z1 = z_offset + this.terrain.getAltitude(point[0] + v[0], point[1] + v[1]);
                gl.glNormal3d(0, 0, 1);
                gl.glTexCoord2d(0, ((10 * t) % 10) / this.size());
                gl.glVertex3d(point[0] - v[0], point[1] - v[1], z0);
                gl.glNormal3d(0, 0, 1);
                gl.glTexCoord2d(1, ((10 * t) % 10) / this.size());
                gl.glVertex3d(point[0] + v[0], point[1] + v[1], z1);
                
                lastPoint = point;
            }
        }
        gl.glEnd();
    }

    /**
     * Make the road ready to be drawn in the first place
     * @param drawable
     */
    public void init(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();
        texture = new Texture(gl, textureFileName, textureExt, true);
    }

    /**
     * Set the tearrin this road is located on
     * @param terrain
     */
    public void setTerrain(Terrain terrain)
    {
        this.terrain = terrain;
    }
}
