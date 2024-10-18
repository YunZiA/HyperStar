<div align="center">
<picture  >
<img src="img/app_icon.png" width="150" height="150" alt="icon"/>
</picture>
</div>

<div align="center" >

# <picture><source media="(prefers-color-scheme: dark)" srcset="img/app_name_dark.png"><source media="(prefers-color-scheme: light)" srcset="img/app_name_light.png"><img  src="img/app_name_light.png" width="180" alt="icon"/></picture>

**Trust Hyper, you will live forever!**

**[简体中文](README.md) | English**

[![GitHub License](https://img.shields.io/github/license/YunZiA/HyperStar)](https://github.com/YunZiA/HyperStar/blob/master/LICENSE)
[![GitHub Stars](https://img.shields.io/github/stars/YunZiA/HyperStar)](https://github.com/YunZiA/HyperStar/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/YunZiA/HyperStar)](https://github.com/YunZiA/HyperStar/forks)


</div>

## Introduction

This is an extremely ordinary Xposed module, mainly designed to customize the Xiaomi HyperOS Control Center, along with some features that the developer considers useful.

## Current supported versions

| System versions | Android versions | Support status       |
|:----------------|:-----------------|:---------------------|
| HyperOS1.0 Beta | 15               | Partially functional |
| HyperOS1.0      | 14               | Supported            |

## Pre-use instructions

Please enable HyperStar in [LSPosed](https://github.com/LSPosed/LSPosed/releases) , then enable the features within the HyperStar app, and restart the scope (Root permission for the module is required).

For `heavily modified third-party Xiaomi HyperOS ROMs` and `heavily modified system apps`, this module <b>will not be compatible</b>. However, it does not guarantee that the functions will definitely fail. If any functions malfunction in such environments, please <b>disable the functions</b> or <b>uninstall the module</b>.

Currently, HyperStar is adapted for devices running Xiaomi HyperOS 1.0 based on Android 14. Functionality may not be fully covered, so please provide me with plenty of feedback!

## Supported apps

| App name         | Package name         |
|:-----------------|:---------------------|
| System UI        | com.android.systemui |
| System UI Plugin | miui.systemui.plugin |
| System launcher  | com.miui.home        |

## Chat & Feedback Group

You can join the group to exchange information or provide feedback on any issues you encounter.

[![badge_qgroup]][qgroup_url]
[![badge_telegram]][telegram_url]

## Translator

Thank you to the following individuals for contributing translations to the HyperStar project:

| Languages | Translator                                                                    |
|:----------|:------------------------------------------------------------------------------|
| English   | [cafayeli](https://t.me/cafayeli) & [Natsukawa Masuzu](https://t.me/Minggg07) |

If you would like to contribute translations to the HyperStar project, please feel free to contact me!


## References

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