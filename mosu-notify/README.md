## 如何使用


### 在application.yml里配置
配置示例：
```yaml
#通知相关配置
mosu:
  notify:
    mail:
      # 邮件通知配置,邮箱一般用于接收业务通知例如收到新的订单，sendto 定义邮件接收者，通常为管理人员
      enable: true
      host: xxx
      username: xxx
      password: xxx
      sendfrom: xxx
      sendto: xxx
      port: 465

    #短信模板通知配置
    #注意配置格式：template-name 请参考NotifyType 枚举值，目的为了区别模版id
    sms:
      enable: false
      #如果是腾讯云短信，则active的值tencent
      #如果是阿里云短信，则active的值aliyun
      active: aliyun
      sign: XXX
      template:
        - name: xxx
          templateId: xxx
      aliyun:
        regionId: xxx
        accessKeyId: xxx
        accessKeySecret: xxx
      tencent:
        appId: 111
        appKey: 111

    #钉钉通知配置
    dingTalk:
      enable: true
      webhook: 111
      atMobiles:
      keywords:
      atAll: false
      secret:
```


### 在SpringBoot里使用
```
查看测试用例
```