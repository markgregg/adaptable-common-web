package io.github.markgregg.common.api.socket

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.markgregg.common.protocol.Response
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.net.URI
import java.util.concurrent.atomic.AtomicReference
import javax.websocket.*

@ClientEndpoint
class WebSocketClientImpl (
    private val endpointURI: URI,
    private val connectionAttempts: Short,
    private val delayBetweenAttemptsToConnect: Long,
    private val socketContainerFactory: SocketContainerFactory
)  : WebSocketClient {
    companion object {
        private val logger = LoggerFactory.getLogger(WebSocketClientImpl::class.java)
    }
    private var session = AtomicReference<Session?>()
    private var messageHandler: ((Response) -> Unit)? = null
    private var textBuffer: StringBuilder? = null

    override val isClosed: Boolean
        get() = session.get() == null

    /***
     *
     */
    @Throws(AgentUnavailableException::class)
    override fun connect() {
        logger.info("Connecting")
        for( attempts in 1..connectionAttempts) {
            logger.info("Connection attempt $attempts")
            try {
                val socketContainer = socketContainerFactory.createSocketContainer()
                socketContainer.connectToServer(this, endpointURI)
                return
            } catch (e: Exception) {
                logger.warn("Failed to connect to agent", e)
            }
            sleep(delayBetweenAttemptsToConnect)
        }
        logger.error("Failed to connect to agent too many attempts")
        throw AgentUnavailableException()
    }

    /***
     *
     */
    override fun close() {
        logger.info("Closing connection")
        session.get()?.close(CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,"Done!"))
        session.set(null)
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param openingSession the userSession which is opened.
     */
    @OnOpen
    override fun onOpen(openingSession: Session?) {
        logger.debug("opening websocket $openingSession")
        session.set(openingSession)
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param closingSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    override fun onClose(closingSession: Session?, reason: CloseReason?) {
        logger.debug("closing websocket $closingSession, reason: $reason")
        session.set(null)
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     * @param last is last message
     */
    @OnMessage
    override fun onMessage(message: String?, last: Boolean) {
        logger.debug("Received $message, last = $last")
        if (textBuffer == null) {
            textBuffer = StringBuilder(message)
        } else {
            textBuffer!!.append(message)
        }
        if (last) {
            logger.debug("Notifying test of new request")
            messageHandler?.invoke(jacksonObjectMapper().readValue(textBuffer.toString(), Response::class.java))
            textBuffer = null
        }
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    override fun addMessageHandler(msgHandler: (Response) -> Unit) {
        messageHandler = msgHandler
    }

    override fun removeMessageHandler() {
        messageHandler = null
    }

    override fun <T>sendMessage(message: T) {
        if( session.get() == null ) {
            logger.error("Socket client has not been initialised")
            throw UninitialisedException()
        }
        session.get()?.asyncRemote?.sendText(jacksonObjectMapper().writeValueAsString(message))
    }
}

