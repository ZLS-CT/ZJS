package com.zephy.zjs.typing

val manualRoots = setOf(
    "java.awt.Color",
    "java.util.ArrayList",
    "java.util.HashMap",
    "com.zephy.zjs.api.client.Keyboard",
    "gg.essential.universal.UKeyboard",
    "net.minecraft.world.InteractionHand",
    "org.lwjgl.opengl.GL11",
    "org.lwjgl.opengl.GL12",
    "org.lwjgl.opengl.GL13",
    "org.lwjgl.opengl.GL14",
    "org.lwjgl.opengl.GL15",
    "org.lwjgl.opengl.GL20",
    "org.lwjgl.opengl.GL21",
    "org.lwjgl.opengl.GL30",
    "org.lwjgl.opengl.GL31",
    "org.lwjgl.opengl.GL32",
    "org.lwjgl.opengl.GL33",
    "org.lwjgl.opengl.GL40",
    "org.lwjgl.opengl.GL41",
    "org.lwjgl.opengl.GL42",
    "org.lwjgl.opengl.GL43",
    "org.lwjgl.opengl.GL44",
    "org.lwjgl.opengl.GL45",
    "org.spongepowered.asm.mixin.injection.callback.CallbackInfo",
)

private val providedTypes = mutableMapOf(
    // Minecraft
    "EquipmentSlot" to "net.minecraft.world.entity.EquipmentSlot",
    "DataComponents" to "net.minecraft.core.component.DataComponents",
    "CustomData" to "net.minecraft.world.item.component.CustomData",
    "ItemLore" to "net.minecraft.world.item.component.ItemLore",
    "CompoundTag" to "net.minecraft.nbt.CompoundTag",
    "RemotePlayer" to "net.minecraft.client.player.RemotePlayer",
    "Items" to "net.minecraft.world.item.Items",
    "ByteArrayTag" to "net.minecraft.nbt.ByteArrayTag",
    "ByteTag" to "net.minecraft.nbt.ByteTag",
    "CompoundTag" to "net.minecraft.nbt.CompoundTag",
    "DoubleTag" to "net.minecraft.nbt.DoubleTag",
    "FloatTag" to "net.minecraft.nbt.FloatTag",
    "IntArrayTag" to "net.minecraft.nbt.IntArrayTag",
    "IntTag" to "net.minecraft.nbt.IntTag",
    "LongArrayTag" to "net.minecraft.nbt.LongArrayTag",
    "LongTag" to "net.minecraft.nbt.LongTag",
    "ShortTag" to "net.minecraft.nbt.ShortTag",
    "StringTag" to "net.minecraft.nbt.StringTag",
    "CollectionTag" to "net.minecraft.nbt.CollectionTag",
    "NbtIo" to "net.minecraft.nbt.NbtIo",
    "NbtAccounter" to "net.minecraft.nbt.NbtAccounter",
    "WallSignBlock" to "net.minecraft.world.level.block.WallSignBlock",
    "SignBlockEntity" to "net.minecraft.world.level.block.entity.SignBlockEntity",

    // ZJS
    "Client" to "com.zephy.zjs.api.client.Client",
    "FileLib" to "com.zephy.zjs.api.client.FileLib",
    "KeyBind" to "com.zephy.zjs.api.client.KeyBind",
    "Keyboard" to "com.zephy.zjs.api.client.Keyboard",
    "MathLib" to "com.zephy.zjs.api.client.MathLib",
    "Settings" to "com.zephy.zjs.api.client.Settings",
    "ZPlayer" to "com.zephy.zjs.api.client.ZPlayer",
    "ZSound" to "com.zephy.zjs.api.client.ZSound",

    "Commands" to "com.zephy.zjs.api.commands.DynamicCommands",
    "RootCommand" to "com.zephy.zjs.api.commands.RootCommand",

    "PlayerMP" to "com.zephy.zjs.api.entity.PlayerMP",
    "ZBlockEntity" to "com.zephy.zjs.api.entity.ZBlockEntity",
    "ZEntity" to "com.zephy.zjs.api.entity.ZEntity",
    "ZTeam" to "com.zephy.zjs.api.entity.ZTeam",

    "NBT" to "com.zephy.zjs.api.inventory.nbt.NBT",
    "NBTBase" to "com.zephy.zjs.api.inventory.nbt.NBTBase",
    "NBTTagCompound" to "com.zephy.zjs.api.inventory.nbt.NBTTagCompound",
    "NBTTagList" to "com.zephy.zjs.api.inventory.nbt.NBTTagList",

    "Inventory" to "com.zephy.zjs.api.inventory.Inventory",
    "ItemType" to "com.zephy.zjs.api.inventory.ItemType",
    "ZItem" to "com.zephy.zjs.api.inventory.ZItem",
    "ZSlot" to "com.zephy.zjs.api.inventory.ZSlot",

    "ChatLib" to "com.zephy.zjs.api.message.ChatLib",
    "TextComponent" to "com.zephy.zjs.api.message.TextComponent",

    "GradientGUIRenderState" to "com.zephy.zjs.api.render.renderstates.GradientGUIRenderState",
    "GUIRenderState" to "com.zephy.zjs.api.render.renderstates.GUIRenderState",
    "TexturedGUIRenderState" to "com.zephy.zjs.api.render.renderstates.TexturedGUIRenderState",

    "BaseGUIRenderer" to "com.zephy.zjs.api.render.BaseGUIRenderer",
    "BaseWorldRenderer" to "com.zephy.zjs.api.render.BaseWorldRenderer",
    "DrawMode" to "com.zephy.zjs.api.render.DrawMode",
    "Gui" to "com.zephy.zjs.api.render.Gui",
    "GUIRenderer" to "com.zephy.zjs.api.render.GUIRenderer",
    "HudRenderLayer" to "com.zephy.zjs.api.render.HudRenderLayer",
    "Image" to "com.zephy.zjs.api.render.Image",
    "PipelineBuilder" to "com.zephy.zjs.api.render.PipelineBuilder",
    "RenderLayers" to "com.zephy.zjs.api.render.RenderLayers",
    "RenderPipelines" to "com.zephy.zjs.api.render.RenderPipelines",
    "RenderSnippet" to "com.zephy.zjs.api.render.RenderSnippet",
    "RenderUtils" to "com.zephy.zjs.api.render.RenderUtils",
    "Text" to "com.zephy.zjs.api.render.Text",
    "VertexFormat" to "com.zephy.zjs.api.render.VertexFormat",
    "WorldRenderer" to "com.zephy.zjs.api.render.WorldRenderer",

    "CancellableEvent" to "com.zephy.zjs.api.triggers.CancellableEvent",
    "ChatTrigger" to "com.zephy.zjs.api.triggers.ChatTrigger",
    "ClassFilterTrigger" to "com.zephy.zjs.api.triggers.ClassFilterTrigger",
    "CommandTrigger" to "com.zephy.zjs.api.triggers.CommandTrigger",
    "EventTrigger" to "com.zephy.zjs.api.triggers.EventTrigger",
    "RegularTrigger" to "com.zephy.zjs.api.triggers.RegularTrigger",
    "StepTrigger" to "com.zephy.zjs.api.triggers.StepTrigger",
    "Trigger" to "com.zephy.zjs.api.triggers.Trigger",
    "TriggerType" to "com.zephy.zjs.api.triggers.TriggerType",

    "Vec2f" to "com.zephy.zjs.api.vec.Vec2f",
    "Vec3f" to "com.zephy.zjs.api.vec.Vec3f",
    "Vec3i" to "com.zephy.zjs.api.vec.Vec3i",

    "BlockFace" to "com.zephy.zjs.api.world.block.BlockFace",
    "ZBlock" to "com.zephy.zjs.api.world.block.ZBlock",
    "ZBlockPos" to "com.zephy.zjs.api.world.block.ZBlockPos",
    "ZBlockType" to "com.zephy.zjs.api.world.block.ZBlockType",

    "Scoreboard" to "com.zephy.zjs.api.world.Scoreboard",
    "Server" to "com.zephy.zjs.api.world.Server",
    "TabList" to "com.zephy.zjs.api.world.TabList",
    "World" to "com.zephy.zjs.api.world.World",
    "ZChunk" to "com.zephy.zjs.api.world.ZChunk",

    "Config" to "com.zephy.zjs.api.Config",

    "ZJS" to "com.zephy.zjs.ZJS",
    "TriggerRegister" to "com.zephy.zjs.engine.Register",
    "Thread" to "com.zephy.zjs.engine.WrappedThread",
    "Priority" to "com.zephy.zjs.api.triggers.Trigger\$Priority",
    "Console" to "com.zephy.zjs.engine.Console",

    // Misc
    "UKeyboard" to "gg.essential.universal.UKeyboard",
    "ArrayList" to "java.util.ArrayList",
    "HashMap" to "java.util.HashMap",
    "Paths" to "java.nio.file.Paths",
    "Files" to "java.nio.file.Files",
    "JavaFile" to "java.io.File",
    "ZipInputStream" to "java.util.zip.ZipInputStream",
    "FileInputStream" to "java.io.FileInputStream",
    "FileOutputStream" to "java.io.FileOutputStream",
    "JavaArray" to "java.lang.reflect.Array",
    "Byte" to "java.lang.Byte",
    "System" to "java.lang.System",
    "CountDownLatch" to "java.util.concurrent.CountDownLatch",
    "Desktop" to "java.awt.Desktop",
    "JavaURL" to "java.net.URL",
    "URI" to "java.net.URI",
    "Color" to "java.awt.Color",
    "Class" to "java.lang.Class",
    "UUID" to "java.util.UUID",
    "SSLContext" to "javax.net.ssl.SSLContext",
    "TrustManagerFactory" to "javax.net.ssl.TrustManagerFactory",
    "KeyStore" to "java.security.KeyStore",
    "CertificateFactory" to "java.security.cert.CertificateFactory",
    "DataInputStream" to "java.io.DataInputStream",
    "DataOutputStream" to "java.io.DataOutputStream",
    "ByteArrayInputStream" to "java.io.ByteArrayInputStream",
    "ByteArrayOutputStream" to "java.io.ByteArrayOutputStream",
    "InputStreamReader" to "java.io.InputStreamReader",
    "OutputStreamWriter" to "java.io.OutputStreamWriter",
    "GZIPInputStream" to "java.util.zip.GZIPInputStream",
    "BufferedReader" to "java.io.BufferedReader",
    "URLEncoder" to "java.net.URLEncoder",
    "URLConnection" to "java.net.URLConnection",
    "FabricLoader" to "net.fabricmc.loader.api.FabricLoader",
    "JavaFileReader" to "java.io.FileReader",
    "Base64" to "java.util.Base64",
    "StandardCopyOption" to "java.nio.file.StandardCopyOption",
    "Field" to "java.lang.reflect.Field",
    "Method" to "java.lang.reflect.Method",
    "AtomicInteger" to "java.util.concurrent.atomic.AtomicInteger",
    "Timer" to "java.util.Timer",
    "TimerTask" to "java.util.TimerTask",

    // GL
    "GL11" to "org.lwjgl.opengl.GL11",
    "GL12" to "org.lwjgl.opengl.GL12",
    "GL13" to "org.lwjgl.opengl.GL13",
    "GL14" to "org.lwjgl.opengl.GL14",
    "GL15" to "org.lwjgl.opengl.GL15",
    "GL20" to "org.lwjgl.opengl.GL20",
    "GL21" to "org.lwjgl.opengl.GL21",
    "GL30" to "org.lwjgl.opengl.GL30",
    "GL31" to "org.lwjgl.opengl.GL31",
    "GL32" to "org.lwjgl.opengl.GL32",
    "GL33" to "org.lwjgl.opengl.GL33",
    "GL40" to "org.lwjgl.opengl.GL40",
    "GL41" to "org.lwjgl.opengl.GL41",
    "GL42" to "org.lwjgl.opengl.GL42",
    "GL43" to "org.lwjgl.opengl.GL43",
    "GL44" to "org.lwjgl.opengl.GL44",
    "GL45" to "org.lwjgl.opengl.GL45",
)

val prologue = """
    /// <reference no-default-lib="true" />
    /// <reference lib="es2015" />
    export {};
    
    declare interface String {
      addFormatting(): string;
      addColor(): string;
      removeFormatting(): string;
      replaceFormatting(): string;
    }
    
    declare interface Number {
      easeOut(to: number, speed: number, jump: number): number;
      easeColor(to: number, speed: number, jump: number): java.awt.Color;
    }

    interface RegisterTypes {
      // client
      chat(...args: (string | unknown)[]): com.zephy.zjs.api.triggers.ChatTrigger;
      tick(ticksElapsed: number): com.zephy.zjs.api.triggers.Trigger;
      step(stepsElapsed: number): com.zephy.zjs.api.triggers.StepTrigger;
      gameLoad(): com.zephy.zjs.api.triggers.Trigger;
      gameUnload(): com.zephy.zjs.api.triggers.Trigger;
      itemTooltip(lore: TextComponent[], item: Item, event: org.spongepowered.asm.mixin.injection.callback.CallbackInfo): com.zephy.zjs.api.triggers.EventTrigger;
      serverConnect(): com.zephy.zjs.api.triggers.Trigger;
      serverDisconnect(): com.zephy.zjs.api.triggers.Trigger;
      
      // gui
      guiOpened(screen: net.minecraft.client.gui.screen.Screen, event: org.spongepowered.asm.mixin.injection.callback.CallbackInfo): com.zephy.zjs.api.triggers.EventTrigger;
      guiClosed(screen: net.minecraft.client.gui.screen.Screen): com.zephy.zjs.api.triggers.Trigger;
      clicked(moseX: number, mouseY: number, button: number, isPressed: boolean): com.zephy.zjs.api.triggers.Trigger;
      scrolled(mouseX: number, mouseY: number, scrollDelta: number): com.zephy.zjs.api.triggers.Trigger;
      dragged(mouseXDelta: number, mouseYDelta: number, mouseX: number, mouseY: number, mouseButton: number): com.zephy.zjs.api.triggers.Trigger;
      guiKey(char: String, keyCode: number, screen: net.minecraft.client.gui.screen.Screen, event: CancellableEvent): com.zephy.zjs.api.triggers.EventTrigger;
      guiMouseClick(mouseX: number, mouseY: number, mouseButton: number, isPressed: boolean, screen: net.minecraft.client.gui.screen.Screen, event: CancellableEvent): com.zephy.zjs.api.triggers.EventTrigger;
      guiMouseDrag(mouseXDelta: number, mouseYDelta: number, mouseX: number, mouseY: number, mouseButton: number, screen: net.minecraft.client.gui.screen.Screen, event: CancellableEvent): com.zephy.zjs.api.triggers.EventTrigger;
      
      // rendering
      preRenderWorld(partialTicks: number): com.zephy.zjs.api.triggers.Trigger;
      postRenderWorld(partialTicks: number): com.zephy.zjs.api.triggers.Trigger;
      preRenderGui(mouseX: number, mouseY: number, screen: net.minecraft.client.gui.screen.Screen, partialTicks: number, drawContext: net.minecraft.client.gui.DrawContext): com.zephy.zjs.api.triggers.Trigger;
      postRenderGui(mouseX: number, mouseY: number, screen: net.minecraft.client.gui.screen.Screen, partialTicks: number, drawContext: net.minecraft.client.gui.DrawContext): com.zephy.zjs.api.triggers.Trigger;
      renderBlockHighlight(position: BlockPos, event: CancellableEvent): com.zephy.zjs.api.triggers.EventTrigger;
      renderBlockEntity(blockEntity: BlockEntity, partialTicks: number, event: CancellableEvent): com.zephy.zjs.api.triggers.RenderBlockEntityTrigger;
      renderEntity(entity: Entity, partialTicks: number, event: CancellableEvent): com.zephy.zjs.api.triggers.RenderEntityTrigger;
      renderPlayerList(event: org.spongepowered.asm.mixin.injection.callback.CallbackInfo): com.zephy.zjs.api.triggers.EventTrigger;
      renderHudOverlay(): com.zephy.zjs.api.triggers.Trigger;
      renderScreenOverlay(drawContext: net.minecraft.client.gui.DrawContext, partialTicks: number): com.zephy.zjs.api.triggers.Trigger;
      renderHideableScreenOverlay(drawContext: net.minecraft.client.gui.DrawContext, partialTicks: number): com.zephy.zjs.api.triggers.Trigger;
      
      // world
      worldLoad(): com.zephy.zjs.api.triggers.Trigger;
      worldUnload(): com.zephy.zjs.api.triggers.Trigger;
      
      // misc
      command(...args: string[]): com.zephy.zjs.api.triggers.CommandTrigger;
    }
  
    declare global {
      const Java: {
        /**
         * Returns the Java Class or Package given by name. If you want to
         * enforce the name is a class, use Java.class() instead.
         */
        type(name: string): java.lang.Package | java.lang.Class<any>;
  
        /**
         * Returns the Java Class given by `className`. Throws an error if the
         * name is not a valid class name.
         */
        class(className: string): java.lang.Class<any>;
      };

      /**
       * Runs `func` in a Java synchronized() block with `lock` as the synchronizer
       */
      function sync(func: () => void, lock: unknown): void;
  
      /**
       * Runs `func` after `delayInMs` milliseconds. A new thread is spawned to accomplish
       * this, which means this function is asynchronous. If you want to avoid the Thread
       * instantiation, use `Client.scheduleTask(delayInTicks, func)`.
       */
      function setTimeout(func: () => void, delayInMs: number): void;

      const ArrayList: typeof java.util.ArrayList;
      interface ArrayList<T> extends java.util.ArrayList<T> {}
      const HashMap: typeof java.util.HashMap;
      interface HashMap<K, V> extends java.util.HashMap<K, V> {}
      
${providedTypes.entries.joinToString("") { (name, type) ->
    "const $name: typeof $type;\ninterface $name extends $type {}\n"
}.prependIndent("      ")}

      /**
       * Registers a new trigger and returns it.
       */
      function register<T extends keyof RegisterTypes>(
        name: T, 
        cb: (...args: Parameters<RegisterTypes[T]>) => void,
      ): ReturnType<RegisterTypes[T]>;

      /**
       * Cancels the given event
       */
      function cancel(event: CancellableEvent | org.spongepowered.asm.mixin.injection.callback.CallbackInfo): void;

      /**
       * Creates a custom trigger. `name` can be used as the first argument of a
       * subsequent call to `register`. Returns an object that can be used to
       * invoke the trigger.
       */
      function createCustomTrigger(name: string): { trigger(...args: unknown[]) };
      
      function easeOut(start: number, finish: number, speed: number, jump?: number): number;
      function easeColor(start: number, finish: number, speed: number, jump?: number): java.awt.Color;

      function print(message: string, color?: java.awt.Color): void;
      function println(message: string, color?: java.awt.Color, end?: string): void;

      const console: {
        assert(condition: boolean, message: string): void;
        clear(): void;
        count(label?: string): void;
        debug(args: unknown[]): void;
        dir(obj: object): void;
        dirxml(obj: object): void;
        error(...args: unknown[]): void;
        group(...args: unknown[]): void;
        groupCollapsed(...args: unknown[]): void;
        groupEnd(...args: unknown[]): void;
        info(...args: unknown[]): void;
        log(...args: unknown[]): void;
        table(data: object, columns?: string[]): void;
        time(label?: string): void;
        timeEnd(label?: string): void;
        trace(...args: unknown[]): void;
        warn(...args: unknown[]): void;
      };
""".trimIndent()
