package com.yogpc.auto_version;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.lang.reflect.InvocationTargetException;

public class AutoVersionPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        try {
            VersionObtainer obtainer = new VersionObtainer(project.getRootDir());
            String versionNameRelease = (String) project.findProperty("versionName");
            if (versionNameRelease == null || versionNameRelease.length() == 0)
                versionNameRelease = obtainer.getNameDebug();
            /* {@link com.android.build.gradle.internal.dsl.BaseAppModuleExtension}
               {@link com.android.build.gradle.BaseExtension} */
            Object ext = project.getExtensions().findByName("android");
            if (ext == null)
                return;
            Class<?> cExt = ext.getClass();
            /* {@link com.android.build.gradle.internal.dsl.DefaultConfig}
             *  {@link com.android.builder.core.DefaultProductFlavor} */
            Object config = cExt.getMethod("getDefaultConfig").invoke(ext);
            Class<?> cConfig = config.getClass();
            cConfig.getMethod("setVersionCode", Integer.class)
                    .invoke(config, obtainer.getCode());
            cConfig.getMethod("setVersionName", String.class)
                    .invoke(config, "");
            /* {@link com.android.build.gradle.internal.dsl.BuildType}
             *  {@link com.android.builder.internal.BaseConfigImpl} */
            NamedDomainObjectContainer<?> types = (NamedDomainObjectContainer<?>)
                    cExt.getMethod("getBuildTypes").invoke(ext);
            Object release = types.maybeCreate("release");
            release.getClass().getMethod("setVersionNameSuffix", String.class)
                    .invoke(release, versionNameRelease);
            Object debug = types.maybeCreate("debug");
            debug.getClass().getMethod("setVersionNameSuffix", String.class)
                    .invoke(debug, obtainer.getNameDebug());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
