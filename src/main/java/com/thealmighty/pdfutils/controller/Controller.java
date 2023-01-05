package com.thealmighty.pdfutils.controller;

import com.thealmighty.pdfutils.configuration.Configs;
import com.thealmighty.pdfutils.service.PdfReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("pdf-utils")
public class Controller {

    @Autowired
    private PdfReaderService pdfReaderService;

    @Autowired
    private Configs configs;

    @GetMapping("generate")
    public void generatePdfs() throws Exception {
        pdfReaderService.readFolderStructure(configs.getInputPath(), configs.getOutputPath());
    }
}
