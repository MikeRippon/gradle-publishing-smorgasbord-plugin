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
            applyBintrayPublishingConfig(project)
            applyPluginPublishingConfig(project, config)
        }
    }

    private static void applyRequiredPlugins(Project project) {
        project.apply(plugin: 'groovy')
        project.apply(plugin: 'java-gradle-plugin')
        project.apply(plugin: 'maven-publish')
        project.apply(plugin: 'com.gradle.plugin-publish')
        project.apply(plugin: 'com.jfrog.bintray')
    }

    private static void applyMavenPublishingConfig(Project project, BasicPublishingConfig config) {
        project.archivesBaseName = config.artifactId

        project.task('sourceJar', type: Jar) << {
            from project.sourceSets.main.allSource
        }

        project.task('javadocJar', type: Jar) << {
            from project.javadoc.destinationDir
        }

        project.publishing {
            publications {
                maven(MavenPublication) {
                    from project.components.java
                    groupId config.groupId
                    artifactId config.artifactId

                    artifact project.sourceJar {
                        classifier = 'sources'
                    }
                    artifact project.javadocJar {
                        classifier = 'javadoc'
                    }
                }
            }
        }

        def repositoryProperty = isSnapshot(project) ? "mavenSnapshotRepository" : "mavenReleaseRepository"
        def repositoryUrl = config.getProperty(repositoryProperty)

        if (repositoryUrl) {
            project.publishing.repositories.maven {
                url repositoryUrl
                if (config.hasMavenRepositoryCredentials()) {
                    credentials {
                        username config.mavenRepositoryUsername
                        password config.mavenRepositoryPassword
                    }
                }
            }
        }
        project.publish.doFirst {
            assert repositoryUrl : "Cannot publish to maven repository, " +
                    "no $repositoryProperty defined in basicPublishing configuration block"
        }
    }

    private static void applyBintrayPublishingConfig(Project project) {
        project.bintrayUpload.doFirst {
            assert !isSnapshot(project) : "Cannot publish snapshot versions to Bintray"
        }

        project.bintray {
            user = user ?: System.getenv('BINTRAY_USER')
            key = key ?: System.getenv('BINTRAY_KEY')
            publications = ['maven']
            pkg {
                repo = repo ?: 'maven'
                name = name ?: project.rootProject.name
            }
        }
    }

    private static void applyPluginPublishingConfig(Project project, BasicPublishingConfig config) {
        project.publishPlugins.doFirst {
            assert config.pluginImplementationClass : "Implementation class must be specified in order to publish plugin"
            assert !isSnapshot(project): "Cannot publish snapshot versions of plugins"
        }

        if (config.pluginImplementationClass != null) {
            registerPluginBundle(project, config)
            applyPluginPortalCredentials(project, config)
            generatePropertiesFile(project, config)
        }
    }

    private static void registerPluginBundle(Project project, config) {
        project.pluginBundle {
            plugins {
                plugin {
                    id = config.pluginId
                    displayName = config.pluginDisplayName
                }
            }
        }
    }

    private static void generatePropertiesFile(Project project, BasicPublishingConfig config) {
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
    private static void applyPluginPortalCredentials(Project project, BasicPublishingConfig config) {
        project.tasks.publishPlugins.doFirst {
            config.applyPluginPortalCredentials()
        }
    }

    private static boolean isSnapshot(Project project) {
        project.version.endsWith("-SNAPSHOT")
    }
}
