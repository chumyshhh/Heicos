package com.heicos.domain.use_case

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.repository.CosplayRepository
import javax.inject.Inject

class DeleteCosplayPreviewUseCase @Inject constructor(
    private val repository: CosplayRepository
) {

    suspend operator fun invoke(cosplayPreview: CosplayPreview) {
        repository.deleteCosplayPreview(cosplayPreview)
    }

}