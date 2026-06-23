package com.zephy.zjs.internal.launch.generation

import com.zephy.zjs.api.Mappings
import com.zephy.zjs.internal.launch.Descriptor
import com.zephy.zjs.internal.launch.DynamicMixinManager
import com.zephy.zjs.internal.launch.Mixin
import org.spongepowered.asm.mixin.transformer.ClassInfo

internal data class GenerationContext(val mixin: Mixin) {
    val mappedClass = Mappings.getMappedClass(mixin.target)
        ?: error("Unknown class name ${mixin.target}")
    val generatedClassName = "CTMixin_\$${mixin.target.replace('.', '_')}\$_${mixinCounter++}"
    val generatedClassFullPath = "${DynamicMixinManager.GENERATED_PACKAGE}/$generatedClassName"

    fun findMethod(method: String): Pair<Mappings.MappedMethod, ClassInfo.Method> {
        val descriptor = Descriptor.Parser(method).parseMethod(full = false)
        return Utils.findMethod(mappedClass, descriptor)
    }

    companion object {
        private var mixinCounter = 0
    }
}
