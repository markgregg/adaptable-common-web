package org.adaptable.common.web

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BinaryResponseTest : FunSpec({

    test("Getters return correct values") {
        val binaryResponse = BinaryResponse(1,"test", mapOf("head" to "value"), false)
        binaryResponse.status shouldBe 1
        binaryResponse.body shouldBe "test"
        binaryResponse.headers shouldBe mapOf("head" to "value")
        binaryResponse.isFile shouldBe false
    }

    test("Binary payload is correctly decoded") {
        val binaryResponse = BinaryResponse(1,"dGVzdA==", mapOf("head" to "value"), false)

        String(binaryResponse.payload()!!) shouldBe "test"
    }

    test("Binary file is corrected decoded") {

        val binaryResponse = BinaryResponse(1, this.javaClass.classLoader.getResource("text.txt")!!.path , mapOf("head" to "value"), true)

        String(binaryResponse.payload()!!) shouldBe "test"
    }

})
