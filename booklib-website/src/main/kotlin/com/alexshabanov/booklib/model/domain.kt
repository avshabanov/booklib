package com.alexshabanov.booklib.model

import java.util.Date

/** Book meta information model */
data class BookMeta(val id: Long?, val title: String, val fileSize: Int, val addDate: Date,
                    val lang: String, val origin: String)

data class NamedValue(val id: Long?, val name: String)

//
// Presentational model
//

data class Book(val meta: BookMeta, val authors: List<NamedValue>, val genres: List<NamedValue>)

