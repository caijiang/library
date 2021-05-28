package me.jiangcai.common.ext.rest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * page response
 * ```json
 *  "_links" : {
 * "self" : {
 * "href" : "http://localhost:52215/projectTypes"
 * },
 * "profile" : {
 * "href" : "http://localhost:52215/profile/projectTypes"
 * },
 * "search" : {
 * "href" : "http://localhost:52215/projectTypes/search"
 * }
 * },
 * ```
 * @author CJ
 */
@Suppress("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
data class PageResponse(
    val _embedded: Map<String, List<ResourceResponse>>,
    /**
     * "size" : 20,
     * "totalElements" : 6,
     * "totalPages" : 1,
     * "number" : 0
     */
    val page: Map<String, Number>
) {
    val content: List<ResourceResponse>
        get() {
            val key = _embedded.keys.first()
            return _embedded[key]!!
        }

    val totalElements: Int
        get() = (page["totalElements"] as Number).toInt()
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ListResponse(
    val _embedded: Map<String, List<ResourceResponse>>
) {
    val content: List<ResourceResponse>
        get() {
            val key = _embedded.keys.first()
            return _embedded[key]!!
        }
}

/**
 * ```json
 *  "_links" : {
 *  "self" : {
 *  "href" : "http://localhost:52215/projectTypes/1"
 *  },
 *  "projectType" : {
 *  "href" : "http://localhost:52215/projectTypes/1"
 *  }
 *  }
 * ```
 */
@Suppress("unused")
class ResourceResponse : HashMap<String, Any>() {
    @Suppress("UNCHECKED_CAST")
    val selfHref: String
        get() {
            return hrefFor("self")!!
        }

    fun hrefFor(name: String): String? {
        @Suppress("UNCHECKED_CAST")
        val links = get("_links") as Map<String, Map<String, String>>
        return links[name]?.get("href")
    }
}