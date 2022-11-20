package org.adaptable.common.web

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.adaptable.common.api.Request
import java.util.*

data class WebRequest(
    val id: UUID,
    val headers: Map<String,String>?,
    val parameters: Map<String,String>?,
    val body: String
) : Request {
    private var json: ObjectNode? = null
    constructor( objectNode: ObjectNode) : this(UUID.randomUUID(), emptyMap(), emptyMap(), objectNode)
    constructor(body: String) : this(UUID.randomUUID(), emptyMap(), emptyMap(), body)
    constructor(headers: Map<String, String>?, parameters: Map<String, String>?, objectNode: ObjectNode)
            : this(UUID.randomUUID(), headers, parameters, objectNode)
    constructor(headers: Map<String, String>?, parameters: Map<String, String>?, body: String)
            : this(UUID.randomUUID(), headers, parameters, body)
    constructor(id: UUID, headers: Map<String, String>?, parameters: Map<String, String>?, objectNode: ObjectNode)
            : this(id,headers, parameters, objectNode.toString() ) {
                json = objectNode
            }

    override fun getItem(name: String): Any {
        return when(name) {
            "text" -> body
            "body" -> asJson()
            "parameter" -> if (parameters == null) {
                    jacksonObjectMapper().createObjectNode()
                } else {
                    jacksonObjectMapper().valueToTree(parameters)
                }
            "header" -> if (headers == null) {
                    jacksonObjectMapper().createObjectNode()
                } else {
                    jacksonObjectMapper().valueToTree(headers)
                }
            else -> throw UnknownPropertyException("$name is not part of this request")
        }
    }

    fun asJson(): ObjectNode {
        if( json == null ) {
            json = try {
                jacksonObjectMapper().readTree(body) as? ObjectNode
                    ?: jacksonObjectMapper().createObjectNode()
            }  catch (e: Exception) {
                jacksonObjectMapper().createObjectNode()
            }
        }
        return json!!
    }

    fun <T> asObject(ofType: Class<T>): T {
        return jacksonObjectMapper().readValue(body, ofType)
    }

}