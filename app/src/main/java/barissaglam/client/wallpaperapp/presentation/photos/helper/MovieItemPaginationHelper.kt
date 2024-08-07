package barissaglam.client.wallpaperapp.presentation.photos.helper

import androidx.lifecycle.viewModelScope
import androidx.paging.PageKeyedDataSource
import barissaglam.client.wallpaperapp.data.viewitem.PhotoViewItem
import barissaglam.client.wallpaperapp.presentation.photos.PhotosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovieItemPaginationHelper(private val viewModel: PhotosViewModel) : PageKeyedDataSource<Int, PhotoViewItem>() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, PhotoViewItem>) {
        viewModel.page = PhotosViewModel.FIRST_PAGE
        viewModel.getPhotosByQuery(true) { viewState ->
            viewState.getData()?.let { photosViewItem ->
                val nextPage = if (photosViewItem.totalPage > viewModel.page.plus(1)) viewModel.page.plus(1) else null
                callback.onResult(photosViewItem.photos, null, nextPage)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoViewItem>) {
        viewModel.page++
        viewModel.getPhotosByQuery(false) { viewState ->
            viewState.getData()?.let { photosViewItem ->
                val nextPage = if (photosViewItem.totalPage > viewModel.page.plus(1)) viewModel.page.plus(1) else null
                callback.onResult(photosViewItem.photos, nextPage)
            }
        }
    }


    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoViewItem>) {
    }
}
