plugins {
    id("java")
}

group = "com.tree3"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("net.dv8tion:JDA:6.3.0")
    // Source: https://mvnrepository.com/artifact/io.github.cdimascio/dotenv-java
    implementation("io.github.cdimascio:dotenv-java:3.2.0")
    annotationProcessor(project(":processor"))
    compileOnly(project(":processor"))
    // Source: https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.18")
    // Source: https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.5.32")
    implementation("org.scilab.forge:jlatexmath:1.0.7")
}
tasks.register<JavaExec>("runBot") {
    dependsOn("assemble")
    group = "application"
    description = "Runs the Bot"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.tree3.Main")
}

tasks.test {
    useJUnitPlatform()
}