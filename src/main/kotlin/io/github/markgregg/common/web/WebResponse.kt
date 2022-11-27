package io.github.markgregg.common.web

import io.github.markgregg.common.api.Payload
import io.github.markgregg.common.api.Response


interface WebResponse<T> : Response, Payload<T> {
    val status: Int?
    val body: String?
    val headers: Map<String,String>?
}