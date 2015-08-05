package uk.co.littlemike.gradle.publishing

import org.gradle.api.Plugin
import org.gradle.api.Project

class SmorgasbordPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.apply(plugin: 'java')
        println "Hello world!"
    }
}
