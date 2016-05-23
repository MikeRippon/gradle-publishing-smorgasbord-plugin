package uk.co.littlemike.gradle.publishing

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class BasicPublishingConfigTest {

    Project project
    BasicPublishingConfig config

    @Before
    void setUp() {
        project = ProjectBuilder.builder().build()
        config = new BasicPublishingConfig(project)
    }

    @Test
    void groupIdUsesOrganisationAndRootProjectName() {
        config.organisation = 'my.org'

        assert config.groupId == 'my.org.' + project.rootProject.name
    }

    @Test
    void artifactIdDefaultsToProjectName() {
        assert config.artifactId == project.name
    }

    @Test
    void pluginDisplayNameDefaultsToProjectName() {
        assert config.pluginDisplayName == project.name
    }

    @Test
    void pluginDisplayNameIsArtifactNameIfSpecified() {
        config.artifactId = 'my-artifact'

        assert config.pluginDisplayName == 'my-artifact'
    }

    @Test
    void pluginIdDefaultsToOrganisationAndProjectName() {
        config.organisation = 'my.org'

        assert config.pluginId == 'my.org.' + project.name
    }

    @Test
    void pluginIdUsesArtifactNameIfSpecified() {
        config.organisation = 'my.org'
        config.artifactId = 'my-plugin'

        assert config.pluginId == 'my.org.my-plugin'
    }

}
