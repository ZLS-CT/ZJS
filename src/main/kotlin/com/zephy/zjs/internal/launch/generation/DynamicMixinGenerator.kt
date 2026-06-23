package com.zephy.zjs.internal.launch.generation

import codes.som.koffee.assembleClass
import codes.som.koffee.modifiers.public
import com.zephy.zjs.internal.launch.Inject
import com.zephy.zjs.internal.launch.MixinDetails
import com.zephy.zjs.internal.launch.ModifyArg
import com.zephy.zjs.internal.launch.ModifyArgs
import com.zephy.zjs.internal.launch.ModifyConstant
import com.zephy.zjs.internal.launch.ModifyExpressionValue
import com.zephy.zjs.internal.launch.ModifyReceiver
import com.zephy.zjs.internal.launch.ModifyReturnValue
import com.zephy.zjs.internal.launch.ModifyVariable
import com.zephy.zjs.internal.launch.Redirect
import com.zephy.zjs.internal.launch.WrapOperation
import com.zephy.zjs.internal.launch.WrapWithCondition
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.Mixin as SPMixin

internal class DynamicMixinGenerator(private val ctx: GenerationContext, private val details: MixinDetails) {
    fun generate(): ByteArray {
        val mixinClassNode = assembleClass(public, ctx.generatedClassFullPath, version = Opcodes.V25) {
            for ((id, injector) in details.injectors) {
                when (injector) {
                    is Inject -> with(InjectGenerator(ctx, id, injector)) { generate() }
                    is Redirect -> with(RedirectGenerator(ctx, id, injector)) { generate() }
                    is ModifyArg -> with(ModifyArgGenerator(ctx, id, injector)) { generate() }
                    is ModifyArgs -> with(ModifyArgsGenerator(ctx, id, injector)) { generate() }
                    is ModifyConstant -> with(ModifyConstantGenerator(ctx, id, injector)) { generate() }
                    is ModifyExpressionValue -> with(ModifyExpressionValueGenerator(ctx, id, injector)) { generate() }
                    is ModifyReceiver -> with(ModifyReceiverGenerator(ctx, id, injector)) { generate() }
                    is ModifyReturnValue -> with(ModifyReturnValueInjector(ctx, id, injector)) { generate() }
                    is ModifyVariable -> with(ModifyVariableGenerator(ctx, id, injector)) { generate() }
                    is WrapOperation -> with(WrapOperationGenerator(ctx, id, injector)) { generate() }
                    is WrapWithCondition -> with(WrapWithConditionGenerator(ctx, id, injector)) { generate() }
                }
            }
        }

        val mixinAnnotation = mixinClassNode.visitAnnotation(SPMixin::class.java.descriptorString(), false)
        val mixin = ctx.mixin
        mixinAnnotation.visit("targets", listOf(ctx.mappedClass.name))
        if (mixin.priority != null) {
            mixinAnnotation.visit("priority", mixin.priority)
        }
        mixinAnnotation.visitEnd()

        val writer = ClassWriter(ClassWriter.COMPUTE_FRAMES)
        mixinClassNode.accept(writer)
        return writer.toByteArray()
    }
}
