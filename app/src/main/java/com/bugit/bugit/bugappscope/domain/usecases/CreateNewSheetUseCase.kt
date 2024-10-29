package com.bugit.bugit.bugappscope.domain.usecases

import com.bugit.bugit.bugappscope.data.BugItRemoteRepository
import com.bugit.bugit.bugappscope.domain.entities.CreateSheetResponse
import com.bugit.bugit.bugappscope.domain.entities.PostCreateSheetModel
import retrofit2.Response

class CreateNewSheetUseCase(private val bugItRemoteRepository: BugItRemoteRepository) {

    suspend fun postCreateSheet(
        postCreateSheetModel: PostCreateSheetModel,
        key: String?
    ): Response<CreateSheetResponse> {
        return bugItRemoteRepository.postCreateSheet(postCreateSheetModel, key)
    }
}