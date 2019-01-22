package com.nextopen.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private static Validator singleInstance;

    private Validator(){}

    public static Validator getInstance(){
        if(singleInstance == null){
            singleInstance = new Validator();
        }
        return singleInstance;
    }

    public boolean isValidQRCode(String qrCode){
        if(isValidQRCodeFormat(qrCode)) {
            String qrDate = qrCode.substring(8, 14);
            return isValidQRDate(qrDate);
        }
        return false;
    }

    private boolean isValidQRCodeFormat(String qrCode){
        //AB123456180718
        //CCNNNNNNDDMMYY
        Pattern pattern = Pattern.compile("[A-Za-z]{2}\\d{12}");
        Matcher matcher = pattern.matcher(qrCode);
        return matcher.matches();
    }

    private boolean isValidQRDate(String date){
        //DDmmYY
        if(date == null || date.length() != 6)
            return false;

        String[] dateStrings = Utility.getDateStrings(date, true);

        int day = Integer.parseInt(dateStrings[0]);
        int month = Integer.parseInt(dateStrings[1]);
        int year = Integer.parseInt(dateStrings[2]);

        Calendar c = Calendar.getInstance();
        Date currentDate = c.getTime();

        c.set(year, month-1, day);
        Date inputDate = c.getTime();

        boolean isValidDate = (inputDate.after(currentDate) || inputDate.equals(currentDate));

        c = null;
        currentDate = null;
        inputDate = null;
        dateStrings = null;

        return isValidDate;
    }

    public boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPhone(String phone) {
        return (phone.length() > 6 && phone.length() < 11);
    }

    public static void main(String[] args) throws Exception {
        String dateInString = "180718";

        new Validator().setValues("Three");

    }

    private String qr1 = "One";
    private String qr2 = "Two";

    private void setValues(String a){
        if(qr1.equals("")){
            qr1 = a;
        }else if (qr2.equals("")) {
            qr2 = a;
        }else{
            qr1 = qr2;
            qr2 = a;
        }

        System.out.println("QR1: "+qr1+"    QR2: "+qr2);
    }
/*
        Validator v = new Validator();
        String[] dateStrings = Utility.getDateStrings("AB123456180718", false);
        System.out.println(v.isValidQRCode("AB123456200718"));
    }*/
}
