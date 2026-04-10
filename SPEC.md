# CarDesktop 开发规范

> 本文档是 CarDesktop 项目的统一开发标准，所有贡献者必须遵守。

---

## 目录

1. [技术架构规范](#1-技术架构规范)
2. [代码规范](#2-代码规范)
3. [UI/UX 设计规范](#3-uiux-设计规范)
4. [驾驶安全规范](#4-驾驶安全规范)
5. [性能规范](#5-性能规范)
6. [兼容性规范](#6-兼容性规范)
7. [Git 工作流](#7-git-工作流)
8. [测试规范](#8-测试规范)
9. [发布规范](#9-发布规范)

---

## 1. 技术架构规范

### 1.1 架构模式：MVVM + Repository

```
┌─────────────────────────────────────────────────┐
│                   UI Layer                       │
│  Composable Screen ←→ ViewModel (StateFlow)     │
├─────────────────────────────────────────────────┤
│                 Domain Layer                     │
│  UseCase (可选，复杂业务时引入)                    │
├─────────────────────────────────────────────────┤
│                  Data Layer                      │
│  Repository ←→ Local(DB/SP) / Remote(API)       │
└─────────────────────────────────────────────────┘
```

**强制规则：**

- UI 层（Composable）**只负责展示**，不包含任何业务逻辑
- ViewModel **不持有 Context 引用**（Application 除外）
- 数据获取**必须通过 Repository**，禁止 ViewModel 直接访问数据源
- Activity/Fragment **不直接操作数据**，只做 Compose 的入口

### 1.2 单向数据流 (UDF)

```
Event → ViewModel → State → UI
  ↑                        │
  └────────────────────────┘
```

- **State**：用 `StateFlow` 或 `State` 暴露，UI 只读
- **Event**：UI 触发的用户操作，通过 ViewModel 方法传递
- **禁止**：UI 直接修改 ViewModel 的内部状态

```kotlin
// ✅ 正确
class MainViewModel : ViewModel() {
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    fun onAppClick(app: AppInfo) { /* 处理逻辑 */ }
}

// ❌ 错误：UI 直接修改状态
fun MainScreen() {
    val apps by viewModel.apps.collectAsState()
    apps.add(newApp)  // 禁止！
}
```

### 1.3 模块职责划分

| 模块 | 职责 | 禁止做的事 |
|---|---|---|
| `ui/screen/` | 页面级 Composable 组合 | 网络请求、数据库操作 |
| `ui/widget/` | 可复用 UI 组件 | 页面导航逻辑、ViewModel 引用 |
| `ui/theme/` | 颜色、尺寸、字体定义 | 业务逻辑 |
| `data/model/` | 纯数据类（POJO/DTO） | UI 逻辑、Context 依赖 |
| `data/repository/` | 数据获取与缓存策略 | UI 展示逻辑 |
| `service/` | 后台服务、定时任务 | UI 操作 |
| `receiver/` | 系统广播接收 | 长时间运行逻辑 |
| `util/` | 纯工具函数（无状态） | 业务逻辑 |

---

## 2. 代码规范

### 2.1 命名规则

| 类型 | 规则 | 示例 |
|---|---|---|
| 包名 | 全小写，点分隔 | `com.cardesktop.ui.screen` |
| 类/接口 | PascalCase | `MainViewModel`, `AppRepository` |
| 函数/变量 | camelCase | `loadApps()`, `currentTime` |
| 常量 | UPPER_SNAKE_CASE | `MAX_GRID_COLUMNS` |
| Composable | PascalCase（带描述性前缀） | `MainScreen`, `DesktopAppIcon` |
| 私有状态变量 | 下划线前缀 | `_apps`, `_isLoading` |
| XML 资源 | snake_case | `ic_launcher_foreground` |

### 2.2 文件组织

```kotlin
// 每个文件的标准顺序
package com.cardesktop.xxx   // 1. 包声明

import ...                   // 2. 导入（按字母排序，Android → 第三方 → 项目）

// 3. 常量/伴生对象
// 4. 主类/函数
// 5. 内部辅助函数
```

### 2.3 Compose 规范

```kotlin
// ✅ Composable 命名：描述"显示什么"，不是"怎么显示"
@Composable
fun WeatherWidget(...)        // 好
@Composable
fun DrawWeatherCard(...)      // 差：Draw 动词前缀

// ✅ 参数顺序：modifier 默认第一个参数（可选时）
@Composable
fun DesktopAppIcon(
    app: AppInfo,              // 必选数据参数
    onClick: () -> Unit,       // 回调
    modifier: Modifier = Modifier,  // modifier 放最后（有默认值）
    iconSize: Dp = 64.dp       // 可选配置参数
)

// ✅ 状态提升：Composable 不持有可变状态
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val apps by viewModel.apps.collectAsState()  // 从 ViewModel 读取
}

// ❌ 禁止在 Composable 中创建可变状态用于业务逻辑
@Composable
fun MainScreen() {
    var apps by remember { mutableStateOf(emptyList()) }  // 不要这样做
}

// ✅ 预览函数必须提供默认参数
@Preview(device = Devices.AUTOMOTIVE_1024p_landscape)
@Composable
private fun MainScreenPreview() {
    CarDesktopTheme { MainScreen() }
}
```

### 2.4 ViewModel 规范

```kotlin
// ✅ 使用 StateFlow 暴露状态
class MainViewModel : ViewModel() {
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    // ✅ 协程在 viewModelScope 中启动
    fun loadApps() {
        viewModelScope.launch {
            _apps.value = appRepository.getApps()
        }
    }
}

// ❌ 禁止在 ViewModel 中持有 Activity/Fragment 引用
class MainViewModel(
    private val activity: MainActivity  // 绝对禁止
) : ViewModel()
```

### 2.5 注释规范

```kotlin
/**
 * 获取所有可启动的已安装应用
 *
 * @param excludeSystem 是否排除系统应用
 * @return 按应用名称排序的应用列表
 */
suspend fun getInstalledApps(excludeSystem: Boolean = false): List<AppInfo>

// 单行注释：说明"为什么"，而不是"做什么"
// 过滤掉没有 LAUNCHER intent 的包，避免显示无法启动的应用
val filtered = packages.filter { hasLauncherIntent(it) }
```

---

## 3. UI/UX 设计规范

### 3.1 布局基准

| 参数 | 值 | 说明 |
|---|---|---|
| 设计稿尺寸 | 1920 × 1080 (16:9) | 基准横屏分辨率 |
| 网格列数 | 4~6 列（可配置） | 桌面应用网格 |
| 网格行数 | 2~3 行 | 主屏可见应用 |
| Dock 高度 | 80dp | 底部固定栏 |
| 状态栏高度 | 48dp | 顶部信息栏 |
| 内容安全区 | 距边缘 24dp | 防止误触 |

### 3.2 组件尺寸

| 组件 | 最小尺寸 | 推荐尺寸 | 说明 |
|---|---|---|---|
| 应用图标 | 48 × 48 dp | 64 × 64 dp | 驾驶安全最低 48dp |
| 触摸热区 | 48 × 48 dp | 64 × 64 dp | 可点击区域 |
| 按钮高度 | 48 dp | 56 dp | 文字按钮 |
| 图标按钮 | 48 × 48 dp | 64 × 64 dp | 图标按钮 |
| 卡片圆角 | 12 dp | 16~20 dp | 统一圆角风格 |
| 图标间距 | 4 dp | 8 dp | 网格内间距 |

### 3.3 字体规范

| 层级 | 字号 | 字重 | 颜色 | 用途 |
|---|---|---|---|---|
| H1 | 48sp | Light | TextPrimary | 时钟显示 |
| H2 | 20sp | Bold | TextPrimary | 页面标题 |
| H3 | 18sp | Medium | TextPrimary | 区域标题 |
| Body | 16sp | Regular | TextPrimary | 正文 |
| Caption | 14sp | Regular | TextSecondary | 辅助说明 |
| Small | 12sp | Regular | TextHint | 次要信息 |

**强制要求：正文最小 14sp，禁止使用 12sp 以下字号作为可读文字。**

### 3.4 色彩使用

```kotlin
// 色彩使用场景对应表
Primary (#00E5FF)      → 高亮元素、选中状态、进度条
Accent  (#FF6D00)      → 强调按钮、警告提示
Background (#0A0E14)   → 全屏背景
Surface  (#141B26)     → 卡片、弹窗背景
TextPrimary (#E8EAF0)  → 标题、正文
TextSecondary (#8A94A6)→ 副标题、辅助文字
TextHint (#5A6478)     → 占位文字、禁用状态
Success (#4CAF50)      → 成功状态
Warning (#FFC107)      → 警告状态
Error (#FF5252)        → 错误状态
```

**强制规则：**

- 所有文字必须与背景对比度 ≥ 4.5:1（WCAG AA）
- 禁止在深色背景上使用纯白文字（`#FFFFFF`），用 `#E8EAF0` 替代
- 新增颜色必须在 `Color.kt` 中定义，禁止硬编码

### 3.5 动效规范

| 场景 | 时长 | 曲线 | 说明 |
|---|---|---|---|
| 页面切换 | 300ms | FastOutSlowIn | Activity 转场 |
| 弹窗出现 | 200ms | Decelerate | 对话框/抽屉 |
| 按钮反馈 | 100ms | Linear | 点击涟漪 |
| 列表滚动 | - | 默认 | 使用系统惯性 |
| 数据加载 | - | - | 显示骨架屏或加载指示器 |

**规则：**

- 禁止无意义的复杂动画（车机性能有限）
- 动画必须可被禁用（`reduceMotion` 无障碍设置）
- 加载超过 500ms 必须显示加载状态

---

## 4. 驾驶安全规范

> **核心原则：驾驶员的注意力永远在路上，不在屏幕上。**

### 4.1 交互限制

| 状态 | 允许 | 禁止 |
|---|---|---|
| **行驶中** | 单次点击操作、语音控制 | 文本输入、复杂滑动、视频播放、长列表滚动 |
| **停车中** | 所有操作 | 无限制 |

### 4.2 行驶模式检测

```kotlin
// 通过传感器判断是否在移动
// 方案 1：GPS 速度
// 方案 2：加速度传感器
// 方案 3：蓝牙 OBD 车速（可选）

enum class DriveState {
    PARKED,      // 停车（速度 = 0 持续 5s+）
    DRIVING      // 行驶（速度 > 0）
}

// 行驶中简化 UI
if (driveState == DriveState.DRIVING) {
    // 增大按钮尺寸
    // 隐藏复杂功能入口
    // 减少屏幕信息密度
}
```

### 4.3 界面安全要求

| 规则 | 说明 |
|---|---|
| 单任务聚焦 | 每个屏幕只做一件事，禁止多任务并行显示 |
| 3 层深度限制 | 最多 3 层导航深度（主页 → 列表 → 详情） |
| 大目标 | 所有可点击元素 ≥ 48dp × 48dp |
| 大文字 | 正文 ≥ 14sp，推荐 16sp |
| 高对比度 | 文字与背景对比度 ≥ 4.5:1 |
| 深色优先 | 默认深色主题，夜间降低亮度 |
| 禁止动画干扰 | 行驶中禁止自动播放动画/视频 |
| 反馈明确 | 每次操作必须有即时视觉/声音反馈 |

### 4.4 元数据标记

```xml
<!-- 允许行驶中使用的 Activity -->
<activity android:name=".MainActivity">
    <meta-data
        android:name="distractionOptimized"
        android:value="true" />
</activity>

<!-- 仅停车时使用的 Activity（设置页等） -->
<activity android:name=".SettingsActivity">
    <!-- 不加 distractionOptimized 标记 -->
</activity>
```

---

## 5. 性能规范

### 5.1 启动性能

| 指标 | 目标 | 测量方式 |
|---|---|---|
| 冷启动时间 | < 2s | `adb shell am start -W` |
| 首帧渲染 | < 1.5s | Perfetto trace |
| 应用列表加载 | < 500ms | 500 个应用 |
| 应用图标加载 | < 100ms/个 | 内存缓存命中 |

### 5.2 运行时性能

| 指标 | 目标 |
|---|---|
| 帧率 | ≥ 30 fps（目标 60 fps） |
| 帧耗时 | < 33ms（30fps）/ < 16ms（60fps） |
| 内存占用 | < 200MB（正常使用） |
| CPU 占用 | < 15%（空闲时 < 5%） |
| 电量消耗 | 后台 < 2%/h |

### 5.3 图片/图标处理

```kotlin
// ✅ 图标缓存：加载一次后缓存到内存
val iconCache = LruCache<String, Bitmap>(50)

fun getAppIcon(packageName: String): Bitmap {
    return iconCache.get(packageName) ?: run {
        val icon = pm.getApplicationIcon(packageName)
        val bitmap = icon.toBitmap(size = 64.dp)
        iconCache.put(packageName, bitmap)
        bitmap
    }
}

// ✅ 图标尺寸统一：不加载原始大图
// 强制 64×64dp，避免 OOM
```

### 5.4 内存管理

- ViewModel 中的列表使用 `ImmutableList` 或 `toList()` 暴露
- 图标位图使用 `LruCache` 管理，上限 50 个
- Activity 不使用时及时 `finish()`
- 监听器/回调在 `onCleared()` 中注销

---

## 6. 兼容性规范

### 6.1 Android 版本支持

| 版本 | API | 支持级别 |
|---|---|---|
| Android 8.0 (Oreo) | 26 | **最低支持** |
| Android 9.0 (Pie) | 28 | 完整支持 |
| Android 10+ | 29+ | **主要目标** |
| Android 14 | 34 | 最新特性 |

### 6.2 屏幕适配

| 屏幕类型 | 分辨率 | 适配策略 |
|---|---|---|
| 7 寸平板 | 1024 × 600 | 基准布局，3 列网格 |
| 10 寸平板 | 1920 × 1200 | 默认布局，4~5 列网格 |
| 车机 10 寸 | 1920 × 720 | 宽屏布局，5~6 列网格 |
| 车机 12 寸 | 2560 × 1600 | 大屏布局，6 列网格 |

**强制规则：**

- 所有尺寸使用 `dp`，字号使用 `sp`
- 禁止硬编码像素值
- 布局使用 `BoxWithConstraints` 或 `WindowInsets` 做响应式
- 图标资源提供多套密度（hdpi ~ xxhdpi）

### 6.3 系统兼容

```kotlin
// 版本判断封装
object ApiLevel {
    fun isAtLeast(version: Int): Boolean =
        Build.VERSION.SDK_INT >= version

    val isOreo get() = isAtLeast(26)
    val isPie get() = isAtLeast(28)
    val isQ get() = isAtLeast(29)
}

// 使用示例
if (ApiLevel.isQ) {
    // Android 10+ 的手势导航适配
}
```

---

## 7. Git 工作流

### 7.1 分支模型

```
main          ← 稳定发布版
  ├── develop ← 开发主线
  │     ├── feature/xxx   ← 新功能
  │     ├── fix/xxx       ← Bug 修复
  │     └── refactor/xxx  ← 重构
  └── release/x.x.x      ← 发布准备
```

### 7.2 Commit 规范

```
<type>(<scope>): <subject>

类型（type）:
  feat:     新功能
  fix:      Bug 修复
  docs:     文档
  style:    格式（不影响代码运行）
  refactor: 重构
  perf:     性能优化
  test:     测试
  chore:    构建/工具变更

示例:
  feat(ui): 添加天气 Widget 组件
  fix(app): 修复应用列表加载崩溃
  docs:     更新开发规范文档
  perf:     优化图标缓存策略，减少 OOM
```

### 7.3 分支保护

- `main` 禁止直接 push，必须通过 PR
- PR 必须至少 1 人 review
- CI 构建通过才能合并

---

## 8. 测试规范

### 8.1 测试金字塔

```
        /  UI 测试 (10%)  \        ← Compose UI 测试
       / 集成测试 (20%)    \       ← Repository + ViewModel
      /   单元测试 (70%)    \      ← 纯逻辑、工具函数
```

### 8.2 测试命名

```kotlin
// 格式：`被测方法_条件_期望结果`
@Test
fun `getInstalledApps_当有已安装应用时_返回非空列表`() { ... }

@Test
fun `searchApps_当关键词为空时_返回空列表`() { ... }
```

### 8.3 覆盖率要求

| 模块 | 最低覆盖率 | 目标 |
|---|---|---|
| ViewModel | 80% | 90% |
| Repository | 70% | 80% |
| Util | 90% | 95% |
| UI (Compose) | 核心页面有测试 | - |

---

## 9. 发布规范

### 9.1 版本号规则

```
主版本.次版本.修订号 (Major.Minor.Patch)

1.0.0 → 首个正式版
1.1.0 → 新增功能（如天气接入）
1.1.1 → Bug 修复
2.0.0 → 架构变更或不兼容更新
```

### 9.2 发布检查清单

- [ ] 所有单元测试通过
- [ ] 无 lint 警告（`./gradlew lint`）
- [ ] ProGuard/R8 混淆测试通过
- [ ] 在最低版本设备上测试通过
- [ ] 在目标车机/平板模拟器上测试通过
- [ ] 冷启动 < 2s
- [ ] 内存峰值 < 200MB
- [ ] 无 ANR / 崩溃
- [ ] CHANGELOG 更新
- [ ] 版本号递增

### 9.3 ProGuard 规则

```proguard
# 保留 Composable
-keep class **.ui.** { *; }

# 保留数据模型
-keep class com.cardesktop.data.model.** { *; }

# 保留广播接收器
-keep class com.cardesktop.receiver.** { *; }
```

---

## 附录

### A. 开发环境配置

| 工具 | 版本 |
|---|---|
| Android Studio | Hedgehog 2023.1.1+ |
| JDK | 17 |
| Gradle | 8.5 |
| Kotlin | 1.9.22 |
| Compose BOM | 2024.01.00 |

### B. 参考文档

- [Android for Cars 开发指南](https://developer.android.google.cn/training/cars)
- [Jetpack Compose 最佳实践](https://developer.android.google.cn/jetpack/compose/best-practices)
- [AOSP 驾驶分心指南](https://source.android.google.cn/devices/automotive/driver_distraction/guidelines)
- [Material 3 设计系统](https://m3.material.io/)
- [Compose 性能优化](https://developer.android.google.cn/jetpack/compose/performance)

### C. 规范变更记录

| 日期 | 版本 | 变更内容 |
|---|---|---|
| 2026-04-10 | 1.0.0 | 初始版本 |
