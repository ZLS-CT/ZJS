package com.chattriggers.ctjs.internal.launch.generation

import com.chattriggers.ctjs.api.Mappings
import com.chattriggers.ctjs.internal.launch.At
import com.chattriggers.ctjs.internal.launch.Constant
import com.chattriggers.ctjs.internal.launch.Descriptor
import com.chattriggers.ctjs.internal.launch.Local
import com.chattriggers.ctjs.internal.launch.Slice
import com.chattriggers.ctjs.internal.utils.descriptorString
import net.fabricmc.loader.impl.FabricLoaderImpl
import net.fabricmc.loader.impl.lib.classtweaker.api.visitor.AccessWidenerVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AnnotationNode
import org.spongepowered.asm.mixin.transformer.ClassInfo
import org.spongepowered.asm.mixin.injection.At as SPAt
import org.spongepowered.asm.mixin.injection.Constant as SPConstant
import org.spongepowered.asm.mixin.injection.Slice as SPSlice

internal object Utils {
    fun createAtAnnotation(at: At): AnnotationNode {
        return AnnotationNode(SPAt::class.descriptorString()).apply {
            if (at.id != null) {
                visit("id", at.id)
            }
            visit("value", at.value)
            if (at.slice != null) {
                visit("slice", at.slice)
            }
            if (at.shift != null) {
                visit("shift", arrayOf(SPAt.Shift::class.java.descriptorString(), at.shift.name))
            }
            if (at.by != null) {
                visit("by", at.by)
            }
            if (at.args != null) {
                visit("args", at.args)
            }
            if (at.target != null) {
                visit("target", at.atTarget.descriptor.descriptor())
            }
            if (at.ordinal != null) {
                visit("ordinal", at.ordinal)
            }
            if (at.opcode != null) {
                visit("opcode", at.opcode)
            }

            visitEnd()
        }
    }

    fun createSliceAnnotation(slice: Slice): AnnotationNode {
        return AnnotationNode(SPSlice::class.descriptorString()).apply {
            if (slice.id != null) {
                visit("id", slice.id)
            }
            if (slice.from != null) {
                visit("from", createAtAnnotation(slice.from))
            }
            if (slice.to != null) {
                visit("to", createAtAnnotation(slice.to))
            }
            visitEnd()
        }
    }

    fun createConstantAnnotation(constant: Constant): AnnotationNode {
        return AnnotationNode(SPConstant::class.descriptorString()).apply {
            if (constant.nullValue != null) {
                visit("nullValue", constant.nullValue)
            }
            if (constant.intValue != null) {
                visit("intValue", constant.intValue)
            }
            if (constant.floatValue != null) {
                visit("floatValue", constant.floatValue)
            }
            if (constant.longValue != null) {
                visit("longValue", constant.longValue)
            }
            if (constant.doubleValue != null) {
                visit("doubleValue", constant.doubleValue)
            }
            if (constant.stringValue != null) {
                visit("stringValue", constant.stringValue)
            }
            if (constant.classValue != null) {
                val name = Mappings.getMappedClassName(constant.classValue)
                visit("classValue", Type.getObjectType(name))
            }
            if (constant.ordinal != null) {
                visit("ordinal", constant.ordinal)
            }
            if (constant.slice != null) {
                visit("slice", constant.slice)
            }
            if (constant.expandZeroConditions != null) {
                visit("expandZeroConditions", constant.expandZeroConditions)
            }
            if (constant.log != null) {
                visit("log", constant.log)
            }
        }
    }

    fun widenField(mappedClass: Mappings.MappedClass, fieldName: String, isMutable: Boolean) {
        val field = mappedClass.fields[fieldName]
            ?: error("Unable to find field $fieldName in class ${mappedClass.name}")
        val accessWidener = FabricLoaderImpl.INSTANCE.classTweaker.visitAccessWidener(mappedClass.name)!!

        accessWidener.visitField(
            mappedClass.name,
            field.type,
            AccessWidenerVisitor.AccessType.ACCESSIBLE,
            false,
        )

        if (isMutable) {
            accessWidener.visitField(
                mappedClass.name,
                field.type,
                AccessWidenerVisitor.AccessType.MUTABLE,
                false,
            )
        }
    }

    fun widenMethod(
        mappedClass: Mappings.MappedClass,
        methodName: String,
        isMutable: Boolean,
    ) {
        val descriptor = Descriptor.Parser(methodName).parseMethod(full = false)
        val mappedMethod = findMethod(mappedClass, descriptor).first
        val accessWidener = FabricLoaderImpl.INSTANCE.classTweaker.visitAccessWidener(mappedClass.name)!!

        accessWidener.visitMethod(
            mappedClass.name,
            mappedMethod.toDescriptor(),
            AccessWidenerVisitor.AccessType.ACCESSIBLE,
            false,
        )

        if (isMutable) {
            accessWidener.visitMethod(
                mappedClass.name,
                mappedMethod.toDescriptor(),
                AccessWidenerVisitor.AccessType.MUTABLE,
                false,
            )
        }
    }

    fun findMethod(
        mappedClass: Mappings.MappedClass,
        descriptor: Descriptor.Method,
    ): Pair<Mappings.MappedMethod, ClassInfo.Method> {
        val parameters = descriptor.parameters

        val classInfo = ClassInfo.forName(mappedClass.name)
            ?: ClassInfo.forName(mappedClass.name)
            ?: ClassInfo.forName(mappedClass.name)
        println("${classInfo?.name} has ${classInfo?.methods?.size} methods")

        val mappedMethods = mappedClass.findMethods(descriptor.name, classInfo)
            ?: error("Cannot find method ${descriptor.name} in class ${mappedClass.name}")

        var value: Pair<Mappings.MappedMethod, ClassInfo.Method>? = null
        for (method in mappedMethods) {
            if (parameters != null) {
                if (method.parameters.size != parameters.size) continue

                if (method.parameters.zip(parameters).any {
                        it.first.type != it.second.descriptor()
                    }
                ) {
                    continue
                }
            }

            println("1 | Found method ${method.name} in class ${mappedClass.name} with descriptor ${method.toDescriptor()}")
            val result = classInfo.findMethodInHierarchy(
                method.name,
                method.toDescriptor(),
                ClassInfo.SearchType.ALL_CLASSES,
                ClassInfo.INCLUDE_ALL or ClassInfo.INCLUDE_INITIALISERS,
            ) ?: continue

            println("2 | Found method ${result.name} in class ${result.name} matching mapped method ${method.name} with descriptor ${method.toDescriptor()}")

            if (value != null) {
                error(
                    "Multiple methods match name ${descriptor.name} in class ${mappedClass.name}, " +
                        "please provide a method descriptor",
                )
            }

            value = method to result
        }

        if (value != null) {
            return value
        }

        error("Unable to match method $descriptor in class ${mappedClass.name}")
    }

    fun getParameterFromLocal(local: Local, name: String = "Local"): InjectorGenerator.Parameter {
        val descriptor = when {
            local.print == true -> {
                // The type doesn't matter, it won't actually be applied
                Descriptor.Primitive.INT
            }
            local.type != null -> {
                if (local.index != null) {
                    require(local.ordinal == null) {
                        "$name that specifies a type and index cannot specify an ordinal"
                    }
                } else {
                    require(local.ordinal != null) {
                        "$name that specifies a type must also specify an index or ordinal"
                    }
                }
                Descriptor.Parser(local.type).parseType(full = true)
            }
            else -> error("$name must specify \"print\", or \"type\" and either \"ordinal\" or \"index\"")
        }

        require(descriptor.isType)

        return InjectorGenerator.Parameter(descriptor, local)
    }
}
