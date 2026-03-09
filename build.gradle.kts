plugins {
    java
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.3"
    jacoco
    id("org.sonarqube") version "7.1.0.6387"
    checkstyle
}

val dataFakerVersion = "2.0.2"
val mapstructVersion = "1.5.5.Final"
val lombokVersion = "1.18.30"
val jacksonNullableVersion = "0.2.6"
val junitVersion = "5.10.0"
val jsonUnitAssertjVersion = "3.2.2"
val instancioVersion = "3.3.0"
val lombokMapstructBindingVersion = "0.2.0"

group = "io.hexlet"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("net.datafaker:datafaker:$dataFakerVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("org.openapitools:jackson-databind-nullable:$jacksonNullableVersion")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:$jsonUnitAssertjVersion")
    testImplementation("org.instancio:instancio-junit:$instancioVersion")
    testImplementation("org.springframework.security:spring-security-test")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:$lombokMapstructBindingVersion")

}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    systemProperty("spring.profiles.active", "development")
}

sonar {
    properties {
        property("sonar.projectKey", "Textile86_hexlet-spring-blog")
        property("sonar.organization", "textile86")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.coverage.exclusions",
            "src/main/java/io/hexlet/spring/dto/**," +
                    "src/main/java/io/hexlet/spring/model/**," +
                    "src/main/java/io/hexlet/spring/component/DataSeeder.java," +
                    "src/main/java/io/hexlet/spring/component/ModelGenerator.java," +
                    "src/main/java/io/hexlet/spring/Application.java"
        )
    }
}

checkstyle {
    toolVersion = "10.12.4"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)  // 🆕
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}