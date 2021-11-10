package com.example.receiptApp.repository.sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.receiptApp.repository.AttachmentRepository.Attachment
import timber.log.Timber
import java.io.FileNotFoundException

class GalleryImagesPaginated(private val dataSource: GalleryImages) : PagingSource<Int, Attachment>()
{

    private var initialLoadSize: Int = 0

    override fun getRefreshKey(state: PagingState<Int, Attachment>): Int?
    {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Attachment>
    {
        return try
        {
            val pageNumber = params.key ?: 0

            if (params.key == null)
            {
                initialLoadSize = params.loadSize
            }

            val offset = if (pageNumber == 2)
            {
                initialLoadSize
            }
            else
            {
                ((pageNumber - 1) * params.loadSize) + (initialLoadSize - params.loadSize)
            }

            val images = dataSource.getImages(params.loadSize, offset) ?: throw FileNotFoundException("images not found")

            val count = images.size

            LoadResult.Page(
                data = images,
                prevKey = if (pageNumber > 0) pageNumber - 1 else null,
                // assume that if a full page is not loaded, that means the end of the data
                nextKey = if (count < params.loadSize) null else pageNumber + 1
            )

        } catch (e: Exception)
        {
            Timber.e(e)
            LoadResult.Error(e)
        }
    }

}