package com.zephy.zjs.internal.launch

import com.zephy.zjs.api.Mappings
import com.zephy.zjs.internal.engine.JSLoader
import com.zephy.zjs.internal.engine.module.ModuleManager
import com.zephy.zjs.internal.launch.generation.DynamicMixinGenerator
import com.zephy.zjs.internal.launch.generation.GenerationContext
import com.zephy.zjs.internal.launch.generation.Utils
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import org.spongepowered.asm.mixin.Mixins
import java.io.ByteArrayInputStream
import java.net.URI
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler

internal object DynamicMixinManager {
    internal const val GENERATED_PROTOCOL = "zjs-generated"
    internal const val GENERATED_MIXIN = "zjs-generated.mixins.json"
    internal const val GENERATED_PACKAGE = "com/zephy/zjs/generated_mixins"

    lateinit var mixins: Map<Mixin, MixinDetails>

    fun initialize() {
        mixins = JSLoader.mixinSetup(ModuleManager.cachedModules.filter { it.metadata.mixinEntry != null })
    }

    fun applyAccessWideners() {
        for ((mixin, details) in mixins) {
            val mappedClass = Mappings.getMappedClass(mixin.target)
                ?: error("Unknown class name ${mixin.target}")
            for ((field, isMutable) in details.fieldWideners) {
                Utils.widenField(mappedClass, field, isMutable)
            }
            for ((method, isMutable) in details.methodWideners) {
                Utils.widenMethod(mappedClass, method, isMutable)
            }
        }
    }

    fun applyMixins() {
        val dynamicMixins = mutableListOf<String>()

        for ((mixin, details) in mixins) {
            val ctx = GenerationContext(mixin)
            val generator = DynamicMixinGenerator(ctx, details)
            ByteBasedStreamHandler[ctx.generatedClassFullPath + ".class"] = generator.generate()
            dynamicMixins += ctx.generatedClassName
        }

        ByteBasedStreamHandler[GENERATED_MIXIN] = createDynamicMixinsJson(dynamicMixins)

        injectConfiguration()
    }

    private fun createDynamicMixinsJson(mixins: List<String>): ByteArray {
        return buildJsonObject {
            put("required", JsonPrimitive(true))
            put("minVersion", JsonPrimitive("0.8"))
            put("package", JsonPrimitive("com.zephy.zjs.generated_mixins"))
            put("compatibilityLevel", JsonPrimitive("JAVA_17"))
            putJsonObject("injectors") {
                put("defaultRequire", JsonPrimitive(1))
            }

            putJsonArray("client") { mixins.forEach(::add) }
        }.toString().toByteArray()
    }

    private fun injectConfiguration() {
        // Credit to hugeblank and his allium project for this setup
        // https://github.com/hugeblank/allium/blob/mixins/src/main/java/dev/hugeblank/allium/AlliumPreLaunch.java
        val classLoader = DynamicMixinManager::class.java.classLoader
        val addUrlMethod = classLoader::class.java.methods.first { it.name == "addUrlFwd" }
        addUrlMethod.isAccessible = true
        addUrlMethod.invoke(classLoader, ByteBasedStreamHandler.url)

        Mixins.addConfiguration(GENERATED_MIXIN)
    }

    private object ByteBasedStreamHandler : URLStreamHandler() {
        private val classBytes = mutableMapOf<String, ByteArray>()

        val url = URL.of(URI(GENERATED_PROTOCOL, null, "/", ""), ByteBasedStreamHandler)

        operator fun set(path: String, bytes: ByteArray) {
            check(classBytes.put(path, bytes) == null)
        }

        override fun openConnection(url: URL): URLConnection? =
            classBytes[url.path.drop(1)]?.let { Connection(url, it) }

        private class Connection(url: URL, private val bytes: ByteArray) : URLConnection(url) {
            override fun getInputStream() = ByteArrayInputStream(bytes)
            override fun connect() = throw UnsupportedOperationException()
        }
    }
}
