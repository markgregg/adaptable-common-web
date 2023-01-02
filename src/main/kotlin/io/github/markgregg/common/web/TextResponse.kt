package io.github.markgregg.common.web

import java.io.BufferedReader
import java.io.FileReader

data class TextResponse (
    override val status: Int,
    override val body: String,
    override val headers: Map<String,String>? = null,
    val isFile: Boolean? = null
) : WebResponse<String> {
    override fun payload(): String =
       if( isFile == true) {
            BufferedReader(FileReader(body)).use {
                it.readText()
            }
        } else {
            body
        }

}
