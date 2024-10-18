<div align="center">
<picture  >
<img src="img/app_icon.png" width="150" height="150" alt="icon"/>
</picture>
</div>

<div align="center" >

# <picture><source media="(prefers-color-scheme: dark)" srcset="img/app_name_dark.png"><source media="(prefers-color-scheme: light)" srcset="img/app_name_light.png"><img  src="img/app_name_light.png" width="180" alt="icon"/></picture>

**信hyper，得永生!**

**简体中文 | [English](README_EN-US.md)**

[![GitHub License](https://img.shields.io/github/license/YunZiA/HyperStar)](https://github.com/YunZiA/HyperStar/blob/master/LICENSE)
[![GitHub Stars](https://img.shields.io/github/stars/YunZiA/HyperStar)](https://github.com/YunZiA/HyperStar/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/YunZiA/HyperStar)](https://github.com/YunZiA/HyperStar/forks)


</div>

## 介绍

这是一个平平无奇的Xposed模块，主要功能为自定义 Xiaomi HyperOS 控制中心，外加一些自己认为有用的功能。

## 当前支持的版本
| 系统版本            | 安卓版本 | 支持状态 |
|:----------------|:-----|:-----|
| HyperOS1.0 Beta | 15   | 部分能用 |
| HyperOS1.0      | 14   | 支持   |

## 使用前说明

请在 [LSPosed](https://github.com/LSPosed/LSPosed/releases) 中启用 HyperStar， 然后在 HyperStar 应用内启用功能后，重启作用域 (需要授予模块 Root 权限)；

对于`修改较多的第三方 Xiaomi HyperOS ROM`、`修改较多的系统软件`，本模块<b>不会适配</b> ，但也不保证功能一定失效，如若功能在此环境下出现异常，请<b>关闭功能</b>或者<b>卸载模块</b>;

目前 HyperStar 是基于 Android 14 的 Xiaomi HyperOS 1.0 的手机设备进行适配，功能并不能保证覆盖完整，请多多向我反馈！


## 模块作用域

| 应用名    | 包名                    |
|:-------|:----------------------|
| 系统界面   | com.android.systemui  |
| 系统界面组件 | miui.systemui.plugin  |
| 系统桌面   | com.miui.home         |

## 交流 & 反馈群组

你可以加入群组交流或者反馈您所遇到的问题。

[![badge_qgroup]][qgroup_url]
[![badge_telegram]][telegram_url]

## 翻译人员

感谢以下人员为HyperStar项目贡献翻译：

| 语言      | 翻译人员                                                                          |
|:--------|:------------------------------------------------------------------------------|
| English | [cafayeli](https://t.me/cafayeli) & [Natsukawa Masuzu](https://t.me/Minggg07) |

如果您想要为 HyperStar 项目贡献翻译，请联系我吧~


## 引用

##### Apache-2.0

- [「Xposed」 by rovo89, Tungstwenty](https://github.com/rovo89/XposedBridge)
- [「XposedBridge」 by rovo89](https://github.com/rovo89/XposedBridge)
- [「miuix-kotlin-multiplatform」 by YuKongA](https://github.com/miuix-kotlin-multiplatform/miuix)
- [「Haze」 by Chris Banes](https://github.com/chrisbanes/haze)


##### GNU Affero General Public License v3

- [「HyperCeiler」by ReChronoRain](https://github.com/ReChronoRain/HyperCeiler)




[qgroup_url]: http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&amp;k=5ONF7LuaoQS6RWEOUYBuA0x4X8ssvHJp&amp;authKey=Pic4VQJxKBJwSjFzsIzbJ50ILs0vAEPjdC8Nat4zmiuJRlftqz9%2FKjrBwZPQTc4I&amp;noverify=0&amp;group_code=810317966

[badge_qgroup]: https://img.shields.io/badge/QQ-群组-4DB8FF?style=for-the-badge&logo=tencentqq

[telegram_url]: https://t.me/+QQWVM0ToHyEyZmRl

[badge_telegram]: https://img.shields.io/badge/dynamic/json?style=for-the-badge&color=2CA5E0&label=Telegram&logo=telegram&query=%24.data.totalSubs&url=https%3A%2F%2Fapi.spencerwoo.com%2Fsubstats%2F%3Fsource%3Dtelegram%26queryKey%3Dcemiuiler