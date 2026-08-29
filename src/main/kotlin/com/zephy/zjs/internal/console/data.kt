package com.zephy.zjs.internal.console

import com.zephy.zjs.engine.LogType
import kotlinx.serialization.Serializable

@Serializable
sealed class H2CMessage

@Serializable
class InitMessage(
    val modVersion: String,
    val firaFontBytes: ByteArray?,
) : H2CMessage()

@Serializable
data object OpenMessage : H2CMessage()

@Serializable
data object TerminateMessage : H2CMessage()

@Serializable
class EvalResultMessage(val id: Int, val result: String) : H2CMessage()

@Serializable
class PrintMessage(
    val text: String,
    val logType: LogType,
    val end: String,
    val color: Int?,
) : H2CMessage()

@Serializable
class PrintErrorMessage(val error: Error) : H2CMessage() {
    @Serializable
    data class Error(val message: String, val trace: List<StackTrace>, val cause: Error?)
}

@Serializable
data class StackTrace(
    val fileName: String?,
    val className: String,
    val methodName: String,
    val lineNumber: Int,
)

@Serializable
data object ClearConsoleMessage : H2CMessage()

@Serializable
sealed class C2HMessage

@Serializable
class EvalTextMessage(val id: Int, val string: String) : C2HMessage()

@Serializable
data object ReloadZJSMessage : C2HMessage()
