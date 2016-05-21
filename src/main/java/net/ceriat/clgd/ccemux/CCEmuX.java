package net.ceriat.clgd.ccemux;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class CCEmuX {
    // Constants
    private static final String EMU_TITLE = "CCEmuX";
    private static final int EMU_WIDTH = 816;
    private static final int EMU_HEIGHT = 456;

    // Emulator components

    /** The emulator's window */
    public EmuWindow window;

    /** A helper object that aids with graphical stuff */
    public Graphics graphics;

    public Logger logger = Logger.getLogger("CCEmuX");

    public File assetsDir = new File("assets");
    public File ccJarFile = new File(assetsDir, "ComputerCraft.jar");

    public CCAssets ccAssets;

    private TerminalRenderer termRenderer;

    public CCEmuX() throws Exception {
        CCEmuX.instance = this;

        if (!assetsDir.exists()) {
            JOptionPane.showMessageDialog(
                null,
                "The assets directory is missing!\nPlease put it in the same directory as CCEmuX.jar.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );

            System.exit(1);
            return;
        }

        if (!ccJarFile.exists()) {
            JOptionPane.showMessageDialog(
                null,
                "There is no ComputerCraft.jar file present.\n" +
                "Please download one from computercraft.info and put it in the assets directory with the name \"ComputerCraft.jar\".",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );

            System.exit(1);
            return;
        }

        logger.setUseParentHandlers(false);

        for (Handler h : logger.getHandlers()) {
            logger.removeHandler(h);
        }

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new LogFormatter());

        logger.addHandler(ch);

        logger.setLevel(Level.ALL);

        window = new EmuWindow(EMU_TITLE, EMU_WIDTH, EMU_HEIGHT);
        window.show();

        window.makeContextActive();
        glClearColor(0.0f, 0.2f, 0.4f, 1.0f);

        graphics = new Graphics(); // must be created after context
        graphics.makeOrthographic(window.getWidth(), window.getHeight());

        ccAssets = new CCAssets(ccJarFile);
        termRenderer = new TerminalRenderer(ccAssets.font, 51, 19, 16.0f, 24.0f);
    }

    /**
     * Starts the emulator loop.
     */
    public void startLoop() {
        Random rand = new Random();

        while (!window.shouldClose()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            termRenderer.startPixelUpdate();

            for (int y = 0; y < 19; ++y) {
                for (int x = 0; x < 51; ++x) {
                    termRenderer.updatePixel(new Point(x, y), new Color(rand.nextInt(255), rand.nextInt(255), 127));
                }
            }

            termRenderer.stopPixelUpdate();

            termRenderer.startTextUpdate();

            for (int y = 0; y < 19; ++y) {
                for (int x = 0; x < 51; ++x) {
                    termRenderer.updateText(new Point(x, y), new Color(rand.nextInt(255), rand.nextInt(255), 127), (char)rand.nextInt(127));
                }
            }

            termRenderer.stopTextUpdate();
            termRenderer.render();

            window.swapBuffers();
            EmuWindow.pollEvents();
        }
    }

    /** A CCEmuX instance */
    public static CCEmuX instance;

    public static void main(String[] args) throws Exception {
        initLibs();

        CCEmuX emux = new CCEmuX();
        emux.startLoop();
    }

    private static void initLibs() throws Exception {
        if (glfwInit() == GLFW_FALSE) {
            throw new Exception("failed to initialise glfw!");
        }
    }
}
