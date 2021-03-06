#version 300 es

precision mediump float;

// per-vertex
layout(location = 0) in vec3 in_Position;
layout(location = 1) in vec2 in_TexCoords;

// per-instance
layout(location = 2) in mat4 in_InstTransform;
layout(location = 6) in vec4 in_InstColour;
layout(location = 7) in vec2 in_UVOffset;
layout(location = 8) in vec2 in_UVScale;

out vec4 sh_Colour;
out vec2 sh_TexCoords;

uniform mat4 u_MVMatrix;
uniform mat4 u_PMatrix;

void main() {
    sh_Colour = in_InstColour;
    sh_TexCoords = in_TexCoords * in_UVScale + in_UVOffset;
    gl_Position = u_PMatrix * u_MVMatrix * in_InstTransform *  vec4(in_Position, 1.0f);
}
