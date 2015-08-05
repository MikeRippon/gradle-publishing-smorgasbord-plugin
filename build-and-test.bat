call gradlew.bat --stacktrace build publishToMavenLocal
call gradlew.bat --stacktrace --settings-file=test-settings.gradle testPlugin
pause