plugins {
    id("java")
}

group = "com.mobigen"
version = "1.0-SNAPSHOT"

object Dependencies {
    object Versions {
        const val SPRING_BOOT_VER = "3.2.1"
        const val JUNIT = "5.9.3"
        const val H2Base = "2.2.224"
        const val LOMBOK_VER = "1.18.30"
        const val OkHttp = "4.12.0"
        const val JWT = "0.9.1"
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
        const val OkHttp = "com.squareup.okhttp3:mockwebserver:${Versions.OkHttp}"
    }

    object Junit {
        const val BOM = "org.junit:junit-bom:${Versions.JUNIT}"
        const val JUPITER = "org.junit.jupiter:junit-jupiter:${Versions.JUNIT}"
    }

    object H2Base {
        const val H2BASE = "com.h2database:h2:${Versions.H2Base}"
    }

    object Lombok {
        const val LOMBOK = "org.projectlombok:lombok:${Versions.LOMBOK_VER}"
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
