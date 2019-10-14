package me.jraynor.opengl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Vbo {
    private final int vboId;
    private final int type;

    private Vbo(int vboId, int type) {
        this.vboId = vboId;
        this.type = type;
    }

    /**
     * Creates a new vbo instance
     *
     * @param type
     * @return the new vbo
     */
    public static Vbo create(int type) {
        int id = GL15.glGenBuffers();
        return new Vbo(id, type);
    }

    /**
     * Bind the vbo for rendering
     */
    public void bind() {
        GL15.glBindBuffer(type, vboId);
    }

    /**
     * Unbind the vbo when done rendering
     */
    public void unbind() {
        GL15.glBindBuffer(type, 0);
    }

    /**
     * Stores the float data into this vbo
     *
     * @param data the data that is to be put into the vbo
     */
    public void storeData(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        storeData(buffer);
    }

    /**
     * Stores the float data into this vbo
     *
     * @param data the data that is to be put into the vbo
     */
    public void storeData(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        storeData(buffer);
    }

    /**
     * Stores the float data into this vbo
     *
     * @param data the data that is to be put into the vbo
     */
    public void storeData(IntBuffer data) {
        GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
    }

    public void storeData(FloatBuffer data) {
        GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
    }

    public void delete() {
        GL15.glDeleteBuffers(vboId);
    }
}
