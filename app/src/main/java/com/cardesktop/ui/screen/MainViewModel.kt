package com.cardesktop.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardesktop.data.model.AppInfo
import com.cardesktop.data.model.WeatherInfo
import com.cardesktop.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 主桌面 ViewModel
 */
class MainViewModel : ViewModel() {

    // 已安装应用列表
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    // 快捷方式（桌面固定的应用）
    private val _shortcuts = MutableStateFlow<List<AppInfo>>(emptyList())
    val shortcuts: StateFlow<List<AppInfo>> = _shortcuts.asStateFlow()

    // 底栏固定应用
    private val _dockApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val dockApps: StateFlow<List<AppInfo>> = _dockApps.asStateFlow()

    // 时间
    private val _currentTime = MutableStateFlow("")
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()

    private val _currentDate = MutableStateFlow("")
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    // 天气
    private val _weather = MutableStateFlow(WeatherInfo())
    val weather: StateFlow<WeatherInfo> = _weather.asStateFlow()

    // 搜索
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<AppInfo>>(emptyList())
    val searchResults: StateFlow<List<AppInfo>> = _searchResults.asStateFlow()

    // 加载状态
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadApps()
        startTimeUpdater()
    }

    /**
     * 加载已安装应用
     */
    private fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            val allApps = AppRepository.getInstalledApps()
            _apps.value = allApps

            // 默认快捷方式：找常用应用
            val defaultPackages = listOf(
                "com.autonavi.minimap",        // 高德地图
                "com.baidu.BaiduMap",          // 百度地图
                "com.android.music",           // 音乐
                "com.android.settings",        // 设置
                "com.android.camera2",         // 相机
                "com.android.deskclock",       // 时钟
            )
            val shortcuts = defaultPackages.mapNotNull { pkg ->
                allApps.find { it.packageName == pkg }
            }
            _shortcuts.value = shortcuts.ifEmpty { allApps.take(6) }

            // 底栏应用
            val dockPackages = listOf("com.android.settings")
            val dock = dockPackages.mapNotNull { pkg ->
                allApps.find { it.packageName == pkg }
            }
            _dockApps.value = dock.ifEmpty { allApps.take(4) }

            _isLoading.value = false
        }
    }

    /**
     * 刷新应用列表（安装/卸载后调用）
     */
    fun refreshApps() {
        loadApps()
    }

    /**
     * 搜索应用
     */
    fun searchApps(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _searchResults.value = if (query.isBlank()) {
                emptyList()
            } else {
                _apps.value.filter {
                    it.label.contains(query, ignoreCase = true)
                }
            }
        }
    }

    /**
     * 更新时间
     */
    private fun startTimeUpdater() {
        viewModelScope.launch {
            while (true) {
                val now = Date()
                _currentTime.value = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
                _currentDate.value = SimpleDateFormat("M月d日 EEEE", Locale.CHINA).format(now)
                kotlinx.coroutines.delay(1000)
            }
        }
    }
}
