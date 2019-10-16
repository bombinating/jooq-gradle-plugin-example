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
import org.flywaydb.gradle.task.FlywayMigrateTask

val genDir = "$projectDir/generated/src/main/java"
val genDir2 = "$projectDir/generated2/src/main/java"

val h2Version: String by project
val jooqVersion: String by project

val dbUrl: String = "jdbc:h2:$buildDir/db/test_db;AUTO_SERVER=true"
val dbUsername: String by project
val dbPassword: String by project

val dbUrl2: String = "jdbc:h2:$buildDir/db/test_db2;AUTO_SERVER=true"
val dbUsername2: String by project
val dbPassword2: String by project

plugins {
    java
    id("dev.bombinating.jooq-codegen")
    id("org.flywaydb.flyway")
}

sourceSets["main"].java {
    srcDirs(genDir, genDir2)
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

val flywayMigrate2 = task<FlywayMigrateTask>("flyway2") {
    url = dbUrl2
    user = dbUsername2
    password = dbPassword2
    locations = arrayOf("filesystem:$projectDir/src/main/resources/db2/migration")
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
            packageName = "dev.bombinating.db"
        }
    }
}

val jooq2 = task<JooqTask>("jooq2") {
    jdbc {
        url = dbUrl2
        username = dbUsername2
        password = dbPassword2
    }
    generator {
        database {
            inputSchema = "PUBLIC"
            excludes = "FlywaySchemaHistory"
        }
        target {
            directory = genDir2
            packageName = "dev.bombinating.db2"
        }
    }
    dependsOn(flywayMigrate2)
}

task<Delete>("cleanGenerated") {
    delete(genDir, genDir2)
}

tasks.getByName("jooq").dependsOn(tasks.getByName("flywayMigrate"))
tasks.getByName("compileJava").dependsOn(tasks.getByName("jooq"), jooq2)
