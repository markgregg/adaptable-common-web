package org.adaptable.common.web

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TextResponseTest : FunSpec({
    test("Getters return correct values") {
        val textResponse = TextResponse(1,"test", mapOf("head" to "value"), false)
        textResponse.status shouldBe 1
        textResponse.body shouldBe "test"
        textResponse.headers shouldBe mapOf("head" to "value")
        textResponse.isFile shouldBe false
    }

    test("Binary payload is correctly decoded") {
        val textResponse = TextResponse(1,"test", mapOf("head" to "value"), false)

        textResponse.payload() shouldBe "test"
    }

    test("Binary file is corrected decoded") {

        val textResponse = TextResponse(1, this.javaClass.classLoader.getResource("text.txt")!!.path , mapOf("head" to "value"), true)

        textResponse.payload() shouldBe "test"
    }

})