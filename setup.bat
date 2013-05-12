
cd %~dp0
rd /S /Q .settings
rd /S /Q target
rd /S /Q work

call mvn clean
call mvn package
call mvn install
call mvn -DdownloadSources=true -DdownloadJavadocs=true -DoutputDirectory=target/eclipse-classes eclipse:eclipse
