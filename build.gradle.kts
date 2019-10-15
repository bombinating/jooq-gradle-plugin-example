import dev.bombinating.gradle.jooq.*
import org.jooq.meta.jaxb.Logging

val genDir = "$projectDir/generated/src/main/java"

val h2Version: String by project
val jooqVersion: String by project

val dbUrl: String by project
val dbUsername: String by project
val dbPassword: String by project

plugins {
    java
    id("dev.bombinating.jooq-codegen")
    id("org.flywaydb.flyway")
}

sourceSets["main"].java {
    srcDir(genDir)
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
    logging = Logging.DEBUG
    jdbc {
        url = dbUrl
        username = dbUsername
        password = dbPassword
    }
    generator {
        database {
            inputSchema = "PUBLIC"
        }
        target {
            directory = genDir
            packageName = "dev.bombinating.db"
        }
    }
}

tasks.register("cleanGenerated") {
    doLast {
        delete(genDir)
    }
}

tasks.getByName("jooq").dependsOn(tasks.getByName("flywayMigrate"))
tasks.getByName("compileJava").dependsOn(tasks.getByName("jooq"))
