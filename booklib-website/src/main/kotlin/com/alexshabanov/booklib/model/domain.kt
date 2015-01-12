package com.alexshabanov.booklib.model

import java.util.Date
import com.truward.time.UtcTime

trait DomainModel

/** Book meta information model */
data class BookMeta(val id: Long?, val title: String, val fileSize: Int, val addDate: UtcTime,
                    val lang: String, val origin: String): DomainModel

data class NamedValue(val id: Long?, val name: String): DomainModel
