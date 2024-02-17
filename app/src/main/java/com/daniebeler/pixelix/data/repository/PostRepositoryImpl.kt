package com.daniebeler.pixelix.data.repository

import com.daniebeler.pixelix.common.Resource
import com.daniebeler.pixelix.data.remote.PixelfedApi
import com.daniebeler.pixelix.data.remote.dto.PostDto
import com.daniebeler.pixelix.domain.model.LikedPostsWithNext
import com.daniebeler.pixelix.domain.model.Post
import com.daniebeler.pixelix.domain.repository.PostRepository
import com.daniebeler.pixelix.utils.NetworkCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.awaitResponse
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val pixelfedApi: PixelfedApi
) : PostRepository {
    override fun getPostById(postId: String): Flow<Resource<Post>> {
        return NetworkCall<Post, PostDto>().makeCall(pixelfedApi.getPostById(postId))
    }

    override fun getPostsByAccountId(
        accountId: String, maxPostId: String
    ): Flow<Resource<List<Post>>> {
        return if (maxPostId.isEmpty()) {
            NetworkCall<Post, PostDto>().makeCallList(
                pixelfedApi.getPostsByAccountId(
                    accountId
                )
            )
        } else {
            NetworkCall<Post, PostDto>().makeCallList(
                pixelfedApi.getPostsByAccountId(
                    accountId, maxPostId
                )
            )
        }
    }

    override fun likePost(postId: String): Flow<Resource<Post>> {
        return NetworkCall<Post, PostDto>().makeCall(pixelfedApi.likePost(postId))
    }

    override fun unlikePost(postId: String): Flow<Resource<Post>> {
        return NetworkCall<Post, PostDto>().makeCall(pixelfedApi.unlikePost(postId))
    }

    override fun bookmarkPost(postId: String): Flow<Resource<Post>> {
        return NetworkCall<Post, PostDto>().makeCall(pixelfedApi.bookmarkPost(postId))
    }

    override fun unBookmarkPost(postId: String): Flow<Resource<Post>> {
        return NetworkCall<Post, PostDto>().makeCall(
            pixelfedApi.unbookmarkPost(
                postId
            )
        )
    }

    override fun getLikedPosts(maxId: String): Flow<Resource<LikedPostsWithNext>> = flow {
        try {
            emit(Resource.Loading())
            val response = if (maxId.isNotBlank()) {
                pixelfedApi.getLikedPosts(maxId).awaitResponse()
            } else {
                pixelfedApi.getLikedPosts().awaitResponse()
            }

            if (response.isSuccessful) {

                val linkHeader = response.headers()["link"] ?: ""

                val onlyLink =
                    linkHeader.substringAfter("rel=\"next\",<", "").substringBefore(">", "")

                val nextMinId = onlyLink.substringAfter("min_id=", "")

                val res = response.body()?.map { it.toModel() } ?: emptyList()

                val result = LikedPostsWithNext(res, nextMinId)
                emit(Resource.Success(result))
            } else {
                emit(Resource.Error("Unknown Error"))
            }
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message ?: "Unknown Error"))
        }
    }

    override fun getBookmarkedPosts(): Flow<Resource<List<Post>>> {
        return NetworkCall<Post, PostDto>().makeCallList(pixelfedApi.getBookmarkedPosts())
    }

    override fun getTrendingPosts(range: String): Flow<Resource<List<Post>>> {
        return NetworkCall<Post, PostDto>().makeCallList(pixelfedApi.getTrendingPosts(range))
    }
}