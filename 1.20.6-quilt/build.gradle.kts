plugins {
    id("dev.architectury.loom") version "1.6-SNAPSHOT"
}

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21
java.toolchain.languageVersion = JavaLanguageVersion.of(21)
group = "ru.vidtu.ias"
base.archivesName = "IAS-Quilt-1.20.6"
evaluationDependsOn(":1.20.6")
val shared = project(":1.20.6")

repositories {
    mavenCentral()
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.terraformersmc.com/releases/")
}

loom {
    silentMojangMappingsLicense()
    runs.named("client") {
        vmArgs(
            "-XX:+IgnoreUnrecognizedVMOptions",
            "-Xmx2G",
            "-XX:+AllowEnhancedClassRedefinition",
            "-XX:HotswapAgent=fatjar",
            "-Dfabric.debug.disableClassPathIsolation=true"
        )
    }
    @Suppress("UnstableApiUsage")
    mixin {
        defaultRefmapName = "ias.mixins.refmap.json"
    }
}

dependencies {
    // Minecraft
    minecraft("com.mojang:minecraft:1.20.6")
    mappings(loom.officialMojangMappings())

    // Quilt
    modImplementation(libs.mixin) // <- Future me, test if this is still needed
    modImplementation(libs.quilt.loader)
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.98.0+1.20.6")
    modImplementation("com.terraformersmc:modmenu:10.0.0-beta.1")

    // Root
    compileOnly(shared)
}

tasks.withType<JavaCompile> {
    source(rootProject.sourceSets.main.get().java)
    source(shared.sourceSets.main.get().java)
    options.encoding = "UTF-8"
    options.release = 21
}

tasks.withType<ProcessResources> {
    from(rootProject.sourceSets.main.get().resources)
    from(shared.sourceSets.main.get().resources)
    inputs.property("version", project.version)
    filesMatching("quilt.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<Jar> {
    from(rootDir.resolve("LICENSE"))
    from(rootDir.resolve("GPL"))
    from(rootDir.resolve("NOTICE"))
    manifest {
        attributes(
            "Specification-Title" to "In-Game Account Switcher",
            "Specification-Version" to project.version,
            "Specification-Vendor" to "VidTu",
            "Implementation-Title" to "IAS-Quilt-1.20.6",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "VidTu"
        )
    }
}
