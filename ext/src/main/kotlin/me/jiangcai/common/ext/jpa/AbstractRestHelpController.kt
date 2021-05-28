package me.jiangcai.common.ext.jpa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.rest.webmvc.PersistentEntityResource
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.core.EmbeddedWrappers
import org.springframework.http.ResponseEntity
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

/**
 * 获取资源集合参考 [org.springframework.data.rest.webmvc.RepositoryEntityController.getCollectionResource]
 * @author CJ
 */
@Suppress("unused", "SpringJavaAutowiredMembersInspection")
abstract class AbstractRestHelpController<T>(
    private val type: Class<T>
) {
    val wrappers = EmbeddedWrappers(false)

    @Autowired
    private lateinit var pagedResourcesAssembler: PagedResourcesAssembler<T>

    protected open fun getDefaultSelfLink(): Link {
        return Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString())
    }

    protected inline fun <reified X> renderList(
        list: List<X>,
        assembler: PersistentEntityResourceAssembler
    ): ResponseEntity<CollectionModel<out Any>> {
        val model = if (list.isEmpty()) {
            val content = listOf<Any>(wrappers.emptyCollectionOf(X::class.java))
            CollectionModel.of(content, getDefaultSelfLink())
        } else {
            val resources = list.map { assembler.toModel(it) }
            CollectionModel.of<EntityModel<Any>>(resources, getDefaultSelfLink())
        }

        return ResponseEntity.ok(model)
    }

    protected fun renderPage(
        page: Page<T>,
        assembler: PersistentEntityResourceAssembler
    ): ResponseEntity<PagedModel<out Any>> {
        val baseLink = Optional.of(getDefaultSelfLink())

        @Suppress("UNCHECKED_CAST")
        val modelAssembler = assembler as RepresentationModelAssembler<T, PersistentEntityResource>

        val model = if (page.isEmpty)
            baseLink.map {
                pagedResourcesAssembler.toEmptyModel(page, type, it)
            }.orElseGet {
                pagedResourcesAssembler.toEmptyModel(page, type)
            }
        else baseLink.map {
            pagedResourcesAssembler.toModel(page, modelAssembler, it)
        }.orElseGet {
            pagedResourcesAssembler.toModel(page, modelAssembler)
        }

        return ResponseEntity.ok(model)
    }
}