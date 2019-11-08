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
import dev.bombinating.gradle.jooq.database
import dev.bombinating.gradle.jooq.generator
import dev.bombinating.gradle.jooq.jdbc
import dev.bombinating.gradle.jooq.target
import org.jooq.meta.jaxb.Logging

val genDir = "$projectDir/generated/src/main/java"

val dbUrl: String = "jdbc:h2:$buildDir/db/test_db;AUTO_SERVER=true"
val dbUsername: String by project
val dbPassword: String by project

plugins {
    java
    id("org.springframework.boot")
    id("dev.bombinating.jooq-codegen")
    id("org.flywaydb.flyway")
}

apply(plugin = "io.spring.dependency-management")

sourceSets["main"].java {
    srcDirs(genDir)
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compile(group = "org.springframework.boot", name = "spring-boot-starter-jooq")
    runtime(group = "com.h2database", name = "h2")
    jooqRuntime(group = "com.h2database", name = "h2")
}

flyway {
    url = dbUrl
    user = dbUsername
    password = dbPassword
    this.configFiles
}

jooq {
    version = "3.11.11" // Note: this overrides and sets the jooq.version value in Spring Boot
    jdbc {
        url = dbUrl
        username = dbUsername
        password = dbPassword
    }
    generator {
        database {
            excludes = "flyway_schema_history"
            includes = "public.*"
        }
        target {
            directory = genDir
            packageName = "dev.bombinating.gradle.jooq.example.boot"
        }
    }
    logging = Logging.INFO
}

task<Delete>("cleanGenerated") {
    delete(genDir)
}

tasks.getByName("jooq").dependsOn(tasks.getByName("flywayMigrate"))
tasks.getByName("compileJava").dependsOn(tasks.getByName("jooq"))
