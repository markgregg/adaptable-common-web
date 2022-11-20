package org.adaptable.common.web

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.*

class WebRequestTest: FunSpec({
    test("Getters return correct values 1") {
        val id = UUID.randomUUID()
        val request = WebRequest(id, mapOf("head" to "value"), mapOf("param" to "value"), "body")
        request.id shouldBe  id
        request.headers shouldBe  mapOf("head" to "value")
        request.parameters shouldBe  mapOf("param" to "value")
        request.body shouldBe  "body"
    }

    test("Getters return correct values 2") {
        val request = WebRequest("body")
        request.body shouldBe  "body"
    }

    test("Getters return correct values 3") {
        val nodes = jacksonObjectMapper().readTree("{\"field\":\"value\"}") as ObjectNode
        val request = WebRequest(nodes)
        request.body shouldBe "{\"field\":\"value\"}"
    }

    test("Getters return correct values 4") {
        val nodes = jacksonObjectMapper().readTree("{\"field\":\"value\"}") as ObjectNode
        val request = WebRequest(mapOf("head" to "value"), mapOf("param" to "value"), nodes)
        request.headers shouldBe  mapOf("head" to "value")
        request.parameters shouldBe  mapOf("param" to "value")
        request.body shouldBe "{\"field\":\"value\"}"
    }

    test("Getters return correct values 5") {
        val request = WebRequest(mapOf("head" to "value"), mapOf("param" to "value"), "body")
        request.headers shouldBe  mapOf("head" to "value")
        request.parameters shouldBe  mapOf("param" to "value")
        request.body shouldBe  "body"
    }

    test("Getters return correct values 6") {
        val id = UUID.randomUUID()
        val nodes = jacksonObjectMapper().readTree("{\"field\":\"value\"}") as ObjectNode
        val request = WebRequest(id, mapOf("head" to "value"), mapOf("param" to "value"), nodes)
        request.id shouldBe  id
        request.headers shouldBe  mapOf("head" to "value")
        request.parameters shouldBe  mapOf("param" to "value")
        request.body shouldBe "{\"field\":\"value\"}"
    }

    test("GetItem returns text") {
        val id = UUID.randomUUID()
        val nodes = jacksonObjectMapper().readTree("{\"field\":\"value\"}") as ObjectNode
        val request = WebRequest(id, mapOf("head" to "value"), mapOf("param" to "value"), nodes)
        request.getItem("text") shouldBe "{\"field\":\"value\"}"
    }

    test("GetItem returns json") {
        val id = UUID.randomUUID()
        val nodes = jacksonObjectMapper().readTree("{\"field\":\"value\"}") as ObjectNode
        val request = WebRequest(id, mapOf("head" to "value"), mapOf("param" to "value"), nodes)
        (request.getItem("body") as ObjectNode)["field"].textValue() shouldBe "value"
    }

    test("GetItem returns parameter") {
        val request = WebRequest(mapOf("head" to "value"), mapOf("param" to "value"), "test")
        (request.getItem("parameter") as ObjectNode)["param"].textValue() shouldBe "value"
    }

    test("GetItem returns empty object if parameters null") {
        val request = WebRequest("test")
        request.getItem("parameter").toString() shouldBe "{}"
    }

    test("GetItem returns header") {
        val request = WebRequest(mapOf("head" to "value"), mapOf("param" to "value"), "test")
        (request.getItem("header") as ObjectNode)["head"].textValue() shouldBe "value"
    }

    test("GetItem returns empty object headers null") {
        val request = WebRequest("test")
        request.getItem("header").toString() shouldBe "{}"
    }

    test("GetItem throw UnknownPropertyException for unknown value") {
        val request = WebRequest("body")
        shouldThrow<UnknownPropertyException> {
            request.getItem("asdsda")
        }

    }

})