package com.bugit.bugit.bugappscope.domain.entities

data class PostCreateSheetModel(
    var requests: List<Requests>? = null
)

data class AddSheet(var properties: Properties? = null)

data class Requests(var addSheet: AddSheet? = null)

data class Properties(var title: String? = null)


data class PostRowToSheet(

    var values: List<List<String>>? = null

)

