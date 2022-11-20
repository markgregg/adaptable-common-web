package org.adaptable.common.web

import java.io.FileInputStream
import java.util.*

data class BinaryResponse(
    override val status: Int,
    override val body: String,
    override val headers: Map<String,String>? = null,
    val isFile: Boolean? = null
) : WebResponse<ByteArray> {

    override fun payload(): ByteArray? {
        return if( isFile == true) {
            FileInputStream(body).use {
                it.readAllBytes()
            }
        } else {
            Base64.getDecoder().decode(body)
        }
    }
}
