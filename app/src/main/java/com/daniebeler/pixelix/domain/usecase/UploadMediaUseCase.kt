package com.daniebeler.pixelix.domain.usecase

import android.content.Context
import android.net.Uri
import com.daniebeler.pixelix.common.Resource
import com.daniebeler.pixelix.domain.model.MediaAttachment
import com.daniebeler.pixelix.domain.model.MediaAttachmentConfiguration
import com.daniebeler.pixelix.domain.repository.PostEditorRepository
import kotlinx.coroutines.flow.Flow

class UploadMediaUseCase(
    private val postEditorRepository: PostEditorRepository
) {
    operator fun invoke(
        url: Uri,
        description: String,
        context: Context,
        mediaAttachmentConfiguration: MediaAttachmentConfiguration
    ): Flow<Resource<MediaAttachment>> {
        return postEditorRepository.uploadMedia(
            url, description, context, mediaAttachmentConfiguration
        )
    }
}