#!/bin/bash
if ./gradlew --stacktrace build publishToMavenLocal; then
    ./gradlew --stacktrace --settings-file=test-settings.gradle testPlugin
fi