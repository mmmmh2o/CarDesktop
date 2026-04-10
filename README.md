# 🚗 CarDesktop - 通用安卓车机/平板桌面

一个使用 **Kotlin + Jetpack Compose** 构建的通用 Android Launcher，车机风格设计，平板也能用。

> 不依赖汽车硬件，任何 Android 设备都可以设置为默认桌面。

---

## 📱 界面预览

```
┌──────────────────────────────────────────────────┐
│  1月10日 星期四                    📶 📡 🔋 16:31  │
├────────────┬─────────────────────┬───────────────┤
│            │   🗺️  🎵  📞  📷   │               │
│   16:31    │   ⚙️  📁  🔍  🎮   │    ☀️ 26°    │
│            │   📺  📖  💬  🌤️   │   北京 · 晴   │
│  4月10日   │   🎯  📊  🎨  🔧   │               │
│   星期四   │                     │               │
├────────────┴─────────────────────┴───────────────┤
│     🗺️ 导航    🎵 音乐    📞 电话    📷 相机      │
├──────────────────────────────────────────────────┤
│  📱全部应用   [应用1] [应用2] [应用3]    ⚙️ 设置   │
└──────────────────────────────────────────────────┘
```

---

## ✨ 功能特性

### 已实现
- [x] **Launcher 模式** — 注册为系统 HOME 桌面
- [x] **横屏全屏** — 强制横屏 + 沉浸式全屏
- [x] **应用抽屉** — 显示所有已安装应用，支持搜索
- [x] **桌面快捷方式** — 主屏常用应用快捷入口
- [x] **Dock 栏** — 底部固定应用 + 全部应用/设置入口
- [x] **时钟 Widget** — 实时时间 + 日期显示
- [x] **天气 Widget** — 天气信息展示（占位）
- [x] **快捷操作栏** — 导航/音乐/电话/相机快速启动
- [x] **应用监听** — 自动检测安装/卸载/更新
- [x] **开机自启** — BootReceiver 支持
- [x] **深色主题** — 车机风格暗色 UI
- [x] **大触摸目标** — 符合驾驶安全规范（64dp+）

### 待开发
- [ ] 天气 API 接入（高德/和风天气）
- [ ] OBD 车辆数据读取（可选）
- [ ] 音乐播放器控件
- [ ] 主题切换（多色系）
- [ ] 壁纸自定义
- [ ] 小部件支持（Android AppWidget）
- [ ] 语音控制
- [ ] 夜间/日间模式自动切换

---

## 🏗️ 项目结构

```
app/src/main/java/com/cardesktop/
├── DesktopApp.kt                 # Application 入口
├── data/
│   ├── model/
│   │   └── Models.kt             # 数据模型（AppInfo, WeatherInfo, Config）
│   └── repository/
│       └── AppRepository.kt      # 已安装应用数据源
├── ui/
│   ├── theme/
│   │   ├── Color.kt              # 车机风格色彩定义
│   │   └── Dimens.kt             # 尺寸常量（大触摸目标）
│   ├── screen/
│   │   ├── MainActivity.kt       # 主桌面入口（全屏沉浸）
│   │   ├── MainScreen.kt         # 主桌面 UI
│   │   ├── MainViewModel.kt      # 主桌面逻辑
│   │   ├── AppDrawerActivity.kt  # 应用抽屉
│   │   └── SettingsActivity.kt   # 设置页面
│   └── widget/
│       ├── AppIcon.kt            # 应用图标组件
│       ├── Widgets.kt            # 时钟/天气/快捷卡片
│       └── DockBar.kt            # 底部 Dock 栏
├── receiver/
│   ├── BootReceiver.kt           # 开机广播
│   └── AppChangeReceiver.kt      # 应用变更监听
└── util/                         # 工具类（待添加）
```

---

## 🛠️ 技术栈

| 技术 | 版本 | 用途 |
|---|---|---|
| Kotlin | 1.9.22 | 编程语言 |
| Jetpack Compose | BOM 2024.01 | UI 框架 |
| Material 3 | - | 设计系统 |
| ViewModel | 2.7.0 | 状态管理 |
| Coroutines | - | 异步任务 |
| Navigation | 2.7.6 | 页面导航 |

---

## 🚀 快速开始

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高
- JDK 17
- Android SDK 34
- 最低支持 Android 8.0 (API 26)

### 构建运行

```bash
# 克隆项目
git clone <repo-url>
cd CarDesktop

# 用 Android Studio 打开，或命令行构建
./gradlew assembleDebug

# 安装到设备
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 设置为默认桌面

1. 安装 APK 后按 Home 键
2. 系统弹出桌面选择器 → 选择 **CarDesktop**
3. 或在 **设置 → 应用 → 默认应用 → 主屏幕应用** 中设置

### 模拟器测试

建议创建 **10.1 寸平板模拟器** (1920×1200, 横屏)：
1. Android Studio → Device Manager → Create Device
2. 选择 Tablet → Pixel Tablet
3. 系统镜像选择 API 34+
4. 启动后横屏即可看到效果

---

## 🎨 设计规范

### 色彩
- **主色**：`#00E5FF` 科技蓝
- **强调**：`#FF6D00` 活力橙
- **背景**：`#0A0E14` 深邃黑蓝
- **卡片**：`#141B26` 半透明深色

### 字号（驾驶安全）
- 标题：20sp
- 正文：16sp（最小 14sp）
- 触摸目标：最小 64dp × 64dp

### 安全规范
- 行驶中禁止复杂交互
- 大按钮、大字体
- 深色主题（夜间驾驶友好）
- 限制每屏信息密度

---

## 📄 License

MIT License — 自由使用和修改。

---

## 🙏 参考

- [Android for Cars - Google](https://developer.android.google.cn/training/cars)
- [AOSP 驾驶分心指南](https://source.android.google.cn/devices/automotive/driver_distraction/guidelines)
- [Jetpack Compose 官方文档](https://developer.android.google.cn/jetpack/compose)
