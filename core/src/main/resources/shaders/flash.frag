#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform float u_flashIntensity;

void main() {
    vec4 color = texture2D(u_texture, v_texCoord);

    vec3 flashColor = mix(color.rgb, vec3(1.0, 1.0, 1.0), u_flashIntensity);

    gl_FragColor = vec4(flashColor, color.a);
}
