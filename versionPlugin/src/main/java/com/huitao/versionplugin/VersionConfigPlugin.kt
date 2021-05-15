package com.huitao.versionplugin

import com.android.build.gradle.*
import com.huitao.versionplugin.Testing.androidTestImplementation
import com.huitao.versionplugin.Testing.testImplementation
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.PluginContainer
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/5/12 20:52
 *desc    :
 *version :
 */
class VersionConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {

    }

    private fun PluginContainer.config(project: Project) {
        whenObjectAdded {
            when (this) {
                is AppPlugin -> {
                    //公共插件
                    project.configCommonPlugin()
                    //公共android配置
                    project.extensions.getByType<AppExtension>().applyAppCommons(project)
                    //公共依赖
                    project.configAppDependencies()
                }

                is LibraryPlugin -> {
                    //公共插件
                    project.configCommonPlugin()
                    //公共android配置
                    project.extensions.getByType<LibraryExtension>().applyLibraryCommons(project)
                    //公共依赖
                    project.configLibraryDependencies()
                }

                is KotlinAndroidPluginWrapper -> {
                    //根据 project build.gradle.kts 中的配置动态设置 kotlinVersion
                    project.getKotlinPluginVersion()?.also { kotlinVersion ->
                        GradlePlugins.kotlinVersion = kotlinVersion
                    }
                }
            }
        }
    }


    ///library module 公共依赖
    private fun Project.configLibraryDependencies() {
        dependencies.apply {
            add(api, fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
            add(implementation, GradlePlugins.kotlinStdlib)
            configTestDependencies()
        }
    }

    private fun Project.configAppDependencies() {
        dependencies.apply {
            add(implementation, fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
            add(implementation, GradlePlugins.kotlinStdlib)
            add(implementation, (project(":baselib")))
            configTestDependencies()
        }
    }

    /**
     * test 依赖配置
     */
    private fun DependencyHandler.configTestDependencies() {
        testImplementation(Testing.testLibraries)
        androidTestImplementation(Testing.androidTestLibraries)
    }


    ///kotlin插件
    private fun Project.configCommonPlugin() {
        plugins.apply("kotlin-android")
        plugins.apply("kotlin-android-extensions")
    }


    ///app module 配置项 此处固定了applicationId
    private fun AppExtension.applyAppCommons(project: Project) {
        defaultConfig {
            applicationId = BuildConfig.applicationId
        }
        applyBaseCommons(project)
    }


    /// app module 配置项
    private fun LibraryExtension.applyLibraryCommons(project: Project) {
        applyBaseCommons(project)
    }


    ///配置android闭包下的公共环境
    private fun BaseExtension.applyBaseCommons(project: Project) {
        compileSdkVersion(BuildConfig.compileSdkVersion)

        defaultConfig {
            minSdkVersion(BuildConfig.minSdkVersion)
            targetSdkVersion(BuildConfig.targetSdkVersion)
            versionCode = BuildConfig.versionCode
            versionName = BuildConfig.versionName
            testInstrumentationRunner = BuildConfig.runner
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        project.tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

}