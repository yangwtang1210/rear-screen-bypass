package de.robv.android.xposed.callbacks;

import de.robv.android.xposed.IXposedHookLoadPackage;

public abstract class XC_LoadPackage implements IXposedHookLoadPackage {
    public static final class LoadPackageParam {
        public ClassLoader classLoader;
        public boolean isFirstApplication;
        public String packageName;
        public String processName;
    }
}
