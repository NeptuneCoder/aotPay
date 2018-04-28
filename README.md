项目描述：
      该项目集成了国内的微信，支付宝和国外的google的应用内购买（inapp）,订阅（subs）功能。

1. google inapp subs  使用过程中可能遇到的问题
    1. 订单为两串数字
    2. 您无法购买
    3. versionCode 线上未发布，将不能获取到商品的信息
    4. 银行卡绑定，个人地址设置
    5. 支付遭拒绝
    6. ip地址切换，同一账号多个设备登陆等
    7. 余额不足等
    8. 混淆配置
    9. 内购需要消耗，而订阅不需要消耗
2. alipay的集成
3. 微信配置，微信的集成，微信设计的思路
4. stripe支付的集成
     1. 支付注意事项，ip切换，信用卡，邮编号码的设置
5. stripe 集成alipay的流程相关业务



1. 在根目录build.gradle中配置需要修改为 apply from : 'config.gradle'

配置该文件：/app/gradle.properties，内容如下
```
KEYALIAS=alias
KEYPASSWORD=pwd
STOREFILEPATH=store file path
STOREPASSWORD= pwd
```
