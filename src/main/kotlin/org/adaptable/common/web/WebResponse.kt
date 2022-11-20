package org.adaptable.common.web

import org.adaptable.common.api.Payload
import org.adaptable.common.api.Response


interface WebResponse<T> : Response, Payload<T> {
    val status: Int?
    val body: String?
    val headers: Map<String,String>?
}