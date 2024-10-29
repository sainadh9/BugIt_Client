package com.bugit.bugit.bugappscope.data

import com.bugit.bugit.bugappscope.domain.entities.CreateRowResponse
import com.bugit.bugit.bugappscope.domain.entities.CreateSheetResponse
import com.bugit.bugit.bugappscope.domain.entities.GetAllSheetsResponse
import com.bugit.bugit.bugappscope.domain.entities.PostCreateSheetModel
import com.bugit.bugit.bugappscope.domain.entities.PostRowToSheet
import retrofit2.Response
import javax.inject.Inject

class BugItRemoteRepository @Inject constructor(private val apiService: ApiServiceInterface) {


    suspend fun getAllSheetData(key: String?): Response<GetAllSheetsResponse> {
        return apiService.getAllSheetData(key)
    }

    suspend fun postCreateSheet(
        createSheetModel: PostCreateSheetModel,
        key: String?
    ): Response<CreateSheetResponse> {
        return apiService.postCreateSheet(createSheetModel, key)
    }

    suspend fun postCreateRow(
        range: String,
        insertDataOption: String,
        valueInputOption: String,
        key: String,
        postRowToSheet: PostRowToSheet,
    ): Response<CreateRowResponse> {
        return apiService.postCreateRow(
            range,
            insertDataOption,
            valueInputOption,
            key,
            postRowToSheet
        )
    }
}