package io.github.rivon.mosu.oss.core;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.InputStream;
import java.util.List;

/**
 * OSS操作模板，提供与对象存储服务（如 AWS S3）相关的基本操作接口
 * 该接口定义了对存储桶（Bucket）和对象（Object）的常见操作，如创建、删除、上传、下载等。
 *
 * @author allen
 **/
public interface OssTemplate {

    /**
     * 创建一个新的存储桶（Bucket）。如果存储桶已经存在，方法将不执行任何操作。
     *
     * @param bucketName 存储桶名称，必须是全局唯一的
     */
    void createBucket(String bucketName);

    /**
     * 获取所有存储桶（Buckets）的列表。
     *
     * @return 存储桶的列表，包含所有当前用户拥有的存储桶对象
     */
    List<Bucket> getAllBuckets();

    /**
     * 根据存储桶名称删除存储桶。只有空存储桶才能被删除。
     *
     * @param bucketName 要删除的存储桶名称
     * @throws Exception 如果删除失败，抛出异常
     */
    void removeBucket(String bucketName) throws Exception;

    /**
     * 上传一个文件到指定的存储桶。此方法允许用户指定文件类型（MIME类型）。
     *
     * @param bucketName 存储桶名称
     * @param objectName 上传到存储桶中的对象名称（包括路径）
     * @param stream 文件流（需要上传的文件内容）
     * @param contextType 文件的 MIME 类型（如 "application/pdf" 或 "image/jpeg"）
     * @return 返回文件上传结果的对象，包含上传后的元数据，如 `ETag` 和 `versionId`
     * @throws Exception 如果上传失败，抛出异常
     */
    PutObjectResult putObject(String bucketName, String objectName, InputStream stream, String contextType) throws Exception;

    /**
     * 上传一个文件到指定的存储桶。此方法使用默认的 MIME 类型（"application/octet-stream"）。
     *
     * @param bucketName 存储桶名称
     * @param objectName 上传到存储桶中的对象名称（包括路径）
     * @param stream 文件流（需要上传的文件内容）
     * @return 返回文件上传结果的对象，包含上传后的元数据，如 `ETag` 和 `versionId`
     * @throws Exception 如果上传失败，抛出异常
     */
    PutObjectResult putObject(String bucketName, String objectName, InputStream stream) throws Exception;

    /**
     * 从指定的存储桶中获取一个对象（文件）。
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶中的对象名称（包括路径）
     * @return 返回该对象的 `S3Object`，包含对象的元数据和内容
     */
    S3Object getObject(String bucketName, String objectName);

    /**
     * 获取存储桶中指定对象的预签名 URL（可以用来访问对象）。预签名 URL 有时间限制，超过时间将不可访问。
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称（包括路径）
     * @param expires 预签名 URL 的过期时间，单位为天
     * @return 返回生成的预签名 URL（一个字符串）
     */
    String getObjectURL(String bucketName, String objectName, Integer expires);

    /**
     * 删除指定存储桶中的对象。
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称（包括路径）
     * @throws Exception 如果删除失败，抛出异常
     */
    void removeObject(String bucketName, String objectName) throws Exception;

    /**
     * 根据存储桶名称和对象名称的前缀（prefix）查询存储桶中的对象。支持递归查询。
     *
     * @param bucketName 存储桶名称
     * @param prefix 对象名称的前缀，支持通配符
     * @param recursive 是否递归查询，如果为 `true`，则会查询子目录中的对象
     * @return 返回符合条件的对象摘要列表，每个 `S3ObjectSummary` 包含对象的元数据（如对象名称、大小等）
     */
    List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive);
}
