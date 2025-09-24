package io.github.rivon.mosu.oss.core;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * OssTemplate的实现类，用于处理与对象存储服务（OSS，通常指 S3）相关的操作
 *
 * @author allen
 **/
@RequiredArgsConstructor // 使用Lombok注解，自动生成构造方法，注入 AmazonS3 实例
public class OssTemplateImpl implements OssTemplate {

    private final AmazonS3 amazonS3; // AmazonS3 客户端，用于与 AWS S3 进行交互

    /**
     * 创建Bucket（存储桶）
     * AmazonS3：<a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_CreateBucket.html">...</a>
     *
     * @param bucketName bucket名称
     *                  创建一个存储桶，若该存储桶不存在，则创建它
     */
    @Override
    @SneakyThrows
    public void createBucket(String bucketName) {
        // 如果存储桶不存在，则创建一个新的存储桶
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.createBucket(bucketName);
        }
    }

    /**
     * 获取所有的存储桶
     * AmazonS3：<a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_ListBuckets.html">...</a>
     *
     * @return 所有存储桶的列表
     */
    @Override
    @SneakyThrows
    public List<Bucket> getAllBuckets() {
        return amazonS3.listBuckets(); // 列出所有存储桶
    }

    /**
     * 通过存储桶名称删除存储桶
     * AmazonS3：<a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_DeleteBucket.html">...</a>
     *
     * @param bucketName 存储桶名称
     *                   删除指定名称的存储桶
     */
    @Override
    @SneakyThrows
    public void removeBucket(String bucketName) {
        amazonS3.deleteBucket(bucketName); // 删除指定名称的存储桶
    }

    /**
     * 上传对象（文件）到指定存储桶
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param stream      文件流
     * @param contextType 文件类型（MIME类型）
     *                    AmazonS3：<a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_PutObject.html">...</a>
     */
    @Override
    @SneakyThrows
    public PutObjectResult putObject(String bucketName, String objectName, InputStream stream, String contextType) {
        // 调用重载的 putObject 方法，提供默认的文件大小和类型
        return putObject(bucketName, objectName, stream, stream.available(), contextType);
    }

    /**
     * 上传对象（文件）到指定存储桶
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream     文件流
     *                   AmazonS3：<a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_PutObject.html">...</a>
     */
    @Override
    @SneakyThrows
    public PutObjectResult putObject(String bucketName, String objectName, InputStream stream) {
        // 调用重载的 putObject 方法，提供默认的文件类型
        return putObject(bucketName, objectName, stream, stream.available(), "application/octet-stream");
    }

    /**
     * 通过存储桶名称和对象名称获取对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 返回 S3Object，包含对象的元数据和内容
     * AmazonS3：<a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObject.html">...</a>
     */
    @Override
    @SneakyThrows
    public S3Object getObject(String bucketName, String objectName) {
        return amazonS3.getObject(bucketName, objectName); // 获取指定存储桶和对象的内容
    }

    /**
     * 获取对象的预签名 URL，允许通过 URL 访问对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expires    URL 的过期时间（单位：天）
     * @return 生成的预签名 URL，用于访问对象
     * AmazonS3：<a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_GeneratePresignedUrl.html">...</a>
     */
    @Override
    @SneakyThrows
    public String getObjectURL(String bucketName, String objectName, Integer expires) {
        // 设置URL的过期时间
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, expires); // 设置过期时间为当前日期加上指定的天数
        URL url = amazonS3.generatePresignedUrl(bucketName, objectName, calendar.getTime()); // 生成预签名URL
        return url.toString(); // 返回URL字符串
    }

    /**
     * 通过存储桶名称和对象名称删除对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     *                   AmazonS3：<a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_DeleteObject.html">...</a>
     */
    @Override
    @SneakyThrows
    public void removeObject(String bucketName, String objectName) {
        amazonS3.deleteObject(bucketName, objectName); // 删除指定的对象
    }

    /**
     * 根据存储桶名称和对象前缀（prefix）获取对象列表
     *
     * @param bucketName 存储桶名称
     * @param prefix     对象前缀，用于筛选对象
     * @param recursive  是否递归查询
     * @return 返回一个包含所有匹配对象的列表
     * AmazonS3：<a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_ListObjects.html">...</a>
     */
    @Override
    @SneakyThrows
    public List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) {
        ObjectListing objectListing = amazonS3.listObjects(bucketName, prefix); // 获取符合条件的对象列表
        return objectListing.getObjectSummaries(); // 返回对象摘要列表
    }

    /**
     * 上传对象到指定存储桶的私有方法，支持设置文件大小和类型
     *
     * @param bucketName  存储桶名称
     * @param objectName  对象名称
     * @param stream      文件流
     * @param size        文件大小
     * @param contextType 文件类型
     * @return 返回 PutObjectResult，包含上传结果的元数据
     */
    @SneakyThrows
    private PutObjectResult putObject(String bucketName, String objectName, InputStream stream, long size,
                                      String contextType) {
        // 将输入流转换为字节数组
        byte[] bytes = IOUtils.toByteArray(stream);
        ObjectMetadata objectMetadata = new ObjectMetadata(); // 创建对象元数据
        objectMetadata.setContentLength(size); // 设置文件大小
        objectMetadata.setContentType(contextType); // 设置文件类型
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes); // 转换为ByteArrayInputStream

        // 上传文件到指定的存储桶
        return amazonS3.putObject(bucketName, objectName, byteArrayInputStream, objectMetadata);
    }
}
