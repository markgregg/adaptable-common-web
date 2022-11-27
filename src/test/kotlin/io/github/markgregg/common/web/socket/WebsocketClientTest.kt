package io.github.markgregg.common.api.socket

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.github.markgregg.common.protocol.Response
import io.github.markgregg.common.protocol.StartTestResponse
import org.mockito.Mockito.mock
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.net.URI
import java.util.concurrent.atomic.AtomicReference
import javax.websocket.CloseReason
import javax.websocket.RemoteEndpoint
import javax.websocket.Session
import javax.websocket.WebSocketContainer

class WebsocketClientTest : FunSpec({
    val socketContainerFactoryMock = mock(SocketContainerFactory::class.java)
    val websocketClient = WebSocketClientImpl(URI("org/adaptable/common/web/test"), 2, 5, socketContainerFactoryMock)

    test("calling connect does not throw an exception if successful") {
        val container = mock(WebSocketContainer::class.java)
        whenever(socketContainerFactoryMock.createSocketContainer()).thenReturn(container)
        websocketClient.connect()
        verify(container).connectToServer(websocketClient, URI("org/adaptable/common/web/test"))
    }

    test("calling connect throws an exception if unable to connect") {
        val container = mock(WebSocketContainer::class.java)
        whenever(socketContainerFactoryMock.createSocketContainer()).thenReturn(container)
        whenever(container.connectToServer(websocketClient, URI("org/adaptable/common/web/test"))).thenAnswer {
            throw Exception("error")
        }

        shouldThrowExactly<AgentUnavailableException> {
            websocketClient.connect()
        }
        verify(container, times(2)).connectToServer(websocketClient, URI("org/adaptable/common/web/test"))
    }


    test("when open messages can be sent down the socket") {
        val openingSession = mock(Session::class.java)
        val asyncEndPoint = mock(RemoteEndpoint.Async::class.java)
        whenever(openingSession.asyncRemote).thenReturn(asyncEndPoint)
        websocketClient.onOpen(openingSession)

        websocketClient.sendMessage("test")
        verify(asyncEndPoint).sendText("\"test\"")
    }

    test("When closed an exception is thrown when sending a message") {
        val openingSession = mock(Session::class.java)
        val asyncEndPoint = mock(RemoteEndpoint.Async::class.java)
        whenever(openingSession.asyncRemote).thenReturn(asyncEndPoint)
        websocketClient.onOpen(openingSession)
        websocketClient.onClose(openingSession, CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,"org/adaptable/common/web/test"))

        shouldThrowExactly<UninitialisedException> {
            websocketClient.sendMessage("org/adaptable/common/web/test")
        }
    }

    test("When message is received and handler available notify") {
        val message = AtomicReference<Response>()
        websocketClient.addMessageHandler { m -> message.set(m)  }

        val response = jacksonObjectMapper().writeValueAsString(StartTestResponse(false, "This is a big error and cannot be fixed"))
        websocketClient.onMessage(response.substring(0, 20), false)
        websocketClient.onMessage(response.substring(20), true)

        message.get().message shouldBe "This is a big error and cannot be fixed"
    }

    test("When message is received and no handler do nothing") {
        val response = jacksonObjectMapper().writeValueAsString(StartTestResponse(false, "This is a big error and cannot be fixed"))
        websocketClient.onMessage(response.substring(0, 20), false)
        websocketClient.onMessage(response.substring(20), true)
    }

    test("When close session is closed and set to null") {
        val session = mock(Session::class.java)
        websocketClient.onOpen(session)
        websocketClient.close()
        val captor = argumentCaptor<CloseReason>()
        verify(session).close(captor.capture())
        captor.firstValue.closeCode shouldBe CloseReason.CloseCodes.NORMAL_CLOSURE
        captor.firstValue.reasonPhrase shouldBe "Done!"
    }

    test("When close and session not open nothing is done") {
        val session = mock(Session::class.java)
        websocketClient.onOpen(session)
        websocketClient.close()
        verify(session, times(0)).close(CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,"Done!"))
    }

})
