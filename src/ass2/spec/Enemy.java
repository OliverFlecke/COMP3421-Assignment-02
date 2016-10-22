package ass2.spec;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

/**
 * Represent an enemy in the game world
 * @author Oliver Fleckenstein
 *
 */
public class Enemy extends TerrainElement 
{
    private float positions[] =
        {   
            0,0,0, // 0
            0,0,1, // 1
            0,1,0, // 2
            0,1,1, // 3
            1,0,0, // 4
            1,0,1, // 5
            1,1,0, // 6
            1,1,1  // 7
        };

    //There should be a matching entry in this array for each entry in
    //the positions array
    private float colors[] = 
        { 
            0.0f, 0.0f, 0.0f,
            1.0f, 0.7f, 0.0f,
            0.0f, 0.0f, 0.0f,
            1.0f, 0.7f, 0.0f,
            0.0f, 0.0f, 0.0f,
            1.0f, 0.7f, 0.0f,
            0.0f, 0.0f, 0.0f,
            1.0f, 0.7f, 0.0f
        }; 

    //Best to use smallest data type possible for indexes 
    //We could even use byte here...
    private short indexes[] = 
        { 
            0,6,4, // bottom
            0,2,6,
            3,5,7, // top
            3,1,5,
            4,6,7, // right
            4,7,5,
            4,5,1, // front
            4,1,0,
            0,1,3, // left
            0,3,2,
            2,7,6, // back
            2,3,7
        };

    //These are not vertex buffer objects, they are just java containers
    private FloatBuffer posData = Buffers.newDirectFloatBuffer(positions);
    private FloatBuffer colorData = Buffers.newDirectFloatBuffer(colors);
    private ShortBuffer indexData = Buffers.newDirectShortBuffer(indexes);

    //We will be using 2 vertex buffer objects
    private int bufferIds[] = new int[2];
    
    private static final String VERTEX_SHADER = "src/ass2/spec/shaders/SphereVertex.glsl";
    private static final String FRAGMENT_SHADER = "src/ass2/spec/shaders/SphereFragment.glsl";
    
    private int shaderprogram;
    
    public Enemy(double x, double y, Terrain terrain)
    {
        this.setTerrain(terrain);
        this.position[0] = x;
        this.position[1] = y;
        this.position[2] = this.getTerrain().getAltitude(x, y);
    }
    
    public void init(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        
        gl.glGenBuffers(2, bufferIds, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[0]);
        // Allocate memory
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, positions.length * Float.BYTES + colors.length* Float.BYTES, 
                   null, GL2.GL_STATIC_DRAW);
        // Load the position data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, positions.length*Float.BYTES,posData);

        // Load the color data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,
                positions.length*Float.BYTES,  //Load after the position data
                colors.length*Float.BYTES,colorData);
        
        // Allocate and load index data
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);
        gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indexes.length * Short.BYTES,
               indexData, GL2.GL_STATIC_DRAW);
                
        try {
            shaderprogram = Shader.initShaders(gl, VERTEX_SHADER,FRAGMENT_SHADER);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void display(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        gl.glScaled(0.5, 0.5, 0.5);
        gl.glTranslated(this.getPosition()[0], this.getPosition()[1], this.getPosition()[2] + 2);
        
        float ambAndDif[]   = { 1.0f, 0.0f, 0.0f, 1.0f };
        float spec[]        = { 1.0f, 0.0f, 1.0f, 1.0f };
        float emm[]         = { 1.0f, 0.0f, 0.0f, 1.0f };
        float shine[]       = { 100.0f };
        
        // Material properties of teapot
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, ambAndDif,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, spec,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, shine,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emm,0);
       
        //Use the shader
        gl.glUseProgram(shaderprogram);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
           
        int vertexColLoc = gl.glGetAttribLocation(shaderprogram,"vertexCol");
        int vertexPosLoc = gl.glGetAttribLocation(shaderprogram,"vertexPos");
               
        // Specify locations for the co-ordinates and color arrays.
        gl.glEnableVertexAttribArray(vertexPosLoc);
        gl.glEnableVertexAttribArray(vertexColLoc);
        gl.glVertexAttribPointer(vertexPosLoc,3, GL.GL_FLOAT, false,0, 0); //last num is the offset
        gl.glVertexAttribPointer(vertexColLoc,3, GL.GL_FLOAT, false,0, positions.length*Float.BYTES);
       
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);
        gl.glDrawElements(GL2.GL_TRIANGLES, 36, GL2.GL_UNSIGNED_SHORT,0);
        gl.glUseProgram(0);
           
        //Un-bind the buffer. 
        //This is not needed in this simple example but good practice
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,0);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER,0);
        
        gl.glPopAttrib();
        gl.glPopMatrix();
    }
}
