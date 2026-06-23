//package com.chattriggers.ctjs.api
//
//import com.chattriggers.ctjs.CTJS
//import com.chattriggers.ctjs.internal.utils.urlEncode
//import net.fabricmc.loader.api.FabricLoader
//import net.fabricmc.mappingio.MappingReader
//import net.fabricmc.mappingio.adapter.MappingNsRenamer
//import net.fabricmc.mappingio.adapter.MappingSourceNsSwitch
//import org.objectweb.asm.Opcodes
//import org.objectweb.asm.Type
//import org.spongepowered.asm.mixin.transformer.ClassInfo
//import org.spongepowered.asm.service.MixinService
//import java.io.ByteArrayInputStream
//import java.net.URI
//import java.nio.file.Files
//import java.util.zip.ZipFile
//import net.fabricmc.mappingio.tree.MappingTree.ElementMapping
//import net.fabricmc.mappingio.tree.MappingTreeView
//import net.fabricmc.mappingio.tree.MemoryMappingTree
//
//object `Mappings-o` {
//    private const val INTERMEDIARY_MAPPINGS_URL_PREFIX = "https://maven.fabricmc.net/net/fabricmc/intermediary/"
//    private const val MOJMAP_VERSION_MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
//
//    // If this is changed, also change the Java.type function in mixinProvidedLibs.js
//    internal val mappedPackages = setOf("Lnet/minecraft/", "Lcom/mojang/blaze3d/")
//
//    val sourceToMappedClassMap = mutableMapOf<String, MappedClass>() // A -> Full
//    val intermediaryToMappedClassMap = mutableMapOf<String, MappedClass>() // class_1038 -> Full
//    val officialToMappedClassMap = mutableMapOf<String, MappedClass>() // RealClass -> Full
//
//    val sourceToIntermediaryMappedClassNameMap = mutableMapOf<String, String>() // A -> class_1038
//    val sourceToOfficialMappedClassNameMap = mutableMapOf<String, String>() // A -> RealClass
//    val intermediaryToSourceMappedClassNameMap = mutableMapOf<String, String>() // class_1038 -> A
//    val intermediaryToOfficialMappedClassNameMap = mutableMapOf<String, String>() // class_1038 -> RealClass
//    val officialToSourceMappedClassNameMap = mutableMapOf<String, String>() // RealClass -> A
//    val officialToIntermediaryMappedClassNameMap = mutableMapOf<String, String>() // RealClass -> class_1038
//
//    internal fun initialize() {
////        //#if MC>=26.1
////        return
////        //#endif
//
//        val container = FabricLoader.getInstance().getModContainer("minecraft")
//        val minecraftVersion = container.get().metadata.version.friendlyString
//        val intermediaryJarName = "intermediary-$minecraftVersion-v2.jar".urlEncode()
//
//        val intermediaryJarBytes = URI("${INTERMEDIARY_MAPPINGS_URL_PREFIX}$minecraftVersion/${intermediaryJarName}").toURL().readBytes()
//        val intermediaryTempJar = Files.createTempFile(CTJS.MOD_ID, "intermediary-mapping").toFile()
//        intermediaryTempJar.writeBytes(intermediaryJarBytes)
//
//        val intermediaryMappingBytes = ZipFile(intermediaryTempJar).use { file ->
//            file.getInputStream(file.getEntry("mappings/mappings.tiny")).readAllBytes()
//        }
//
//        val intermediaryMappingFile = Files.createTempFile(CTJS.MOD_ID, "intermediary-mapping.tiny").toFile()
//        intermediaryMappingFile.writeBytes(intermediaryMappingBytes)
//
//        println("format: ${MappingReader.detectFormat(intermediaryMappingFile.toPath())}")
//        val intermediaryTree = MemoryMappingTree(true)
//        MappingReader.read(intermediaryMappingFile.toPath(), intermediaryTree)
//
//        println("i | types : ${intermediaryTree.dstNamespaces} | ${intermediaryTree.srcNamespace}")
////        intermediaryTree.classes.forEach { clazz ->
//////            println("i | found clazz $clazz")
//////            clazz.fields.forEach { field ->
//////                println("i | found field ${field.intermediaryUnmappedName} -> ${field.intermediaryMappedName} (${field.intermediaryUnmappedType} -> ${field.intermediaryMappedType})")
//////            }
//////            clazz.methods.forEach { method ->
//////                println("i | found method ${method.intermediaryUnmappedName} -> ${method.intermediaryMappedName} (${method.intermediaryUnmappedType} -> ${method.intermediaryMappedType})")
//////            }
////
////            val fields = mutableMapOf<String, MappedField>()
////            clazz.fields.forEach { field ->
////                val unmappedName = field.intermediaryUnmappedName // A
////                val mappedName = field.intermediaryMappedName // class_1000
////                val unmappedType = field.intermediaryUnmappedType // A
////                val mappedType = field.intermediaryMappedType // class_1000
////
////                fields[unmappedName] = MappedField(
////                    name = Mapping(unmappedName, mappedName),
////                    type = Mapping(unmappedType.descriptor, mappedType.descriptor),
////                )
////            }
////
////            val unmappedMethodNameToMethodMap = mutableMapOf<String, MutableList<MappedMethod>>()
////            val mappedMethodNameToMethodMap = mutableMapOf<String, MutableList<MappedMethod>>()
////            clazz.methods.forEach { method ->
////                val unmappedName = method.intermediaryUnmappedName // A
////                val mappedName = method.intermediaryMappedName // class_1000
////                val unmappedType = method.intermediaryUnmappedType // A
////                val mappedType = method.intermediaryMappedType // class_1000
////
////                val mappedMethod = MappedMethod(
////                    name = Mapping(unmappedName, mappedName),
////                    parameters = method.args.sortedBy { it.lvIndex }.mapIndexed { index, param ->
////                        MappedParameter(
////                            Mapping(param.intermediaryUnmappedName, param.intermediaryMappedName),
////                            Mapping(
////                                unmappedType.argumentTypes[index].descriptor,
////                                mappedType.argumentTypes[index].descriptor,
////                            ),
////                            param.lvIndex,
////                        )
////                    },
////                    returnType = Mapping(unmappedType.returnType.descriptor, mappedType.returnType.descriptor),
////                )
////                unmappedMethodNameToMethodMap.getOrPut(unmappedName, ::mutableListOf).add(mappedMethod)
////                mappedMethodNameToMethodMap.getOrPut(mappedName, ::mutableListOf).add(mappedMethod)
////            }
////
////            intermediaryToMappedClassMap[clazz.intermediaryUnmappedName] = MappedClass(
////                name = Mapping(clazz.intermediaryUnmappedName, clazz.intermediaryMappedName),
////                fields,
////                unmappedMethodNameToMethodMap,
////                mappedMethodNameToMethodMap,
////            )
////            sourceToIntermediaryMappedClassNameMap[clazz.intermediaryMappedName] = clazz.intermediaryUnmappedName
////
//////            println("intermediary -> source == ${clazz.intermediaryUnmappedName} -> ${clazz.intermediaryMappedName}")
////        }
//
//        val manifestJson = URI(MOJMAP_VERSION_MANIFEST_URL).toURL().readText()
//        val versionUrl = Regex(
//            """"id"\s*:\s*"${Regex.escape(minecraftVersion)}".*?"url"\s*:\s*"([^"]+)"""",
//            RegexOption.DOT_MATCHES_ALL
//        ).find(manifestJson)?.groupValues?.get(1) ?: error("Could not find version $minecraftVersion in manifest")
//
//        val versionJson = URI(versionUrl).toURL().readText()
//        val mojmapUrl = Regex(
//            """"client_mappings".*?"url"\s*:\s*"([^"]+)"""",
//            RegexOption.DOT_MATCHES_ALL
//        ).find(versionJson)?.groupValues?.get(1) ?: error("Could not find client mappings in version $minecraftVersion")
//
//        val mojmapBytes = URI(mojmapUrl).toURL().readBytes()
//        val mojmapTree = MemoryMappingTree(true)
//        MappingReader.read(ByteArrayInputStream(mojmapBytes).bufferedReader(), mojmapTree)
//
//        println("m | types : ${mojmapTree.dstNamespaces} | ${mojmapTree.srcNamespace}")
////        mojmapTree.classes.forEach { clazz ->
//////            println("m | found clazz $clazz")
//////            clazz.fields.forEach { field ->
//////                println("m | found field ${field.officialUnmappedName} -> ${field.officialMappedName} (${field.officialUnmappedType} -> ${field.officialMappedType})")
//////            }
//////            clazz.methods.forEach { method ->
//////                println("m | found method ${method.officialUnmappedName} -> ${method.officialMappedName} (${method.officialUnmappedType} -> ${method.officialMappedType})")
//////            }
////
////            val fields = mutableMapOf<String, MappedField>()
////            clazz.fields.forEach { field ->
////                val unmappedName = field.officialUnmappedName
////                val mappedName = field.officialMappedName
////                val unmappedType = field.officialUnmappedType
////                val mappedType = field.officialMappedType
////
////                fields[unmappedName] = MappedField(
////                    name = Mapping(unmappedName, mappedName),
////                    type = Mapping(unmappedType.descriptor, mappedType.descriptor),
////                )
////            }
////
////            val unmappedMethodNameToMethodMap = mutableMapOf<String, MutableList<MappedMethod>>()
////            val mappedMethodNameToMethodMap = mutableMapOf<String, MutableList<MappedMethod>>()
////            clazz.methods.forEach { method ->
////                val unmappedName = method.officialUnmappedName
////                val mappedName = method.officialMappedName
////                val unmappedType = method.officialUnmappedType
////                val mappedType = method.officialMappedType
////
////                val mappedMethod = MappedMethod(
////                    name = Mapping(unmappedName, mappedName),
////                    parameters = method.args.sortedBy { it.lvIndex }.mapIndexed { index, param ->
////                        MappedParameter(
////                            Mapping(param.officialUnmappedName, param.officialMappedName),
////                            Mapping(
////                                unmappedType.argumentTypes[index].descriptor,
////                                mappedType.argumentTypes[index].descriptor,
////                            ),
////                            param.lvIndex,
////                        )
////                    },
////                    returnType = Mapping(unmappedType.returnType.descriptor, mappedType.returnType.descriptor),
////                )
////                unmappedMethodNameToMethodMap.getOrPut(unmappedName, ::mutableListOf).add(mappedMethod)
////                mappedMethodNameToMethodMap.getOrPut(mappedName, ::mutableListOf).add(mappedMethod)
////            }
////
////            sourceToOfficialMappedClassMap[clazz.officialUnmappedName] = MappedClass(
////                name = Mapping(clazz.officialUnmappedName, clazz.officialMappedName),
////                fields,
////                unmappedMethodNameToMethodMap,
////                mappedMethodNameToMethodMap,
////            )
////            officialToSourceMappedClassNameMap[clazz.officialMappedName] = clazz.officialUnmappedName
////
//////            println("source -> official == ${clazz.officialUnmappedName} -> ${clazz.officialMappedName}")
////        }
//
//
////        val combinedTree = MemoryMappingTree()
////        intermediaryTree.accept(MappingNsRenamer(combinedTree, mapOf("official" to "source")))
////        mojmapTree.accept(MappingNsRenamer(combinedTree, mapOf("target" to "official")))
////        println("c | types : ${combinedTree.dstNamespaces} | ${combinedTree.srcNamespace}")
////        combinedTree.classes.forEach { clazz ->
////            println("c | found clazz $clazz")
////            clazz.fields.forEach { field ->
////                println("c found field ${field.sourceName} -> ${field.intermediaryName} -> ${field.officialName} (${field.sourceType} -> ${field.intermediaryType} -> ${field.officialType})")
////            }
////            clazz.methods.forEach { method ->
////                println("c found method ${method.sourceName} -> ${method.intermediaryName} -> ${method.officialName} (${method.sourceType} -> ${method.intermediaryType} -> ${method.officialType})")
////            }
////        }
//
//        data class MethodKey(val className: String, val methodName: String, val methodDesc: String)
//        data class MojmapMapping(
//            val officialName: String,
//            val officialDesc: String?,
//            val paramNames: List<String?>?,
//        )
//
//        val obfuscatedToMojmapMethod = mutableMapOf<MethodKey, MojmapMapping>()
//        val obfuscatedToMojmapClass = mutableMapOf<String, String>()
//        val obfuscatedToMojmapField = mutableMapOf<Pair<String, String>, MojmapMapping>()
//
//        println("Total method parameters in official mappings: ${mojmapTree.classes.sumOf { c -> c.methods.sumOf { m -> m.args.size } }}")
//        mojmapTree.classes.forEach { clazz ->
//            val classSourceName = clazz.getName("target")!!
//            val classOfficialName = clazz.srcName
////            println("m | found clazz $classSourceName -> $classOfficialName")
//
//            obfuscatedToMojmapClass[classSourceName] = classOfficialName
//            clazz.methods.forEach { method ->
//                val methodSourceName = method.getName("target")!!
//                val methodSourceDesc = method.getDesc("target")!!
//                val methodOfficialName = method.srcName
//                val methodOfficialDesc = method.srcDesc
//
//                for (mapping in method.args) {
//                    println("a1")
//                }
//
////                println("m1 | found method ${methodSourceName} -> ${methodOfficialName} (${methodSourceDesc} -> ${methodOfficialDesc})")
//                obfuscatedToMojmapMethod[MethodKey(classSourceName, methodSourceName, methodSourceDesc)] = MojmapMapping(
//                    officialName = methodOfficialName,
//                    officialDesc = methodOfficialDesc,
//                    paramNames = method.args.sortedBy { it.lvIndex }.map { it.getName("target") },
//                )
//            }
//            clazz.fields.forEach { field ->
//                val fieldSourceName = field.getName("target")!!
////                val fieldSourceDesc = field.getDesc("target")!!
//                val fieldOfficialName = field.srcName
//                val fieldOfficialDesc = field.srcDesc
//
////                println("m | found field ${fieldSourceName} -> ${fieldOfficialName} (${fieldSourceDesc} -> ${fieldOfficialDesc})")
//                obfuscatedToMojmapField[classSourceName to fieldSourceName] = MojmapMapping(fieldOfficialName, fieldOfficialDesc, null)
//            }
//        }
//
//        println("Total method parameters in intermediary mappings: ${intermediaryTree.classes.sumOf { c -> c.methods.sumOf { m -> m.args.size } }}")
//        intermediaryTree.classes.forEach { clazz ->
//            val classSourceName = clazz.srcName
////            println("11 | source: $sourceName")
//            val classIntermediaryName = clazz.getName("intermediary") ?: return@forEach
////            println("12 | intermediary: $intermediaryName")
//            val classOfficialName = obfuscatedToMojmapClass[classSourceName] ?: return@forEach
////            println("13 | official: $officialName")
//
//            val sourceFields = mutableMapOf<String, MappedField>()
//            val intermediaryFields = mutableMapOf<String, MappedField>()
//            val officialFields = mutableMapOf<String, MappedField>()
//            clazz.fields.forEach { field ->
//                val fieldSourceName = field.srcName
//                val fieldSourceDesc = field.srcDesc!!
//
//                val fieldIntermediaryName = field.getName("intermediary")!!
//                val fieldIntermediaryDesc = field.getDesc("intermediary")!!
//
//                val fieldOfficialMapping = obfuscatedToMojmapField[classSourceName to fieldSourceName]
//                val fieldOfficialName = fieldOfficialMapping?.officialName!!
//                val fieldOfficialDesc = fieldOfficialMapping.officialDesc!!
//
//                val mappedField = MappedField(
//                    name = Mapping(fieldSourceName, fieldIntermediaryName, fieldOfficialName),
//                    type = Mapping(fieldSourceDesc, fieldIntermediaryDesc, fieldOfficialDesc),
//                )
//
//                sourceFields[fieldSourceName] = mappedField
//                intermediaryFields[fieldIntermediaryName] = mappedField
//                officialFields[fieldOfficialName] = mappedField
//            }
//
//            val sourceMethods = mutableMapOf<String, MutableList<MappedMethod>>()
//            val intermediaryMethods = mutableMapOf<String, MutableList<MappedMethod>>()
//            val officialMethods = mutableMapOf<String, MutableList<MappedMethod>>()
//            clazz.methods.forEach { method ->
//                val methodSourceName = method.srcName
//                val methodSourceDesc = method.srcDesc!!
//                val methodIntermediaryName = method.getName("intermediary")!!
//                val methodIntermediaryDesc = method.getDesc("intermediary")!!
//
//                val methodOfficialMapping = obfuscatedToMojmapMethod[MethodKey(classSourceName, methodSourceName, methodSourceDesc)]
//                val methodOfficialName = methodOfficialMapping?.officialName!!
//                val methodOfficialDesc = methodOfficialMapping.officialDesc!!
//
////                println("m2 | found method ${methodSourceName} -> ${methodIntermediaryName} -> ${methodOfficialName} (${methodSourceDesc} -> ${methodIntermediaryDesc} -> ${methodOfficialDesc})")
//
////                val argTypes = Type.getArgumentTypes(methodIntermediaryDesc)
////                val argsSorted = method.args.sortedBy { it.lvIndex }
////                val isStatic = methodSourceDesc // or check method flags if available
////                val lvOffset = 1 // instance methods: slot 0 = this; static = 0
//
//                for (mapping in method.args) {
//                    println("a2")
//                }
////                for (mapping in method.args) {
////                    println("a1 = ${mapping.srcName} -> ${mapping.method} -> ${mapping.lvIndex}")
////                }
//
//                val argsSortedByLv = method.args.sortedBy { it.lvIndex }
//                val mappedMethod = MappedMethod(
//                    name = Mapping(methodSourceName, methodIntermediaryName, methodOfficialName),
////                    parameters = method.args.sortedBy { it.lvIndex }.mapIndexed { index, param ->
////                        val paramSourceName = param.srcName
////                        val paramIntermediaryName = param.getName("intermediary")!!
////                        val paramOfficialName = methodOfficialMapping.paramNames?.getOrNull(index)
////                        MappedParameter(
////                            name = Mapping(paramSourceName, paramIntermediaryName, paramOfficialName!!),
////                            type = Mapping(
////                                Type.getArgumentTypes(methodSourceDesc)[index].descriptor,
////                                Type.getArgumentTypes(methodIntermediaryDesc)[index].descriptor,
////                                Type.getArgumentTypes(methodOfficialDesc)[index].descriptor,
////                            ),
////                            lvtIndex = param.lvIndex,
////                        )
////                    },
//                    parameters = method.args.sortedBy { it.lvIndex }.mapIndexed { index, param ->
//                        println("${index} -> ${param}")
////                        val param = argsSortedByLv.find { it.lvIndex == index + lvOffset }
//                        val paramSourceName = param?.srcName ?: return@forEach
//                        val paramIntermediaryName = param?.getName("intermediary") ?: return@forEach
//                        val paramOfficialName = methodOfficialMapping.paramNames?.getOrNull(index) ?: return@forEach
//                        MappedParameter(
//                            name = Mapping(paramSourceName!!, paramIntermediaryName, paramOfficialName!!),
//                            type = Mapping(
//                                Type.getArgumentTypes(methodSourceDesc)[index].descriptor,
//                                Type.getArgumentTypes(methodIntermediaryDesc)[index].descriptor,
//                                Type.getArgumentTypes(methodOfficialDesc)[index].descriptor,
//                            ),
//                            lvtIndex = param?.lvIndex ?: index,
//                        )
//                    },
//                    returnType = Mapping(
//                        Type.getReturnType(methodSourceDesc).descriptor,
//                        Type.getReturnType(methodIntermediaryDesc).descriptor,
//                        Type.getReturnType(methodOfficialDesc).descriptor,
//                    ),
//                )
//                sourceMethods.getOrPut(methodSourceName, ::mutableListOf).add(mappedMethod)
//                intermediaryMethods.getOrPut(methodIntermediaryName, ::mutableListOf).add(mappedMethod)
//                officialMethods.getOrPut(methodOfficialName, ::mutableListOf).add(mappedMethod)
//            }
//
//            val mappedClass = MappedClass(
//                name = Mapping(classSourceName, classIntermediaryName, classOfficialName),
//                sourceFields = sourceFields,
//                intermediaryFields = intermediaryFields,
//                officialFields = officialFields,
//                sourceMethods = sourceMethods,
//                intermediaryMethods = intermediaryMethods,
//                officialMethods = officialMethods,
//            )
//
////            println("Mapped class: $classSourceName -> classIntermediaryName -> $classOfficialName")
//            sourceToMappedClassMap[classSourceName] = mappedClass
//            intermediaryToMappedClassMap[classIntermediaryName] = mappedClass
//            officialToMappedClassMap[classOfficialName] = mappedClass
//
//            sourceToIntermediaryMappedClassNameMap[classSourceName] = classIntermediaryName
//            sourceToOfficialMappedClassNameMap[classSourceName] = classOfficialName
//            intermediaryToSourceMappedClassNameMap[classIntermediaryName] = classSourceName
//            intermediaryToOfficialMappedClassNameMap[classIntermediaryName] = classOfficialName
//            officialToSourceMappedClassNameMap[classOfficialName] = classSourceName
//            officialToIntermediaryMappedClassNameMap[classOfficialName] = classIntermediaryName
//        }
//    }
//
//    // class_1000 -> class_1000.class?
//    internal fun getUnmappedClass(unmappedClassName: String): MappedClass {
//        val name = normalizeClassName(unmappedClassName)
//        val classNode = MixinService.getService().bytecodeProvider.getClassNode(unmappedClassName)
//
//        val sourceFields = mutableMapOf<String, MappedField>()
//        val intermediaryFields = mutableMapOf<String, MappedField>()
//        val officialFields = mutableMapOf<String, MappedField>()
//        classNode.fields.forEach {
//            val type = it.desc
//            val fieldName = it.name
//            val mappedField = MappedField(
//                Mapping(fieldName, fieldName, fieldName),
//                Mapping(type, type, mapClassNameFromIntermediary(type) ?: type),
//            )
//            sourceFields[fieldName] = mappedField
//            intermediaryFields[fieldName] = mappedField
//            officialFields[fieldName] = mappedField
//        }
//
//        val sourceMethods = mutableMapOf<String, MutableList<MappedMethod>>()
//        val intermediaryMethods = mutableMapOf<String, MutableList<MappedMethod>>()
//        val officialMethods = mutableMapOf<String, MutableList<MappedMethod>>()
//        for (method in classNode.methods) {
//            val isStatic = method.access and Opcodes.ACC_STATIC != 0
//            var lvtIndex = if (isStatic) 0 else 1
//
//            val params = mutableListOf<MappedParameter>()
//            Type.getArgumentTypes(method.desc).forEachIndexed { index, type ->
//                val paramType = type.descriptor
//                val paramName = method.parameters?.get(index)?.name ?: return@forEachIndexed
//
//                params.add(
//                    MappedParameter(
//                        Mapping(paramName, paramName, paramName),
//                        Mapping(paramType, paramType, mapClassNameFromIntermediary(paramType) ?: paramType),
//                        lvtIndex,
//                    )
//                )
//
//                if (type == Type.DOUBLE_TYPE || type == Type.LONG_TYPE) {
//                    lvtIndex += 2
//                } else {
//                    lvtIndex++
//                }
//            }
//
//            val returnType = Type.getReturnType(method.desc).descriptor
//            val methodName = method.name
//
//            val mappedMethod = MappedMethod(
//                Mapping(methodName, methodName, methodName),
//                params,
//                Mapping(returnType, returnType, mapClassNameFromIntermediary(returnType) ?: returnType),
//            )
//            sourceMethods.getOrPut(methodName, ::mutableListOf).add(mappedMethod)
//            intermediaryMethods.getOrPut(methodName, ::mutableListOf).add(mappedMethod)
//            officialMethods.getOrPut(methodName, ::mutableListOf).add(mappedMethod)
//        }
//
//        sourceToIntermediaryMappedClassNameMap[name] = name
//        intermediaryToSourceMappedClassNameMap[name] = name
//        intermediaryToOfficialMappedClassNameMap[name] = name
//        officialToSourceMappedClassNameMap[name] = name
//        officialToIntermediaryMappedClassNameMap[name] = name
//        sourceToOfficialMappedClassNameMap[name] = name
//
//        return MappedClass(
//            name = Mapping(name, name, name),
//            sourceFields = sourceFields,
//            intermediaryFields = intermediaryFields,
//            officialFields = officialFields,
//            sourceMethods = sourceMethods,
//            intermediaryMethods = intermediaryMethods,
//            officialMethods = officialMethods,
//        ).also {
//            sourceToMappedClassMap[name] = it
//            intermediaryToMappedClassMap[name] = it
//            officialToMappedClassMap[name] = it
//        }
//    }
//
//    // class_1000 -> RealClass.class
//    internal fun getMappedClass(unmappedClassName: String): MappedClass? {
//        val className = normalizeClassName(unmappedClassName)
//
////        println("0 | className: $className")
//        sourceToMappedClassMap[className]?.let {
////            println("1 | source | $className -> ${it.name}")
//            return it
//        }
//
//        intermediaryToMappedClassMap[className]?.let {
////            println("1 | intermediary | $className -> ${it.name}")
//            return it
//        }
//
//        officialToMappedClassMap[className]?.let {
////            println("1 | official | $className -> ${it.name}")
//            return it
//        }
//
////        println("2 | unknown | $className")
//        return null
//
////        val className = normalizeClassName(unmappedClassName)
////        var sourceClassName: String? = null
////
////        // Input: A
////        if (sourceToOfficialMappedClassMap.containsKey(className)) {
////            println("1 | source -> source | ${className}")
////            sourceClassName = className
////        }
////
////        // Input: RealClass
////        if (officialToSourceMappedClassNameMap.containsKey(className)) {
////            sourceClassName = officialToSourceMappedClassNameMap[className]
////            println("1 | official -> source | ${className} -> ${sourceClassName}")
////        }
////
////        // Input: class_1000
////        if (intermediaryToSourceMappedClassMap.containsKey(className)) {
////            sourceClassName = intermediaryToSourceMappedClassMap[normalizeClassName(className)]?.name?.value
////            println("1 | intermediary -> source | ${className} -> ${sourceClassName}")
////        }
////
////        if (sourceClassName == null) {
////            println("2 | unknown | ${className}")
////            return null
////        }
////        println("3 | source -> official | ${sourceClassName} -> ${sourceToOfficialMappedClassMap[sourceClassName]}")
////        return sourceToOfficialMappedClassMap[sourceClassName]
//    }
//
//    // class_1000 -> RealClass
//    internal fun getMappedClassName(unmappedClassName: String): String? {
////        println("11 | intermediary: $unmappedClassName")
//        return getMappedClass(unmappedClassName)?.name?.official
//    }
//
//    /**
//     * Gets a classes unmapped class name, or throws an error if it is not mapped
//     */
//    // RealClass.class -> class_1000?
//    @JvmStatic
//    fun unmapClass(clazz: Class<*>) = unmapClassName(clazz.name)
//
//    /**
//     * Gets an unmapped class name from a mapped class name, or returns null if
//     * it either does not exist or is not mapped.
//     */
//    // RealClass -> class_1000?
//    @JvmStatic
//    fun unmapClassName(className: String): String? {
//        val name = normalizeClassName(className)
//        val sourceName = officialToSourceMappedClassNameMap[name] ?: return null
//        return sourceToIntermediaryMappedClassNameMap[sourceName]
//    }
//
//    /**
//     * Gets the mapped class name from an unmapped class name or null if the class
//     * name does not exist. Note that this is not required to use mapped classes,
//     * as Rhino performs this mapping automatically during runtime.
//     */
//    @JvmStatic
//    fun mapClassNameFromIntermediary(intermediaryClassName: String): String? {
////        println("3 | intermediary: $intermediaryClassName")
//        return getMappedClassName(intermediaryClassName)
//    }
//
//    private fun normalizeClassName(className: String) = (if (className.startsWith('L') && className.endsWith(';')) {
//        className.drop(1).dropLast(1)
//    } else {
//        className.removeSuffix(".class")
//    }).replace('.', '/')
//
//    data class Mapping(
//        val source: String,
//        val intermediary: String,
//        val official: String,
//    ) {
//        val value: String
//            get() = official
//        val original: String
//            get() = intermediary
//    }
//
//    data class MappedField(val name: Mapping, val type: Mapping)
//
//    class MappedParameter(
//        val name: Mapping,
//        val type: Mapping,
//        val lvtIndex: Int,
//    )
//
//    class MappedMethod(
//        val name: Mapping,
//        val parameters: List<MappedParameter>,
//        val returnType: Mapping,
//    ) {
//        fun toSourceDescriptor() = buildString {
//            append('(')
//            parameters.forEach {
//                append(it.type.source)
//            }
//            append(')')
//            append(returnType.source)
//        }
//
//        fun toIntermediaryDescriptor() = buildString {
//            append('(')
//            parameters.forEach {
//                append(it.type.intermediary)
//            }
//            append(')')
//            append(returnType.intermediary)
//        }
//
//        fun toOfficialDescriptor() = buildString {
//            append('(')
//            parameters.forEach {
//                append(it.type.official)
//            }
//            append(')')
//            append(returnType.official)
//        }
//
//        fun toDescriptor() = toOfficialDescriptor()
//        fun toFullDescriptor() = toFullOfficialDescriptor()
//
//        fun toFullSourceDescriptor() = name.source + toSourceDescriptor()
//        fun toFullIntermediaryDescriptor() = name.intermediary + toIntermediaryDescriptor()
//        fun toFullOfficialDescriptor() = name.official + toOfficialDescriptor()
//    }
//
//    class MappedClass(
//        val name: Mapping,
//        val sourceFields: Map<String, MappedField>,
//        val intermediaryFields: Map<String, MappedField>,
//        val officialFields: Map<String, MappedField>,
//        val sourceMethods: Map<String, List<MappedMethod>>,
//        val intermediaryMethods: Map<String, List<MappedMethod>>,
//        val officialMethods: Map<String, List<MappedMethod>>,
//    ) {
//        fun findMethods(name: String, classInfo: ClassInfo?): List<MappedMethod>? {
////            println("0 | ${this.name.original} -> ${this.name.intermediary} -> ${this.name.official} | ${name} | ${classInfo?.superName}")
////            sourceMethods.keys.forEach { println("1 | ${it}") }
//            sourceMethods[name]?.let { return it }
//
////            intermediaryMethods.keys.forEach { println("2 | ${it}") }
//            intermediaryMethods[name]?.let { return it }
//
////            officialMethods.keys.forEach { println("3 | ${it}") }
//            officialMethods[name]?.let { return it }
//
//            if (classInfo == null) return null
//
//            // TODO: zephy: i think this is wrong
//            val superClass = sourceToMappedClassMap[classInfo.superName]
//                ?: intermediaryToMappedClassMap[classInfo.superName]
//                ?: officialToMappedClassMap[classInfo.superName]
//            if (superClass != null) {
//                return superClass.findMethods(name, classInfo.superClass)
//            }
//
//            val methods = mutableListOf<MappedMethod>()
//            for (itf in classInfo.interfaces) {
//                val itfClass = sourceToMappedClassMap[itf]
//                    ?: intermediaryToMappedClassMap[itf]
//                    ?: officialToMappedClassMap[itf]
//                    ?: continue
//                itfClass.findMethods(name, null)?.let { methods += it }
//            }
//
//            return if (methods.isEmpty()) null else methods
//        }
//    }
//
////    private val ElementMapping.sourceName: String?
////        get() = getName("source")
////    private val ElementMapping.intermediaryName: String?
////        get() = getName("intermediary")
////    private val ElementMapping.officialName: String?
////        get() = getName("target")
////    private val MappingTreeView.MemberMappingView.sourceType: Type?
////        get() = getDesc("source")?.let { Type.getType(it) }
////    private val MappingTreeView.MemberMappingView.intermediaryType: Type?
////        get() = getDesc("intermediary")?.let { Type.getType(it) }
////    private val MappingTreeView.MemberMappingView.officialType: Type?
////        get() = getDesc("target")?.let { Type.getType(it) }
//
////    private val ElementMapping.officialUnmappedName: String
////        get() = getName("target")!!
////    private val ElementMapping.officialMappedName: String
////        get() = getName("source")!!
////    private val MappingTreeView.MemberMappingView.officialUnmappedType: Type
////        get() = Type.getType(getDesc("target"))
////    private val MappingTreeView.MemberMappingView.officialMappedType: Type
////        get() = Type.getType(getDesc("source"))
////
////    private val ElementMapping.intermediaryUnmappedName: String
////        get() = getName("intermediary")!!
////    private val ElementMapping.intermediaryMappedName: String
////        get() = getName("official")!!
////    private val MappingTreeView.MemberMappingView.intermediaryUnmappedType: Type
////        get() = Type.getType(getDesc("intermediary"))
////    private val MappingTreeView.MemberMappingView.intermediaryMappedType: Type
////        get() = Type.getType(getDesc("official"))
//}
