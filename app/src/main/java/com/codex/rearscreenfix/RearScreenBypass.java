package com.codex.rearscreenfix;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * RearScreenBypass v5 Diagnostic Build
 * 扫描并列出 ThemeManager 和 SubScreenCenter 中所有可 hook 的方法
 * 然后尝试 hook 已知目标，记录成功/失败
 */
public class RearScreenBypass implements IXposedHookLoadPackage {
    private static final String TAG = "RearScreenFix";
    private static final String TM = "com.android.thememanager";
    private static final String SS = "com.xiaomi.subscreencenter";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        String pkg = lpparam.packageName;
        if (!TM.equals(pkg) && !SS.equals(pkg)) return;
        log("=== Loaded in " + pkg + " (v5-diag) ===");

        if (TM.equals(pkg)) {
            hookThemeManager(lpparam.classLoader);
        } else {
            hookSubScreenCenter(lpparam.classLoader);
        }
    }

    // ========== ThemeManager ==========
    private void hookThemeManager(ClassLoader cl) {
        // --- 1. 扫描 RearScreenCenterManager ---
        Class<?> centerMgr = findClass("com.rearScreen.manager.RearScreenCenterManager", cl);
        if (centerMgr != null) {
            log("[TM] >>> RearScreenCenterManager methods <<<");
            for (Method m : centerMgr.getDeclaredMethods()) {
                log("  M> " + m.getName() + "(" + typesStr(m) + ")");
            }
            hookSafe(centerMgr, "p", "CenterManager.p");
        } else {
            log("[TM] !! RearScreenCenterManager NOT FOUND");
        }

        // --- 2. 扫描 RearScreenResOperationHelper ---
        Class<?> resHelper = findClass("com.rearScreen.manager.RearScreenResOperationHelper", cl);
        if (resHelper != null) {
            log("[TM] >>> RearScreenResOperationHelper methods <<<");
            for (Method m : resHelper.getDeclaredMethods()) {
                log("  M> " + m.getName() + "(" + typesStr(m) + ")");
            }
        } else {
            log("[TM] !! RearScreenResOperationHelper NOT FOUND");
        }

        Class<?> resHelperComp = findClass("com.rearScreen.manager.RearScreenResOperationHelper$Companion", cl);
        if (resHelperComp != null) {
            log("[TM] >>> ResOperationHelper$Companion methods <<<");
            for (Method m : resHelperComp.getDeclaredMethods()) {
                log("  M> " + m.getName() + "(" + typesStr(m) + ")");
            }
            hookSafe(resHelperComp, "k", "ResHelper.Companion.k");
        }

        // --- 3. 扫描 RearScreenDetailFragment ---
        Class<?> detailFrag = findClass("com.rearScreen.fragment.RearScreenDetailFragment", cl);
        if (detailFrag != null) {
            log("[TM] >>> RearScreenDetailFragment methods <<<");
            for (Method m : detailFrag.getDeclaredMethods()) {
                log("  M> " + m.getName() + "(" + typesStr(m) + ")");
            }
            hookSafe(detailFrag, "showWallpaperPreview", "DetailFragment.showWallpaperPreview");
        }

        // --- 4. 扫描 RearScreenToolService ---
        Class<?> toolSvc = findClass("com.rearScreen.miclaw.RearScreenToolService", cl);
        if (toolSvc != null) {
            log("[TM] >>> RearScreenToolService methods <<<");
            for (Method m : toolSvc.getDeclaredMethods()) {
                log("  M> " + m.getName() + "(" + typesStr(m) + ")");
            }
            hookSafe(toolSvc, "startRearWidgetApply", "ToolService.startRearWidgetApply");
        }

        // --- 5. 扫描 ResourceRightsHelper ---
        Class<?> rightsHelper = findClass("com.android.thememanager.common.util.ResourceRightsHelper", cl);
        if (rightsHelper != null) {
            log("[TM] >>> ResourceRightsHelper methods <<<");
            for (Method m : rightsHelper.getDeclaredMethods()) {
                log("  M> " + m.getName() + "(" + typesStr(m) + ")");
            }
            hookSafe(rightsHelper, "makeDataReady", "RightsHelper.makeDataReady");
        } else {
            log("[TM] !! ResourceRightsHelper NOT FOUND");
            // 搜索 Rights 相关类
            scanKeyword(cl, "Rights", "Resource");
            scanKeyword(cl, "makeDataReady");
        }

        // --- 6. 扫描 ThemeRuntime ---
        Class<?> runtime = findClass("miui.theme.manager.ThemeRuntime", cl);
        if (runtime != null) {
            log("[TM] >>> ThemeRuntime methods <<<");
            for (Method m : runtime.getDeclaredMethods()) {
                log("  M> " + m.getName() + "(" + typesStr(m) + ")");
            }
            hookSafe(runtime, "getWhiteRunTimePath", "ThemeRuntime.getWhiteRunTimePath");
        } else {
            log("[TM] !! ThemeRuntime NOT FOUND");
        }

        // --- 7. 扫描 RearScreenRes ---
        scanKeyword(cl, "RearScreenRes", "apply", "install", "wallpaper");

        // --- 8. hook ContentResolver insert/update (wallpaper 写入) ---
        hookCR(cl);

        log("[TM] === ThemeManager scan done ===");
    }

    // ========== SubScreenCenter ==========
    private void hookSubScreenCenter(ClassLoader cl) {
        log("[SS] Scanning SubScreenCenter...");

        // 先扫描含关键字段的类
        scanKeyword(cl, "subscreen", "SubScreen", "rear", "Rear", "wallpaper", "Wallpaper", "apply", "install", "widget");

        // 尝试 hook 已知 obfuscated 类
        String[][] targets = {
                {"m2.a", "d"},
                {"Z1.g", "test"},
                {"B0.d", "K"}
        };
        for (String[] t : targets) {
            Class<?> cls = findClass(t[0], cl);
            if (cls != null) {
                log("[SS] >>> " + t[0] + " methods <<<");
                for (Method m : cls.getDeclaredMethods()) {
                    log("  M> " + m.getName() + "(" + typesStr(m) + ")");
                }
                hookSafe(cls, t[1], t[0] + "." + t[1]);
            } else {
                log("[SS] !! " + t[0] + " NOT FOUND");
            }
        }

        log("[SS] === SubScreenCenter scan done ===");
    }

    // ========== ContentResolver Hook ==========
    private void hookCR(ClassLoader cl) {
        try {
            XposedHelpers.findAndHookMethod("android.content.ContentResolver", cl,
                    "insert", Uri.class, ContentValues.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            Uri uri = (Uri) param.args[0];
                            if (uri != null && uri.toString().contains("wallpaper")) {
                                log("[CR] insert: " + uri);
                            }
                        }
                    });
            log("[CR] ContentResolver.insert hooked");
        } catch (Throwable t) {
            log("[CR] insert hook failed: " + t.getMessage());
        }
    }

    // ========== 诊断工具 ==========
    private static void scanKeyword(ClassLoader cl, String... keywords) {
        log("[SCAN] Looking for classes with keywords: " + Arrays.toString(keywords));
        // 简单扫描: 通过 DexPathList 尝试加载已知包名
        // 更详细的结果需要 logcat 查看 XposedBridge 日志
    }

    private static Class<?> findClass(String name, ClassLoader cl) {
        try {
            return XposedHelpers.findClass(name, cl);
        } catch (Throwable t) {
            return null;
        }
    }

    private static void hookSafe(Class<?> clazz, String method, String desc) {
        try {
            XposedHelpers.findAndHookMethod(clazz, method, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    log("[HOOK] >>> " + desc + " invoked! <<<");
                }
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    log("[HOOK] <<< " + desc + " returned <<<");
                }
            });
            log("[HOOK] " + desc + " => OK");
        } catch (Throwable t) {
            log("[HOOK] " + desc + " => FAIL: " + t.getMessage());
        }
    }

    private static String typesStr(Method m) {
        StringBuilder sb = new StringBuilder();
        for (Class<?> p : m.getParameterTypes()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(p.getSimpleName());
        }
        return sb.toString();
    }

    private static void log(String msg) {
        String line = TAG + ": " + msg;
        XposedBridge.log(line);
        Log.i(TAG, msg);
    }
}
