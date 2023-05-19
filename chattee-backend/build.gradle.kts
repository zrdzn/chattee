group = "io.github.zrdzn.web.chattee.backend"

dependencies {
    val javalin = "5.3.0"
    implementation("io.javalin:javalin:$javalin")

    val javalinRoutingExtensions = "5.3.2-alpha.1-SNAPSHOT"
    implementation("io.javalin.community.routing:routing-core:$javalinRoutingExtensions")
    implementation("io.javalin.community.routing:routing-annotated:$javalinRoutingExtensions")

    val openapi = "5.3.0-alpha.8-SNAPSHOT"
    annotationProcessor("io.javalin.community.openapi:openapi-annotation-processor:$openapi")
    implementation("io.javalin.community.openapi:javalin-openapi-plugin:$openapi")
    implementation("io.javalin.community.openapi:javalin-swagger-plugin:$openapi")

    implementation("org.panda-lang:expressible:1.3.5")
    implementation("org.panda-lang:panda-utilities:0.5.3-alpha")

    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.0-beta.5")

    val hikari = "5.0.1"
    implementation("com.zaxxer:HikariCP:$hikari")

    val slf4j = "2.0.6"
    implementation("org.slf4j:slf4j-simple:$slf4j")

    val tinylog = "2.5.0"
    implementation("org.tinylog:tinylog-impl:$tinylog")

    val bcrypt = "0.9.0"
    implementation("at.favre.lib:bcrypt:$bcrypt")

    val postgres = "42.5.1"
    implementation("org.postgresql:postgresql:$postgres")

    val jackson = "2.14.1"
    implementation("com.fasterxml.jackson.core:jackson-databind:$jackson")
    implementation("com.fasterxml.jackson.module:jackson-modules-base:$jackson")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jackson")

    val junit = "5.9.1"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("chattee-backend-${archiveVersion.get()}.jar")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "io.github.zrdzn.web.chattee.backend.ChatteeLauncher"
    }
}
