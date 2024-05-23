plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    //akka dependecies
    //implementation ("com.typesafe.akka:akka-actor-typed_2.13:2.6.18")

    implementation("com.typesafe.akka:akka-actor-typed_2.13:2.6.15")
    implementation("com.typesafe.akka:akka-cluster-typed_2.13:2.6.15")
    implementation("com.typesafe.akka:akka-remote_2.13:2.6.15")

    //test dependecies
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}