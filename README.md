# What is it?

A plugin that greatly simplifies publishing of Java/Groovy projects in return for a number of trade-offs.

# Assumptions

- You want to publish only a single artifact per-module
- You are publishing plain old Java/Groovy or a Gradle plugin
- You are happy publishing using one of the few supported methods (feel free to add more!)

# I want to publish a Java/Groovy library

- Make sure your projects and modules are named sensibly, as this will determine the artifact names (see below)
- [Apply the plugin](https://plugins.gradle.org/plugin/uk.co.littlemike.publishing-smorgasbord-plugin)
- Set your version and organisation. The organisation will be used as the first part of the artifact group id and 
   should probably match your package naming for consistency
```
version = '0.1-SNAPSHOT'
basicPublishing {
    organisation = 'org.foo'
}
```

You're done! Publish using one of the methods below

## What does this give me?

The maven GroupID will default to `<organisation>.<root project name>`
The artifact id will be `<project name>`
So your final artifact will look something like `org.foo.bar-project:baz-module:1.0`

## Publish to maven local

Use the standard gradle task `publishToMavenLocal`

## Publish to Bintray

- Create a repository called `maven`
- Create your package manually in Bintray. The package name *must* be set to the `root project name`
- Set your bintray username and api key as environment variables `BINTRAY_USER` and `BINTRAY_KEY` 
- Use the usual gradle task `bintrayUpload`

# I want to publish a Gradle plugin

- Add your plugin implementation class to the config. The plugin id will default to `<organisation>.<project name>`
```
basicPublishing {
    organisation = 'org.foo'
    implementationClass = 'org.foo.bar.Baz'
}
```
- No need to create your plugin properties file, it will be generated and included in the plugin jar automatically
- Set your gradle credentials as environment variables `gradlePublishKey` and `gradlePublishSecret`
- Publish using the usual task `publishPlugins`