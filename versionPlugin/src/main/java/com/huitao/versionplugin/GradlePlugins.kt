package com.huitao.versionplugin

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/5/13 13:46
 *desc    :
 *version :
 */
object GradlePlugins {
    var kotlinVersion = "1.4.0"
    const val ANDROID = "com.android.tools.build:gradle:4.0.1"

    val kotlinStdlib
        get() = "org.jetbrains.kotlin:kotlin-stdlib:$KotlinVersion"

    interface GradlePlugin {
        val ID: String
        val VERSION: String
        val APPLY: Boolean
            get() = true
    }

    object kotlin : GradlePlugin {
        override val ID = "gradle-plugin"
        override val VERSION = "1.4.0"
    }

}