package uk.co.littlemike.gradle.publishing

import org.gradle.api.Project

class BasicPublishingConfig {
    String organisation
    String artifactId

    String pluginImplementationClass
    String pluginDisplayName
    String pluginPortalKey
    String pluginPortalSecret

    String mavenSnapshotRepository
    String mavenReleaseRepository
    String mavenRepositoryUsername
    String mavenRepositoryPassword

    Project project

    public BasicPublishingConfig(Project project) {
        this.project = project
    }

    public void validate() {
        assert organisation != null : "Organisation must be specified (eg. com.foo)"
    }

    public String getArtifactId() {
        artifactId ?: project.name
    }

    public String getGroupId() {
        "${organisation}.${project.rootProject.name}"
    }

    public String getPluginDisplayName() {
        pluginDisplayName ?: artifactId
    }

    public String getPluginId() {
        "${organisation}.${artifactId}"
    }

    public applyPluginPortalCredentials() {
        if (getPluginPortalKey()) {
            System.properties.setProperty("gradle.publish.key", getPluginPortalKey())
        }
        if (getPluginPortalSecret()) {
            System.properties.setProperty("gradle.publish.secret", getPluginPortalSecret())
        }
    }

    public String getPluginPortalKey() {
        return System.getenv('gradlePublishKey') ?: pluginPortalKey
    }

    public String getPluginPortalSecret() {
        return System.getenv('gradlePublishSecret') ?: pluginPortalSecret
    }

    public boolean hasMavenRepositoryCredentials() {
        return getMavenRepositoryUsername()
    }

    public String getMavenRepositoryUsername() {
        System.getenv('MAVEN_REPOSITORY_USERNAME') ?: mavenRepositoryUsername
    }

    public String getMavenRepositoryPassword() {
        System.getenv('MAVEN_REPOSITORY_PASSWORD') ?: mavenRepositoryPassword
    }
}

