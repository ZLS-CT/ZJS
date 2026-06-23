package com.zephy.zjs.api

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.spongepowered.asm.mixin.transformer.ClassInfo
import org.spongepowered.asm.service.MixinService

object Mappings {
    // If this is changed, also change the Java.type function in mixinProvidedLibs.js
    internal val mappedPackages = setOf("Lnet/minecraft/", "Lcom/mojang/blaze3d/")

    val mappedClassMap = mutableMapOf<String, MappedClass>()

    internal fun getMappedClass(className: String): MappedClass? {
        val name = normalizeClassName(className)

        if (mappedPackages.none { name.startsWith(it.drop(1).dropLast(1)) }) return null

        return mappedClassMap.getOrPut(name) {
            try {
                loadClass(name)
            } catch (e: ClassNotFoundException) {
                return null
            }
        }
    }

    internal fun getMappedClassName(className: String): String? =
        getMappedClass(className)?.name

    /**
     * Gets a classes unmapped class name, or throws an error if it is not mapped
     */
    @JvmStatic
    fun unmapClass(clazz: Class<*>) = clazz.name.replace('.', '/')

    /**
     * Gets an unmapped class name from a mapped class name, or returns null if
     * it either does not exist or is not mapped.
     */
    @JvmStatic
    fun unmapClassName(className: String) = normalizeClassName(className)

    /**
     * Gets the mapped class name from an unmapped class name or null if the class
     * name does not exist. Note that this is not required to use mapped classes,
     * as Rhino performs this mapping automatically during runtime.
     */
    @JvmStatic
    fun mapClassName(className: String) = normalizeClassName(className)

    private fun normalizeClassName(className: String) =
        (if (className.startsWith('L') && className.endsWith(';')) {
            className.drop(1).dropLast(1)
        } else {
            className.removeSuffix(".class")
        }).replace('.', '/')


    private fun loadClass(name: String): MappedClass {
        val classNode = MixinService.getService().bytecodeProvider.getClassNode(name)

        val fields = mutableMapOf<String, MappedField>()
        classNode.fields.forEach {
            fields[it.name] = MappedField(it.name, it.desc)
        }

        val methods = mutableMapOf<String, MutableList<MappedMethod>>()
        for (method in classNode.methods) {
            val isStatic = method.access and Opcodes.ACC_STATIC != 0
            var lvtIndex = if (isStatic) 0 else 1

            val params = mutableListOf<MappedParameter>()
            Type.getArgumentTypes(method.desc).forEachIndexed { index, type ->
                val paramName = method.parameters?.get(index)?.name ?: "p$index"
                params.add(MappedParameter(paramName, type.descriptor, lvtIndex))
                lvtIndex += if (type == Type.DOUBLE_TYPE || type == Type.LONG_TYPE) 2 else 1
            }

            methods.getOrPut(method.name, ::mutableListOf)
                .add(MappedMethod(method.name, params, Type.getReturnType(method.desc).descriptor))
        }

        return MappedClass(name = name, fields = fields, methods = methods)
    }

    data class MappedField(val name: String, val type: String)

    class MappedParameter(val name: String, val type: String, val lvtIndex: Int)

    class MappedMethod(
        val name: String,
        val parameters: List<MappedParameter>,
        val returnType: String,
    ) {
        fun toDescriptor() = buildString {
            append('(')
            parameters.forEach { append(it.type) }
            append(')')
            append(returnType)
        }

        fun toFullDescriptor() = name + toDescriptor()
    }

    class MappedClass(
        val name: String,
        val fields: Map<String, MappedField>,
        val methods: Map<String, List<MappedMethod>>,
    ) {
        fun findMethods(name: String, classInfo: ClassInfo?): List<MappedMethod>? {
            methods[name]?.let { return it }
            if (classInfo == null) return null

            val superClass = mappedClassMap[classInfo.superName]
            if (superClass != null) {
                return superClass.findMethods(name, classInfo.superClass)
            }

            val inherited = mutableListOf<MappedMethod>()
            for (itf in classInfo.interfaces) {
                mappedClassMap[itf]?.findMethods(name, null)?.let { inherited += it }
            }

            return if (inherited.isEmpty()) null else inherited
        }
    }
}
