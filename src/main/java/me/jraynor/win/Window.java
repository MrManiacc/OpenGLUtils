package me.jraynor.win;
import me.jraynor.misc.Input;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.tinylog.Logger;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    public static int width = 1080, height = 720;
    private boolean fullscreen = false, resizeable = false, vSync = false;
    private String title = "JEngine";
    private long window;

    public Window(int width, int height, boolean fullscreen, boolean resizeable, boolean vSync, String title) {
        Window.width = width;
        Window.height = height;
        this.fullscreen = fullscreen;
        this.resizeable = resizeable;
        this.vSync = vSync;
        this.title = title;
    }

    public Window(int width, int height, boolean fullscreen) {
        this(width, height, fullscreen, false, false, "JEngine");
    }

    public Window(int width, int height) {
        this(width, height, false);
    }

    /**
     * Start the window
     */
    public void start(Engine loopable) {
        loopable.onStart();
        init();
        defaultHints();
        createWindow();
        pushWindow();
        finishWindow();
        loopCallback(loopable);
        stop();
        loopable.onStop();
    }

    /**
     * Creates the opengl window context
     */
    private void init() {
        Logger.info("Starting window with of width {} and height {}", width, height);
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            Logger.error("Failed to initialize window, shutting down");
            try {
                Thread.sleep(1000);
                System.exit(-1);
            } catch (InterruptedException e) {
            }
        }
    }


    /**
     * the default hints to be ran
     */
    private void defaultHints() {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, resizeable ? GLFW_TRUE : GLFW_FALSE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        if (fullscreen) {
            Logger.info("Attempting to create a windowed full screen window");
            //Not working
        }
    }

    /**
     * Create the window
     */
    private void createWindow() {
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            Logger.error("Failed to create window, shutting down");
            try {
                Thread.sleep(1000);
                System.exit(-2);
            } catch (InterruptedException e) {
            }
        }

        Input.init(window);
    }

    /**
     * Get the thread stack and push a new frame, centers the window as well
     */
    private void pushWindow() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
            Logger.info("Window pushed to stack frame and centered on main monitor");
        }
    }

    /**
     * The final step, sets current context and disables/enables vsync, then shows the window
     */
    private void finishWindow() {
        glfwMakeContextCurrent(window);
        glfwSwapInterval(vSync ? 1 : 0);
        glfwShowWindow(window);
        GL.createCapabilities();
        glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
    }


    /**
     * Handles the update and render method
     *
     * @param engine the class to be updated
     */
    private void loopCallback(Engine engine) {
        float passed = System.nanoTime();
        float counter = 0;
        while (!glfwWindowShouldClose(window)) {

            long current = System.nanoTime();
            float delta = (float) ((current - passed) / 1E9);
            passed = current;

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            engine.onUpdate(delta);
            if (counter <= 0.05) {
                counter += delta;
            } else {
                engine.onTick(counter);
                counter = 0;
            }
            Input.update();

            glfwSwapBuffers(window);
            glfwPollEvents();


        }
    }

    /**
     * Shutdown the window gracefully
     */
    public void stop() {
        Logger.info("Stopping window gracefully");
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
        Logger.info("Window closed gracefully");
    }

}
