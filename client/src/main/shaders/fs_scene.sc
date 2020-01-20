$input v_texcoord0, v_color0, v_textureId

#include "bgfx_shader.sh"

SAMPLER2DARRAY(s_texColor, 0);

void main()
{
	gl_FragColor = texture2DArray(s_texColor, vec3(v_texcoord0.xy, v_textureId)) * v_color0;
}
