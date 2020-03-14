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
import dev.bombinating.gradle.jooq.generate
import dev.bombinating.gradle.jooq.generator
import dev.bombinating.gradle.jooq.jdbc
import dev.bombinating.gradle.jooq.matchers
import dev.bombinating.gradle.jooq.pojoClass
import dev.bombinating.gradle.jooq.strategy
import dev.bombinating.gradle.jooq.tables
import dev.bombinating.gradle.jooq.target
import org.jooq.meta.jaxb.GeneratedAnnotationType
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.GeneratedAnnotationType.JAVAX_ANNOTATION_GENERATED

val genDir = "$projectDir/generated/src/main/java"

val h2Version: String by project
val jooqVersion: String by project

val dbUrl: String = "jdbc:h2:$buildDir/db/test_db;AUTO_SERVER=true"
val dbUsername: String by project
val dbPassword: String by project

plugins {
    java
    id("dev.bombinating.jooq-codegen")
    id("org.flywaydb.flyway")
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
    runtime(group = "com.h2database", name = "h2", version = h2Version)
    jooqRuntime(group = "com.h2database", name = "h2", version = h2Version)
}

flyway {
    url = dbUrl
    user = dbUsername
    password = dbPassword
}

jooq {
    version = jooqVersion
    jdbc {
        url = dbUrl
        username = dbUsername
        password = dbPassword
    }
    generator {
        database {
            inputSchema = "PUBLIC"
            excludes = "FlywaySchemaHistory"
        }
        target {
            directory = genDir
            packageName = "dev.bombinating.gradle.jooq.example.simple"
        }
        generate {
            isGeneratedAnnotation = true
        }
    }
    logging = Logging.WARN
}

task<Delete>("cleanGenerated") {
    delete(genDir)
}

tasks.getByName("jooq").dependsOn(tasks.getByName("flywayMigrate"))
tasks.getByName("compileJava").dependsOn(tasks.getByName("jooq"))
