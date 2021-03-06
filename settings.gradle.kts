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

pluginManagement {
    val flywayPluginVersion: String by settings
    val jooqPluginVersion: String by settings
    val releasePluginVersion: String by settings
    val springBootPluginVersion: String by settings
    plugins {
        id("dev.bombinating.jooq-codegen") version jooqPluginVersion
        id("org.flywaydb.flyway") version flywayPluginVersion
        id("net.researchgate.release") version releasePluginVersion
        id("org.springframework.boot") version springBootPluginVersion
    }
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

include("simple", "entity", "jpa", "task", "task-only", "groovy", "spring-boot")