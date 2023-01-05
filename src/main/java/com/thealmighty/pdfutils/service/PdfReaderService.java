package com.thealmighty.pdfutils.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
public class PdfReaderService {

    /*
        we assume that the structure is the following
        (root) Spring Professional Certification Course TEST
           |
           |-- Module 1
           |      |-- File 1
           |      |-- File n
           |
           |-- Module n
                  |-- File 1
                  |-- File n
     */

    public void readFolderStructure(String inputPath, String outputPath) throws Exception {
        File dir = new File(inputPath);
        //get the root folder and converting it into an array where every item is a module
        File[] files = dir.listFiles();
        //loop over module
        assert files != null;
        Arrays.stream(files).toList().forEach(folder -> {
            //converting into arraylist
            List<File> filesRaw = new ArrayList<>();
            Collections.addAll(filesRaw, Objects.requireNonNull(folder.listFiles()));

            //cleaning files filtering not-directory and files that contains "question" and pdfs
            List<File> filesClean = filesRaw.stream()
                    .filter(x -> (!x.isDirectory() && x.getName().contains("Question") && x.getAbsolutePath().contains("pdf")))
                    .collect(Collectors.toList());

            String moduleName = folder.getAbsolutePath().split("\\\\")[5];
            try {
                log.info("{} will be processed soon...", moduleName);
                processFiles(Objects.requireNonNull(filesClean), moduleName, outputPath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void processFiles(List<File> files, String moduleName, String outputPath) throws Exception {
        PDDocument existingDoc = new PDDocument();
        int count = 0;

        for (File file : files) {
            PDDocument currentDoc = PDDocument.load(file);
            log.debug("The document {} have {} pages", file.getName(), currentDoc.getNumberOfPages());

            int pageBeforeCleaning = currentDoc.getNumberOfPages();
            count += currentDoc.getNumberOfPages();

            //remove first page
            currentDoc.removePage(currentDoc.getPage(0));
            log.debug("Now the document has {} pages", currentDoc.getNumberOfPages());

            //remove last page
            currentDoc.removePage(currentDoc.getNumberOfPages() - 1);
            log.debug("Removed pages 1/{} and {}/{}", pageBeforeCleaning, currentDoc.getNumberOfPages(), currentDoc.getNumberOfPages());

            //merge the cleaned doc with the existing doc
            PDFMergerUtility merger = new PDFMergerUtility();
            merger.appendDocument(existingDoc, currentDoc);
            merger.mergeDocuments();
            currentDoc.close();
        }

        log.debug("Total pages before cleaning: {} - Total pages to delete: {}", count, files.size() * 2);
        log.info("Saving \"{}\" with {} pages...", moduleName, existingDoc.getNumberOfPages());

        //saving and closing doc
        existingDoc.save(outputPath +  "\\\\" + moduleName + ".pdf");
        existingDoc.close();
        log.info("Saved!");
    }
}
