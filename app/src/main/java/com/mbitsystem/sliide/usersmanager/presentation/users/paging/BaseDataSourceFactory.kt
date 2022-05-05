package com.mbitsystem.sliide.usersmanager.presentation.users.paging

import androidx.paging.DataSource
import androidx.paging.PositionalDataSource

open class BaseDataSourceFactory<T> : DataSource.Factory<Int, T>() {

    protected open var lastSource: PositionalDataSource<T>? = null

    private var items: List<T> = listOf()

    protected fun loadRangeInternal(startPosition: Int, loadCount: Int, list: List<T>): List<T> = list.subList(startPosition, startPosition + loadCount)

    override fun create(): DataSource<Int, T> = object : PositionalDataSource<T>() {

        override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) = with(items) {
            val offset = params.startPosition
            val limit = if (params.startPosition + params.loadSize > size) size else params.startPosition + params.loadSize
            callback.onResult(subList(offset, limit))
        }

        override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) = with(items) {
            val position = computeInitialLoadPosition(params, size)
            val loadSize = computeInitialLoadSize(params, position, size)
            val resultList = loadRangeInternal(position, loadSize, this)
            callback.onResult(resultList, position, size)
        }
    }.apply { lastSource = this }

    fun removeItemAndInvalidate(item: T) {
        items = items.filter { it != item }
        invalidate()
    }

    fun addItems(newItems: List<T>){
        submitItems(items + newItems)
    }

    fun submitItems(newItems: List<T>){
        items = newItems
        invalidate()
    }

    fun invalidate(){
        lastSource?.invalidate()
    }
}
