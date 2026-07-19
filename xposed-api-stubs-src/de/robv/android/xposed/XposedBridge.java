package de.robv.android.xposed;

public final class XposedBridge {
    private XposedBridge() {
    }

    public static void log(String text) {
        System.err.println(text);
    }

    public static void log(Throwable t) {
        if (t != null) {
            t.printStackTrace(System.err);
        }
    }
}
