# TIGERs Mannheim 自动裁判盒

![GitHub Release](https://img.shields.io/github/v/release/herryli/AutoReferee)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/lkhcode/AutoReferee/build.yml)
![Docker Image Version](https://img.shields.io/docker/v/herryli124/auto-referee)



本项目是 [TIGERs-Mannheim/AutoReferee](https://github.com/TIGERs-Mannheim/AutoReferee) 的Fork版本，该版本旨在针对`浙江省大学生机器人竞赛-小型足球机器人赛道`与`中国机器人大赛暨Robocup世界杯中国公开赛-小型组`的比赛规则，对自动裁判盒进行定制与修改，目前实现了以下修改

1. 对内置的`ssl-game-controller`进行了替换，使用汉化后的定制版本[lkhcode/ssl-game-controller](https://github.com/lkhcode/ssl-game-controller)
2. 对于替换的`ssl-game-controller`，是通过更换依赖实现的，现在获取GC的依赖从[TIGERs-Mannheim/ssl-game-controller-maven](https://github.com/TIGERs-Mannheim/ssl-game-controller-maven)换成了[lkhcode/ssl-game-controller](https://github.com/lkhcode/ssl-game-controller-maven),此依赖提供Github Package与Jitpack两种版本可供安装，你可以尝试使用，目前使用Jitpack版本，当前锁定的是[GC的v0.0.1-beta版本](https://github.com/lkhcode/ssl-game-controller/releases/tag/v0.0.1-beta)，目前仅做了`windows_amd64`与`linux_amd64`的支持,如有对于arm64，darwin(macOS等苹果的操作系统)的适配需求，请提交issue或联系`ausksyc@outlook.com`
3. 对于Docker镜像，此版本也做了支持，你可以在[herryli124/autoreferee](https://hub.docker.com/r/herryli124/auto-referee)找到


这是 TIGERs Mannheim 团队的自动裁判系统实现。它基于TIGERs的 AI 框架 Sumatra 开发。
本项目使用 Gradle 构建应用程序，IntelliJ 作为主要的 IDE。
所有依赖项将自动下载，因此构建时需要网络连接。

## 系统要求
 * Java JDK (最新的 LTS 版本，具体要求版本可以在
  [这里](buildSrc/src/main/groovy/sumatra.java.gradle)找到，目前使用JDK21)
 * 网络连接
 * 对操作系统无特殊要求

## 构建
首先，确保你已经克隆了 Git 仓库。从 GitHub 下载的 ZIP 文件无法使用，因为构建依赖于 Git 标签。

根据你的系统平台运行 `./build.sh` 或 `./build.bat`。

## 运行
根据你的系统平台运行 `./run.sh` 或 `run.bat`。

你可以传入 `-h` 参数来查看可用的命令行参数。

你也可以尝试通过 docker 运行。我提供了两个 docker 镜像：
 * `herryli124/auto-referee` - 不带 GUI 的 autoReferee
 * `herryli124/auto-referee-vnc` - 带 GUI 的 autoReferee

要使用 GUI 运行 autoReferee，可以使用以下命令：

```shell
docker run --net host  -v /tmp/.X11-unix:/tmp/.X11-unix -e DISPLAY=unix$DISPLAY herryli124/auto-referee-vnc
```

> 注意：该命令仅在运行 X 服务器的 Linux 系统上有效。
> 或者，你也可以使用 VNC 客户端连接到容器。

无GUI（headless）版本可以使用以下命令运行：

```shell
docker run --net host herryli124/auto-referee
```
## IntelliJ 配置
IntelliJ 可以读取 Gradle 配置并使用 Gradle 执行构建。
请确保在 Build, Execution, Deployment -> Build Tools -> Gradle 下配置 Gradle 用于构建和测试。

# 使用说明

## GUI
GUI 由多个可以动态排列的视图组成。如果某个视图没有显示，可以从菜单(Views)中添加。

### 自动裁判视图
在**自动裁判**视图中，你可以将自动裁判设置为以下模式之一:
 * Off 关闭: 完全关闭自动裁判
 * Passive 被动: 自动裁判将检测事件并显示在比赛日志中，但不会向游戏控制器发送任何信息
 * Active 主动: 自动裁判将检测事件并将其发送到游戏控制器

此视图还允许停用某些事件检测，如针对省赛的无边界规则，你可以尝试取消球出界的事件监测，当然你也可以选择在`ssl-game-controller`中忽略AutoReferee的事件

### 比赛日志视图
**Game Log**视图显示所有发生的、对自动裁判行为有影响的事件。可以使用表格下方的复选框切换不同类型事件的显示。

### 球速视图
**Ball Speed**视图显示场上球的速度。右侧的滑块组件可用于调整图表的时间窗口。如果勾选了**非 RUNNING 状态时暂停**复选框，当比赛不在 RUNNING 状态时图表将自动暂停。可以使用暂停/恢复按钮覆盖此暂停。请注意，如果用户手动暂停图表，即使比赛状态回到 RUNNING，图表也不会自动恢复。

图表将显示三条线:
 * 最大球速: 规则定义的球速限制
 * 初始球速: 基于球的轨迹估算的初始球速(踢球速度)。随时间推移估算会更准确
 * 球速: 当前估计的球速

## 配置
所有配置选项都可以通过**Cfg**选项卡访问。**Cfg**选项卡本身包含多个子选项卡，用于修改应用程序不同部分的参数。你可以进行必要的修改并**Apply**它们。要使更改永久生效，请按**Save**将其写入磁盘。按**Reload**重新读取所有当前应用的值。

### 视觉端口
默认情况下，应用程序将尝试从 **224.5.23.2:10006** 接收视觉帧。
你可以在 [config/moduli/autoreferee.xml](config/moduli/autoreferee.xml) 中更改默认地址和端口。

## 裁判端口
默认情况下，应用程序将尝试从 **224.5.23.1:10003** 接收裁判消息。
你可以在 [config/moduli/autoreferee.xml](config/moduli/autoreferee.xml) 中更改默认地址和端口。

### 自动裁判检测器
可以通过**autoreferee**配置部分更改自动裁判的行为。它包含检测器的参数。

### 规则约束和几何参数
规则定义的参数可以在**ruleConst**下找到。

默认几何参数从 [config/geometry/DIV_A.txt](config/geometry/DIV_A.txt) 读取。
可以在 [config/moduli/autoreferee.xml](config/moduli/autoreferee.xml) 中更改要读取的文件。

## 激活内置的 ssl-game-controller
自动裁判系统内置了官方的 ssl-game-controller。要激活它，请在 [config/moduli/autoreferee.xml](config/moduli/autoreferee.xml) 中将 `gameController` 改为 true。
自动裁判系统将在内部启动游戏控制器并连接到其 websocket API，以便能够通过**Ref**视图发送一些基本命令。
该视图还包含一个启动 GC 的 UI 的按钮。


