package org.adaptable.common.api.socket

import javax.websocket.ContainerProvider
import javax.websocket.WebSocketContainer

class SocketContainerFactoryImpl : SocketContainerFactory {
    override fun createSocketContainer(): WebSocketContainer {
        return ContainerProvider.getWebSocketContainer()
    }
}