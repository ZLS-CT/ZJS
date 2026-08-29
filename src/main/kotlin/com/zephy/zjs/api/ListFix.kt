package com.zephy.zjs.api

import java.lang.reflect.Field
import java.lang.reflect.Method

object ListFix {
    fun getField(event: Any, fieldName: String): MutableList<Any?> {
        val field = event.javaClass.getDeclaredField(fieldName)
        if (!field.isAccessible) field.isAccessible = true
        return field.get(event) as MutableList<Any?>
    }
    fun getField(event: Any, fieldName: String, index: Int): Any? {
        return getField(event, fieldName)[index]
    }
    fun getField(event: Any, field: Field): MutableList<Any?> {
        if (!field.isAccessible) field.isAccessible = true
        return field.get(event) as MutableList<Any?>
    }
    fun getField(event: Any, field: Field, index: Int): Any? {
        return getField(event, field)[index]
    }

    fun getMethod(event: Any, methodName: String): MutableList<Any?> {
        val method = event.javaClass.getDeclaredMethod(methodName)
        if (!method.isAccessible) method.isAccessible = true
        return method.invoke(event) as MutableList<Any?>
    }
    fun getMethod(event: Any, methodName: String, index: Int): Any? {
        return getMethod(event, methodName)[index]
    }
    fun getMethod(event: Any, method: Method): MutableList<Any?> {
        if (!method.isAccessible) method.isAccessible = true
        return method.invoke(event) as MutableList<Any?>
    }
    fun getMethod(event: Any, method: Method, index: Int): Any? {
        return getMethod(event, method)[index]
    }

    fun addField(event: Any, fieldName: String, str: String) {
        getField(event, fieldName).add(str)
    }
    fun addField(event: Any, field: Field, str: String) {
        getField(event, field).add(str)
    }
    fun addM(event: Any, methodName: String, str: String) {
        getMethod(event, methodName).add(str)
    }
    fun addM(event: Any, method: Method, str: String) {
        getMethod(event, method).add(str)
    }

    fun clearField(event: Any, fieldName: String) {
        getField(event, fieldName).clear()
    }
    fun clearField(event: Any, field: Field) {
        getField(event, field).clear()
    }
    fun clearM(event: Any, methodName: String) {
        getMethod(event, methodName).clear()
    }
    fun clearM(event: Any, method: Method) {
        getMethod(event, method).clear()
    }

    fun setField(event: Any, fieldName: String, index: Int, str: String) {
        getField(event, fieldName)[index] = str
    }
    fun setField(event: Any, field: Field, index: Int, str: String) {
        getField(event, field)[index] = str
    }
    fun setMethod(event: Any, methodName: String, index: Int, str: String) {
        getMethod(event, methodName)[index] = str
    }
    fun setMethod(event: Any, method: Method, index: Int, str: String) {
        getMethod(event, method)[index] = str
    }

    fun removeField(event: Any, fieldName: String, index: Int) {
        getField(event, fieldName).removeAt(index)
    }
    fun removeField(event: Any, field: Field, index: Int) {
        getField(event, field).removeAt(index)
    }
    fun removeMethod(event: Any, methodName: String, index: Int) {
        getMethod(event, methodName).removeAt(index)
    }
    fun removeMethod(event: Any, method: Method, index: Int) {
        getMethod(event, method).removeAt(index)
    }

    fun insertField(event: Any, fieldName: String, index: Int, str: String) {
        getField(event, fieldName).add(index, str)
    }
    fun insertField(event: Any, field: Field, index: Int, str: String) {
        getField(event, field).add(index, str)
    }
    fun insertMethod(event: Any, methodName: String, index: Int, str: String) {
        getMethod(event, methodName).add(index, str)
    }
    fun insertMethod(event: Any, method: Method, index: Int, str: String) {
        getMethod(event, method).add(index, str)
    }

    fun sizeField(event: Any, fieldName: String): Int {
        return getField(event, fieldName).size
    }
    fun sizeField(event: Any, field: Field): Int {
        return getField(event, field).size
    }
    fun sizeMethod(event: Any, methodName: String): Int {
        return getMethod(event, methodName).size
    }
    fun sizeMethod(event: Any, method: Method): Int {
        return getMethod(event, method).size
    }
}
