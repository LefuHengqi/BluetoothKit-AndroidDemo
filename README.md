# 快速链接

1. [ Android SDK概述](https://xinzhiyun.feishu.cn/docx/SbKCdKZhJobiNYxQLEdc7XgUnYc)

2. [ Android SDK 集成方式](https://xinzhiyun.feishu.cn/docx/OD4rdSlWrobDIZxn0CBcD68bn1c)

3. [  Android SDK 设备扫描](https://xinzhiyun.feishu.cn/docx/SE2GdDxg1odTT5xgSbscB0F7nv7)

4. [ Android SDK 人体秤接入](https://xinzhiyun.feishu.cn/docx/HGrqdpGqioulIhxhsbwcmlLkn7g)

5. [ Android SDK厨房秤接入](https://xinzhiyun.feishu.cn/docx/ILwQdEPoCozXSDxAQufcvOffn4f)

6. [ Android SDK 计算库使用](https://xinzhiyun.feishu.cn/docx/L0UxdNKFPorB77xBjnmcqijtnVb)

7. [Android示例程序地址](https://github.com/LefuHengqi/BluetoothKit-AndroidDemo)

8. [乐福开放平台](https://uniquehealth.lefuenergy.com/unique-open-web/#/home)&#x20;

9. [ 乐福开放平台配置设备指南](https://xinzhiyun.feishu.cn/docx/VxXUdI86sorr96xSdn1cakkZndd)

10. [ iOS SDK概述](https://xinzhiyun.feishu.cn/docx/MaHEdRx7Bo02Q8x60pFcrzRgnkf)

11. [ 常见问题](https://xinzhiyun.feishu.cn/docx/Y3HgdVqXHoGhZqxG9BscGxCPnhh)

1) [ Android SDK Overview](https://xinzhiyun.feishu.cn/docx/M6g2d4WAMoPCErxsUZAckymRnbd)

2) [ Android SDK Integration](https://xinzhiyun.feishu.cn/docx/Lw0idhnjYoqZsqxtxvfcGuS8nac)

3) [ Android SDK Device Scanning](https://xinzhiyun.feishu.cn/docx/MOkZdHf0fo4S4YxqWjycaXJPnXf)

4) [ Android SDK Integrated body scale](https://xinzhiyun.feishu.cn/docx/Im7CdLR14oQ74vxBUZpcEpRrndB)

5) [ Android SDK Integrated kitchen scale](https://xinzhiyun.feishu.cn/docx/VB3pd6xO1oPXbexoof8cSsEGnAh)

6) [ Android SDK Calculation library usage](https://xinzhiyun.feishu.cn/docx/HxxTdPd24oDWjyx4QfHccZUVnbd)

7) [Android sample program address](https://github.com/LefuHengqi/BluetoothKit-AndroidDemo)

8) [Lefu Open Platform](https://uniquehealth.lefuenergy.com/unique-open-web/#/home) &#x20;

9) [ Lefu Open Platform Configuration Device Guide](https://xinzhiyun.feishu.cn/docx/Gw38d5JskoShyFxKnwIcvIaznhb)

10) [ iOS SDK Overview](https://xinzhiyun.feishu.cn/docx/FZYwd286NouHxoxXVpccUCZ9n4c)

11) [ Android FAQs](https://xinzhiyun.feishu.cn/docx/VDDGdJsuxoMwRZxf56ZcjReRnrh)



# 概述

## Android SDK

| 模块             | 描述     | 必须     | 集成方式                                               |
| -------------- | ------ | ------ | -------------------------------------------------- |
| ppbasekit      | 基础模块   | 是      | api 'com.lefu.ppbasekit:ppbasekit:4.0.7'           |
| ppbluetoothkit | 蓝牙协议模块 | 根据业务选择 | api 'com.lefu.ppbluetoothkit:ppbluetoothkit:4.0.7' |
| ppcalculatekit | 体脂计算库  | 根据业务选择 | api 'com.lefu.ppcalculatekit:ppcalculatekit:4.0.7' |

PPBaseKit是基础模块，必须集成

PPBluetoothKit是针对人体秤和食物秤进行封装的SDK，包含蓝牙扫描、蓝牙连接逻辑、数据解析逻辑

PPCalculateKit是针对体脂计算封装的SDK，包含了4电极交流算法、4电极直流算法、8电极交流算法

## 示例程序

为了让客户快速实现称重以及对应的功能而实现，提供了一个示例程序，示例程序中包含体脂计算模块和设备功能模块。

* 设备功能模块目前支持的设备包含：蓝牙秤、食物秤、Torre系列蓝牙WiFi体脂秤。

* 体脂计算模块支持4电极交流算法、4电极直流算法、8电极交流算法。

* [GitHub - LefuHengqi/BluetoothKit-AndroidDemo](https://github.com/LefuHengqi/BluetoothKit-AndroidDemo)

![](https://xinzhiyun.feishu.cn/space/api/box/stream/download/asynccode/?code=Y2RiMzg0OTg0ZWQ3NThjYTQ3NTIyZThkNjQxOWU5NGVfa3dwOUFxYU1EMDRpTmd4SzlaNDBKRzV2VVhFVERCNlRfVG9rZW46VkwxZGJNWVFWbzdLQXJ4eXZoSWM5czNvblZGXzE3MzAzNDYyNjE6MTczMDM0OTg2MV9WNA)



## 商用版本程序

* 可在GooglePlay或其他应用商店搜索下载 "Unique Health"

* 也可扫描下面的二维码进行下载



![](https://xinzhiyun.feishu.cn/space/api/box/stream/download/asynccode/?code=ZmZlMGY1OWI2NGU4NmJmNjcyZWEwNTcyODVmZmYwMGFfaExLOUZCS0xMZmIzUWxZZUFQaWduYzZPSFRLR1BLczhfVG9rZW46V3ZTTGJ3NVM2b1lHRHV4SFRUVWNVOHIzbllnXzE3MzAzNDYyNjE6MTczMDM0OTg2MV9WNA)



# 常见问题

[ Android 常见问题](https://xinzhiyun.feishu.cn/docx/Y3HgdVqXHoGhZqxG9BscGxCPnhh)











