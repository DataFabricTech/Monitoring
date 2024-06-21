plugins {
    id("java")
}

group = "com.mobigen"
version = "1.0-SNAPSHOT"

allprojects {
    group = "${group}.monitoring"
    version = "1.0-SNAPSHOT"
}

repositories {
    mavenCentral()
}

object Dependencies {
    object Versions {
        const val SPRING_BOOT_VER = "3.3.0"
        const val JUNIT = "5.9.3"
        const val H2BASE = "2.2.224"
        const val LOMBOK_VER = "1.18.30"
        const val OKHTTP = "4.12.0"
        const val JWT = "0.9.1"
        const val JSON = "1.1.1"
        const val MYSQL = "8.0.28"
        const val POSTGRESQL = "42.7.3"
    }

    object Spring {
        const val BOOT = "org.springframework.boot:spring-boot-starter:${Versions.SPRING_BOOT_VER}"
        const val BOOT_STARTER = "org.springframework.boot:spring-boot-starter:${Versions.SPRING_BOOT_VER}"
        const val STARTER_WEB = "org.springframework.boot:spring-boot-starter-web:${Versions.SPRING_BOOT_VER}"
        const val JPA = "org.springframework.boot:spring-boot-starter-data-jpa:${Versions.SPRING_BOOT_VER}"

        const val TEST = "org.springframework.boot:spring-boot-starter-test:${Versions.SPRING_BOOT_VER}"
    }

    object JWP {
        const val JWT = "io.jsonwebtoken:jjwt:${Versions.JWT}"
    }

    object OkHttp {
        const val OkHttp = "com.squareup.okhttp3:mockwebserver:${Versions.OKHTTP}"
    }

    object Junit {
        const val BOM = "org.junit:junit-bom:${Versions.JUNIT}"
        const val JUPITER = "org.junit.jupiter:junit-jupiter:${Versions.JUNIT}"
    }

    object H2Base {
        const val H2BASE = "com.h2database:h2:${Versions.H2BASE}"
    }

    object Lombok {
        const val LOMBOK = "org.projectlombok:lombok:${Versions.LOMBOK_VER}"
    }

    object Json {
        const val JSON = "com.googlecode.json-simple:json-simple:${Versions.JSON}"
    }

    object DB {
        const val MYSQL = "mysql:mysql-connector-java:${Versions.MYSQL}"
        const val POSTGRESQL = "org.postgresql:postgresql:${Versions.POSTGRESQL}"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring
    implementation(Dependencies.Spring.BOOT)
    implementation(Dependencies.Spring.BOOT_STARTER)
    implementation(Dependencies.Spring.STARTER_WEB)

    // JPA
    implementation(Dependencies.Spring.JPA)
    implementation(Dependencies.H2Base.H2BASE)

    // Lombok
    annotationProcessor(Dependencies.Lombok.LOMBOK)
    implementation(Dependencies.Lombok.LOMBOK)

    // OKHttp
    implementation(Dependencies.OkHttp.OkHttp)

    // JWT
    implementation(Dependencies.JWP.JWT)

    // Json
    implementation(Dependencies.Json.JSON)

    // DB
    implementation(Dependencies.DB.POSTGRESQL)

    // Test
    testImplementation(platform(Dependencies.Junit.BOM))
    testImplementation(Dependencies.Junit.JUPITER)
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes["Main-Class"] = "com.mobigen.monitoring.MonitoringApplication"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
