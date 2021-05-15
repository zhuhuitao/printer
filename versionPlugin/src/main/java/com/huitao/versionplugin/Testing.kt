package com.huitao.versionplugin

import org.gradle.api.artifacts.dsl.DependencyHandler

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/5/13 10:13
 *desc    :
 *version :
 */
object Testing {
    private const val testImplementation = "testImplementation"
    private const val androidTestImplementation = "androidTestImplementation"

    private const val jUnit = "junit:junit:4.12"
    private const val androidJunit = "androidx.test.ext:junit:1.1.1"
    private const val androidRunner = "androidx.test:runner:1.2.0"
    private const val espresso = "androidx.test.espresso:espresso-core:3.2.0"

    val androidTestLibraries = arrayListOf<String>().apply {
        add(androidJunit)
        add(androidRunner)
        add(espresso)
    }

    fun DependencyHandler.androidTestImplementation(list: List<String>) {
        list.forEach {
            add(androidTestImplementation, it)
        }
    }


    val testLibraries = arrayListOf<String>().apply {
        add(jUnit)
    }

    fun DependencyHandler.testImplementation(list: List<String>) {
        list.forEach { dependency ->
            add(testImplementation, dependency)
        }
    }
}