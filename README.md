<p align="center">
  <img src="src/main/resources/easycreate_logo.png" alt="EASY CREATE" width="560">
</p>

# EASY CREATE · 简易机械动力

从一条传送带开始，简化常用部件的制作，用末影设备连接远处的仓储与动力。EASY CREATE 为机械动力补充便利配方、无线物流和原生思索，让工厂的布置更加自由。

[下载](https://github.com/Yesaz0erq/Easy-Create-Supplement/releases) · [问题反馈](https://github.com/Yesaz0erq/Easy-Create-Supplement/issues) · [MIT 许可](LICENSE)

## 连接你的工厂

- **简化制作**：直接制作多种机壳、材料与机械部件，缩短基础生产线的准备流程。
- **传输物品**：末影溜槽保留智能溜槽的筛选功能，通过同频无线红石信号终端传送物品。
- **传输流体**：末影流体储罐支持管道、多方块存储和无线流体传输。
- **传输动力**：末影转速控制器将真实动力源的旋转传至接收端，支持调速与共享应力。
- **跟随思索**：三种末影设备各有两章教程，演示基本用法、终端配对与传输过程，支持简体中文及英文。

无线传输需要两端位于同一维度且区块已加载，没有距离限制，也不会自动加载区块。将光标放在物品上并按住 **W** 查看思索。

## 1.0-K2

- 统一末影溜槽、末影流体储罐和末影转速控制器的分步思索风格。
- 新增缠魂配方：**1 个紫水晶碎片 → 1 个回响碎片（100%）**。
- 创造马达改用 10 格机械合成：上下两行均为“异彩化合物、坚固板、坚固板”，中行为“暗影机壳、光辉机壳、转速控制器、传动杆”。
- 同时提供 Forge 1.20.1 和 NeoForge 1.21.1 版本。

## 下载与安装

| Minecraft | 加载器（构建版本） | Java | Create（构建版本） | 源码 |
| --- | --- | --- | --- | --- |
| 1.21.1 | NeoForge 21.1.227 | 21 | 6.0.10-280 | 仓库根目录 |
| 1.20.1 | Forge 47.4.23 | 17 | 6.0.8-291 | [forge-1.20.1](forge-1.20.1) |

在 [Releases](https://github.com/Yesaz0erq/Easy-Create-Supplement/releases) 中选择对应 Minecraft 与加载器的 JAR，与对应版本的 Create 及其必需前置一起放入 `mods` 文件夹。客户端与服务端均需安装。

可选安装 **JEI** 查看合成与加工配方。两个版本的 JAR 请按游戏环境分别使用。

## 从源码构建

仓库根目录是 NeoForge 1.21.1 工程；`forge-1.20.1/` 是可独立构建的 Forge 1.20.1 工程。每个工程仅包含源码、运行资源与 Gradle 构建文件。

进入目标工程，使用上表对应的 Java 版本执行：

```sh
# Linux / macOS
./gradlew build
```

```powershell
# Windows
.\gradlew.bat build
```

首次构建需要联网下载依赖。成品位于所选工程的 `build/libs/`。启动开发客户端使用 `runClient`。

## 反馈与贡献

欢迎通过 [Issues](https://github.com/Yesaz0erq/Easy-Create-Supplement/issues) 报告问题或提出建议。报告问题时请提供 Minecraft 版本、加载器与模组版本、复现步骤，以及相关日志或崩溃报告。

项目代码采用 [MIT 许可](LICENSE)。Create 衍生资源的许可见 [第三方声明](THIRD_PARTY_NOTICES.md)。作者：OFFSET Inc.
