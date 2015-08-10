package uk.co.littlemike.gradle.publishing

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar

class SmorgasbordPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        applyRequiredPlugins(project)
        def config = project.extensions.create("basicPublishing", BasicPublishingConfig, project)

        project.afterEvaluate {
            config.validate()
            applyMavenPublishingConfig(project, config)
            applyPluginPublishingConfig(project, config)
        }
    }

    private void applyRequiredPlugins(Project project) {
        project.apply(plugin: 'groovy')
        project.apply(plugin: 'java-gradle-plugin')
        project.apply(plugin: 'maven-publish')
        project.apply(plugin: 'com.gradle.plugin-publish')
    }

    private void applyMavenPublishingConfig(Project project, BasicPublishingConfig config) {
        project.archivesBaseName = config.artifactId

        project.publishing {
            publications {
                plugin(MavenPublication) {
                    from project.components.java
                    groupId config.groupId
                    artifactId config.artifactId
                }
            }
        }
    }

    private void applyPluginPublishingConfig(Project project, BasicPublishingConfig config) {
        project.publishPlugins.doFirst {
            assert config.pluginImplementationClass : "Implementation class must be specified in order to publish plugin"
            assert !project.version.endsWith("-SNAPSHOT") : "Cannot publish snapshot versions of plugins"
        }

        if (config.pluginImplementationClass != null) {
            registerPluginBundle(project, config)
            getPluginPublishPropertiesFromEnvironment(project)
            generatePropertiesFile(project, config)
        }
    }

    private void registerPluginBundle(Project project, config) {
        project.pluginBundle {
            plugins {
                plugin {
                    id = config.pluginId
                    displayName = config.pluginDisplayName
                }
            }
        }
    }

    private void generatePropertiesFile(Project project, config) {
        def resourceLocation = new File(project.buildDir, 'generated-resources');
        project.jar {
            doFirst {
                File pluginProperties = new File(resourceLocation, "META-INF/gradle-plugins/${config.pluginId}.properties")
                pluginProperties.parentFile.mkdirs()
                pluginProperties.delete()
                pluginProperties << "implementation-class=${config.pluginImplementationClass}"
            }
            from resourceLocation
        }
    }

    /**
     * Hack to allow plugin publish key to be specified as an environment variable in Travis-CI
     * Taken from: https://discuss.gradle.org/t/add-apikey-and-apisecret-to-pluginbundle-extension-for-plugin-publish-plugin/8636/4
     * This can be removed when https://issues.gradle.org/browse/GRADLE-3273 is resolved
     */
    private void getPluginPublishPropertiesFromEnvironment(Project project) {
        project.task('setupPluginUpload') << {
            def key = System.getenv('gradlePublishKey')
            def secret = System.getenv('gradlePublishSecret')

            if (!key || !secret) {
                throw new RuntimeException("gradlePublishKey and/or gradlePublishSecret are not defined environment variables")
            }

            System.properties.setProperty("gradle.publish.key", key)
            System.properties.setProperty("gradle.publish.secret", secret)
        }
        project.tasks.publishPlugins.dependsOn project.tasks.setupPluginUpload
    }
}
