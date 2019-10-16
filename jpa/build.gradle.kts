/*
 * Copyright 2019 Andrew Geery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import dev.bombinating.gradle.jooq.*

val genDir = "$projectDir/generated/src/main/java"
val jooqVersion: String by project

plugins {
    java
    id("dev.bombinating.jooq-codegen")
}

sourceSets["main"].java {
    srcDirs(genDir)
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compile(group = "org.jooq", name = "jooq", version = jooqVersion)
    jooqRuntime(project(":entity"))
}

jooq {
    version = jooqVersion
    generator {
        database {
            name = "org.jooq.meta.extensions.jpa.JPADatabase"
            properties {
                property("packages" to "dev.bombinating.gradle.jooq.example.entity")
                property("useAttributeConverters" to "true")
                property("unqualifiedSchema" to "true")
            }
        }
        target {
            directory = genDir
            packageName = "dev.bombinating.gradle.jooq.example.jpa"
        }
    }
}

tasks.register<Delete>("cleanGenerated") {
    delete(genDir)
}

tasks.getByName("compileJava").dependsOn(tasks.getByName("jooq"))
