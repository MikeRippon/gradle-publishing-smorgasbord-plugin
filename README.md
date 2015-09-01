# What is it?

A plugin that greatly simplifies publishing of Java/Groovy projects in return for a number of trade-offs.

# Assumptions

- You want to publish only a single artifact per-module
- You are publishing plain old Java/Groovy or a Gradle plugin
- You are happy publishing using one of the few supported methods (feel free to add more!)

# I want to publish a Java/Groovy library

1. Make sure your projects and modules are named sensibly, as this will determine the artifact names (see below)
2. [Apply the plugin](https://plugins.gradle.org/plugin/uk.co.littlemike.publishing-smorgasbord-plugin)
3. Set your version and organisation. The organisation will be used as the first part of the artifact group id and 
   should probably match your package naming for consistency
```
version = '0.1-SNAPSHOT'
basicPublishing {
    organisation = 'org.foo'
}
```
4. You're done! Publish using one of the methods below

## What does this give me?

- The maven GroupID will default to `<organisation>.<root project name>`
- The artifact name will be `<project name>-<version>`

## Publish to maven local

- Use the standard gradle task `publishToMavenLocal`

## Publish to Bintray

1. Create a repository called `maven`
2. Create your package manually in Bintray. The package name *must* be set to the `root project name`
3. Set your bintray username and api key as environment variables `BINTRAY_USER` and `BINTRAY_KEY` 
4. Use the usual gradle task `bintrayUpload`

# I want to publish a Gradle plugin

1. Add your plugin implementation class to the config. The plugin id will default to `<organisation>.<project name>`
```
basicPublishing {
    organisation = 'org.foo'
    implementationClass = 'org.foo.bar.Baz'
}
```
2. No need to create your plugin properties file, it will be generated and included in the plugin jar automatically
3. Publish using the usual task `publishPlugins`