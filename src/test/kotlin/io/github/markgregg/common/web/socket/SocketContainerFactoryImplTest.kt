package io.github.markgregg.common.api.socket

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import javax.websocket.WebSocketContainer

class SocketContainerFactoryImplTest : FunSpec({

	test("createSocketContainer") {
		SocketContainerFactoryImpl().createSocketContainer().shouldBeInstanceOf<WebSocketContainer>()
	}
})
