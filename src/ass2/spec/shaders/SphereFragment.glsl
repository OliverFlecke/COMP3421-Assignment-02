#version 120   
 
uniform sampler2D tex0;
uniform vec4 bkg_color;
uniform float time;
void main (void)
{
  vec2 p = -1.0 + 2.0 * gl_TexCoord[0].xy;
  float r = sqrt(dot(p,p));
  if (r < 1.0)
  {
    vec2 uv;
    float f = (1.0-sqrt(1.0-r))/(r);
    uv.x = p.x*f + time;
    uv.y = p.y*f + time;
    gl_FragColor = texture2D(tex0,uv);
  }
  else
  {
    gl_FragColor = bkg_color;
  }
}