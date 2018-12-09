# Overview
Gradle plugin that set version of Android project by date and Git commit status automatically.

# Getting Started

Add following snippets to your root project's `build.gradle` file:
```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.yogpc:auto-version:0.0.+'
    }
}
```
and apply following diffs to your `app` project's `build.gradle` file:
```diff
 apply plugin: 'com.android.application'
+apply plugin: 'auto-version'
 
 android {
     defaultConfig {
-        versionCode 1
-        versionName "0.0.1"
     }
 }
```
and add following snippets to `gradle.properties` file:
```properties
versionName=0.0.1
```
