#version 130

in vec4 vertexPos;
in vec4 vertexCol;

out vec3 frag_col;
out vec3 N;
out vec3 v;

void main(void) 
{
    v = vec3(gl_ModelViewMatrix * gl_Vertex);
    N = normalize(gl_NormalMatrix * gl_Normal);
    frag_col = vec3(vertexCol[0], vertexCol[1], vertexCol[2]);

    gl_Position = gl_ModelViewProjectionMatrix * vertexPos;
}


