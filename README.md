# Overview
Auto set android apk versionCode and versionName by git commit count.

# Getting Started

Add following snippets to your root project's `build.gradle` file:
```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.yogpc:auto-version:0.0.1'
    }
}
apply plugin: 'auto-version'
```
