package com.project.ordernote.utils;

import static com.itextpdf.text.BaseColor.BLACK;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

public class RoundRectangle implements PdfPCellEvent {
    

    @Override
    public void cellLayout(PdfPCell cell, com.itextpdf.text.Rectangle rect, PdfContentByte[] canvas) {
        PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
        cb.setColorStroke(BLACK);
        cb.roundRectangle(
                rect.getLeft() + 1.5f, rect.getBottom() + 1.5f, rect.getWidth() - 3,
                rect.getHeight() - 3, 4);
        cb.stroke();
    }
}
