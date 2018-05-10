#### 项目描述：
      该项目集成了国内的微信，支付宝和国外的google的应用内购买（inapp）,订阅（subs）功能。
      以及stripe,stripe+alipay支付的sdk。

#### 项目的演变

        开始开发的时候，将所有的支付类型都放在了同一个moudle中，随着支付方式越来越多，
        出现在有些项目中用不到的支付方式，造成了代码冗余。在这个时候开始，将各种支付单独的以
        一个moudle的方式独立维护，当需要某几种支付方式一起使用时，再起一个项目整合相关的api。
        从而达到最大程度的降低代码耦合。也就是现在能看到的每种支付都是独立的moudle.

##### 支付宝官方资料：https://docs.open.alipay.com/204/105297/
##### 微信接入文档：https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_1

微信和支付宝没什么好说的，官方文档说的很详细。






1. google inapp subs  使用过程中可能遇到的问题
    1. 订单为两串数字
    2. 您无法购买
    3. versionCode 线上未发布，将不能获取到商品的信息
    4. 银行卡绑定，个人地址设置
    5. 支付遭拒绝
    6. ip地址切换，同一账号多个设备登陆等
    7. 余额不足等
    8. 混淆配置
        ```

        ```
    9. 内购需要消耗，而订阅不需要消耗
    当我定义了内购商品，通过调用订阅方法时，


4. stripe支付的集成
     1. 支付注意事项，ip切换，信用卡，邮编号码的设置

5. stripe 集成alipay的流程相关业务



1. 在根目录build.gradle中配置需要修改为 apply from : 'config.gradle'

配置签名文件：/app/gradle.properties
```
KEYALIAS=alias
KEYPASSWORD=pwd
STOREFILEPATH=store file path
STOREPASSWORD= pwd
```


## License

[Apache License 2.0](LICENSE)


