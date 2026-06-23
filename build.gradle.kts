plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
}

if (!project.hasProperty("full")) {
    project.gradle.startParameter.excludedTaskNames.add("kspKotlin")
}

group = "com.zephy.zjs"
version = property("mod_version").toString()

dependencies {
    implementation(project(":rhino"))
    include(project(":rhino"))

    val universalCraftVersion = project.findProperty("universalcraft").toString()
    modImplementation(include("gg.essential:universalcraft-${universalCraftVersion}:${libs.versions.universalcraft.get()}")!!)
    implementation(include("gg.essential:vigilance:${libs.versions.vigilance.get()}")!!)
    implementation(include("gg.essential:elementa:${libs.versions.elementa.get()}")!!)
    modImplementation(libs.bundles.included) { include(this) }

    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":typing-generator"))
    ksp(project(":typing-generator"))
}

loom {
    val local = file("src/main/resources/zjs.accesswidener")
    val global = file("../../src/main/resources/zjs.accesswidener")
    accessWidenerPath.set(if (local.exists()) local else global)

    runConfigs {
        named("client") {
            ideConfigGenerated(true)
            programArgs("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
        }
    }
}

base {
    archivesName.set(property("archives_base_name") as String)
}

tasks {
    processResources {
        val minecraftVersion = project.platform.mcVersionStr
        val version = project.version
        val minFabricApiVersion = project.findProperty("min-fabric-api")?.toString()

        inputs.property("version", version)
        inputs.property("minecraft_version", minecraftVersion)
        inputs.property("min_fabric_api_version", minFabricApiVersion.toString())

        filesMatching("fabric.mod.json") {
            expand(mapOf(
                "version" to version,
                "minecraft_version" to minecraftVersion,
                "min_fabric_api_version" to minFabricApiVersion,
            ))
        }

        val javaVersion = project.java.toolchain.languageVersion.get().asInt()
        inputs.property("compatibilityLevel", javaVersion)
        filesMatching("zjs.mixins.json") {
            filter { line ->
                line.replace("JAVA_\$compatibilityLevel", "JAVA_$javaVersion")
            }
        }
    }
}

afterEvaluate {
    val hasRemapJar = tasks.findByName("remapJar") != null
    val outputTaskName = if (hasRemapJar) "remapJar" else "jar"

    tasks.register<Copy>("collectJars") {
        group = "build"
        description = "Copies this version's non-shadowed JARs to main/jars"

        val outputDir = projectDir.resolve("../../jars").normalize()
        dependsOn(outputTaskName)

        from(tasks.named(outputTaskName)) {
            include("*.jar")
            exclude { it.name.contains(" 1.2") && it.name.contains("-all") }
            rename {
                "${rootProject.name}-${version}+${project.platform.mcVersionStr}.jar"
            }
        }
        into(outputDir)

//        if (project.platform.mcVersion == 12111) {
//            into(file("${System.getenv("APPDATA")}/ModrinthApp/profiles/ChatTriggers 1.21.11/mods"))
//        }
//        if (project.platform.mcVersion == 260102) {
//            into(file("${System.getenv("APPDATA")}/ModrinthApp/profiles/Test New ChatTriggers 26.1.2/mods"))
//        }
        if (project.platform.mcVersion == 260200) {
            into(file("${System.getenv("APPDATA")}/ModrinthApp/profiles/Test ZLS 26.2/mods"))
        }
    }

    tasks.named("build") {
        finalizedBy("collectJars")
    }

    configurations.named("default") {
        isCanBeConsumed = true
        isCanBeResolved = false
    }

    artifacts {
        add("default", tasks.named(outputTaskName))
    }
}
