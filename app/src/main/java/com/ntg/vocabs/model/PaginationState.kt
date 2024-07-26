package com.ntg.vocabs.model

enum class PaginationState {
    REQUEST_INACTIVE,
    LOADING,
    PAGINATING,
    ERROR,
    PAGINATION_EXHAUST,
    EMPTY,
    FETCHED
}
