package dev.dennis.osfx.util;

import com.google.common.collect.ImmutableMap;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static org.lwjgl.glfw.GLFW.*;

public class KeyMapping {
    private static final ImmutableMap<Integer, Integer> GLFW_TO_JAVA = ImmutableMap.<Integer, Integer>builder()
            .put(GLFW_KEY_APOSTROPHE, -1)
            .put(GLFW_KEY_ESCAPE, KeyEvent.VK_ESCAPE)
            .put(GLFW_KEY_ENTER, KeyEvent.VK_ENTER)
            .put(GLFW_KEY_TAB, KeyEvent.VK_TAB)
            .put(GLFW_KEY_BACKSPACE, KeyEvent.VK_BACK_SPACE)
            .put(GLFW_KEY_INSERT, KeyEvent.VK_INSERT)
            .put(GLFW_KEY_DELETE, KeyEvent.VK_DELETE)
            .put(GLFW_KEY_RIGHT, KeyEvent.VK_RIGHT)
            .put(GLFW_KEY_LEFT, KeyEvent.VK_LEFT)
            .put(GLFW_KEY_DOWN, KeyEvent.VK_DOWN)
            .put(GLFW_KEY_UP, KeyEvent.VK_UP)
            .put(GLFW_KEY_PAGE_UP, KeyEvent.VK_PAGE_UP)
            .put(GLFW_KEY_PAGE_DOWN, KeyEvent.VK_PAGE_DOWN)
            .put(GLFW_KEY_HOME, KeyEvent.VK_HOME)
            .put(GLFW_KEY_END, KeyEvent.VK_END)
            .put(GLFW_KEY_CAPS_LOCK, KeyEvent.VK_CAPS_LOCK)
            .put(GLFW_KEY_SCROLL_LOCK, KeyEvent.VK_SCROLL_LOCK)
            .put(GLFW_KEY_NUM_LOCK, KeyEvent.VK_NUM_LOCK)
            .put(GLFW_KEY_PRINT_SCREEN, KeyEvent.VK_PRINTSCREEN)
            .put(GLFW_KEY_PAUSE, KeyEvent.VK_PAUSE)
            .put(GLFW_KEY_F1, KeyEvent.VK_F1)
            .put(GLFW_KEY_F2, KeyEvent.VK_F2)
            .put(GLFW_KEY_F3, KeyEvent.VK_F3)
            .put(GLFW_KEY_F4, KeyEvent.VK_F4)
            .put(GLFW_KEY_F5, KeyEvent.VK_F5)
            .put(GLFW_KEY_F6, KeyEvent.VK_F6)
            .put(GLFW_KEY_F7, KeyEvent.VK_F7)
            .put(GLFW_KEY_F8, KeyEvent.VK_F8)
            .put(GLFW_KEY_F9, KeyEvent.VK_F9)
            .put(GLFW_KEY_F10, KeyEvent.VK_F10)
            .put(GLFW_KEY_F11, KeyEvent.VK_F11)
            .put(GLFW_KEY_F12, KeyEvent.VK_F12)
            .put(GLFW_KEY_F13, KeyEvent.VK_F13)
            .put(GLFW_KEY_F14, KeyEvent.VK_F14)
            .put(GLFW_KEY_F15, KeyEvent.VK_F15)
            .put(GLFW_KEY_F16, KeyEvent.VK_F16)
            .put(GLFW_KEY_F17, KeyEvent.VK_F17)
            .put(GLFW_KEY_F18, KeyEvent.VK_F18)
            .put(GLFW_KEY_F19, KeyEvent.VK_F19)
            .put(GLFW_KEY_F20, KeyEvent.VK_F20)
            .put(GLFW_KEY_F21, KeyEvent.VK_F21)
            .put(GLFW_KEY_F22, KeyEvent.VK_F22)
            .put(GLFW_KEY_F23, KeyEvent.VK_F23)
            .put(GLFW_KEY_F24, KeyEvent.VK_F24)
            // This doesn't exist but lets just map it to F24
            .put(GLFW_KEY_F25, KeyEvent.VK_F24)
            .put(GLFW_KEY_KP_0, -1)
            .put(GLFW_KEY_KP_1, -1)
            .put(GLFW_KEY_KP_2, -1)
            .put(GLFW_KEY_KP_3, -1)
            .put(GLFW_KEY_KP_4, -1)
            .put(GLFW_KEY_KP_5, -1)
            .put(GLFW_KEY_KP_6, -1)
            .put(GLFW_KEY_KP_7, -1)
            .put(GLFW_KEY_KP_8, -1)
            .put(GLFW_KEY_KP_9, -1)
            .put(GLFW_KEY_KP_ENTER, KeyEvent.VK_ENTER)
            .build();

    public static int mapGlfwKeyToJava(int key) {
        return GLFW_TO_JAVA.getOrDefault(key, key);
    }

    public static int mapGlfwModifiersToJava(int mods) {
        int modifiers = 0;
        if ((mods & GLFW_MOD_SHIFT) != 0) {
            modifiers |= InputEvent.SHIFT_MASK;
        }
        if ((mods & GLFW_MOD_CONTROL) != 0) {
            modifiers |= InputEvent.CTRL_MASK;
        }
        if ((mods & GLFW_MOD_ALT) != 0) {
            modifiers |= InputEvent.ALT_MASK;
        }
        return modifiers;
    }
}
