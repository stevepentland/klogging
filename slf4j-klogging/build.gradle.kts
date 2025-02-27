/*

   Copyright 2021-2023 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

plugins {
    id("klogging-kotlin-jvm")
    id("klogging-spotless")
    id("klogging-publishing")
    alias(libs.plugins.testLogger)
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

kotlin {
    explicitApi()

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }

    sourceSets.all {
        languageSettings.apply {
            languageVersion = "1.8"
            apiVersion = "1.6"
        }
    }
}

dependencies {
    api(libs.klogging)
    api(libs.slf4j)

    testImplementation(libs.kotest.junit)
    testImplementation(libs.kotest.datatest)
    testImplementation(libs.html.reporter)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

testlogger {
    showPassed = false
    showSkipped = true
    showFailed = true
}
