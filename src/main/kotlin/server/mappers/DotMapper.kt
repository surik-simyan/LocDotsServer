package surik.simyan.locdots.server.mappers

import surik.simyan.locdots.server.data.Dot as DataDot
import surik.simyan.locdots.server.domain.Dot as DomainDot

fun List<DataDot>?.toDomain(): List<DomainDot> = if (this.isNullOrEmpty()) {
    emptyList()
} else {
    this.map { it.toDomain() }
}

fun DataDot.toDomain() = DomainDot(id.toHexString(), message, location, dateTime)