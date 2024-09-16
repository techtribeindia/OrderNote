package com.project.ordernote.utils;

import java.io.File;

public interface PDFGeneratorListener {


        void onPDFGenerated(File pdfFile);
        void onError(String message);
    }

