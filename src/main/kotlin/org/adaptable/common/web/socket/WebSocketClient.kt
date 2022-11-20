package org.adaptable.common.api.socket

import org.adaptable.common.protocol.Response
import javax.websocket.CloseReason
import javax.websocket.Session

interface WebSocketClient {
    val isClosed: Boolean
    fun connect()
    fun close()
    fun onOpen(openingSession: Session?)
    fun onClose(closingSession: Session?, reason: CloseReason?)
    fun onMessage(message: String?, last: Boolean)
    fun addMessageHandler(msgHandler: (Response) -> Unit)
    fun removeMessageHandler()
    fun <T>sendMessage(message: T)
}