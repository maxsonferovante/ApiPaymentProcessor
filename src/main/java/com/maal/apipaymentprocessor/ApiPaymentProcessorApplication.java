package com.maal.apipaymentprocessor;

import com.maal.apipaymentprocessor.infrastructure.config.GraalVmNativeConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(GraalVmNativeConfiguration.class)
public class ApiPaymentProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiPaymentProcessorApplication.class, args);
    }

}
