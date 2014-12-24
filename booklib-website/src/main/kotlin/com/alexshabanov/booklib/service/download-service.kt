package com.alexshabanov.booklib.service

import com.alexshabanov.booklib.model.BookMeta
import javax.servlet.http.HttpServletResponse
import org.springframework.beans.factory.BeanInitializationException
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import org.slf4j.LoggerFactory
import java.util.Date
import org.springframework.util.Assert

/** Encapsulates download service for books. */
trait BookDownloadService {

  fun download(id: Long, response: HttpServletResponse)
}

/** Demo download service - always redirects to sample book. */
class DemoBookDownloadService: BookDownloadService {

  override fun download(id: Long, response: HttpServletResponse) {
    response.sendRedirect("/assets/demo/sample.fb2.zip")
  }
}

/** S3-based download service */
class S3BookDownloadService(val bookDao: BookDao,
                            val s3Client: AmazonS3,
                            val bucketName: String,
                            val bucketKeyPrefix: String,
                            val bucketKeySuffix: String,
                            val urlExpirationMillis: Long): BookDownloadService {
  val log = LoggerFactory.getLogger(this.javaClass)

  override fun download(id: Long, response: HttpServletResponse) {
    // [1] Get book meta information
    val book = bookDao.getBookById(id)

    // [2] Construct S3 'gen presign url' request
    val s3Key = "${bucketKeyPrefix}/${book.origin}/${book.id}${bucketKeySuffix}"
    val expirationTime = Date(System.currentTimeMillis() + urlExpirationMillis)
    val genUrlRequest = GeneratePresignedUrlRequest(bucketName, s3Key).withExpiration(expirationTime)

    // [3] Execute it
    val presignedUrl = s3Client.generatePresignedUrl(genUrlRequest)
    val presignedUrlStr = presignedUrl.toString()

    log.debug("Using presignedUrlStr={} exp")

    // [4] Send redirect
    response.sendRedirect(presignedUrlStr)
  }
}

/** Download service initializer. */
class BookDownloadServiceFactory(val bookDao: BookDao) {

  var downloadMode = ""
  var accessKey = ""
  var secretKey = ""
  var bucketName = ""
  var bucketKeyPrefix = ""
  var bucketKeySuffix = ""
  var urlExpirationMillis = 0L

  fun getDownloadService(): BookDownloadService {
    Assert.hasText(downloadMode, "downloadMode should be initialized")

    if ("DEMO".equals(downloadMode)) {
      return DemoBookDownloadService()
    } else if ("S3".equals(downloadMode)) {
      return createS3BookDownloadService()
    }

    throw BeanInitializationException("Unknown downloadMode=${downloadMode}")
  }

  /** Helper S3 service creator. */
  private fun createS3BookDownloadService(): S3BookDownloadService {
    Assert.hasText(accessKey, "accessKey should be initialized")
    Assert.hasText(secretKey, "secretKey should be initialized")
    Assert.hasText(bucketName, "bucketName should be initialized")
    Assert.hasText(bucketKeyPrefix, "bucketKeyPrefix should be initialized")
    Assert.isTrue(urlExpirationMillis > 0, "urlExpirationMillis should be greater than zero")

    // Create aws credentials object and initialize S3 client
    val awsCred = BasicAWSCredentials(accessKey, secretKey)
    val s3Client = AmazonS3Client(awsCred)

    return S3BookDownloadService(bookDao = bookDao, s3Client = s3Client, bucketName = bucketName,
        bucketKeyPrefix = bucketKeyPrefix, bucketKeySuffix = bucketKeySuffix,
        urlExpirationMillis = urlExpirationMillis)
  }
}
