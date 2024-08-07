package barissaglam.client.wallpaperapp.presentation.photos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import barissaglam.client.wallpaperapp.base.viewmodel.BaseViewModel
import barissaglam.client.wallpaperapp.data.viewitem.PhotoViewItem
import barissaglam.client.wallpaperapp.domain.usecase.CategoryUseCase
import barissaglam.client.wallpaperapp.domain.usecase.PhotosUseCase
import barissaglam.client.wallpaperapp.presentation.photos.helper.MovieItemPaginationHelper
import barissaglam.client.wallpaperapp.util.Resource
import barissaglam.client.wallpaperapp.view.categoryview.Category
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class PhotosViewModel @Inject constructor(categoryUseCase: CategoryUseCase, private val photosUseCase: PhotosUseCase) : BaseViewModel() {
    /** LiveData for ViewState **/
    private val _liveDataViewState = MutableLiveData<PhotosFragmentViewState>()
    val liveDataViewState: LiveData<PhotosFragmentViewState> = _liveDataViewState

    var page = FIRST_PAGE
    var selectedCategoryIndex = 0
    val categoryList = ArrayList<Category>()
    var paginationHelper = MovieItemPaginationHelper(this)

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery



    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        page = FIRST_PAGE

    }

    init {
        categoryList.addAll(categoryUseCase.getCategories())
    }

    fun getMovieList(): LiveData<PagedList<PhotoViewItem>> {
        val config = PagedList.Config.Builder()
            .setPageSize(30)
            .setInitialLoadSizeHint(30)
            .setPrefetchDistance(5)
            .setEnablePlaceholders(true)
            .build()
        return initPagedListBuilder(config).build()
    }

    private fun initPagedListBuilder(config: PagedList.Config): LivePagedListBuilder<Int, PhotoViewItem> {
        val dataSourceFactory = object : DataSource.Factory<Int, PhotoViewItem>() {
            override fun create(): DataSource<Int, PhotoViewItem> {
                return paginationHelper
            }
        }
        return LivePagedListBuilder(dataSourceFactory, config)
    }

    fun getPhotosByQuery(updateViewState: Boolean, block: (PhotosFragmentViewState) -> Unit) {
        _searchQuery.value?.let {
            photosUseCase.getPhotosByQuery(query = it, page = page)
                .onEach { response ->
                    val viewState = PhotosFragmentViewState(response)
                    block(viewState)
                    if (updateViewState || response is Resource.Success || response is Resource.Error) {
                        _liveDataViewState.value = viewState
                    }
                }.launchIn(viewModelScope)
        }
    }

    companion object {
        const val FIRST_PAGE = 1
    }

}