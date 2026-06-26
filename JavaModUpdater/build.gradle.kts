plugins {
    java
}

tasks.named<Jar>("jar") {
    archiveFileName.set("JavaModUpdater.jar")
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "com.zephy.zjs.javamodupdater.postexit.PostExitMain"
            )
        )
    }
}
