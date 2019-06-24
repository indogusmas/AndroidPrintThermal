package com.indogusmas.testminipos;

import android.content.Context;

public class PrintReceipt {

    public static boolean  printBillFromOrder(Context context){
        if(MainActivity.BLUETOOTH_PRINTER.IsNoConnection()){
            return false;
        }

        double totalBill=0.00, netBill=0.00, totalVat=0.00;

        //LF = Line feed
        MainActivity.BLUETOOTH_PRINTER.Begin();
        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//CENTER
        MainActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);	//30 * 0.125mm
        MainActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);//normal
        MainActivity.BLUETOOTH_PRINTER.BT_Write("Company Name");

        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);
        MainActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);
        MainActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);

        //BT_Write() method will initiate the printer to start printing.
        MainActivity.BLUETOOTH_PRINTER.BT_Write("Branch Name: " + "Stuttgart Branch" +
                "\nOrder No: " + "1245784256454" +
                "\nBill No: " + "554741254854" +
                "\nTrn. Date:" + "29/12/2015" +
                "\nSalesman:" + "Mr. Salesman");

        MainActivity.BLUETOOTH_PRINTER.LF();
       // MainActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));
        MainActivity.BLUETOOTH_PRINTER.LF();

        MainActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);//LEFT
        MainActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);	//50 * 0.125mm
        MainActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);//normal font

        //static sales record are generated
        SalesModel.generatedMoneyReceipt();

        for(int i=0;i<StaticValue.arrayListSalesModel.size();i++){
            SalesModel salesModel = StaticValue.arrayListSalesModel.get(i);
            MainActivity.BLUETOOTH_PRINTER.BT_Write(salesModel.getProductShortName());
            MainActivity.BLUETOOTH_PRINTER.LF();
            MainActivity.BLUETOOTH_PRINTER.BT_Write(" " + salesModel.getSalesAmount() + "x" + salesModel.getUnitSalesCost() +
                    "=" + Utility.doubleFormatter(salesModel.getSalesAmount() * salesModel.getUnitSalesCost()) + "" + StaticValue.CURRENCY);
            MainActivity.BLUETOOTH_PRINTER.LF();

            totalBill=totalBill + (salesModel.getUnitSalesCost() * salesModel.getSalesAmount());
        }

        MainActivity.BLUETOOTH_PRINTER.LF();
    //    MainActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));


        MainActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//RIGHT
        MainActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);	//50 * 0.125mm
        MainActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte)0x00);//normal font

        totalVat=Double.parseDouble(Utility.doubleFormatter(totalBill*(StaticValue.VAT/100)));
        netBill=totalBill+totalVat;

        MainActivity.BLUETOOTH_PRINTER.LF();
       // MainActivity.BLUETOOTH_PRINTER.BT_Write("Total Bill:" + Utility.doubleFormatter(totalBill) + "" + StaticValue.CURRENCY);

        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.BT_Write(Double.toString(StaticValue.VAT) + "% VAT:" + Utility.doubleFormatter(totalVat) + "" +
                StaticValue.CURRENCY);

        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//center
     //   MainActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));


        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);
        MainActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//Right
        MainActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x9);
     //   MainActivity.BLUETOOTH_PRINTER.BT_Write("Net Bill:" + Utility.doubleFormatter(netBill) + "" + StaticValue.CURRENCY);

        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//center
        MainActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);//normal font
     //   MainActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));

        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);//left


        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);//left

        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.SetAlignMode((byte)1);//Center
        MainActivity.BLUETOOTH_PRINTER.BT_Write("\n\nThank You\nPOWERED By SIAS ERP");


        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.LF();
        MainActivity.BLUETOOTH_PRINTER.LF();
        return true;
    }
}
