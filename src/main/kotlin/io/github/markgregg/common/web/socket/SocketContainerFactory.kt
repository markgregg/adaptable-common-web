package io.github.markgregg.common.api.socket

import javax.websocket.WebSocketContainer

interface SocketContainerFactory {
    fun createSocketContainer(): WebSocketContainer
}