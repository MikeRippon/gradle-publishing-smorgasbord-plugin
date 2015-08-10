package uk.co.littlemike.gradle.publishing

import org.gradle.api.Project

class BasicPublishingConfig {
    String organisation
    String artifactId
    String pluginImplementationClass
    String pluginDisplayName

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
}

