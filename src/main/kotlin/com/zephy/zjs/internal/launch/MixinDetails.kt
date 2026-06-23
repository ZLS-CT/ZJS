package com.zephy.zjs.internal.launch

import com.zephy.zjs.engine.MixinCallback

internal data class MixinDetails(
    val injectors: MutableList<MixinCallback> = mutableListOf(),
    val fieldWideners: MutableMap<String, Boolean> = mutableMapOf(),
    val methodWideners: MutableMap<String, Boolean> = mutableMapOf(),
)
