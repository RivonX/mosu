## 如何使用


### 在application.yml里配置
配置示例：
```yaml
mosu:
  oss:
    endpoint: xxx
    accessKey: xxx
    secretKey: xxx
    path-style-access: false
```


### 在SpringBoot里使用
(1)、注入OssTemplate
```java
    import org.springframework.beans.factory.annotation.Autowired;

    @Autowired
    private OssTemplate ossTemplate
    
   // 上传示例
   ossTemplate.putObject( bucketName,  objectName, InputStream stream, String contextType);
   ...
```
(1)、功能接口一览
```java
    /**
     * 创建bucket
     * @param bucketName bucket名称
     */
    void createBucket(String bucketName);

    /**
     * 获取所有的bucket
     * @return Bucket列表
     */
    List<Bucket> getAllBuckets();

    /**
     * 通过bucket名称删除bucket
     * @param bucketName bucket名称
     */
    void removeBucket(String bucketName);

    /**
     * 上传文件
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream 文件流
     * @param contextType 文件类型
     * @return true 成功，false 失败
     */
    boolean putObject(String bucketName, String objectName, InputStream stream, String contextType);

    /**
     * 上传文件
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream 文件流
     * @return true 成功，false 失败
     */
    boolean putObject(String bucketName, String objectName, InputStream stream);

    /**
     * 获取文件
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return S3Object（可以通过InputStream inputStream = s3Object.getObjectContent()来获取InputStream）
     */
    S3Object getObject(String bucketName, String objectName);

    /**
     * 获取对象的url链接
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires 过期时间
     * @return 链接
     */
    String getObjectURL(String bucketName, String objectName, Integer expires);

    /**
     * 通过bucketName和objectName删除对象
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return true 成功，false 失败
     */
    boolean removeObject(String bucketName, String objectName);

    /**
     * 根据文件前置查询文件
     * @param bucketName bucket名称
     * @param prefix 前缀
     * @param recursive 是否递归查询
     * @return S3ObjectSummary 列表
     */
    List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive);
```


## AmazonS3扩展知识
什么是AmazonS3？

https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/userguide/Welcome.html

Amazon Simple Storage Service（Amazon S3，Amazon简便存储服务）是 AWS 最早推出的云服务之一，经过多年的发展，S3 协议在对象存储行业事实上已经成为标准。
- 提供了统一的接口 REST/SOAP 来统一访问任何数据
- 对 S3 来说，存在里面的数据就是对象名（键），和数据（值）
- 不限量，单个文件最高可达 5TB，可动态扩容。
- 高速。每个 bucket 下每秒可达 3500 PUT/COPY/POST/DELETE 或 5500 GET/HEAD 请求。
- 具备版本，权限控制能力
- 备数据生命周期管理能力
- 作为一个对象存储服务，S3 功能真的很完备，行业的标杆，目前市面上大部分OSS对象存储服务都支持AmazonS3。

支持的厂商有：
- 阿里云OSS兼容S3
- 华为云OBS兼容S3
- 七牛云Kodo兼容S3
- 又拍云USS兼容S3
- 腾讯云COS兼容S3
- 百度云BOS兼容S3
- Minio兼容S3

市面上OSS对象存储服务基本都支持AmazonS3，我们封装我们的自己的starter那么就必须考虑适配，迁移，可扩展。
比喻说我们今天使用的是阿里云OSS对接阿里云OSS的SDK，后天我们使用的是腾讯COS对接是腾讯云COS，
我们何不直接对接AmazonS3实现呢，这样后续不需要调整代码，只需要去各个云服务商配置就好了。
