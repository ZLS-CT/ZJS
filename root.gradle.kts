plugins {
    kotlin("jvm") version "2.4.10" apply false
    id("gg.essential.multi-version.root")
}

preprocess.strictExtraMappings.set(true)
preprocess {
    val fabric26_02_00 = createNode("26.2-fabric",   26_02_00, null)
    val fabric26_01_02 = createNode("26.1.2-fabric", 26_01_02, null)

    fabric26_02_00.link(fabric26_01_02)
}

subprojects {
    afterEvaluate {
        tasks.findByName("preprocessTestCode")?.enabled = false
    }
}
