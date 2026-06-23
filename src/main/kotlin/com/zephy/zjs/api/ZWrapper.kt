package com.zephy.zjs.api

interface ZWrapper<MCClass> {
    val mcValue: MCClass

    fun toMC(): MCClass = mcValue
}
