package com.thealmighty.pdfutils.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class Configs {

    @Value("${folder.path.input}")
    private String inputPath;

    @Value("${folder.path.output}")
    private String outputPath;
}
