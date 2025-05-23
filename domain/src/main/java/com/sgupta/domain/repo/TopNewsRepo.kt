package com.sgupta.domain.repo

import androidx.paging.PagingData
import com.sgupta.core.network.Resource
import com.sgupta.domain.model.NewsDataModel
import com.sgupta.domain.model.request.NewsRequestParam
import com.sgupta.domain.model.request.NewsSearchQueryRequestParam
import kotlinx.coroutines.flow.Flow

interface TopNewsRepo {
    fun getTopNews(param: NewsRequestParam): Flow<Resource<NewsDataModel>>
    fun getCountryNews(param: NewsRequestParam): Flow<PagingData<NewsDataModel>>
    fun getCategoryNews(param: NewsRequestParam): Flow<PagingData<NewsDataModel>>
    fun getNewsSearchQuery(param: NewsSearchQueryRequestParam): Flow<Resource<NewsDataModel>>
}