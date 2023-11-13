package com.example.pdfreader.office.configs

import org.slf4j.LoggerFactory

object MatchLicense {
    private val log = LoggerFactory.getLogger(MatchLicense::class.java)

    fun init() {
        try {
            log.info("Thực hiện cấp phép `aspose-words` -> Loại bỏ watermark ở đầu trang")
            /*
              Thực hiện cấp phép tệp -> Loại bỏ watermark ở đầu trang
             */
//            val inputStream: InputStream = ClassPathResource("license.xml").inputStream
//            val license = License()
//            license.setLicense(inputStream)
        } catch (e: Exception) {
            log.error("Cấp phép `aspose-words` thất bại: {}", e.message)
        }
    }
}
