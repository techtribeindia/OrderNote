package com.project.ordernote.utils;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static com.itextpdf.text.BaseColor.GRAY;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.itextpdf.kernel.pdf.PdfDocument;

import com.itextpdf.text.Document;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;

import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.model.ReportsFilterDetails_Model;
import com.project.ordernote.data.model.ReportsItemwiseCalculationDetail_Model;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.InputStream;
import java.util.Objects;

public class PDFGenerator {
//  public  PdfDocument pdfDocument ;
//  public  Paint paint ;
//  public  PdfDocument.PageInfo pageInfo ;
//  public  PdfDocument.Page page ;
 // public  Canvas canvas ;
 // public  int yPosition ;
  public  PDFGeneratorListener pdfGeneratorListener;
    PdfDocument pdfDoc;
    Document layoutDocument ;
    File file;
    RoundRectangle roundRectange;

    FragmentActivity fragmentActivity;
    Context context;
    String pdfType , supervisorname = "";
    HashMap<String, OrderDetails_Model> orderDetailsHashmap = new HashMap<>();
    HashMap<String, JSONObject> statuswisetotalcountdetailsjson ;
    HashMap<String, List<OrderItemDetails_Model>> orderwiseOrderItemDetails_statuswisereport;
    List<OrderItemDetails_Model> orderItemsbasedOnOrderId = new ArrayList<>();
    List<OrderItemDetails_Model> orderItems;
    ReportsFilterDetails_Model selectedFilterValue;
    PDFGeneratorListener listener;
    double totalItemPrice = 0 , totalfinalprice = 0 , discount = 0;

    HashMap<String, ReportsItemwiseCalculationDetail_Model> itemwiseTotalHashmap = new HashMap<>();
    List<String> itemwiseTotalHashmapKeyList = new ArrayList<>();

    HashMap<String , List<String>> statuswiseOrderid = new HashMap<>();

    private static int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    private SessionManager sessionManager;

    public PDFGenerator(FragmentActivity fragmentActivity, Context context, String pdfType, HashMap<String, OrderDetails_Model> orderDetailsHashmapp, List<OrderItemDetails_Model> orderItems, ReportsFilterDetails_Model value ,String supervisornamee, PDFGeneratorListener listener) {
        itemwiseTotalHashmapKeyList = new ArrayList<>();
        itemwiseTotalHashmap = new HashMap<>();
        this.fragmentActivity = fragmentActivity;
        this.context = context;
        this.pdfType = pdfType;
        this.orderDetailsHashmap = orderDetailsHashmapp;
        this.orderItems = orderItems;
        this.selectedFilterValue = value;
        this.listener = listener;
        this.supervisorname = supervisornamee;
        sessionManager = new SessionManager(context, Constants.USERPREF_NAME);
         try{
            file = null;layoutDocument=null;
        }
        catch (Exception e){
            e.printStackTrace();
        }

       //  CheckForPermission_And_CreatePdf();
        prepareDataForPDF();

    }

    public PDFGenerator(FragmentActivity fragmentActivity, Context context, String pdfType, HashMap<String, JSONObject> statuswisetotalcountdetailsjsonn, HashMap<String, List<OrderItemDetails_Model>> orderwiseOrderItemDetailss, HashMap<String, List<String>> statuswiseOrderid ,String supervisornamee,PDFGeneratorListener listener) {

        sessionManager = new SessionManager(context, Constants.USERPREF_NAME);
        itemwiseTotalHashmapKeyList = new ArrayList<>();
        statuswisetotalcountdetailsjson = new HashMap<>();
        this.fragmentActivity = fragmentActivity;
        this.context = context;
        this.pdfType = pdfType;
        this.statuswisetotalcountdetailsjson = statuswisetotalcountdetailsjsonn;
        this.orderwiseOrderItemDetails_statuswisereport = orderwiseOrderItemDetailss;
        this.statuswiseOrderid = statuswiseOrderid;
        this.listener = listener;
        this.supervisorname = supervisornamee;

        try{
            file = null;layoutDocument=null;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        prepareDataForStatueWisePDF();

    }

    private void prepareDataForStatueWisePDF() {

        try{
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            File folder = new File(path);
            //  File folder = new File(fol, "pdf");
            if (!folder.exists()) {
                boolean bool = folder.mkdirs();
            }
            try {
               // String filename = "Statuswise item details.pdf";

                String filename = "Statuswise item details "+DateParserClass.getMillisecondsFromDate(DateParserClass.getDateInStandardFormat())+".pdf";


                file = new File(folder, filename);
                file.createNewFile();
                try {
                    FileOutputStream fOut = new FileOutputStream(file);
                    layoutDocument = new  Document();
                    PdfWriter.getInstance(layoutDocument, fOut);
                    layoutDocument.open();

                    roundRectange = new RoundRectangle();



                    if(pdfType.equals(Constants.today_statuswise_pdf) || pdfType.equals(Constants.week_statuswise_pdf)){
                        generateStatusWiseOrdersListPDF(  );
                    }




                    layoutDocument.close();
                    listener.onPDFGenerated(file);

                } catch (Exception e) {
                    e.printStackTrace();
                }




                // }
            } catch (IOException e) {


                Log.i("error", e.getLocalizedMessage());
            } catch (Exception ex) {


                ex.printStackTrace();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void generateStatusWiseOrdersListPDF() {


        try{

            Font StoretitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
                    Font.BOLD);
            Font subtitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14,
                    Font.BOLD);

            Font valueFont_10Bold= new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.BOLD);
            Font valueFont_8Bold= new Font(Font.FontFamily.TIMES_ROMAN, 10,
                    Font.BOLD);

            Font valueFont_10= new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.NORMAL);
            Font valueFont_8= new Font(Font.FontFamily.TIMES_ROMAN, 10,
                    Font.NORMAL);
            Font valueFont_1= new Font(Font.FontFamily.TIMES_ROMAN, 1,
                    Font.NORMAL);

            BaseColor customBLueColor = new BaseColor(77, 25, 211); // Replace with your RGB values

            Font valueFont_15_BLueColor= new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.BOLD, customBLueColor);
            BaseColor customWhiteColor = new BaseColor(255, 255, 255); // Replace with your RGB values
            Font valueWhiteFont_10Bold= new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.BOLD,customWhiteColor);
            BaseColor customGreyColor = new BaseColor(180, 180, 180); // Replace with your RGB values
            BaseColor customPinkColor = new BaseColor(253, 192, 191);
            BaseColor customPalePurpleColor = new BaseColor(207, 191, 255);

            double totalAmount_PaidAmount =0  ,totalAmount_BillAmount = 0 ;
            String closingBalance = "0" , openingbalance ="0";
            RoundRectangle roundRectange = new RoundRectangle();
            PdfPTable wholePDFContentOutline_table = new PdfPTable(1);

            PdfPTable wholePDFContentWithImage_and_table = new PdfPTable(1);
            PdfPTable wholePDFContentWithOut_Outline_table = new PdfPTable(1);

         //   PdfPTable tmcLogoImage_table = new PdfPTable(new float[] { 50, 25 ,25 });


            try {
                PdfPCell table_Cell = new PdfPCell();
                table_Cell.setPaddingTop(5);
                table_Cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_Cell.setCalculatedHeight(5f);
                table_Cell.setVerticalAlignment(Element.ALIGN_RIGHT);
                table_Cell.setBorder(Rectangle.NO_BORDER);
             //   tmcLogoImage_table.addCell(table_Cell);

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                PdfPCell table_Cell = new PdfPCell();
                table_Cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_Cell.setCalculatedHeight(5f);
                table_Cell.setVerticalAlignment(Element.ALIGN_RIGHT);
                table_Cell.setBorder(Rectangle.NO_BORDER);
           //     tmcLogoImage_table.addCell(table_Cell);

            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                PdfPCell table_Cell = new PdfPCell(addLogo(layoutDocument));
                table_Cell.setBorder(Rectangle.NO_BORDER);
                table_Cell.setPaddingRight(10);
                table_Cell.setCalculatedHeight(5f);
                table_Cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_Cell.setVerticalAlignment(Element.ALIGN_RIGHT);
            //    tmcLogoImage_table.addCell(table_Cell);



            } catch (Exception e) {
                e.printStackTrace();
            }



            try{
                Phrase phrasebilltimeDetails = new Phrase(" "+'\n'+'\n'+" ", valueFont_8);
                PdfPCell phrasebilltimedetailscell = new PdfPCell(phrasebilltimeDetails);
                phrasebilltimedetailscell.setBorder(Rectangle.NO_BORDER);
                phrasebilltimedetailscell.setHorizontalAlignment(Element.ALIGN_LEFT);
                phrasebilltimedetailscell.setVerticalAlignment(Element.ALIGN_CENTER);
                phrasebilltimedetailscell.setPaddingLeft(10);
                phrasebilltimedetailscell.setPaddingBottom(6);
            ///    tmcLogoImage_table.addCell(phrasebilltimedetailscell);
                try {
                    PdfPCell table_Cell = new PdfPCell();
                    table_Cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table_Cell.setCalculatedHeight(5f);
                    table_Cell.setVerticalAlignment(Element.ALIGN_RIGHT);
                    table_Cell.setBorder(Rectangle.NO_BORDER);
            //        tmcLogoImage_table.addCell(table_Cell);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    PdfPCell table_Cell = new PdfPCell();
                    table_Cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table_Cell.setCalculatedHeight(5f);
                    table_Cell.setVerticalAlignment(Element.ALIGN_RIGHT);
                    table_Cell.setBorder(Rectangle.NO_BORDER);
                //    tmcLogoImage_table.addCell(table_Cell);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }


            PdfPTable billtimeDetails_table = new PdfPTable(2);
            try {

                Phrase phrasebilltimeDetails = new Phrase("DATE : " + DateParserClass.getDateInStandardFormat() + "      TIME : " + DateParserClass.getTime_newFormat(), valueFont_8);
                PdfPCell phrasebilltimedetailscell = new PdfPCell(phrasebilltimeDetails);
                phrasebilltimedetailscell.setBorder(Rectangle.NO_BORDER);
                phrasebilltimedetailscell.setHorizontalAlignment(Element.ALIGN_LEFT);
                phrasebilltimedetailscell.setVerticalAlignment(Element.ALIGN_CENTER);
                phrasebilltimedetailscell.setPaddingLeft(10);
                phrasebilltimedetailscell.setPaddingBottom(6);
                billtimeDetails_table.addCell(phrasebilltimedetailscell);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                PdfPTable supervisorNameDetails_table = new PdfPTable(1);

                Phrase phraseSupervisorNameLabelTitle = new Phrase("Supervisor Name : " + String.valueOf(supervisorname) + "  ", valueFont_8Bold);

                PdfPCell phraseSupervisorNameLabelTitlecell = new PdfPCell(phraseSupervisorNameLabelTitle);
                phraseSupervisorNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                phraseSupervisorNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                phraseSupervisorNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_RIGHT);
                phraseSupervisorNameLabelTitlecell.setPaddingLeft(6);
                phraseSupervisorNameLabelTitlecell.setPaddingBottom(3);
                phraseSupervisorNameLabelTitlecell.setPaddingRight(25);

                supervisorNameDetails_table.addCell(phraseSupervisorNameLabelTitlecell);


                try {
                    PdfPCell supervisorDetails = new PdfPCell(supervisorNameDetails_table);

                    supervisorDetails.setBorder(Rectangle.NO_BORDER);

                    billtimeDetails_table.addCell(supervisorDetails);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                PdfPCell addBorder_billTimeDetails = new PdfPCell(billtimeDetails_table);
                addBorder_billTimeDetails.setBorder(Rectangle.NO_BORDER);
                addBorder_billTimeDetails.setPaddingTop(5);
                addBorder_billTimeDetails.setBorderWidthBottom(01);
                addBorder_billTimeDetails.setBorderColor(GRAY);


                wholePDFContentWithOut_Outline_table.addCell(addBorder_billTimeDetails);

            } catch (Exception e) {
                e.printStackTrace();
            }




            PdfPTable Whole_Warehouse_and_RetailerDetails_table = new PdfPTable(new float[] { 50, 50 });

            try{
                PdfPTable Whole_WarehouseDetails_table = new PdfPTable(1);

                try {


                    Phrase phrasecompanyDetailsTitle = new Phrase( sessionManager.getVendorname(), subtitleFont);

                    PdfPCell phrasecompanyDetailsTitlecell = new PdfPCell(phrasecompanyDetailsTitle);
                    phrasecompanyDetailsTitlecell.setBorder(Rectangle.NO_BORDER);
                    phrasecompanyDetailsTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    phrasecompanyDetailsTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phrasecompanyDetailsTitlecell.setPaddingBottom(4);
                    phrasecompanyDetailsTitlecell.setPaddingLeft(6);
                    Whole_WarehouseDetails_table.addCell(phrasecompanyDetailsTitlecell);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Phrase phrasecompanyAddressDetails = new Phrase("60, Great Cotton Road , \nTuticorin ( Thoothukudi ) , . \nTamilnadu - 628001 , India . \nGSTIN : 33AJTPS1766K1ZI", valueFont_10);
                    //Phrase phrasecompanyAddressDetails = new Phrase("23/33, Chidambaram Street, \n Balaji Nagar Rd , Subash Nagar, Chromepet,\nChennai – 44 ,India.\nGSTIN 09,", valueFont_10);

                    PdfPCell phrasecompanyAddressDetailscell = new PdfPCell(phrasecompanyAddressDetails);
                    phrasecompanyAddressDetailscell.setBorder(Rectangle.NO_BORDER);
                    phrasecompanyAddressDetailscell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    phrasecompanyAddressDetailscell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phrasecompanyAddressDetailscell.setPaddingBottom(10);
                    phrasecompanyAddressDetailscell.setPaddingLeft(6);

                    Whole_WarehouseDetails_table.addCell(phrasecompanyAddressDetailscell);

                } catch (Exception e) {
                    e.printStackTrace();
                }





                PdfPTable Whole_SupplerDetails_table = new PdfPTable(new float[] { 100  });
                try{
                    try {

                        Phrase phraseretailerNameLabelTitle = new Phrase("Filter Details", valueFont_10Bold);

                        PdfPCell phraseretailerNameLabelTitlecell = new PdfPCell(phraseretailerNameLabelTitle);
                        phraseretailerNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                        phraseretailerNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        phraseretailerNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                        phraseretailerNameLabelTitlecell.setPaddingLeft(6);
                        phraseretailerNameLabelTitlecell.setPaddingBottom(10);
                        Whole_SupplerDetails_table.addCell(phraseretailerNameLabelTitlecell);





                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    try {

                        Phrase phraseretailerNameLabelTitle ;
                        if(pdfType.equals(Constants.today_statuswise_pdf)){
                            phraseretailerNameLabelTitle = new Phrase("Filter Type : StatusWise orders for today"+"\n", valueFont_10);
                        }
                        else  if( pdfType.equals(Constants.week_statuswise_pdf)){
                            phraseretailerNameLabelTitle = new Phrase("Filter Type :StatusWise orders in this week"+"\n", valueFont_10);

                        }
                        else {
                            phraseretailerNameLabelTitle = new Phrase("Filter Type : -  "+"\n", valueFont_10);

                        }




                        PdfPCell phraseretailerNameLabelTitlecell = new PdfPCell(phraseretailerNameLabelTitle);
                        phraseretailerNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                        phraseretailerNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        phraseretailerNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                        phraseretailerNameLabelTitlecell.setPaddingLeft(6);
                        phraseretailerNameLabelTitlecell.setPaddingBottom(10);
                        Whole_SupplerDetails_table.addCell(phraseretailerNameLabelTitlecell);



                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }




                    try {

                        try {
                            if (pdfType.equals(Constants.today_statuswise_pdf)) {


                                Phrase dateTitle = new Phrase("Date : "+String.valueOf(DateParserClass.getDateTimeInReadableFormat())+"\n", valueFont_10Bold);
                                PdfPCell dateTitlecell = new PdfPCell(dateTitle);
                                dateTitlecell.setBorder(Rectangle.NO_BORDER);
                                dateTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                dateTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                dateTitlecell.setPaddingLeft(6);
                                dateTitlecell.setPaddingBottom(10);
                                Whole_SupplerDetails_table.addCell(dateTitlecell);



                            }
                            else if (pdfType.equals(Constants.week_statuswise_pdf)) {

                                Phrase dateTitle = new Phrase("From : "+String.valueOf(DateParserClass.getFirstDayOfWeek(DateParserClass.getDateInStandardFormat()))+"\n", valueFont_10Bold);
                                PdfPCell dateTitlecell = new PdfPCell(dateTitle);
                                dateTitlecell.setBorder(Rectangle.NO_BORDER);
                                dateTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                dateTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                dateTitlecell.setPaddingLeft(6);
                                dateTitlecell.setPaddingBottom(10);
                                Whole_SupplerDetails_table.addCell(dateTitlecell);


                                Phrase todateTitle = new Phrase("To : "+String.valueOf(DateParserClass.getDateInStandardFormat())+"\n", valueFont_10Bold);
                                PdfPCell todateTitlecell = new PdfPCell(todateTitle);
                                todateTitlecell.setBorder(Rectangle.NO_BORDER);
                                todateTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                todateTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                todateTitlecell.setPaddingLeft(6);
                                todateTitlecell.setPaddingBottom(10);
                                Whole_SupplerDetails_table.addCell(todateTitlecell);



                            }

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }




                }
                catch (Exception e) {
                    e.printStackTrace();
                }




                try {
                    PdfPCell Whole_WarehouseDetails_table_Cell = new PdfPCell(Whole_WarehouseDetails_table);
                    Whole_WarehouseDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                    Whole_WarehouseDetails_table_Cell.setBorderWidthRight(01);
                    Whole_WarehouseDetails_table_Cell.setPaddingTop(5);

                    Whole_Warehouse_and_RetailerDetails_table.addCell(Whole_WarehouseDetails_table_Cell);


                    PdfPCell Whole_SupplerDetails_table_Cell = new PdfPCell(Whole_SupplerDetails_table);
                    Whole_SupplerDetails_table_Cell.setPaddingTop(5);
                    Whole_SupplerDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                    Whole_Warehouse_and_RetailerDetails_table.addCell(Whole_SupplerDetails_table_Cell);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    PdfPCell Whole_Warehouse_and_RetailerDetails_table_Cell = new PdfPCell(Whole_Warehouse_and_RetailerDetails_table);
                    Whole_Warehouse_and_RetailerDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                    Whole_Warehouse_and_RetailerDetails_table_Cell.setBorderWidthTop(01);
                    wholePDFContentWithOut_Outline_table.addCell(Whole_Warehouse_and_RetailerDetails_table_Cell);

                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{



                try{



                    try {

                        if (statuswisetotalcountdetailsjson.containsKey(Constants.pending_status)) {
                            if (statuswiseOrderid.containsKey(Constants.pending_status)) {

                                itemwiseTotalHashmap = new HashMap<>();
                                orderItemsbasedOnOrderId = new ArrayList<>();
                                itemwiseTotalHashmapKeyList = new ArrayList<>();


                                List<String> orderidList = statuswiseOrderid.get(Constants.pending_status);
                                if (orderidList.size() > 0) {
                                boolean isHeadingLabelAdded = false;
                                    for (int iterator = 0; iterator < orderidList.size(); iterator++) {
                                        String orderidFromHashmap = orderidList.get(iterator);


                                        orderItemsbasedOnOrderId = new ArrayList<>(Objects.requireNonNull(orderwiseOrderItemDetails_statuswisereport.get(orderidFromHashmap)));

                                        // orderItemsbasedOnOrderId.addAll(Objects.requireNonNull(orderwiseOrderItemDetails_statuswisereport.get(orderidFromHashmap)));
                                        if (!orderItemsbasedOnOrderId.isEmpty()) {
                                            if (!isHeadingLabelAdded){
                                                isHeadingLabelAdded = true;

                                                PdfPTable statusLabel_table = new PdfPTable(new float[]{100});

                                                Phrase statusLabel_Title = new Phrase(Constants.created_pending_status, valueFont_10);

                                                PdfPCell statusLabel_Titlecell = new PdfPCell(statusLabel_Title);
                                                statusLabel_Titlecell.setBorder(Rectangle.NO_BORDER);
                                                statusLabel_Titlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                statusLabel_Titlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                statusLabel_Titlecell.setBorderWidthRight(01);
                                                statusLabel_Titlecell.setPaddingLeft(6);
                                                statusLabel_Titlecell.setPaddingBottom(10);
                                                statusLabel_table.addCell(statusLabel_Titlecell);


                                                try {
                                                    PdfPCell itemDetails_table_Cell = new PdfPCell(statusLabel_table);
                                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                    itemDetails_table_Cell.setBorderWidthTop(1);
                                                    itemDetails_table_Cell.setBackgroundColor(customGreyColor);
                                                    itemDetails_table_Cell.setBorderWidthBottom(01);
                                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                                try {
                                                    PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                                    try {

                                                        Phrase phrasCtgynameLabelTitle = new Phrase(" S.No  ", valueFont_10);

                                                        PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                                        phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                        phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                                        phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                                        phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                                        itemDetailsLabel_table.addCell(phraseCtgyNameLabelTitlecell);


                                                        Phrase phrasMaleCountLabelTitle = new Phrase("Item Name", valueFont_10);

                                                        PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                                        phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                                        phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                                        phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                                        itemDetailsLabel_table.addCell(phraseMaleCountLabelTitlecell);


                                                        Phrase phrasFemaleCountLabelTitle = new Phrase("Quantity", valueFont_10);

                                                        PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                                        phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                                        phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                                        phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                                        itemDetailsLabel_table.addCell(phraseFemaleCountLabelTitlecell);


                                                        Phrase phrasTotalCountLabelTitle = new Phrase("Weight", valueFont_10);

                                                        PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                                        phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseCountLabelTitlecell.setPaddingLeft(6);
                                                        phraseCountLabelTitlecell.setPaddingBottom(10);
                                                        phraseCountLabelTitlecell.setBorderWidthRight(01);

                                                        itemDetailsLabel_table.addCell(phraseCountLabelTitlecell);


                                                        Phrase phraseWeightLabelTitle = new Phrase("Price", valueFont_10);

                                                        PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                                        phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseWeightLabelTitlecell.setPaddingLeft(6);
                                                        phraseWeightLabelTitlecell.setPaddingBottom(10);
                                                        phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                                        itemDetailsLabel_table.addCell(phraseWeightLabelTitlecell);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    try {
                                                        PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                        itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                        itemDetails_table_Cell.setBorderWidthTop(1);
                                                        itemDetails_table_Cell.setBackgroundColor(customGreyColor);
                                                        itemDetails_table_Cell.setBorderWidthBottom(01);
                                                        wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                            }

                                            try {
                                                for (int iteratr = 0; iteratr < orderItemsbasedOnOrderId.size(); iteratr++) {
                                                    OrderItemDetails_Model orderItemDetailsModel = orderItemsbasedOnOrderId.get(iteratr);

                                                    if (itemwiseTotalHashmap.containsKey(orderItemDetailsModel.getMenuitemkey())) {
                                                        ReportsItemwiseCalculationDetail_Model modelFromHashmap = itemwiseTotalHashmap.get(orderItemDetailsModel.getMenuitemkey());

                                                        int quantityFromHashmap = 0, quantityFromItemArray = 0;
                                                        double weightFromHashmap = 0, weightFromItemArray = 0, priceFromHashmap = 0, priceFromItemArray = 0;

                                                        quantityFromHashmap = Objects.requireNonNull(modelFromHashmap).getTotalquantity();
                                                        weightFromHashmap = modelFromHashmap.getTotalweight();
                                                        priceFromHashmap = modelFromHashmap.getTotalprice();
                                                        quantityFromItemArray = orderItemDetailsModel.getQuantity();
                                                        weightFromItemArray = orderItemDetailsModel.getGrossweight();
                                                        priceFromItemArray = orderItemDetailsModel.getTotalprice();

                                                        try {
                                                            modelFromHashmap.setTotalweight(weightFromHashmap + weightFromItemArray);
                                                            modelFromHashmap.setTotalquantity(quantityFromHashmap + quantityFromItemArray);
                                                            modelFromHashmap.setTotalprice(priceFromHashmap + priceFromItemArray);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }


                                                    } else {
                                                        ReportsItemwiseCalculationDetail_Model modelFromHashmap = new ReportsItemwiseCalculationDetail_Model();

                                                        modelFromHashmap.setMenuitemkey(orderItemDetailsModel.getMenuitemkey());
                                                        modelFromHashmap.setTotalprice(orderItemDetailsModel.getTotalprice());
                                                        modelFromHashmap.setMenuitemname(orderItemDetailsModel.getItemname());
                                                        modelFromHashmap.setTotalquantity(orderItemDetailsModel.getQuantity());
                                                        modelFromHashmap.setTotalweight(orderItemDetailsModel.getGrossweight());
                                                        itemwiseTotalHashmapKeyList.add(orderItemDetailsModel.getMenuitemkey());
                                                        itemwiseTotalHashmap.put(orderItemDetailsModel.getMenuitemkey(), modelFromHashmap);


                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();

                                            }


                                        } else {
                                            orderItemsbasedOnOrderId = new ArrayList<>();

                                        }


                                    }
                                }


                            }
                            if (itemwiseTotalHashmapKeyList.size() > 8) {
                                try {


                                    for (int iterator = 0; iterator < 9; iterator++) {
                                        String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                                        try {

                                            //20, 30 , 12 ,10, 12 , 16
                                            //19, 18, 15, 15, 15, 18

                                            PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});


                                            try {
                                                Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator + 1), valueFont_10Bold);

                                                PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                                phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);


                                                Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);
                                                PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                                phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);


                                                Phrase phrasQuantityLabelTitle = null;
                                                phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                                PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                                phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);


                                                phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                                phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                                phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);


                                               // Phrase phrasBatchpriceLabelTitle = null;
                                                // phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                Phrase phrasBatchpriceLabelTitle = null;
                                                if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                }
                                                else{
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                                }

                                                PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                                phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                                phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                                phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);


                                                Phrase phrasNotesLabelTitle = null;

                                                phrasNotesLabelTitle = new Phrase((" ₹ " + String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                                PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                                phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setPaddingLeft(6);

                                                phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                                phraseNotesLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                            try {
                                                PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                itemDetails_table_Cell.setBorderWidthBottom(1);
                                                //itemDetails_table_Cell.setBackgroundColor(WHITE);
                                                wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                               // tmcLogoImage_table.setHorizontalAlignment(Element.ALIGN_RIGHT);
                               // tmcLogoImage_table.setTotalWidth(10f);
                              //  layoutDocument.add(tmcLogoImage_table);

                                PdfPCell wholePDFWithOutBordercell = new PdfPCell(wholePDFContentWithOut_Outline_table);
                                wholePDFWithOutBordercell.setCellEvent(roundRectange);
                                wholePDFWithOutBordercell.setPadding(1);
                                wholePDFWithOutBordercell.setBorder(Rectangle.NO_BORDER);
                                wholePDFContentOutline_table.addCell(wholePDFWithOutBordercell);
                                wholePDFContentOutline_table.setWidthPercentage(100);


                                layoutDocument.add(wholePDFContentOutline_table);
                                PdfPTable wholePDFContentWithOut_Outline_table2 = new PdfPTable(1);
                                try {
                                    layoutDocument.newPage();


                                    try {


                                        for (int iterator = 9; iterator < itemwiseTotalHashmapKeyList.size(); iterator++) {
                                            String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                                            try {


                                                PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});


                                                try {
                                                    Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator + 1), valueFont_10Bold);

                                                    PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                                    phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                    phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                                    phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                                    phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);


                                                    Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);

                                                    PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                                    phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                    phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                                    phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                                    phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);


                                                    Phrase phrasQuantityLabelTitle = null;
                                                    phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                                    PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                                    phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                    phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);


                                                    phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                                    phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                                    phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);


                                                   // Phrase phrasBatchpriceLabelTitle = null;
                                                   // phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                    Phrase phrasBatchpriceLabelTitle = null;
                                                    if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                                        phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                    }
                                                    else{
                                                        phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                                    }

                                                    PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                                    phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                    phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                                    phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                                    phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);


                                                    Phrase phrasNotesLabelTitle = null;

                                                    phrasNotesLabelTitle = new Phrase((" ₹ " + String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                                    PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                                    phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                    phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseNotesLabelTitlecell.setPaddingLeft(6);

                                                    phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                                    phraseNotesLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                    itemDetails_table_Cell.setBorderWidthBottom(1);
                                                    // itemDetails_table_Cell.setBackgroundColor(WHITE);
                                                    wholePDFContentWithOut_Outline_table2.addCell(itemDetails_table_Cell);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                            catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    //extraas







                                    //finaladd
                                    try {


                                        PdfPTable wholePDFContentOutline_table2 = new PdfPTable(1);


                                        PdfPCell wholePDFWithOutBordercell2 = new PdfPCell(wholePDFContentWithOut_Outline_table2);
                                        wholePDFWithOutBordercell2.setCellEvent(roundRectange);
                                        wholePDFWithOutBordercell2.setPadding(1);
                                        wholePDFWithOutBordercell2.setBorder(Rectangle.NO_BORDER);
                                        wholePDFContentOutline_table2.addCell(wholePDFWithOutBordercell2);
                                        wholePDFContentOutline_table2.setWidthPercentage(100);


                                        layoutDocument.add(wholePDFContentOutline_table2);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                            else {
                                try {
                                    for (int iterator = 0; iterator < itemwiseTotalHashmapKeyList.size(); iterator++) {
                                        String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                                        try {

                                            //20, 30 , 12 ,10, 12 , 16
                                            //19, 18, 15, 15, 15, 18

                                            PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});


                                            try {
                                                Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator + 1), valueFont_10Bold);

                                                PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                                phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);


                                                Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);

                                                PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                                phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);


                                                Phrase phrasQuantityLabelTitle = null;
                                                phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                                PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                                phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);


                                                phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                                phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                                phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);


                                               // Phrase phrasBatchpriceLabelTitle = null;
                                               // phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);

                                                Phrase phrasBatchpriceLabelTitle = null;
                                                if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                }
                                                else{
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                                }

                                                PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                                phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                                phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                                phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);


                                                Phrase phrasNotesLabelTitle = null;

                                                phrasNotesLabelTitle = new Phrase((" ₹ " + String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                                PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                                phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setPaddingLeft(6);

                                                phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                                phraseNotesLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                            try {
                                                PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                itemDetails_table_Cell.setBorderWidthBottom(1);
                                                // itemDetails_table_Cell.setBackgroundColor(WHITE);
                                                wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                 catch (Exception e) {
                                    e.printStackTrace();
                                }




                            }

                            try {
                                PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                try {

                                    Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                    phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                    Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                    phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                    Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                    phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                    Phrase phrasTotalCountLabelTitle = new Phrase("Total   ", valueFont_10Bold);

                                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setPaddingLeft(6);
                                    phraseCountLabelTitlecell.setPaddingBottom(10);
                                    phraseCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                    Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(statuswisetotalcountdetailsjson.get(Constants.pending_status).getDouble("pricebeforediscount")), valueFont_10);

                                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                                    phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                    itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);

                                    itemDetails_table_Cell.setBorderWidthBottom(01);
                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                try {

                                    Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                    phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                    Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                    phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                    Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                    phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                    Phrase phrasTotalCountLabelTitle = new Phrase("Discount   ", valueFont_10Bold);

                                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setPaddingLeft(6);
                                    phraseCountLabelTitlecell.setPaddingBottom(10);
                                    phraseCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                    Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(statuswisetotalcountdetailsjson.get(Constants.pending_status).getDouble("discount")), valueFont_10);

                                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                                    phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                    itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);


                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                try {

                                    Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                    phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                    Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                    phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                    Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                    phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                    Phrase phrasTotalCountLabelTitle = new Phrase("Final price   ", valueFont_10Bold);

                                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setPaddingLeft(6);
                                    phraseCountLabelTitlecell.setPaddingBottom(10);
                                    phraseCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                    Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(statuswisetotalcountdetailsjson.get(Constants.pending_status).getDouble("price")), valueFont_10);

                                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                                    phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                    itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                    itemDetails_table_Cell.setBorderWidthTop(1);

                                    itemDetails_table_Cell.setBorderWidthBottom(01);
                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }


                            try {
                                PdfPTable EnptytableLabel_table = new PdfPTable(new float[]{35, 65});


                                try {
                                    Phrase phraseParticularsLabelTitle = new Phrase("    ", valueFont_10Bold);

                                    PdfPCell phraseParticularsLabelTitlecell = new PdfPCell(phraseParticularsLabelTitle);
                                    phraseParticularsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseParticularsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseParticularsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseParticularsLabelTitlecell.setPaddingLeft(6);
                                    phraseParticularsLabelTitlecell.setPaddingTop(5);
                                    phraseParticularsLabelTitlecell.setPaddingBottom(10);
                                    EnptytableLabel_table.addCell(phraseParticularsLabelTitlecell);


                                    Phrase phrasQuantityLabelTitle = new Phrase("     ", valueFont_10Bold);
                                    PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                    phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseQuantityLabelTitlecell.setPaddingTop(5);
                                    phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                    phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                    EnptytableLabel_table.addCell(phraseQuantityLabelTitlecell);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(EnptytableLabel_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);

                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        if (statuswisetotalcountdetailsjson.containsKey(Constants.accepted_status)) {

                            itemwiseTotalHashmap = new HashMap<>();
                            orderItemsbasedOnOrderId = new ArrayList<>();
                            itemwiseTotalHashmapKeyList = new ArrayList<>();

                            if (statuswiseOrderid.containsKey(Constants.accepted_status)) {
                                List<String> orderidList = statuswiseOrderid.get(Constants.accepted_status);
                                if (orderidList.size() > 0) {
                                   boolean isHeadingLabelAdded = false;
                                    for (int iterator = 0; iterator < orderidList.size(); iterator++) {
                                        String orderidFromHashmap = orderidList.get(iterator);

                                        orderItemsbasedOnOrderId = new ArrayList<>(Objects.requireNonNull(orderwiseOrderItemDetails_statuswisereport.get(orderidFromHashmap)));

                                        // orderItemsbasedOnOrderId.addAll(Objects.requireNonNull(orderwiseOrderItemDetails_statuswisereport.get(orderidFromHashmap)));
                                        if (!orderItemsbasedOnOrderId.isEmpty()) {
                                            if (!isHeadingLabelAdded){
                                                isHeadingLabelAdded = true;


                                                PdfPTable statusLabel_table = new PdfPTable(new float[]{100});

                                                Phrase statusLabel_Title = new Phrase(Constants.placed_pending_status, valueFont_10);

                                                PdfPCell statusLabel_Titlecell = new PdfPCell(statusLabel_Title);
                                                statusLabel_Titlecell.setBorder(Rectangle.NO_BORDER);
                                                statusLabel_Titlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                statusLabel_Titlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                statusLabel_Titlecell.setBorderWidthRight(01);
                                                statusLabel_Titlecell.setPaddingLeft(6);
                                                statusLabel_Titlecell.setPaddingBottom(10);
                                                statusLabel_table.addCell(statusLabel_Titlecell);


                                                try {
                                                    PdfPCell itemDetails_table_Cell = new PdfPCell(statusLabel_table);
                                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                    itemDetails_table_Cell.setBorderWidthTop(1);
                                                    itemDetails_table_Cell.setBackgroundColor(customGreyColor);
                                                    itemDetails_table_Cell.setBorderWidthBottom(01);
                                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                                try {
                                                    PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                                    try {

                                                        Phrase phrasCtgynameLabelTitle = new Phrase(" S.No  ", valueFont_10);

                                                        PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                                        phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                        phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                                        phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                                        phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                                        itemDetailsLabel_table.addCell(phraseCtgyNameLabelTitlecell);


                                                        Phrase phrasMaleCountLabelTitle = new Phrase("Item Name", valueFont_10);

                                                        PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                                        phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                                        phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                                        phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                                        itemDetailsLabel_table.addCell(phraseMaleCountLabelTitlecell);


                                                        Phrase phrasFemaleCountLabelTitle = new Phrase("Quantity", valueFont_10);

                                                        PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                                        phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                                        phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                                        phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                                        itemDetailsLabel_table.addCell(phraseFemaleCountLabelTitlecell);


                                                        Phrase phrasTotalCountLabelTitle = new Phrase("Weight", valueFont_10);

                                                        PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                                        phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseCountLabelTitlecell.setPaddingLeft(6);
                                                        phraseCountLabelTitlecell.setPaddingBottom(10);
                                                        phraseCountLabelTitlecell.setBorderWidthRight(01);

                                                        itemDetailsLabel_table.addCell(phraseCountLabelTitlecell);


                                                        Phrase phraseWeightLabelTitle = new Phrase("Price", valueFont_10);

                                                        PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                                        phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseWeightLabelTitlecell.setPaddingLeft(6);
                                                        phraseWeightLabelTitlecell.setPaddingBottom(10);
                                                        phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                                        itemDetailsLabel_table.addCell(phraseWeightLabelTitlecell);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    try {
                                                        PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                        itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                        itemDetails_table_Cell.setBorderWidthTop(1);
                                                        itemDetails_table_Cell.setBackgroundColor(customGreyColor);
                                                        itemDetails_table_Cell.setBorderWidthBottom(01);
                                                        wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                            }

                                            try {
                                                for (int iteratr = 0; iteratr < orderItemsbasedOnOrderId.size(); iteratr++) {
                                                    OrderItemDetails_Model orderItemDetailsModel = orderItemsbasedOnOrderId.get(iteratr);

                                                    if (itemwiseTotalHashmap.containsKey(orderItemDetailsModel.getMenuitemkey())) {
                                                        ReportsItemwiseCalculationDetail_Model modelFromHashmap = itemwiseTotalHashmap.get(orderItemDetailsModel.getMenuitemkey());

                                                        int quantityFromHashmap = 0, quantityFromItemArray = 0;
                                                        double weightFromHashmap = 0, weightFromItemArray = 0, priceFromHashmap = 0, priceFromItemArray = 0;

                                                        quantityFromHashmap = Objects.requireNonNull(modelFromHashmap).getTotalquantity();
                                                        weightFromHashmap = modelFromHashmap.getTotalweight();
                                                        priceFromHashmap = modelFromHashmap.getTotalprice();
                                                        quantityFromItemArray = orderItemDetailsModel.getQuantity();
                                                        weightFromItemArray = orderItemDetailsModel.getGrossweight();
                                                        priceFromItemArray = orderItemDetailsModel.getTotalprice();

                                                        try {
                                                            modelFromHashmap.setTotalweight(weightFromHashmap + weightFromItemArray);
                                                            modelFromHashmap.setTotalquantity(quantityFromHashmap + quantityFromItemArray);
                                                            modelFromHashmap.setTotalprice(priceFromHashmap + priceFromItemArray);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }


                                                    } else {
                                                        ReportsItemwiseCalculationDetail_Model modelFromHashmap = new ReportsItemwiseCalculationDetail_Model();

                                                        modelFromHashmap.setMenuitemkey(orderItemDetailsModel.getMenuitemkey());
                                                        modelFromHashmap.setTotalprice(orderItemDetailsModel.getTotalprice());
                                                        modelFromHashmap.setMenuitemname(orderItemDetailsModel.getItemname());
                                                        modelFromHashmap.setTotalquantity(orderItemDetailsModel.getQuantity());
                                                        modelFromHashmap.setTotalweight(orderItemDetailsModel.getGrossweight());
                                                        itemwiseTotalHashmapKeyList.add(orderItemDetailsModel.getMenuitemkey());
                                                        itemwiseTotalHashmap.put(orderItemDetailsModel.getMenuitemkey(), modelFromHashmap);


                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();

                                            }


                                        } else {
                                            orderItemsbasedOnOrderId = new ArrayList<>();

                                        }


                                    }
                                }


                            }
                            if (itemwiseTotalHashmapKeyList.size() > 8) {
                                try {


                                    for (int iterator = 0; iterator < 9; iterator++) {
                                        String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                                        try {

                                            //20, 30 , 12 ,10, 12 , 16
                                            //19, 18, 15, 15, 15, 18

                                            PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});


                                            try {
                                                Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator + 1), valueFont_10Bold);

                                                PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                                phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);


                                                Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);
                                                PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                                phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);


                                                Phrase phrasQuantityLabelTitle = null;
                                                phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                                PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                                phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);


                                                phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                                phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                                phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);


                                              //  Phrase phrasBatchpriceLabelTitle = null;
                                              //  phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                Phrase phrasBatchpriceLabelTitle = null;
                                                if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                }
                                                else{
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                                }

                                                PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                                phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                                phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                                phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);


                                                Phrase phrasNotesLabelTitle = null;

                                                phrasNotesLabelTitle = new Phrase((" ₹ " + String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                                PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                                phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setPaddingLeft(6);

                                                phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                                phraseNotesLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                            try {
                                                PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                itemDetails_table_Cell.setBorderWidthBottom(1);
                                                //itemDetails_table_Cell.setBackgroundColor(WHITE);
                                                wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                               // tmcLogoImage_table.setHorizontalAlignment(Element.ALIGN_RIGHT);
                              //  tmcLogoImage_table.setTotalWidth(10f);
                              //  layoutDocument.add(tmcLogoImage_table);

                                PdfPCell wholePDFWithOutBordercell = new PdfPCell(wholePDFContentWithOut_Outline_table);
                                wholePDFWithOutBordercell.setCellEvent(roundRectange);
                                wholePDFWithOutBordercell.setPadding(1);
                                wholePDFWithOutBordercell.setBorder(Rectangle.NO_BORDER);
                                wholePDFContentOutline_table.addCell(wholePDFWithOutBordercell);
                                wholePDFContentOutline_table.setWidthPercentage(100);


                                layoutDocument.add(wholePDFContentOutline_table);
                                PdfPTable wholePDFContentWithOut_Outline_table2 = new PdfPTable(1);
                                try {
                                    layoutDocument.newPage();


                                    try {


                                        for (int iterator = 9; iterator < itemwiseTotalHashmapKeyList.size(); iterator++) {
                                            String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                                            try {


                                                PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});


                                                try {
                                                    Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator + 1), valueFont_10Bold);

                                                    PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                                    phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                    phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                                    phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                                    phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);


                                                    Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);

                                                    PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                                    phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                    phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                                    phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                                    phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);


                                                    Phrase phrasQuantityLabelTitle = null;
                                                    phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                                    PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                                    phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                    phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);


                                                    phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                                    phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                                    phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);


                                                  //  Phrase phrasBatchpriceLabelTitle = null;
                                                   // phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                    Phrase phrasBatchpriceLabelTitle = null;
                                                    if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                                        phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                    }
                                                    else{
                                                        phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                                    }

                                                    PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                                    phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                    phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                                    phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                                    phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);


                                                    Phrase phrasNotesLabelTitle = null;

                                                    phrasNotesLabelTitle = new Phrase((" ₹ " + String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                                    PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                                    phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                    phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseNotesLabelTitlecell.setPaddingLeft(6);

                                                    phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                                    phraseNotesLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                    itemDetails_table_Cell.setBorderWidthBottom(1);
                                                    // itemDetails_table_Cell.setBackgroundColor(WHITE);
                                                    wholePDFContentWithOut_Outline_table2.addCell(itemDetails_table_Cell);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    //extraas
/*                                    try {


                                        try {
                                            PdfPTable EnptytableLabel_table = new PdfPTable(new float[]{35, 65});


                                            try {
                                                Phrase phraseParticularsLabelTitle = new Phrase("    ", valueFont_10Bold);

                                                PdfPCell phraseParticularsLabelTitlecell = new PdfPCell(phraseParticularsLabelTitle);
                                                phraseParticularsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseParticularsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseParticularsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseParticularsLabelTitlecell.setPaddingLeft(6);
                                                phraseParticularsLabelTitlecell.setPaddingTop(5);
                                                phraseParticularsLabelTitlecell.setPaddingBottom(10);
                                                EnptytableLabel_table.addCell(phraseParticularsLabelTitlecell);


                                                Phrase phrasQuantityLabelTitle = new Phrase("     ", valueFont_10Bold);
                                                PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                                phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                                phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseQuantityLabelTitlecell.setPaddingTop(5);
                                                phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                                phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                                EnptytableLabel_table.addCell(phraseQuantityLabelTitlecell);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                            try {
                                                PdfPCell itemDetails_table_Cell = new PdfPCell(EnptytableLabel_table);
                                                itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);

                                                wholePDFContentWithOut_Outline_table2.addCell(itemDetails_table_Cell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


 */

                                    //finaladd
                                    try {


                                        PdfPTable wholePDFContentOutline_table2 = new PdfPTable(1);


                                        PdfPCell wholePDFWithOutBordercell2 = new PdfPCell(wholePDFContentWithOut_Outline_table2);
                                        wholePDFWithOutBordercell2.setCellEvent(roundRectange);
                                        wholePDFWithOutBordercell2.setPadding(1);
                                        wholePDFWithOutBordercell2.setBorder(Rectangle.NO_BORDER);
                                        wholePDFContentOutline_table2.addCell(wholePDFWithOutBordercell2);
                                        wholePDFContentOutline_table2.setWidthPercentage(100);


                                        layoutDocument.add(wholePDFContentOutline_table2);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                            else {
                                try {
                                    for (int iterator = 0; iterator < itemwiseTotalHashmapKeyList.size(); iterator++) {
                                        String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                                        try {

                                            //20, 30 , 12 ,10, 12 , 16
                                            //19, 18, 15, 15, 15, 18

                                            PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});


                                            try {
                                                Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator + 1), valueFont_10Bold);

                                                PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                                phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);


                                                Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);

                                                PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                                phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);


                                                Phrase phrasQuantityLabelTitle = null;
                                                phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                                PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                                phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);


                                                phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                                phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                                phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);


                                              //  Phrase phrasBatchpriceLabelTitle = null;
                                              //  phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                Phrase phrasBatchpriceLabelTitle = null;
                                                if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                }
                                                else{
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                                }

                                                PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                                phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                                phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                                phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);


                                                Phrase phrasNotesLabelTitle = null;

                                                phrasNotesLabelTitle = new Phrase((" ₹ " + String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                                PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                                phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setPaddingLeft(6);

                                                phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                                phraseNotesLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                            try {
                                                PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                itemDetails_table_Cell.setBorderWidthBottom(1);
                                                // itemDetails_table_Cell.setBackgroundColor(WHITE);
                                                wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }




                            }


                            try {
                                PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                try {

                                    Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                    phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                    Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                    phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                    Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                    phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                    Phrase phrasTotalCountLabelTitle = new Phrase("Total   ", valueFont_10Bold);

                                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setPaddingLeft(6);
                                    phraseCountLabelTitlecell.setPaddingBottom(10);
                                    phraseCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                    Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(statuswisetotalcountdetailsjson.get(Constants.accepted_status).getDouble("pricebeforediscount")), valueFont_10);
                                //    Phrase phraseWeightLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                                    phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                    itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                    itemDetails_table_Cell.setBorderWidthBottom(1);

                                     wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }


                            try {
                                PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                try {

                                    Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                    phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                    Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                    phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                    Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                    phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                    Phrase phrasTotalCountLabelTitle = new Phrase("Discount   ", valueFont_10Bold);

                                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setPaddingLeft(6);
                                    phraseCountLabelTitlecell.setPaddingBottom(10);
                                    phraseCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                    Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(statuswisetotalcountdetailsjson.get(Constants.accepted_status).getDouble("discount")), valueFont_10);

                                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                                    phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                    itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                    //itemDetails_table_Cell.setBorderWidthTop(1);

                                    //itemDetails_table_Cell.setBorderWidthBottom(01);
                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            try {
                                PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                try {

                                    Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                    phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                    Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                    phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                    Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                    phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                    Phrase phrasTotalCountLabelTitle = new Phrase("Final price   ", valueFont_10Bold);

                                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setPaddingLeft(6);
                                    phraseCountLabelTitlecell.setPaddingBottom(10);
                                    phraseCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                    Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(statuswisetotalcountdetailsjson.get(Constants.accepted_status).getDouble("price")), valueFont_10);

                                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                                    phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                    itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                    itemDetails_table_Cell.setBorderWidthTop(1);

                                    itemDetails_table_Cell.setBorderWidthBottom(01);
                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                try {

                                    Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                     phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                    Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                     phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                    Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);

                                    itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                    Phrase phrasTotalCountLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setPaddingLeft(6);
                                    phraseCountLabelTitlecell.setPaddingBottom(10);

                                    itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                    Phrase phraseWeightLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);

                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                        if (statuswisetotalcountdetailsjson.containsKey(Constants.rejected_status)) {
                            if (statuswiseOrderid.containsKey(Constants.rejected_status)) {

                                itemwiseTotalHashmap = new HashMap<>();
                                orderItemsbasedOnOrderId = new ArrayList<>();
                                itemwiseTotalHashmapKeyList = new ArrayList<>();

                                boolean isHeadingLabelAdded = false;
                                List<String> orderidList = statuswiseOrderid.get(Constants.rejected_status);
                                if (orderidList.size() > 0) {

                                    for (int iterator = 0; iterator < orderidList.size(); iterator++) {
                                        String orderidFromHashmap = orderidList.get(iterator);

                                        orderItemsbasedOnOrderId = new ArrayList<>(Objects.requireNonNull(orderwiseOrderItemDetails_statuswisereport.get(orderidFromHashmap)));

                                        // orderItemsbasedOnOrderId.addAll(Objects.requireNonNull(orderwiseOrderItemDetails_statuswisereport.get(orderidFromHashmap)));
                                        if (!orderItemsbasedOnOrderId.isEmpty()) {

                                            if (!isHeadingLabelAdded){
                                                isHeadingLabelAdded = true;


                                                PdfPTable statusLabel_table = new PdfPTable(new float[]{100});

                                                Phrase statusLabel_Title = new Phrase(Constants.rejected_status, valueFont_10);

                                                PdfPCell statusLabel_Titlecell = new PdfPCell(statusLabel_Title);
                                                statusLabel_Titlecell.setBorder(Rectangle.NO_BORDER);
                                                statusLabel_Titlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                statusLabel_Titlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                statusLabel_Titlecell.setBorderWidthRight(01);
                                                statusLabel_Titlecell.setPaddingLeft(6);
                                                statusLabel_Titlecell.setPaddingBottom(10);
                                                statusLabel_table.addCell(statusLabel_Titlecell);


                                                try {
                                                    PdfPCell itemDetails_table_Cell = new PdfPCell(statusLabel_table);
                                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                    itemDetails_table_Cell.setBorderWidthTop(1);
                                                    itemDetails_table_Cell.setBackgroundColor(customGreyColor);
                                                    itemDetails_table_Cell.setBorderWidthBottom(01);
                                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                                try {
                                                    PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                                    try {

                                                        Phrase phrasCtgynameLabelTitle = new Phrase(" S.No  ", valueFont_10);

                                                        PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                                        phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                        phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                                        phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                                        phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                                        itemDetailsLabel_table.addCell(phraseCtgyNameLabelTitlecell);


                                                        Phrase phrasMaleCountLabelTitle = new Phrase("Item Name", valueFont_10);

                                                        PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                                        phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                                        phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                                        phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                                        itemDetailsLabel_table.addCell(phraseMaleCountLabelTitlecell);


                                                        Phrase phrasFemaleCountLabelTitle = new Phrase("Quantity", valueFont_10);

                                                        PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                                        phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                                        phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                                        phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                                        itemDetailsLabel_table.addCell(phraseFemaleCountLabelTitlecell);


                                                        Phrase phrasTotalCountLabelTitle = new Phrase("Weight", valueFont_10);

                                                        PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                                        phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseCountLabelTitlecell.setPaddingLeft(6);
                                                        phraseCountLabelTitlecell.setPaddingBottom(10);
                                                        phraseCountLabelTitlecell.setBorderWidthRight(01);

                                                        itemDetailsLabel_table.addCell(phraseCountLabelTitlecell);


                                                        Phrase phraseWeightLabelTitle = new Phrase("Price", valueFont_10);

                                                        PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                                        phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseWeightLabelTitlecell.setPaddingLeft(6);
                                                        phraseWeightLabelTitlecell.setPaddingBottom(10);
                                                        phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                                        itemDetailsLabel_table.addCell(phraseWeightLabelTitlecell);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    try {
                                                        PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                        itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                        itemDetails_table_Cell.setBorderWidthTop(1);
                                                        itemDetails_table_Cell.setBackgroundColor(customGreyColor);
                                                        itemDetails_table_Cell.setBorderWidthBottom(01);
                                                        wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                            }



                                            try {
                                                for (int iteratr = 0; iteratr < orderItemsbasedOnOrderId.size(); iteratr++) {
                                                    OrderItemDetails_Model orderItemDetailsModel = orderItemsbasedOnOrderId.get(iteratr);

                                                    if (itemwiseTotalHashmap.containsKey(orderItemDetailsModel.getMenuitemkey())) {
                                                        ReportsItemwiseCalculationDetail_Model modelFromHashmap = itemwiseTotalHashmap.get(orderItemDetailsModel.getMenuitemkey());

                                                        int quantityFromHashmap = 0, quantityFromItemArray = 0;
                                                        double weightFromHashmap = 0, weightFromItemArray = 0, priceFromHashmap = 0, priceFromItemArray = 0;

                                                        quantityFromHashmap = Objects.requireNonNull(modelFromHashmap).getTotalquantity();
                                                        weightFromHashmap = modelFromHashmap.getTotalweight();
                                                        priceFromHashmap = modelFromHashmap.getTotalprice();
                                                        quantityFromItemArray = orderItemDetailsModel.getQuantity();
                                                        weightFromItemArray = orderItemDetailsModel.getGrossweight();
                                                        priceFromItemArray = orderItemDetailsModel.getTotalprice();

                                                        try {
                                                            modelFromHashmap.setTotalweight(weightFromHashmap + weightFromItemArray);
                                                            modelFromHashmap.setTotalquantity(quantityFromHashmap + quantityFromItemArray);
                                                            modelFromHashmap.setTotalprice(priceFromHashmap + priceFromItemArray);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }


                                                    } else {
                                                        ReportsItemwiseCalculationDetail_Model modelFromHashmap = new ReportsItemwiseCalculationDetail_Model();

                                                        modelFromHashmap.setMenuitemkey(orderItemDetailsModel.getMenuitemkey());
                                                        modelFromHashmap.setTotalprice(orderItemDetailsModel.getTotalprice());
                                                        modelFromHashmap.setMenuitemname(orderItemDetailsModel.getItemname());
                                                        modelFromHashmap.setTotalquantity(orderItemDetailsModel.getQuantity());
                                                        modelFromHashmap.setTotalweight(orderItemDetailsModel.getGrossweight());
                                                        itemwiseTotalHashmapKeyList.add(orderItemDetailsModel.getMenuitemkey());
                                                        itemwiseTotalHashmap.put(orderItemDetailsModel.getMenuitemkey(), modelFromHashmap);


                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();

                                            }


                                        } else {
                                            orderItemsbasedOnOrderId = new ArrayList<>();

                                        }


                                    }
                                }


                            }
                            if (itemwiseTotalHashmapKeyList.size() > 8) {
                                try {


                                    for (int iterator = 0; iterator < 9; iterator++) {
                                        String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                                        try {

                                            //20, 30 , 12 ,10, 12 , 16
                                            //19, 18, 15, 15, 15, 18

                                            PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});


                                            try {
                                                Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator + 1), valueFont_10Bold);

                                                PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                                phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);


                                                Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);
                                                PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                                phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);


                                                Phrase phrasQuantityLabelTitle = null;
                                                phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                                PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                                phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);


                                                phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                                phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                                phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);


                                              //  Phrase phrasBatchpriceLabelTitle = null;
                                              //  phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);

                                                Phrase phrasBatchpriceLabelTitle = null;
                                                if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                }
                                                else{
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                                }

                                                PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                                phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                                phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                                phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);


                                                Phrase phrasNotesLabelTitle = null;

                                                phrasNotesLabelTitle = new Phrase((" ₹ " + String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                                PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                                phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setPaddingLeft(6);

                                                phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                                phraseNotesLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                            try {
                                                PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                itemDetails_table_Cell.setBorderWidthBottom(1);
                                                //itemDetails_table_Cell.setBackgroundColor(WHITE);
                                                wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                               // tmcLogoImage_table.setHorizontalAlignment(Element.ALIGN_RIGHT);
                               // tmcLogoImage_table.setTotalWidth(10f);
                               // layoutDocument.add(tmcLogoImage_table);

                                PdfPCell wholePDFWithOutBordercell = new PdfPCell(wholePDFContentWithOut_Outline_table);
                                wholePDFWithOutBordercell.setCellEvent(roundRectange);
                                wholePDFWithOutBordercell.setPadding(1);
                                wholePDFWithOutBordercell.setBorder(Rectangle.NO_BORDER);
                                wholePDFContentOutline_table.addCell(wholePDFWithOutBordercell);
                                wholePDFContentOutline_table.setWidthPercentage(100);


                                layoutDocument.add(wholePDFContentOutline_table);
                                PdfPTable wholePDFContentWithOut_Outline_table2 = new PdfPTable(1);
                                try {
                                    layoutDocument.newPage();


                                    try {


                                        for (int iterator = 9; iterator < itemwiseTotalHashmapKeyList.size(); iterator++) {
                                            String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                                            try {


                                                PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});


                                                try {
                                                    Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator + 1), valueFont_10Bold);

                                                    PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                                    phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                    phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                                    phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                                    phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);


                                                    Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);

                                                    PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                                    phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                    phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                                    phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                                    phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);


                                                    Phrase phrasQuantityLabelTitle = null;
                                                    phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                                    PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                                    phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                    phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);


                                                    phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                                    phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                                    phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);


                                                  //  Phrase phrasBatchpriceLabelTitle = null;
                                                    //phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                    Phrase phrasBatchpriceLabelTitle = null;
                                                    if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                                        phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                    }
                                                    else{
                                                        phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                                    }

                                                    PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                                    phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                    phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                                    phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                                    phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);


                                                    Phrase phrasNotesLabelTitle = null;

                                                    phrasNotesLabelTitle = new Phrase((" ₹ " + String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                                    PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                                    phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                    phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseNotesLabelTitlecell.setPaddingLeft(6);

                                                    phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                                    phraseNotesLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                    itemDetails_table_Cell.setBorderWidthBottom(1);
                                                    // itemDetails_table_Cell.setBackgroundColor(WHITE);
                                                    wholePDFContentWithOut_Outline_table2.addCell(itemDetails_table_Cell);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }





                                    //finaladd
                                    try {


                                        PdfPTable wholePDFContentOutline_table2 = new PdfPTable(1);


                                        PdfPCell wholePDFWithOutBordercell2 = new PdfPCell(wholePDFContentWithOut_Outline_table2);
                                        wholePDFWithOutBordercell2.setCellEvent(roundRectange);
                                        wholePDFWithOutBordercell2.setPadding(1);
                                        wholePDFWithOutBordercell2.setBorder(Rectangle.NO_BORDER);
                                        wholePDFContentOutline_table2.addCell(wholePDFWithOutBordercell2);
                                        wholePDFContentOutline_table2.setWidthPercentage(100);


                                        layoutDocument.add(wholePDFContentOutline_table2);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                            else {
                                try {
                                    for (int iterator = 0; iterator < itemwiseTotalHashmapKeyList.size(); iterator++) {
                                        String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                                        try {

                                            //20, 30 , 12 ,10, 12 , 16
                                            //19, 18, 15, 15, 15, 18

                                            PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});


                                            try {
                                                Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator + 1), valueFont_10Bold);

                                                PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                                phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);


                                                Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);

                                                PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                                phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);


                                                Phrase phrasQuantityLabelTitle = null;
                                                phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                                PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                                phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);


                                                phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                                phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                                phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);


                                               // Phrase phrasBatchpriceLabelTitle = null;
                                               // phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10)
                                                Phrase phrasBatchpriceLabelTitle = null;
                                                if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                }
                                                else{
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                                }

                                                PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                                phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                                phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                                phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);


                                                Phrase phrasNotesLabelTitle = null;

                                                phrasNotesLabelTitle = new Phrase((" ₹ " + String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                                PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                                phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setPaddingLeft(6);

                                                phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                                phraseNotesLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                            try {
                                                PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                itemDetails_table_Cell.setBorderWidthBottom(1);
                                                // itemDetails_table_Cell.setBackgroundColor(WHITE);
                                                wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }




                            }


                            try {
                                PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                try {

                                    Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                    phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                    Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                    phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                    Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                    phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                    Phrase phrasTotalCountLabelTitle = new Phrase("Total   ", valueFont_10Bold);

                                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setPaddingLeft(6);
                                    phraseCountLabelTitlecell.setPaddingBottom(10);
                                    phraseCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                    Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(statuswisetotalcountdetailsjson.get(Constants.rejected_status).getDouble("pricebeforediscount")), valueFont_10);

                                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                                    phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                    itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                   // itemDetails_table_Cell.setBorderWidthTop(1);

                                    itemDetails_table_Cell.setBorderWidthBottom(01);
                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }


                            try {
                                PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                try {

                                    Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                    phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                    Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                    phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                    Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                    phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                    Phrase phrasTotalCountLabelTitle = new Phrase("Discount   ", valueFont_10Bold);

                                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setPaddingLeft(6);
                                    phraseCountLabelTitlecell.setPaddingBottom(10);
                                    phraseCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                    Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(statuswisetotalcountdetailsjson.get(Constants.rejected_status).getDouble("discount")), valueFont_10);

                                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                                    phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                    itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                  //  itemDetails_table_Cell.setBorderWidthTop(1);

                                    //itemDetails_table_Cell.setBorderWidthBottom(01);
                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            try {
                                PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                try {

                                    Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                    phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                    Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                    phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                    Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                    phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                    Phrase phrasTotalCountLabelTitle = new Phrase("Final price   ", valueFont_10Bold);

                                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setPaddingLeft(6);
                                    phraseCountLabelTitlecell.setPaddingBottom(10);
                                    phraseCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                    Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(statuswisetotalcountdetailsjson.get(Constants.rejected_status).getDouble("price")), valueFont_10);

                                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                                    phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                    itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                    itemDetails_table_Cell.setBorderWidthTop(1);

                                    itemDetails_table_Cell.setBorderWidthBottom(01);
                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                try {

                                    Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                     phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                    Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                     phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                    Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);

                                    itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                    Phrase phrasTotalCountLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setPaddingLeft(6);
                                    phraseCountLabelTitlecell.setPaddingBottom(10);

                                    itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                    Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(""), valueFont_10);

                                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                                     itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);

                                     wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                        if (statuswisetotalcountdetailsjson.containsKey(Constants.cancelled_status)) {
                            if (statuswiseOrderid.containsKey(Constants.cancelled_status)) {

                                itemwiseTotalHashmap = new HashMap<>();
                                orderItemsbasedOnOrderId = new ArrayList<>();
                                itemwiseTotalHashmapKeyList = new ArrayList<>();
                                boolean isHeadingLabelAdded = false;
                                List<String> orderidList = statuswiseOrderid.get(Constants.cancelled_status);
                                if (orderidList.size() > 0) {

                                    for (int iterator = 0; iterator < orderidList.size(); iterator++) {
                                        String orderidFromHashmap = orderidList.get(iterator);

                                        orderItemsbasedOnOrderId = new ArrayList<>(Objects.requireNonNull(orderwiseOrderItemDetails_statuswisereport.get(orderidFromHashmap)));

                                        // orderItemsbasedOnOrderId.addAll(Objects.requireNonNull(orderwiseOrderItemDetails_statuswisereport.get(orderidFromHashmap)));
                                        if (!orderItemsbasedOnOrderId.isEmpty()) {

                                            if (!isHeadingLabelAdded){
                                                isHeadingLabelAdded = true;

                                                PdfPTable statusLabel_table = new PdfPTable(new float[]{100});

                                                Phrase statusLabel_Title = new Phrase(Constants.cancelled_status, valueFont_10);

                                                PdfPCell statusLabel_Titlecell = new PdfPCell(statusLabel_Title);
                                                statusLabel_Titlecell.setBorder(Rectangle.NO_BORDER);
                                                statusLabel_Titlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                statusLabel_Titlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                statusLabel_Titlecell.setBorderWidthRight(01);
                                                statusLabel_Titlecell.setPaddingLeft(6);
                                                statusLabel_Titlecell.setPaddingBottom(10);
                                                statusLabel_table.addCell(statusLabel_Titlecell);


                                                try {
                                                    PdfPCell itemDetails_table_Cell = new PdfPCell(statusLabel_table);
                                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                    itemDetails_table_Cell.setBorderWidthTop(1);
                                                    itemDetails_table_Cell.setBackgroundColor(customGreyColor);
                                                    itemDetails_table_Cell.setBorderWidthBottom(01);
                                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                                try {
                                                    PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                                    try {

                                                        Phrase phrasCtgynameLabelTitle = new Phrase(" S.No  ", valueFont_10);

                                                        PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                                        phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                        phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                                        phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                                        phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                                        itemDetailsLabel_table.addCell(phraseCtgyNameLabelTitlecell);


                                                        Phrase phrasMaleCountLabelTitle = new Phrase("Item Name", valueFont_10);

                                                        PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                                        phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                                        phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                                        phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                                        itemDetailsLabel_table.addCell(phraseMaleCountLabelTitlecell);


                                                        Phrase phrasFemaleCountLabelTitle = new Phrase("Quantity", valueFont_10);

                                                        PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                                        phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                                        phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                                        phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                                        itemDetailsLabel_table.addCell(phraseFemaleCountLabelTitlecell);


                                                        Phrase phrasTotalCountLabelTitle = new Phrase("Weight", valueFont_10);

                                                        PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                                        phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseCountLabelTitlecell.setPaddingLeft(6);
                                                        phraseCountLabelTitlecell.setPaddingBottom(10);
                                                        phraseCountLabelTitlecell.setBorderWidthRight(01);

                                                        itemDetailsLabel_table.addCell(phraseCountLabelTitlecell);


                                                        Phrase phraseWeightLabelTitle = new Phrase("Price", valueFont_10);

                                                        PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                                        phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                        phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                        phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                        phraseWeightLabelTitlecell.setPaddingLeft(6);
                                                        phraseWeightLabelTitlecell.setPaddingBottom(10);
                                                        phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                                        itemDetailsLabel_table.addCell(phraseWeightLabelTitlecell);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    try {
                                                        PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                        itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                        itemDetails_table_Cell.setBorderWidthTop(1);
                                                        itemDetails_table_Cell.setBackgroundColor(customGreyColor);
                                                        itemDetails_table_Cell.setBorderWidthBottom(01);
                                                        wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                            }



                                            try {
                                                for (int iteratr = 0; iteratr < orderItemsbasedOnOrderId.size(); iteratr++) {
                                                    OrderItemDetails_Model orderItemDetailsModel = orderItemsbasedOnOrderId.get(iteratr);

                                                    if (itemwiseTotalHashmap.containsKey(orderItemDetailsModel.getMenuitemkey())) {
                                                        ReportsItemwiseCalculationDetail_Model modelFromHashmap = itemwiseTotalHashmap.get(orderItemDetailsModel.getMenuitemkey());

                                                        int quantityFromHashmap = 0, quantityFromItemArray = 0;
                                                        double weightFromHashmap = 0, weightFromItemArray = 0, priceFromHashmap = 0, priceFromItemArray = 0;

                                                        quantityFromHashmap = Objects.requireNonNull(modelFromHashmap).getTotalquantity();
                                                        weightFromHashmap = modelFromHashmap.getTotalweight();
                                                        priceFromHashmap = modelFromHashmap.getTotalprice();
                                                        quantityFromItemArray = orderItemDetailsModel.getQuantity();
                                                        weightFromItemArray = orderItemDetailsModel.getGrossweight();
                                                        priceFromItemArray = orderItemDetailsModel.getTotalprice();

                                                        try {
                                                            modelFromHashmap.setTotalweight(weightFromHashmap + weightFromItemArray);
                                                            modelFromHashmap.setTotalquantity(quantityFromHashmap + quantityFromItemArray);
                                                            modelFromHashmap.setTotalprice(priceFromHashmap + priceFromItemArray);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }


                                                    } else {
                                                        ReportsItemwiseCalculationDetail_Model modelFromHashmap = new ReportsItemwiseCalculationDetail_Model();

                                                        modelFromHashmap.setMenuitemkey(orderItemDetailsModel.getMenuitemkey());
                                                        modelFromHashmap.setTotalprice(orderItemDetailsModel.getTotalprice());
                                                        modelFromHashmap.setMenuitemname(orderItemDetailsModel.getItemname());
                                                        modelFromHashmap.setTotalquantity(orderItemDetailsModel.getQuantity());
                                                        modelFromHashmap.setTotalweight(orderItemDetailsModel.getGrossweight());
                                                        itemwiseTotalHashmapKeyList.add(orderItemDetailsModel.getMenuitemkey());
                                                        itemwiseTotalHashmap.put(orderItemDetailsModel.getMenuitemkey(), modelFromHashmap);


                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();

                                            }


                                        } else {
                                            orderItemsbasedOnOrderId = new ArrayList<>();

                                        }


                                    }
                                }


                            }
                            if (itemwiseTotalHashmapKeyList.size() > 8) {
                                try {


                                    for (int iterator = 0; iterator < 9; iterator++) {
                                        String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                                        try {

                                            //20, 30 , 12 ,10, 12 , 16
                                            //19, 18, 15, 15, 15, 18

                                            PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});


                                            try {
                                                Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator + 1), valueFont_10Bold);

                                                PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                                phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);


                                                Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);
                                                PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                                phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);


                                                Phrase phrasQuantityLabelTitle = null;
                                                phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                                PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                                phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);


                                                phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                                phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                                phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);


                                               // Phrase phrasBatchpriceLabelTitle = null;
                                               // phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                Phrase phrasBatchpriceLabelTitle = null;
                                                if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                }
                                                else{
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                                }

                                                PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                                phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                                phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                                phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);


                                                Phrase phrasNotesLabelTitle = null;

                                                phrasNotesLabelTitle = new Phrase((" ₹ " + String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                                PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                                phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setPaddingLeft(6);

                                                phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                                phraseNotesLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                            try {
                                                PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                itemDetails_table_Cell.setBorderWidthBottom(1);
                                                //itemDetails_table_Cell.setBackgroundColor(WHITE);
                                                wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                               // tmcLogoImage_table.setHorizontalAlignment(Element.ALIGN_RIGHT);
                               // tmcLogoImage_table.setTotalWidth(10f);
                               // layoutDocument.add(tmcLogoImage_table);

                                PdfPCell wholePDFWithOutBordercell = new PdfPCell(wholePDFContentWithOut_Outline_table);
                                wholePDFWithOutBordercell.setCellEvent(roundRectange);
                                wholePDFWithOutBordercell.setPadding(1);
                                wholePDFWithOutBordercell.setBorder(Rectangle.NO_BORDER);
                                wholePDFContentOutline_table.addCell(wholePDFWithOutBordercell);
                                wholePDFContentOutline_table.setWidthPercentage(100);


                                layoutDocument.add(wholePDFContentOutline_table);
                                PdfPTable wholePDFContentWithOut_Outline_table2 = new PdfPTable(1);
                                try {
                                    layoutDocument.newPage();


                                    try {


                                        for (int iterator = 9; iterator < itemwiseTotalHashmapKeyList.size(); iterator++) {
                                            String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                                            try {


                                                PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});


                                                try {
                                                    Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator + 1), valueFont_10Bold);

                                                    PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                                    phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                    phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                                    phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                                    phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);


                                                    Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);

                                                    PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                                    phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                    phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                                    phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                                    phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);


                                                    Phrase phrasQuantityLabelTitle = null;
                                                    phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                                    PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                                    phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                    phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);


                                                    phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                                    phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                                    phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);


                                                  //  Phrase phrasBatchpriceLabelTitle = null;
                                                    //phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                    Phrase phrasBatchpriceLabelTitle = null;
                                                    if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                                        phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                    }
                                                    else{
                                                        phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                                    }

                                                    PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                                    phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                    phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                                    phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                                    phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);


                                                    Phrase phrasNotesLabelTitle = null;

                                                    phrasNotesLabelTitle = new Phrase((" ₹ " + String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                                    PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                                    phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                    phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                    phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                    phraseNotesLabelTitlecell.setPaddingLeft(6);

                                                    phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                                    phraseNotesLabelTitlecell.setPaddingBottom(10);
                                                    itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                    itemDetails_table_Cell.setBorderWidthBottom(1);
                                                    // itemDetails_table_Cell.setBackgroundColor(WHITE);
                                                    wholePDFContentWithOut_Outline_table2.addCell(itemDetails_table_Cell);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    //finaladd
                                    try {


                                        PdfPTable wholePDFContentOutline_table2 = new PdfPTable(1);


                                        PdfPCell wholePDFWithOutBordercell2 = new PdfPCell(wholePDFContentWithOut_Outline_table2);
                                        wholePDFWithOutBordercell2.setCellEvent(roundRectange);
                                        wholePDFWithOutBordercell2.setPadding(1);
                                        wholePDFWithOutBordercell2.setBorder(Rectangle.NO_BORDER);
                                        wholePDFContentOutline_table2.addCell(wholePDFWithOutBordercell2);
                                        wholePDFContentOutline_table2.setWidthPercentage(100);


                                        layoutDocument.add(wholePDFContentOutline_table2);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                            else {
                                try {
                                    for (int iterator = 0; iterator < itemwiseTotalHashmapKeyList.size(); iterator++) {
                                        String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                                        try {

                                            //20, 30 , 12 ,10, 12 , 16
                                            //19, 18, 15, 15, 15, 18

                                            PdfPTable itemDetailsLabel_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});


                                            try {
                                                Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator + 1), valueFont_10Bold);

                                                PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                                phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);


                                                Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);

                                                PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                                phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                                phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                                phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);


                                                Phrase phrasQuantityLabelTitle = null;
                                                phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                                PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                                phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);


                                                phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                                phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                                phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);


                                              //  Phrase phrasBatchpriceLabelTitle = null;
                                               // phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                Phrase phrasBatchpriceLabelTitle = null;
                                                if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                                }
                                                else{
                                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                                }

                                                PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                                phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                                phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                                phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);


                                                Phrase phrasNotesLabelTitle = null;

                                                phrasNotesLabelTitle = new Phrase((" ₹ " + String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                                PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                                phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                                phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                                phraseNotesLabelTitlecell.setPaddingLeft(6);

                                                phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                                phraseNotesLabelTitlecell.setPaddingBottom(10);
                                                itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                            try {
                                                PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                                itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                                itemDetails_table_Cell.setBorderWidthBottom(1);
                                                // itemDetails_table_Cell.setBackgroundColor(WHITE);
                                                wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }


                            try {
                                PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                try {

                                    Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                    phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                    Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                    phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                    Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                    phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                    Phrase phrasTotalCountLabelTitle = new Phrase("Total   ", valueFont_10Bold);

                                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setPaddingLeft(6);
                                    phraseCountLabelTitlecell.setPaddingBottom(10);
                                    phraseCountLabelTitlecell.setBorderWidthRight(01);

                                    itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                    Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(statuswisetotalcountdetailsjson.get(Constants.cancelled_status).getDouble("price")), valueFont_10);

                                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                                    phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                    itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);


                                    itemDetails_table_Cell.setBorderWidthBottom(01);
                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }


                        try {
                            PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                            try {

                                Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                Phrase phrasTotalCountLabelTitle = new Phrase("Discount   ", valueFont_10Bold);

                                PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                phraseCountLabelTitlecell.setPaddingLeft(6);
                                phraseCountLabelTitlecell.setPaddingBottom(10);
                                phraseCountLabelTitlecell.setBorderWidthRight(01);

                                itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(statuswisetotalcountdetailsjson.get(Constants.cancelled_status).getDouble("discount")), valueFont_10);

                                PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                phraseWeightLabelTitlecell.setPaddingLeft(6);
                                phraseWeightLabelTitlecell.setPaddingBottom(10);
                                phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                //itemDetails_table_Cell.setBorderWidthTop(1);

                                //itemDetails_table_Cell.setBorderWidthBottom(01);
                                wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        try {
                            PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                            try {

                                Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                                phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                                phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                                phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                                itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                Phrase phrasTotalCountLabelTitle = new Phrase("Final price   ", valueFont_10Bold);

                                PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                phraseCountLabelTitlecell.setPaddingLeft(6);
                                phraseCountLabelTitlecell.setPaddingBottom(10);
                                phraseCountLabelTitlecell.setBorderWidthRight(01);

                                itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(statuswisetotalcountdetailsjson.get(Constants.cancelled_status).getDouble("price")), valueFont_10);

                                PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                phraseWeightLabelTitlecell.setPaddingLeft(6);
                                phraseWeightLabelTitlecell.setPaddingBottom(10);
                                phraseWeightLabelTitlecell.setBorderWidthRight(01);
                                itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                itemDetails_table_Cell.setBorderWidthTop(1);

                                itemDetails_table_Cell.setBorderWidthBottom(01);
                                wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }


                            try {
                                PdfPTable itemDetailstotal_table = new PdfPTable(new float[]{10, 30, 20, 20, 20});

                                try {

                                    Phrase phrasCtgynameLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                     phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseCtgyNameLabelTitlecell);


                                    Phrase phrasMaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                     phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                                    itemDetailstotal_table.addCell(phraseMaleCountLabelTitlecell);


                                    Phrase phrasFemaleCountLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);

                                    itemDetailstotal_table.addCell(phraseFemaleCountLabelTitlecell);


                                    Phrase phrasTotalCountLabelTitle = new Phrase("   ", valueFont_10);

                                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseCountLabelTitlecell.setPaddingLeft(6);
                                    phraseCountLabelTitlecell.setPaddingBottom(10);

                                    itemDetailstotal_table.addCell(phraseCountLabelTitlecell);


                                    Phrase phraseWeightLabelTitle = new Phrase("", valueFont_10);

                                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                                     itemDetailstotal_table.addCell(phraseWeightLabelTitlecell);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailstotal_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);

                                     wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }


                        }




                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                    //finaladd
                    try {

                       // tmcLogoImage_table.setHorizontalAlignment(Element.ALIGN_RIGHT);
                       // tmcLogoImage_table.setTotalWidth(10f);
                       // layoutDocument.add(tmcLogoImage_table);




                        PdfPCell wholePDFWithOutBordercell = new PdfPCell(wholePDFContentWithOut_Outline_table);
                        wholePDFWithOutBordercell.setCellEvent(roundRectange);
                        wholePDFWithOutBordercell.setPadding(1);
                        wholePDFWithOutBordercell.setBorder(Rectangle.NO_BORDER);
                        wholePDFContentOutline_table.addCell(wholePDFWithOutBordercell);
                        wholePDFContentOutline_table.setWidthPercentage(100);


                        layoutDocument.add(wholePDFContentOutline_table);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }




        }
        catch (Exception e){
            e.printStackTrace();
        }



    }

    private void CheckForPermission_And_CreatePdf() {

        if (SDK_INT >= 30) {
            try {
                if (!Environment.isExternalStorageManager()) {
                    Intent getpermission = new Intent();
                    getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    context.startActivity(getpermission);
                } else {
                    try {

                        prepareDataForPDF();


                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {

                if (SDK_INT >= Build.VERSION_CODES.R) {

                    if (Environment.isExternalStorageManager()) {
                        try {

                            prepareDataForPDF();


                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    } else {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setData(Uri.parse(String.format("package:%s", context.getPackageName())));
                            fragmentActivity.startActivityForResult(intent, 2296);
                        } catch (Exception e) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            fragmentActivity.startActivityForResult(intent, 2296);
                        }
                    }

                }
                else {


                    int writeExternalStoragePermission = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
                    //Log.d("ExportInvoiceActivity", "writeExternalStoragePermission "+writeExternalStoragePermission);
                    // If do not grant write external storage permission.
                    if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                        // Request user to grant write external storage permission.
                        ActivityCompat.requestPermissions(fragmentActivity, new String[]{WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
                    } else {

                        try {
                            prepareDataForPDF();


                        } catch (Exception e) {
                            e.printStackTrace();
                            ;
                        }
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }

    private void prepareDataForPDF() {

        try{
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            File folder = new File(path);
            //  File folder = new File(fol, "pdf");
            if (!folder.exists()) {
                boolean bool = folder.mkdirs();
            }
            try {
                String filename = "Categorywise sales details "+DateParserClass.getMillisecondsFromDate(DateParserClass.getDateInStandardFormat())+".pdf";



                file = new File(folder, filename);
                file.createNewFile();
                try {
                    FileOutputStream fOut = new FileOutputStream(file);
                    layoutDocument = new  Document();
                    PdfWriter.getInstance(layoutDocument, fOut);
                    layoutDocument.open();

                    roundRectange = new RoundRectangle();



                    if(pdfType.equals(Constants.datewiseConsolidatedPDF) || pdfType.equals(Constants.buyerwiseConsolidatedPDF)){
                        generateConsolidatedPDF(  );
                    }



                    layoutDocument.close();
                    listener.onPDFGenerated(file);

                } catch (Exception e) {
                    e.printStackTrace();
                }




                // }
            } catch (IOException e) {


                Log.i("error", e.getLocalizedMessage());
            } catch (Exception ex) {


                ex.printStackTrace();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    public  void generateConsolidatedPDF() {


        try{

            Font StoretitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
                    Font.BOLD);
            Font subtitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14,
                    Font.BOLD);

            Font valueFont_10Bold= new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.BOLD);
            Font valueFont_8Bold= new Font(Font.FontFamily.TIMES_ROMAN, 10,
                    Font.BOLD);

            Font valueFont_10= new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.NORMAL);
            Font valueFont_8= new Font(Font.FontFamily.TIMES_ROMAN, 10,
                    Font.NORMAL);
            Font valueFont_1= new Font(Font.FontFamily.TIMES_ROMAN, 1,
                    Font.NORMAL);

            BaseColor customBLueColor = new BaseColor(77, 25, 211); // Replace with your RGB values

            Font valueFont_15_BLueColor= new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.BOLD, customBLueColor);
            BaseColor customWhiteColor = new BaseColor(255, 255, 255); // Replace with your RGB values
            Font valueWhiteFont_10Bold= new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.BOLD,customWhiteColor);
            BaseColor customGreyColor = new BaseColor(180, 180, 180); // Replace with your RGB values
            BaseColor customPinkColor = new BaseColor(253, 192, 191);
            BaseColor customPalePurpleColor = new BaseColor(207, 191, 255);

            double totalAmount_PaidAmount =0  ,totalAmount_BillAmount = 0 ;
            String closingBalance = "0" , openingbalance ="0";
            RoundRectangle roundRectange = new RoundRectangle();
            PdfPTable wholePDFContentOutline_table = new PdfPTable(1);

            PdfPTable wholePDFContentWithImage_and_table = new PdfPTable(1);
            PdfPTable wholePDFContentWithOut_Outline_table = new PdfPTable(1);

            PdfPTable tmcLogoImage_table = new PdfPTable(new float[] { 50, 25 ,25 });


            try {
                PdfPCell table_Cell = new PdfPCell();
                table_Cell.setPaddingTop(5);
                table_Cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_Cell.setCalculatedHeight(5f);
                table_Cell.setVerticalAlignment(Element.ALIGN_RIGHT);
                table_Cell.setBorder(Rectangle.NO_BORDER);
                tmcLogoImage_table.addCell(table_Cell);

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                PdfPCell table_Cell = new PdfPCell();
                table_Cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_Cell.setCalculatedHeight(5f);
                table_Cell.setVerticalAlignment(Element.ALIGN_RIGHT);
                table_Cell.setBorder(Rectangle.NO_BORDER);
                tmcLogoImage_table.addCell(table_Cell);

            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                PdfPCell table_Cell = new PdfPCell(addLogo(layoutDocument));
                table_Cell.setBorder(Rectangle.NO_BORDER);
                table_Cell.setPaddingRight(10);
                table_Cell.setCalculatedHeight(5f);
                table_Cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_Cell.setVerticalAlignment(Element.ALIGN_RIGHT);
                tmcLogoImage_table.addCell(table_Cell);



            } catch (Exception e) {
                e.printStackTrace();
            }



            try{
                Phrase phrasebilltimeDetails = new Phrase(" "+'\n'+'\n'+" ", valueFont_8);
                PdfPCell phrasebilltimedetailscell = new PdfPCell(phrasebilltimeDetails);
                phrasebilltimedetailscell.setBorder(Rectangle.NO_BORDER);
                phrasebilltimedetailscell.setHorizontalAlignment(Element.ALIGN_LEFT);
                phrasebilltimedetailscell.setVerticalAlignment(Element.ALIGN_CENTER);
                phrasebilltimedetailscell.setPaddingLeft(10);
                phrasebilltimedetailscell.setPaddingBottom(6);
                tmcLogoImage_table.addCell(phrasebilltimedetailscell);
                try {
                    PdfPCell table_Cell = new PdfPCell();
                    table_Cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table_Cell.setCalculatedHeight(5f);
                    table_Cell.setVerticalAlignment(Element.ALIGN_RIGHT);
                    table_Cell.setBorder(Rectangle.NO_BORDER);
                    tmcLogoImage_table.addCell(table_Cell);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    PdfPCell table_Cell = new PdfPCell();
                    table_Cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table_Cell.setCalculatedHeight(5f);
                    table_Cell.setVerticalAlignment(Element.ALIGN_RIGHT);
                    table_Cell.setBorder(Rectangle.NO_BORDER);
                    tmcLogoImage_table.addCell(table_Cell);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }


            PdfPTable billtimeDetails_table = new PdfPTable(2);
            try {

                Phrase phrasebilltimeDetails = new Phrase("DATE : " + DateParserClass.getDateInStandardFormat() + "      TIME : " + DateParserClass.getTime_newFormat(), valueFont_8);
                PdfPCell phrasebilltimedetailscell = new PdfPCell(phrasebilltimeDetails);
                phrasebilltimedetailscell.setBorder(Rectangle.NO_BORDER);
                phrasebilltimedetailscell.setHorizontalAlignment(Element.ALIGN_LEFT);
                phrasebilltimedetailscell.setVerticalAlignment(Element.ALIGN_CENTER);
                phrasebilltimedetailscell.setPaddingLeft(10);
                phrasebilltimedetailscell.setPaddingBottom(6);
                billtimeDetails_table.addCell(phrasebilltimedetailscell);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                PdfPTable supervisorNameDetails_table = new PdfPTable(1);

                Phrase phraseSupervisorNameLabelTitle = new Phrase("Supervisor Name : " + String.valueOf(supervisorname) + "  ", valueFont_8Bold);

                PdfPCell phraseSupervisorNameLabelTitlecell = new PdfPCell(phraseSupervisorNameLabelTitle);
                phraseSupervisorNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                phraseSupervisorNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                phraseSupervisorNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_RIGHT);
                phraseSupervisorNameLabelTitlecell.setPaddingLeft(6);
                phraseSupervisorNameLabelTitlecell.setPaddingBottom(3);
                phraseSupervisorNameLabelTitlecell.setPaddingRight(25);

                supervisorNameDetails_table.addCell(phraseSupervisorNameLabelTitlecell);


                try {
                    PdfPCell supervisorDetails = new PdfPCell(supervisorNameDetails_table);

                    supervisorDetails.setBorder(Rectangle.NO_BORDER);

                    billtimeDetails_table.addCell(supervisorDetails);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                PdfPCell addBorder_billTimeDetails = new PdfPCell(billtimeDetails_table);
                addBorder_billTimeDetails.setBorder(Rectangle.NO_BORDER);
                addBorder_billTimeDetails.setPaddingTop(5);
                addBorder_billTimeDetails.setBorderWidthBottom(01);
                addBorder_billTimeDetails.setBorderColor(GRAY);


                wholePDFContentWithOut_Outline_table.addCell(addBorder_billTimeDetails);

            } catch (Exception e) {
                e.printStackTrace();
            }




            PdfPTable Whole_Warehouse_and_RetailerDetails_table = new PdfPTable(new float[] { 50, 50 });

            try{
                PdfPTable Whole_WarehouseDetails_table = new PdfPTable(1);

                try {


                    Phrase phrasecompanyDetailsTitle = new Phrase( sessionManager.getVendorname(), subtitleFont);

                    PdfPCell phrasecompanyDetailsTitlecell = new PdfPCell(phrasecompanyDetailsTitle);
                    phrasecompanyDetailsTitlecell.setBorder(Rectangle.NO_BORDER);
                    phrasecompanyDetailsTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    phrasecompanyDetailsTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phrasecompanyDetailsTitlecell.setPaddingBottom(4);
                    phrasecompanyDetailsTitlecell.setPaddingLeft(6);
                    Whole_WarehouseDetails_table.addCell(phrasecompanyDetailsTitlecell);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                  //  Phrase phrasecompanyAddressDetails = new Phrase("23/33, Chidambaram Street, \n Balaji Nagar Rd , Subash Nagar, Chromepet,\nChennai – 44 ,India.\nGSTIN 09,", valueFont_10);
                    Phrase phrasecompanyAddressDetails = new Phrase("60, Great Cotton Road , \nTuticorin ( Thoothukudi ) , . \nTamilnadu - 628001 , India . \nGSTIN : 33AJTPS1766K1ZI", valueFont_10);

                    PdfPCell phrasecompanyAddressDetailscell = new PdfPCell(phrasecompanyAddressDetails);
                    phrasecompanyAddressDetailscell.setBorder(Rectangle.NO_BORDER);
                    phrasecompanyAddressDetailscell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    phrasecompanyAddressDetailscell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phrasecompanyAddressDetailscell.setPaddingBottom(10);
                    phrasecompanyAddressDetailscell.setPaddingLeft(6);

                    Whole_WarehouseDetails_table.addCell(phrasecompanyAddressDetailscell);

                } catch (Exception e) {
                    e.printStackTrace();
                }





                PdfPTable Whole_SupplerDetails_table = new PdfPTable(new float[] { 100  });
                try{



                    try {

                            Phrase phraseretailerNameLabelTitle = new Phrase("Filter Details", valueFont_10Bold);

                            PdfPCell phraseretailerNameLabelTitlecell = new PdfPCell(phraseretailerNameLabelTitle);
                            phraseretailerNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                            phraseretailerNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            phraseretailerNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                            phraseretailerNameLabelTitlecell.setPaddingLeft(6);
                            phraseretailerNameLabelTitlecell.setPaddingBottom(10);
                            Whole_SupplerDetails_table.addCell(phraseretailerNameLabelTitlecell);





                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    try {

                        Phrase phraseretailerNameLabelTitle = new Phrase("Filter Type : "+selectedFilterValue.getSelectedFilterType() +" - Accepted Orders"+"\n", valueFont_10Bold);

                        PdfPCell phraseretailerNameLabelTitlecell = new PdfPCell(phraseretailerNameLabelTitle);
                        phraseretailerNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                        phraseretailerNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        phraseretailerNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                        phraseretailerNameLabelTitlecell.setPaddingLeft(6);
                        phraseretailerNameLabelTitlecell.setPaddingBottom(10);
                        Whole_SupplerDetails_table.addCell(phraseretailerNameLabelTitlecell);



                    } catch (Exception e) {
                        e.printStackTrace();
                    }




                    try {

                        try {
                            if (selectedFilterValue.getSelectedFilterType().equals(Constants.today_filter)) {
                                Phrase filterTitle = new Phrase(Constants.today_filter+" : "+"\n", valueFont_10Bold);
                                PdfPCell filterTitlecell = new PdfPCell(filterTitle);
                                filterTitlecell.setBorder(Rectangle.NO_BORDER);
                                filterTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                filterTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                filterTitlecell.setPaddingLeft(6);
                                filterTitlecell.setPaddingBottom(10);
                            //    Whole_SupplerDetails_table.addCell(filterTitlecell);


                                Phrase dateTitle = new Phrase("Date : "+String.valueOf(DateParserClass.convertGivenTimeStampToDate(selectedFilterValue.getStartDate()))+"\n", valueFont_10Bold);
                                PdfPCell dateTitlecell = new PdfPCell(dateTitle);
                                dateTitlecell.setBorder(Rectangle.NO_BORDER);
                                dateTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                dateTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                dateTitlecell.setPaddingLeft(6);
                                dateTitlecell.setPaddingBottom(10);
                                Whole_SupplerDetails_table.addCell(dateTitlecell);



                            }
                            else if (selectedFilterValue.getSelectedFilterType().equals(Constants.yesterday_filter)) {
                                Phrase filterTitle = new Phrase(Constants.yesterday_filter+" : "+"\n", valueFont_10Bold);
                                PdfPCell filterTitlecell = new PdfPCell(filterTitle);
                                filterTitlecell.setBorder(Rectangle.NO_BORDER);
                                filterTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                filterTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                filterTitlecell.setPaddingLeft(6);
                                filterTitlecell.setPaddingBottom(10);
                         //       Whole_SupplerDetails_table.addCell(filterTitlecell);


                                Phrase dateTitle = new Phrase("Date : "+String.valueOf(DateParserClass.convertGivenTimeStampToDate(selectedFilterValue.getStartDate()))+"\n", valueFont_10Bold);
                                PdfPCell dateTitlecell = new PdfPCell(dateTitle);
                                dateTitlecell.setBorder(Rectangle.NO_BORDER);
                                dateTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                dateTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                dateTitlecell.setPaddingLeft(6);
                                dateTitlecell.setPaddingBottom(10);
                                Whole_SupplerDetails_table.addCell(dateTitlecell);



                            }
                            else if (selectedFilterValue.getSelectedFilterType().equals(Constants.last7day_filter)) {

                                Phrase filterTitle = new Phrase(Constants.last7day_filter+" : "+"\n", valueFont_10Bold);
                                PdfPCell filterTitlecell = new PdfPCell(filterTitle);
                                filterTitlecell.setBorder(Rectangle.NO_BORDER);
                                filterTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                filterTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                filterTitlecell.setPaddingLeft(6);
                                filterTitlecell.setPaddingBottom(10);
                              //  Whole_SupplerDetails_table.addCell(filterTitlecell);


                                Phrase dateTitle = new Phrase("From Date : "+String.valueOf(DateParserClass.convertGivenTimeStampToDate(selectedFilterValue.getStartDate()))+"\n", valueFont_10Bold);
                                PdfPCell dateTitlecell = new PdfPCell(dateTitle);
                                dateTitlecell.setBorder(Rectangle.NO_BORDER);
                                dateTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                dateTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                dateTitlecell.setPaddingLeft(6);
                                dateTitlecell.setPaddingBottom(10);
                                Whole_SupplerDetails_table.addCell(dateTitlecell);


                                Phrase todateTitle = new Phrase("To Date : "+String.valueOf(DateParserClass.convertGivenTimeStampToDate(selectedFilterValue.getEndDate()))+"\n", valueFont_10Bold);
                                PdfPCell todateTitlecell = new PdfPCell(todateTitle);
                                todateTitlecell.setBorder(Rectangle.NO_BORDER);
                                todateTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                todateTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                todateTitlecell.setPaddingLeft(6);
                                todateTitlecell.setPaddingBottom(10);
                                Whole_SupplerDetails_table.addCell(todateTitlecell);



                            }
                            else if (selectedFilterValue.getSelectedFilterType().equals(Constants.buyerwise_filter)) {

                                Phrase filterTitle = new Phrase(Constants.buyerwise_filter+" : "+"\n", valueFont_10Bold);
                                PdfPCell filterTitlecell = new PdfPCell(filterTitle);
                                filterTitlecell.setBorder(Rectangle.NO_BORDER);
                                filterTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                filterTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                filterTitlecell.setPaddingLeft(6);
                                filterTitlecell.setPaddingBottom(10);
                             //   Whole_SupplerDetails_table.addCell(filterTitlecell);


                                Phrase buyerTitle = new Phrase("Selected Buyer : "+String.valueOf(String.valueOf(selectedFilterValue.getSelectedBuyerName()))+"\n", valueFont_10Bold);
                                PdfPCell buyerTitlecell = new PdfPCell(buyerTitle);
                                buyerTitlecell.setBorder(Rectangle.NO_BORDER);
                                buyerTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                buyerTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                buyerTitlecell.setPaddingLeft(6);
                                buyerTitlecell.setPaddingBottom(10);
                                Whole_SupplerDetails_table.addCell(buyerTitlecell);




                                Phrase dateTitle = new Phrase("From Date : "+String.valueOf(DateParserClass.convertGivenTimeStampToDate(selectedFilterValue.getStartDate()))+"\n", valueFont_10Bold);
                                PdfPCell dateTitlecell = new PdfPCell(dateTitle);
                                dateTitlecell.setBorder(Rectangle.NO_BORDER);
                                dateTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                dateTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                dateTitlecell.setPaddingLeft(6);
                                dateTitlecell.setPaddingBottom(10);
                                Whole_SupplerDetails_table.addCell(dateTitlecell);


                                Phrase todateTitle = new Phrase("To Date : "+String.valueOf(DateParserClass.convertGivenTimeStampToDate(selectedFilterValue.getEndDate()))+"\n", valueFont_10Bold);
                                PdfPCell todateTitlecell = new PdfPCell(todateTitle);
                                todateTitlecell.setBorder(Rectangle.NO_BORDER);
                                todateTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                todateTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                todateTitlecell.setPaddingLeft(6);
                                todateTitlecell.setPaddingBottom(10);
                                Whole_SupplerDetails_table.addCell(todateTitlecell);




                            }
                            else if (selectedFilterValue.getSelectedFilterType().equals(Constants.customdatewise_filter)) {



                                Phrase filterTitle = new Phrase(Constants.customdatewise_filter+" : "+"\n", valueFont_10Bold);
                                PdfPCell filterTitlecell = new PdfPCell(filterTitle);
                                filterTitlecell.setBorder(Rectangle.NO_BORDER);
                                filterTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                filterTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                filterTitlecell.setPaddingLeft(6);
                                filterTitlecell.setPaddingBottom(10);
                               // Whole_SupplerDetails_table.addCell(filterTitlecell);



                                Phrase dateTitle = new Phrase("From Date : "+String.valueOf(DateParserClass.convertGivenTimeStampToDate(selectedFilterValue.getStartDate()))+"\n", valueFont_10Bold);
                                PdfPCell dateTitlecell = new PdfPCell(dateTitle);
                                dateTitlecell.setBorder(Rectangle.NO_BORDER);
                                dateTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                dateTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                dateTitlecell.setPaddingLeft(6);
                                dateTitlecell.setPaddingBottom(10);
                                Whole_SupplerDetails_table.addCell(dateTitlecell);


                                Phrase todateTitle = new Phrase("To Date : "+String.valueOf(DateParserClass.convertGivenTimeStampToDate(selectedFilterValue.getEndDate()))+"\n", valueFont_10Bold);
                                PdfPCell todateTitlecell = new PdfPCell(todateTitle);
                                todateTitlecell.setBorder(Rectangle.NO_BORDER);
                                todateTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                todateTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                todateTitlecell.setPaddingLeft(6);
                                todateTitlecell.setPaddingBottom(10);
                                Whole_SupplerDetails_table.addCell(todateTitlecell);







                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }




                }
                catch (Exception e) {
                    e.printStackTrace();
                }




                try {
                    PdfPCell Whole_WarehouseDetails_table_Cell = new PdfPCell(Whole_WarehouseDetails_table);
                    Whole_WarehouseDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                    Whole_WarehouseDetails_table_Cell.setBorderWidthRight(01);
                    Whole_WarehouseDetails_table_Cell.setPaddingTop(5);

                    Whole_Warehouse_and_RetailerDetails_table.addCell(Whole_WarehouseDetails_table_Cell);


                    PdfPCell Whole_SupplerDetails_table_Cell = new PdfPCell(Whole_SupplerDetails_table);
                    Whole_SupplerDetails_table_Cell.setPaddingTop(5);
                    Whole_SupplerDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                    Whole_Warehouse_and_RetailerDetails_table.addCell(Whole_SupplerDetails_table_Cell);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    PdfPCell Whole_Warehouse_and_RetailerDetails_table_Cell = new PdfPCell(Whole_Warehouse_and_RetailerDetails_table);
                    Whole_Warehouse_and_RetailerDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                    Whole_Warehouse_and_RetailerDetails_table_Cell.setBorderWidthTop(01);
                    wholePDFContentWithOut_Outline_table.addCell(Whole_Warehouse_and_RetailerDetails_table_Cell);

                } catch (Exception e) {
                    e.printStackTrace();
                }







                try{
                    PdfPTable itemDetailsLabel_table = new PdfPTable(new float[] {10 ,30 , 20 , 20 , 20  });



                    try {





                        Phrase phrasCtgynameLabelTitle = new Phrase(" S.No  ", valueFont_10);

                        PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                        phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                        phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                        phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                        phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                        phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                        itemDetailsLabel_table.addCell(phraseCtgyNameLabelTitlecell);




                        Phrase phrasMaleCountLabelTitle = new Phrase("Item Name", valueFont_10);

                        PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                        phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                        phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                        phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                        phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                        phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                        itemDetailsLabel_table.addCell(phraseMaleCountLabelTitlecell);



                        Phrase phrasFemaleCountLabelTitle = new Phrase("Quantity", valueFont_10);

                        PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                        phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                        phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                        phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                        phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                        phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                        itemDetailsLabel_table.addCell(phraseFemaleCountLabelTitlecell);




                        Phrase phrasTotalCountLabelTitle = new Phrase("Weight", valueFont_10);

                        PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                        phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                        phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                        phraseCountLabelTitlecell.setPaddingLeft(6);
                        phraseCountLabelTitlecell.setPaddingBottom(10);
                        phraseCountLabelTitlecell.setBorderWidthRight(01);

                        itemDetailsLabel_table.addCell(phraseCountLabelTitlecell);


                        Phrase phraseWeightLabelTitle = new Phrase("Price", valueFont_10);

                        PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                        phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                        phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                        phraseWeightLabelTitlecell.setPaddingLeft(6);
                        phraseWeightLabelTitlecell.setPaddingBottom(10);
                        phraseWeightLabelTitlecell.setBorderWidthRight(01);
                        itemDetailsLabel_table.addCell(phraseWeightLabelTitlecell);


                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }




                    try {
                        PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                        itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                        itemDetails_table_Cell.setBorderWidthTop(1);
                        itemDetails_table_Cell.setBackgroundColor(customGreyColor);
                        itemDetails_table_Cell.setBorderWidthBottom(01);
                        wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }






                }
                catch (Exception e){
                    e.printStackTrace();
                }


                try{
                    for(String orderid : orderDetailsHashmap.keySet()) {
                        OrderDetails_Model orderDetailsModel = orderDetailsHashmap.get(orderid);
                        try {
                            double value = orderDetailsModel.getDiscount();
                            discount = value + discount;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            double value = orderDetailsModel.getTotalprice();
                            totalfinalprice = value + totalfinalprice;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }



                    }
                    }
                catch (Exception e){
                    e.printStackTrace();
                }

                try{
                    for(int iteratr = 0 ; iteratr < orderItems.size(); iteratr++ ){
                        OrderItemDetails_Model orderItemDetailsModel = orderItems.get(iteratr);
                        try{
                            double value = orderItemDetailsModel.getTotalprice();
                            totalItemPrice = value + totalItemPrice;
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }

                        if(itemwiseTotalHashmap.containsKey(orderItemDetailsModel.getMenuitemkey())){
                            ReportsItemwiseCalculationDetail_Model modelFromHashmap  =  itemwiseTotalHashmap.get(orderItemDetailsModel.getMenuitemkey());

                            int quantityFromHashmap = 0 , quantityFromItemArray = 0 ;
                            double weightFromHashmap = 0 , weightFromItemArray = 0 , priceFromHashmap = 0 , priceFromItemArray = 0 ;

                            quantityFromHashmap     = modelFromHashmap.getTotalquantity();
                            weightFromHashmap       = modelFromHashmap.getTotalweight();
                            priceFromHashmap        = modelFromHashmap.getTotalprice();
                            quantityFromItemArray   = orderItemDetailsModel.getQuantity();
                            weightFromItemArray       = orderItemDetailsModel.getGrossweight();
                            priceFromItemArray        = orderItemDetailsModel.getTotalprice();

                            try{
                                modelFromHashmap.setTotalweight(weightFromHashmap+weightFromItemArray);
                                modelFromHashmap.setTotalquantity(quantityFromHashmap+quantityFromItemArray);
                                modelFromHashmap.setTotalprice(priceFromHashmap+priceFromItemArray);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }


                        }
                        else {
                            ReportsItemwiseCalculationDetail_Model modelFromHashmap = new ReportsItemwiseCalculationDetail_Model();

                            modelFromHashmap.setMenuitemkey(orderItemDetailsModel.getMenuitemkey());
                            modelFromHashmap.setTotalprice(orderItemDetailsModel.getTotalprice());
                            modelFromHashmap.setMenuitemname(orderItemDetailsModel.getItemname());
                            modelFromHashmap.setTotalquantity(orderItemDetailsModel.getQuantity());
                            modelFromHashmap.setTotalweight(orderItemDetailsModel.getGrossweight());
                            itemwiseTotalHashmapKeyList.add(orderItemDetailsModel.getMenuitemkey());
                            itemwiseTotalHashmap.put(orderItemDetailsModel.getMenuitemkey() , modelFromHashmap);


                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();

                }





                if(itemwiseTotalHashmapKeyList.size()>8){
                    try {


                        for (int iterator = 0; iterator < 9; iterator++) {
                            String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                            try {

                                //20, 30 , 12 ,10, 12 , 16
                                //19, 18, 15, 15, 15, 18

                                PdfPTable itemDetailsLabel_table  = new PdfPTable(new float[]{10 , 30 , 20 , 20 , 20  });


                                try {
                                    Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator+1), valueFont_10Bold);

                                    PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                   phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                   phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                   phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                   phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                   phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                    phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                    itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);



                                    Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);

                                    PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                    phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                    phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                     phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                    itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);






                                    Phrase phrasQuantityLabelTitle = null;
                                    phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                    PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                    phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);



                                    phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                    phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                    phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                    itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);



                                    Phrase phrasBatchpriceLabelTitle = null;
                                    if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                    phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                    }
                                    else{
                                        phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                    }

                                    PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                    phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                    phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                    phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                    itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);






                                    Phrase phrasNotesLabelTitle = null;

                                    phrasNotesLabelTitle =  new Phrase((" ₹ "+String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                    PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                    phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseNotesLabelTitlecell.setPaddingLeft(6);

                                    phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                    phraseNotesLabelTitlecell.setPaddingBottom(10);
                                    itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);



                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                    itemDetails_table_Cell.setBorderWidthBottom(1);
                                    //itemDetails_table_Cell.setBackgroundColor(WHITE);
                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }




                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                    tmcLogoImage_table.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tmcLogoImage_table.setTotalWidth(10f);
                    layoutDocument.add(tmcLogoImage_table);

                    PdfPCell wholePDFWithOutBordercell = new PdfPCell(wholePDFContentWithOut_Outline_table);
                    wholePDFWithOutBordercell.setCellEvent(roundRectange);
                    wholePDFWithOutBordercell.setPadding(1);
                    wholePDFWithOutBordercell.setBorder(Rectangle.NO_BORDER);
                    wholePDFContentOutline_table.addCell(wholePDFWithOutBordercell);
                    wholePDFContentOutline_table.setWidthPercentage(100);


                    layoutDocument.add(wholePDFContentOutline_table);
                    PdfPTable wholePDFContentWithOut_Outline_table2 = new PdfPTable(1);
                    try{
                        layoutDocument.newPage();



                        try {


                            for (int iterator = 9; iterator < itemwiseTotalHashmapKeyList.size(); iterator++) {
                                String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                                try {


                                   
                                    PdfPTable itemDetailsLabel_table  = new PdfPTable(new float[]{10 , 30 , 20 , 20 , 20  });


                                    try {
                                        Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator+1), valueFont_10Bold);

                                        PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                        phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                        phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                        phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                        phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                        phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                        itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);



                                        Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);

                                        PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                        phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                        phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                        phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                        phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                        phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                        phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                        itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);






                                        Phrase phrasQuantityLabelTitle = null;
                                        phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                        PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                        phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                        phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);



                                        phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                        phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                        phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                        itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);



                               //         Phrase phrasBatchpriceLabelTitle = null;
                                 //       phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                        Phrase phrasBatchpriceLabelTitle = null;
                                        if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                            phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                        }
                                        else{
                                            phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                        }

                                        PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                        phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                        phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                        phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                        phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                        phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                        itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);






                                        Phrase phrasNotesLabelTitle = null;

                                        phrasNotesLabelTitle =  new Phrase((" ₹ "+String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                        PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                        phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                        phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                        phraseNotesLabelTitlecell.setPaddingLeft(6);

                                        phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                        phraseNotesLabelTitlecell.setPaddingBottom(10);
                                        itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);



                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                        itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                        itemDetails_table_Cell.setBorderWidthBottom(1);
                                        // itemDetails_table_Cell.setBackgroundColor(WHITE);
                                        wholePDFContentWithOut_Outline_table2.addCell(itemDetails_table_Cell);


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }




                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }


/*
                        //extraas
                        try{





                            try {
                                PdfPTable EnptytableLabel_table = new PdfPTable(new float[]{35, 65});


                                try {
                                    Phrase phraseParticularsLabelTitle = new Phrase("    ", valueFont_10Bold);

                                    PdfPCell phraseParticularsLabelTitlecell = new PdfPCell(phraseParticularsLabelTitle);
                                    phraseParticularsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseParticularsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseParticularsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseParticularsLabelTitlecell.setPaddingLeft(6);
                                    phraseParticularsLabelTitlecell.setPaddingTop(5);
                                    phraseParticularsLabelTitlecell.setPaddingBottom(10);
                                    EnptytableLabel_table.addCell(phraseParticularsLabelTitlecell);


                                    Phrase phrasQuantityLabelTitle = new Phrase("     ", valueFont_10Bold);
                                    PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                    phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseQuantityLabelTitlecell.setPaddingTop(5);
                                    phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                    phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                    EnptytableLabel_table.addCell(phraseQuantityLabelTitlecell);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(EnptytableLabel_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);

                                    wholePDFContentWithOut_Outline_table2.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                        }
                        catch (Exception e ){
                            e.printStackTrace();
                        }


 */
/*
                        //finaladd
                        try {




                            PdfPTable wholePDFContentOutline_table2 = new PdfPTable(1);


                            PdfPCell wholePDFWithOutBordercell2 = new PdfPCell(wholePDFContentWithOut_Outline_table2);
                            wholePDFWithOutBordercell2.setCellEvent(roundRectange);
                            wholePDFWithOutBordercell2.setPadding(1);
                            wholePDFWithOutBordercell2.setBorder(Rectangle.NO_BORDER);
                            wholePDFContentOutline_table2.addCell(wholePDFWithOutBordercell2);
                            wholePDFContentOutline_table2.setWidthPercentage(100);


                            layoutDocument.add(wholePDFContentOutline_table2);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


 */


                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                }
                else {
                    try {
                        for (int iterator = 0; iterator < itemwiseTotalHashmapKeyList.size(); iterator++) {
                            String itemkey = itemwiseTotalHashmapKeyList.get(iterator);
                            try {

                                //20, 30 , 12 ,10, 12 , 16
                                //19, 18, 15, 15, 15, 18

                                PdfPTable itemDetailsLabel_table  = new PdfPTable(new float[]{10 , 30 , 20 , 20 , 20  });


                                try {
                                    Phrase phraseSnoDetailsLabelTitle = new Phrase(String.valueOf(iterator+1), valueFont_10Bold);

                                    PdfPCell phraseSnoDetailsLabelTitlecell = new PdfPCell(phraseSnoDetailsLabelTitle);
                                    phraseSnoDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseSnoDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseSnoDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseSnoDetailsLabelTitlecell.setBorderWidthRight(01);
                                    phraseSnoDetailsLabelTitlecell.setPaddingLeft(6);
                                    phraseSnoDetailsLabelTitlecell.setPaddingBottom(10);
                                    itemDetailsLabel_table.addCell(phraseSnoDetailsLabelTitlecell);



                                    Phrase phraseEarTagDetailsLabelTitle = new Phrase(String.valueOf(itemwiseTotalHashmap.get(itemkey).getMenuitemname()), valueFont_10Bold);

                                    PdfPCell phraseEarTagDetailsLabelTitlecell = new PdfPCell(phraseEarTagDetailsLabelTitle);
                                    phraseEarTagDetailsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseEarTagDetailsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    phraseEarTagDetailsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseEarTagDetailsLabelTitlecell.setBorderWidthRight(01);
                                    phraseEarTagDetailsLabelTitlecell.setPaddingLeft(6);
                                    phraseEarTagDetailsLabelTitlecell.setPaddingBottom(10);
                                    itemDetailsLabel_table.addCell(phraseEarTagDetailsLabelTitlecell);






                                    Phrase phrasQuantityLabelTitle = null;
                                    phrasQuantityLabelTitle = new Phrase((String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalquantity())) + " Nos", valueFont_10);

                                    PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                    phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);



                                    phraseQuantityLabelTitlecell.setBorderWidthRight(01);
                                    phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                    phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                    itemDetailsLabel_table.addCell(phraseQuantityLabelTitlecell);



                                  //  Phrase phrasBatchpriceLabelTitle = null;
                                   // phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                    Phrase phrasBatchpriceLabelTitle = null;
                                    if(itemwiseTotalHashmap.get(itemkey).getTotalweight() > 0 ){
                                        phrasBatchpriceLabelTitle = new Phrase(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalweight()))) + " Kg", valueFont_10);
                                    }
                                    else{
                                        phrasBatchpriceLabelTitle = new Phrase(String.valueOf(" - "));
                                    }

                                    PdfPCell phraseBatchPriceLabelTitlecell = new PdfPCell(phrasBatchpriceLabelTitle);
                                    phraseBatchPriceLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseBatchPriceLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseBatchPriceLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseBatchPriceLabelTitlecell.setBorderWidthRight(01);
                                    phraseBatchPriceLabelTitlecell.setPaddingLeft(6);


                                    phraseBatchPriceLabelTitlecell.setPaddingBottom(10);
                                    itemDetailsLabel_table.addCell(phraseBatchPriceLabelTitlecell);






                                    Phrase phrasNotesLabelTitle = null;

                                    phrasNotesLabelTitle =  new Phrase((" ₹ "+String.valueOf(itemwiseTotalHashmap.get(itemkey).getTotalprice())) + " ", valueFont_10);
                                    PdfPCell phraseNotesLabelTitlecell = new PdfPCell(phrasNotesLabelTitle);
                                    phraseNotesLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                    phraseNotesLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    phraseNotesLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    phraseNotesLabelTitlecell.setPaddingLeft(6);

                                    phraseNotesLabelTitlecell.setBorderWidthRight(01);
                                    phraseNotesLabelTitlecell.setPaddingBottom(10);
                                    itemDetailsLabel_table.addCell(phraseNotesLabelTitlecell);



                                } catch (Exception e) {
                                    e.printStackTrace();
                                }




                                try {
                                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsLabel_table);
                                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                    itemDetails_table_Cell.setBorderWidthBottom(1);
                                    // itemDetails_table_Cell.setBackgroundColor(WHITE);
                                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }




                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


/*
                    //extraas
                    try{



                        try {
                            PdfPTable EnptytableLabel_table = new PdfPTable(new float[]{35, 65});


                            try {
                                Phrase phraseParticularsLabelTitle = new Phrase("    ", valueFont_10Bold);

                                PdfPCell phraseParticularsLabelTitlecell = new PdfPCell(phraseParticularsLabelTitle);
                                phraseParticularsLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                phraseParticularsLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                phraseParticularsLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                phraseParticularsLabelTitlecell.setPaddingLeft(6);
                                phraseParticularsLabelTitlecell.setPaddingTop(5);
                                phraseParticularsLabelTitlecell.setPaddingBottom(10);
                                EnptytableLabel_table.addCell(phraseParticularsLabelTitlecell);


                                Phrase phrasQuantityLabelTitle = new Phrase("     ", valueFont_10Bold);
                                PdfPCell phraseQuantityLabelTitlecell = new PdfPCell(phrasQuantityLabelTitle);
                                phraseQuantityLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                                phraseQuantityLabelTitlecell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                phraseQuantityLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                                phraseQuantityLabelTitlecell.setPaddingTop(5);
                                phraseQuantityLabelTitlecell.setPaddingLeft(6);
                                phraseQuantityLabelTitlecell.setPaddingBottom(10);
                                EnptytableLabel_table.addCell(phraseQuantityLabelTitlecell);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            try {
                                PdfPCell itemDetails_table_Cell = new PdfPCell(EnptytableLabel_table);
                                itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                                 wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    catch (Exception e ){
                        e.printStackTrace();
                    }

 */

                    try{

                        tmcLogoImage_table.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tmcLogoImage_table.setTotalWidth(10f);
                        layoutDocument.add(tmcLogoImage_table);



                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

/*
                    //finaladd
                    try {

                        tmcLogoImage_table.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tmcLogoImage_table.setTotalWidth(10f);
                        layoutDocument.add(tmcLogoImage_table);




                        PdfPCell wholePDFWithOutBordercell = new PdfPCell(wholePDFContentWithOut_Outline_table);
                        wholePDFWithOutBordercell.setCellEvent(roundRectange);
                        wholePDFWithOutBordercell.setPadding(1);
                        wholePDFWithOutBordercell.setBorder(Rectangle.NO_BORDER);
                        wholePDFContentOutline_table.addCell(wholePDFWithOutBordercell);
                        wholePDFContentOutline_table.setWidthPercentage(100);


                        layoutDocument.add(wholePDFContentOutline_table);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


 */


                }





            }
            catch (Exception e){
                e.printStackTrace();
            }




            try{
                PdfPTable itemDetailsTotal_table = new PdfPTable(new float[] {10 ,30 , 20 , 20 , 20  });



                try {





                    Phrase phrasCtgynameLabelTitle = new Phrase("  ", valueFont_10);

                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                    phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                    itemDetailsTotal_table.addCell(phraseCtgyNameLabelTitlecell);




                    Phrase phrasMaleCountLabelTitle = new Phrase(" ", valueFont_10);

                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                    phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                    itemDetailsTotal_table.addCell(phraseMaleCountLabelTitlecell);



                    Phrase phrasFemaleCountLabelTitle = new Phrase(" ", valueFont_10);

                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                    phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                    itemDetailsTotal_table.addCell(phraseFemaleCountLabelTitlecell);




                    Phrase phrasTotalCountLabelTitle = new Phrase(" Total ", valueFont_10Bold);

                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseCountLabelTitlecell.setPaddingLeft(6);
                    phraseCountLabelTitlecell.setPaddingBottom(10);
                    phraseCountLabelTitlecell.setBorderWidthRight(01);

                    itemDetailsTotal_table.addCell(phraseCountLabelTitlecell);


                    Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(totalItemPrice), valueFont_10);

                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                    phraseWeightLabelTitlecell.setBorderWidthRight(01);
                    itemDetailsTotal_table.addCell(phraseWeightLabelTitlecell);


                }
                catch (Exception e){
                    e.printStackTrace();
                }




                try {
                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsTotal_table);
                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                    ///itemDetails_table_Cell.setBorderWidthTop(1);
                   // itemDetails_table_Cell.setBackgroundColor(customGreyColor);
                    itemDetails_table_Cell.setBorderWidthBottom(01);
                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                }
                catch (Exception e) {
                    e.printStackTrace();
                }






            }
            catch (Exception e){
                e.printStackTrace();
            }

            try{
                PdfPTable itemDetailsTotal_table = new PdfPTable(new float[] {10 ,30 , 20 , 20 , 20  });



                try {





                    Phrase phrasCtgynameLabelTitle = new Phrase("  ", valueFont_10);

                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                    phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                    itemDetailsTotal_table.addCell(phraseCtgyNameLabelTitlecell);




                    Phrase phrasMaleCountLabelTitle = new Phrase(" ", valueFont_10);

                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                    phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                    itemDetailsTotal_table.addCell(phraseMaleCountLabelTitlecell);



                    Phrase phrasFemaleCountLabelTitle = new Phrase(" ", valueFont_10);

                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                    phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                    itemDetailsTotal_table.addCell(phraseFemaleCountLabelTitlecell);




                    Phrase phrasTotalCountLabelTitle = new Phrase(" Discount ", valueFont_10Bold);

                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseCountLabelTitlecell.setPaddingLeft(6);
                    phraseCountLabelTitlecell.setPaddingBottom(10);
                    phraseCountLabelTitlecell.setBorderWidthRight(01);

                    itemDetailsTotal_table.addCell(phraseCountLabelTitlecell);


                    Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(discount), valueFont_10);

                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                    phraseWeightLabelTitlecell.setBorderWidthRight(01);
                    itemDetailsTotal_table.addCell(phraseWeightLabelTitlecell);


                }
                catch (Exception e){
                    e.printStackTrace();
                }




                try {
                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsTotal_table);
                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                   // itemDetails_table_Cell.setBorderWidthTop(1);
                    // itemDetails_table_Cell.setBackgroundColor(customGreyColor);
                    //itemDetails_table_Cell.setBorderWidthBottom(01);
                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                }
                catch (Exception e) {
                    e.printStackTrace();
                }






            }
            catch (Exception e){
                e.printStackTrace();
            }

            try{
                PdfPTable itemDetailsTotal_table = new PdfPTable(new float[] {10 ,30 , 20 , 20 , 20  });



                try {





                    Phrase phrasCtgynameLabelTitle = new Phrase("  ", valueFont_10);

                    PdfPCell phraseCtgyNameLabelTitlecell = new PdfPCell(phrasCtgynameLabelTitle);
                    phraseCtgyNameLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseCtgyNameLabelTitlecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    phraseCtgyNameLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseCtgyNameLabelTitlecell.setBorderWidthRight(01);
                    phraseCtgyNameLabelTitlecell.setPaddingLeft(6);
                    phraseCtgyNameLabelTitlecell.setPaddingBottom(10);
                    itemDetailsTotal_table.addCell(phraseCtgyNameLabelTitlecell);




                    Phrase phrasMaleCountLabelTitle = new Phrase(" ", valueFont_10);

                    PdfPCell phraseMaleCountLabelTitlecell = new PdfPCell(phrasMaleCountLabelTitle);
                    phraseMaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseMaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    phraseMaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseMaleCountLabelTitlecell.setBorderWidthRight(01);
                    phraseMaleCountLabelTitlecell.setPaddingLeft(6);
                    phraseMaleCountLabelTitlecell.setPaddingBottom(10);
                    itemDetailsTotal_table.addCell(phraseMaleCountLabelTitlecell);



                    Phrase phrasFemaleCountLabelTitle = new Phrase(" ", valueFont_10);

                    PdfPCell phraseFemaleCountLabelTitlecell = new PdfPCell(phrasFemaleCountLabelTitle);
                    phraseFemaleCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseFemaleCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    phraseFemaleCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseFemaleCountLabelTitlecell.setPaddingLeft(6);
                    phraseFemaleCountLabelTitlecell.setPaddingBottom(10);
                    phraseFemaleCountLabelTitlecell.setBorderWidthRight(01);

                    itemDetailsTotal_table.addCell(phraseFemaleCountLabelTitlecell);




                    Phrase phrasTotalCountLabelTitle = new Phrase(" Final price ", valueFont_10Bold);

                    PdfPCell phraseCountLabelTitlecell = new PdfPCell(phrasTotalCountLabelTitle);
                    phraseCountLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseCountLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    phraseCountLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseCountLabelTitlecell.setPaddingLeft(6);
                    phraseCountLabelTitlecell.setPaddingBottom(10);
                    phraseCountLabelTitlecell.setBorderWidthRight(01);

                    itemDetailsTotal_table.addCell(phraseCountLabelTitlecell);


                    Phrase phraseWeightLabelTitle = new Phrase(String.valueOf(totalfinalprice), valueFont_10);

                    PdfPCell phraseWeightLabelTitlecell = new PdfPCell(phraseWeightLabelTitle);
                    phraseWeightLabelTitlecell.setBorder(Rectangle.NO_BORDER);
                    phraseWeightLabelTitlecell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    phraseWeightLabelTitlecell.setVerticalAlignment(Element.ALIGN_CENTER);
                    phraseWeightLabelTitlecell.setPaddingLeft(6);
                    phraseWeightLabelTitlecell.setPaddingBottom(10);
                    phraseWeightLabelTitlecell.setBorderWidthRight(01);
                    itemDetailsTotal_table.addCell(phraseWeightLabelTitlecell);


                }
                catch (Exception e){
                    e.printStackTrace();
                }




                try {
                    PdfPCell itemDetails_table_Cell = new PdfPCell(itemDetailsTotal_table);
                    itemDetails_table_Cell.setBorder(Rectangle.NO_BORDER);
                    itemDetails_table_Cell.setBorderWidthTop(1);
                    // itemDetails_table_Cell.setBackgroundColor(customGreyColor);
                    itemDetails_table_Cell.setBorderWidthBottom(01);
                    wholePDFContentWithOut_Outline_table.addCell(itemDetails_table_Cell);


                }
                catch (Exception e) {
                    e.printStackTrace();
                }






            }
            catch (Exception e){
                e.printStackTrace();
            }


            //finaladd
            try {




                PdfPCell wholePDFWithOutBordercell = new PdfPCell(wholePDFContentWithOut_Outline_table);
                wholePDFWithOutBordercell.setCellEvent(roundRectange);
                wholePDFWithOutBordercell.setPadding(1);
                wholePDFWithOutBordercell.setBorder(Rectangle.NO_BORDER);
                wholePDFContentOutline_table.addCell(wholePDFWithOutBordercell);
                wholePDFContentOutline_table.setWidthPercentage(100);


                layoutDocument.add(wholePDFContentOutline_table);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Close the document


        System.out.println("PDF Created Successfully!");
     }




    public PdfPCell addLogo(Document document) throws DocumentException {
        PdfPCell cellImage ;
        try { // Get user Settings GeneralSettings getUserSettings =

            Rectangle rectDoc = document.getPageSize();
            float width = rectDoc.getWidth();
            float height = rectDoc.getHeight();
            float imageStartX = width - document.rightMargin() - 3315f;
            float imageStartY = height - document.topMargin() - 280f;

            System.gc();

            InputStream ims = context.getAssets().open("ordernotelogo.png"); // image from assets folder
            Bitmap bmp = BitmapFactory.decodeStream(ims);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            bmp.compress(Bitmap.CompressFormat.PNG, 80, stream);

            byte[] byteArray = stream.toByteArray();
            // PdfImage img = new PdfImage(arg0, arg1, arg2)

            // Converting byte array into image Image img =
            Image img = Image.getInstance(byteArray); // img.scalePercent(50);
            img.setAlignment(Image.ALIGN_RIGHT );
            img.scaleAbsolute(60f, 45f);
            img.setAbsolutePosition(90f, 120f); // Adding Image
            img.setTransparency (new int [] { 0x00, 0x10 });
            img.setWidthPercentage(100);
            img.setScaleToFitHeight(true);
            cellImage= new PdfPCell(img, false);
            cellImage.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellImage.setVerticalAlignment(Element.ALIGN_RIGHT);
            return cellImage;

        } catch (Exception e) {
            e.printStackTrace();
            cellImage= new PdfPCell();
            return cellImage;

        }

    }




}


/*

    public PDFGenerator(Context context, String pdfType , List<OrderDetails_Model> orderDetails, List<OrderItemDetails_Model> orderItems , ReportsFilterDetails_Model filterDetailsModel, PDFGeneratorListener listener ) {
       //  pdfDocument = new PdfDocument();
       //  paint = new Paint();
       //  pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
       //  page = pdfDocument.startPage(pageInfo);
       //  canvas = page.getCanvas();

        // Drawing order details
      //  paint.setTextSize(12);
     //   yPosition = 50;

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        File folder = new File(path);
        //  File folder = new File(fol, "pdf");
        if (!folder.exists()) {
            boolean bool = folder.mkdirs();
        }
        try {
             String filename = "Category wise sales details.pdf";



            file = new File(folder, filename);
            file.createNewFile();
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                 layoutDocument = new  Document();
                PdfWriter.getInstance(layoutDocument, fOut);
                layoutDocument.open();

                roundRectange = new RoundRectangle();



                if(pdfType.equals(Constants.datewiseConsolidatedPDF)){
                      generateConsolidatedPDF( );
                }
                else if(pdfType.equals(Constants.datewiseConsolidatedPDF)){

                }



                layoutDocument.close();
                listener.onPDFGenerated(file);

            } catch (Exception e) {
                e.printStackTrace();
            }




            // }
        } catch (IOException e) {


            Log.i("error", e.getLocalizedMessage());
        } catch (Exception ex) {


            ex.printStackTrace();
        }






        // Save PDF
       try {
            File pdfFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "OrderDetails.pdf");
            FileOutputStream outputStream = new FileOutputStream(pdfFile);

            pdfDocument.writeTo(outputStream);
            pdfDocument.close();
            listener.onPDFGenerated(pdfFile);
        } catch (IOException e) {
            listener.onError(e.getMessage());
        }






    }

 */