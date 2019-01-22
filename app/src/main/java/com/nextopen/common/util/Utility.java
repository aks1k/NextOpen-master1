package com.nextopen.common.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;

import com.nextopen.R;
import com.nextopen.vo.CountryVO;

public class Utility implements IContants {
    public static String[] getDateStrings(String qrDate, boolean isQRCode){
        if(isQRCode){
            //DDmmYY
            return new String[]{qrDate.substring(0,2),qrDate.substring(2,4),IContants.YEAR_PREFIX+qrDate.substring(4,6)};
        }else{
            //CCNNNNNNDDMMYY
            return new String[]{qrDate.substring(8,10),qrDate.substring(10,12),IContants.YEAR_PREFIX+qrDate.substring(12,14)};
        }
    }

    public static void manageQRCode(String qrCodeFirst, String qrCodeSecond, String qrCodeNew, Context context){
        if(qrCodeFirst.equals(STRING_BLANK)){
            qrCodeFirst = qrCodeNew;
        }else if (qrCodeSecond.equals(STRING_BLANK)) {
            qrCodeSecond = qrCodeNew;
        }else{
            qrCodeFirst = qrCodeSecond;
            qrCodeSecond = qrCodeNew;
        }

        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_QR1, qrCodeFirst);
        editor.putString(PREF_QR2, qrCodeSecond);
        editor.commit();
    }

    public static void showProgress(final boolean show, final ProgressBar progressBar, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            progressBar.setVisibility(show ? View.GONE : View.VISIBLE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

    public static final CountryVO[] COUNTRIES = new CountryVO[]{
            new CountryVO(1, "Afghanistan", "AF", "93"),
            new CountryVO(2, "Albania", "AL", "355"),
            new CountryVO(3, "Algeria", "DZ", "213"),
            new CountryVO(4, "American Samoa", "AS", "1-684"),
            new CountryVO(5, "Andorra", "AD", "376"),
            new CountryVO(6, "Angola", "AO", "244"),
            new CountryVO(7, "Anguilla", "AI", "1-264"),
            new CountryVO(8, "Antarctica", "AQ", "672"),
            new CountryVO(9, "Antigua and Barbuda", "AG", "1-268"),
            new CountryVO(10, "Argentina", "AR", "54"),
            new CountryVO(11, "Armenia", "AM", "374"),
            new CountryVO(12, "Aruba", "AW", "297"),
            new CountryVO(13, "Australia", "AU", "61"),
            new CountryVO(14, "Austria", "AT", "43"),
            new CountryVO(15, "Azerbaijan", "AZ", "994"),
            new CountryVO(16, "Bahamas", "BS", "1-242"),
            new CountryVO(17, "Bahrain", "BH", "973"),
            new CountryVO(18, "Bangladesh", "BD", "880"),
            new CountryVO(19, "Barbados", "BB", "1-246"),
            new CountryVO(20, "Belarus", "BY", "375"),
            new CountryVO(21, "Belgium", "BE", "32"),
            new CountryVO(22, "Belize", "BZ", "501"),
            new CountryVO(23, "Benin", "BJ", "229"),
            new CountryVO(24, "Bermuda", "BM", "1-441"),
            new CountryVO(25, "Bhutan", "BT", "975"),
            new CountryVO(26, "Bolivia", "BO", "591"),
            new CountryVO(27, "Bosnia and Herzegowina", "BA", "387"),
            new CountryVO(28, "Botswana", "BW", "267"),
            new CountryVO(29, "Bouvet Island", "BV", "47"),
            new CountryVO(30, "Brazil", "BR", "55"),
            new CountryVO(31, "British Indian Ocean Territory", "IO", "246"),
            new CountryVO(32, "Brunei Darussalam", "BN", "673"),
            new CountryVO(33, "Bulgaria", "BG", "359"),
            new CountryVO(34, "Burkina Faso", "BF", "226"),
            new CountryVO(35, "Burundi", "BI", "257"),
            new CountryVO(36, "Cambodia", "KH", "855"),
            new CountryVO(37, "Cameroon", "CM", "237"),
            new CountryVO(38, "Canada", "CA", "1"),
            new CountryVO(39, "Cape Verde", "CV", "238"),
            new CountryVO(40, "Cayman Islands", "KY", "1-345"),
            new CountryVO(41, "Central African Republic", "CF", "236"),
            new CountryVO(42, "Chad", "TD", "235"),
            new CountryVO(43, "Chile", "CL", "56"),
            new CountryVO(44, "China", "CN", "86"),
            new CountryVO(45, "Christmas Island", "CX", "61"),
            new CountryVO(46, "Cocos new CountryVO(Keeling) Islands", "CC", "61"),
            new CountryVO(47, "Colombia", "CO", "57"),
            new CountryVO(48, "Comoros", "KM", "269"),
            new CountryVO(49, "Congo Democratic Republic of", "CG", "242"),
            new CountryVO(50, "Cook Islands", "CK", "682"),
            new CountryVO(51, "Costa Rica", "CR", "506"),
            //new CountryVO(52, "Cote D""Ivoire", "CI", "225"),
            new CountryVO(53, "Croatia", "HR", "385"),
            new CountryVO(54, "Cuba", "CU", "53"),
            new CountryVO(55, "Cyprus", "CY", "357"),
            new CountryVO(56, "Czech Republic", "CZ", "420"),
            new CountryVO(57, "Denmark", "DK", "45"),
            new CountryVO(58, "Djibouti", "DJ", "253"),
            new CountryVO(59, "Dominica", "DM", "1-767"),
            new CountryVO(60, "Dominican Republic", "DO", "1-809"),
            new CountryVO(61, "Timor-Leste", "TL", "670"),
            new CountryVO(62, "Ecuador", "EC", "593"),
            new CountryVO(63, "Egypt", "EG", "20"),
            new CountryVO(64, "El Salvador", "SV", "503"),
            new CountryVO(65, "Equatorial Guinea", "GQ", "240"),
            new CountryVO(66, "Eritrea", "ER", "291"),
            new CountryVO(67, "Estonia", "EE", "372"),
            new CountryVO(68, "Ethiopia", "ET", "251"),
            new CountryVO(69, "Falkland Islands new CountryVO(Malvinas)", "FK", "500"),
            new CountryVO(70, "Faroe Islands", "FO", "298"),
            new CountryVO(71, "Fiji", "FJ", "679"),
            new CountryVO(72, "Finland", "FI", "358"),
            new CountryVO(73, "France", "FR", "33"),
            new CountryVO(75, "French Guiana", "GF", "594"),
            new CountryVO(76, "French Polynesia", "PF", "689"),
            //new CountryVO(77, "French Southern Territories", "TF", ""),
            new CountryVO(78, "Gabon", "GA", "241"),
            new CountryVO(79, "Gambia", "GM", "220"),
            new CountryVO(80, "Georgia", "GE", "995"),
            new CountryVO(81, "Germany", "DE", "49"),
            new CountryVO(82, "Ghana", "GH", "233"),
            new CountryVO(83, "Gibraltar", "GI", "350"),
            new CountryVO(84, "Greece", "GR", "30"),
            new CountryVO(85, "Greenland", "GL", "299"),
            new CountryVO(86, "Grenada", "GD", "1-473"),
            new CountryVO(87, "Guadeloupe", "GP", "590"),
            new CountryVO(88, "Guam", "GU", "1-671"),
            new CountryVO(89, "Guatemala", "GT", "502"),
            new CountryVO(90, "Guinea", "GN", "224"),
            new CountryVO(91, "Guinea-bissau", "GW", "245"),
            new CountryVO(92, "Guyana", "GY", "592"),
            new CountryVO(93, "Haiti", "HT", "509"),
            new CountryVO(94, "Heard Island and McDonald Islands", "HM", "011"),
            new CountryVO(95, "Honduras", "HN", "504"),
            new CountryVO(96, "Hong Kong", "HK", "852"),
            new CountryVO(97, "Hungary", "HU", "36"),
            new CountryVO(98, "Iceland", "IS", "354"),
            new CountryVO(99, "India", "IN", "91"),
            new CountryVO(100, "Indonesia", "ID", "62"),
            new CountryVO(101, "Iran new CountryVO(Islamic Republic of)", "IR", "98"),
            new CountryVO(102, "Iraq", "IQ", "964"),
            new CountryVO(103, "Ireland", "IE", "353"),
            new CountryVO(104, "Israel", "IL", "972"),
            new CountryVO(105, "Italy", "IT", "39"),
            new CountryVO(106, "Jamaica", "JM", "1-876"),
            new CountryVO(107, "Japan", "JP", "81"),
            new CountryVO(108, "Jordan", "JO", "962"),
            new CountryVO(109, "Kazakhstan", "KZ", "7"),
            new CountryVO(110, "Kenya", "KE", "254"),
            new CountryVO(111, "Kiribati", "KI", "686"),
            //new CountryVO(112, "Korea, Democratic People""KDP", "KP", "850"),
            new CountryVO(113, "South Korea", "KR", "82"),
            new CountryVO(114, "Kuwait", "KW", "965"),
            new CountryVO(115, "Kyrgyzstan", "KG", "996"),
            //new CountryVO(116, "Lao People""s Democratic Republic", "LA", "856"),
            new CountryVO(117, "Latvia", "LV", "371"),
            new CountryVO(118, "Lebanon", "LB", "961"),
            new CountryVO(119, "Lesotho", "LS", "266"),
            new CountryVO(120, "Liberia", "LR", "231"),
            new CountryVO(121, "Libya", "LY", "218"),
            new CountryVO(122, "Liechtenstein", "LI", "423"),
            new CountryVO(123, "Lithuania", "LT", "370"),
            new CountryVO(124, "Luxembourg", "LU", "352"),
            new CountryVO(125, "Macao", "MO", "853"),
            new CountryVO(126, "Macedonia, The Former Yugoslav Republic of", "MK", "389"),
            new CountryVO(127, "Madagascar", "MG", "261"),
            new CountryVO(128, "Malawi", "MW", "265"),
            new CountryVO(129, "Malaysia", "MY", "60"),
            new CountryVO(130, "Maldives", "MV", "960"),
            new CountryVO(131, "Mali", "ML", "223"),
            new CountryVO(132, "Malta", "MT", "356"),
            new CountryVO(133, "Marshall Islands", "MH", "692"),
            new CountryVO(134, "Martinique", "MQ", "596"),
            new CountryVO(135, "Mauritania", "MR", "222"),
            new CountryVO(136, "Mauritius", "MU", "230"),
            new CountryVO(137, "Mayotte", "YT", "262"),
            new CountryVO(138, "Mexico", "MX", "52"),
            new CountryVO(139, "Micronesia, Federated States of", "FM", "691"),
            new CountryVO(140, "Moldova", "MD", "373"),
            new CountryVO(141, "Monaco", "MC", "377"),
            new CountryVO(142, "Mongolia", "MN", "976"),
            new CountryVO(143, "Montserrat", "MS", "1-664"),
            new CountryVO(144, "Morocco", "MA", "212"),
            new CountryVO(145, "Mozambique", "MZ", "258"),
            new CountryVO(146, "Myanmar", "MM", "95"),
            new CountryVO(147, "Namibia", "NA", "264"),
            new CountryVO(148, "Nauru", "NR", "674"),
            new CountryVO(149, "Nepal", "NP", "977"),
            new CountryVO(150, "Netherlands", "NL", "31"),
            new CountryVO(151, "Netherlands Antilles", "AN", "599"),
            new CountryVO(152, "New Caledonia", "NC", "687	"),
            new CountryVO(153, "New Zealand", "NZ", "64"),
            new CountryVO(154, "Nicaragua", "NI", "505"),
            new CountryVO(155, "Niger", "NE", "227"),
            new CountryVO(156, "Nigeria", "NG", "234"),
            new CountryVO(157, "Niue", "NU", "683"),
            new CountryVO(158, "Norfolk Island", "NF", "672"),
            new CountryVO(159, "Northern Mariana Islands", "MP", "1-670"),
            new CountryVO(160, "Norway", "NO", "47"),
            new CountryVO(161, "Oman", "OM", "968"),
            new CountryVO(162, "Pakistan", "PK", "92"),
            new CountryVO(163, "Palau", "PW", "680"),
            new CountryVO(164, "Panama", "PA", "507"),
            new CountryVO(165, "Papua New Guinea", "PG", "675"),
            new CountryVO(166, "Paraguay", "PY", "595"),
            new CountryVO(167, "Peru", "PE", "51"),
            new CountryVO(168, "Philippines", "PH", "63"),
            new CountryVO(169, "Pitcairn", "PN", "64"),
            new CountryVO(170, "Poland", "PL", "48"),
            new CountryVO(171, "Portugal", "PT", "351"),
            new CountryVO(172, "Puerto Rico", "PR", "1-787"),
            new CountryVO(173, "Qatar", "QA", "974"),
            new CountryVO(174, "Reunion", "RE", "262"),
            new CountryVO(175, "Romania", "RO", "40"),
            new CountryVO(176, "Russian Federation", "RU", "7"),
            new CountryVO(177, "Rwanda", "RW", "250"),
            new CountryVO(178, "Saint Kitts and Nevis", "KN", "1-869"),
            new CountryVO(179, "Saint Lucia", "LC", "1-758"),
            new CountryVO(180, "Saint Vincent and the Grenadines", "VC", "1-784"),
            new CountryVO(181, "Samoa", "WS", "685"),
            new CountryVO(182, "San Marino", "SM", "378"),
            new CountryVO(183, "Sao Tome and Principe", "ST", "239"),
            new CountryVO(184, "Saudi Arabia", "SA", "966"),
            new CountryVO(185, "Senegal", "SN", "221"),
            new CountryVO(186, "Seychelles", "SC", "248"),
            new CountryVO(187, "Sierra Leone", "SL", "232"),
            new CountryVO(188, "Singapore", "SG", "65"),
            new CountryVO(189, "Slovakia new CountryVO(Slovak Republic)", "SK", "421"),
            new CountryVO(190, "Slovenia", "SI", "386"),
            new CountryVO(191, "Solomon Islands", "SB", "677"),
            new CountryVO(192, "Somalia", "SO", "252"),
            new CountryVO(193, "South Africa", "ZA", "27"),
            new CountryVO(194, "South Georgia and the South Sandwich Islands", "GS", "500"),
            new CountryVO(195, "Spain", "ES", "34"),
            new CountryVO(196, "Sri Lanka", "LK", "94"),
            new CountryVO(197, "Saint Helena, Ascension and Tristan da Cunha", "SH", "290"),
            new CountryVO(198, "St. Pierre and Miquelon", "PM", "508"),
            new CountryVO(199, "Sudan", "SD", "249"),
            new CountryVO(200, "Suriname", "SR", "597"),
            new CountryVO(201, "Svalbard and Jan Mayen Islands", "SJ", "47"),
            new CountryVO(202, "Swaziland", "SZ", "268"),
            new CountryVO(203, "Sweden", "SE", "46"),
            new CountryVO(204, "Switzerland", "CH", "41"),
            new CountryVO(205, "Syrian Arab Republic", "SY", "963"),
            new CountryVO(206, "Taiwan", "TW", "886"),
            new CountryVO(207, "Tajikistan", "TJ", "992"),
            new CountryVO(208, "Tanzania, United Republic of", "TZ", "255"),
            new CountryVO(209, "Thailand", "TH", "66"),
            new CountryVO(210, "Togo", "TG", "228"),
            new CountryVO(211, "Tokelau", "TK", "690"),
            new CountryVO(212, "Tonga", "TO", "676"),
            new CountryVO(213, "Trinidad and Tobago", "TT", "1-868"),
            new CountryVO(214, "Tunisia", "TN", "216"),
            new CountryVO(215, "Turkey", "TR", "90"),
            new CountryVO(216, "Turkmenistan", "TM", "993"),
            new CountryVO(217, "Turks and Caicos Islands", "TC", "1-649"),
            new CountryVO(218, "Tuvalu", "TV", "688"),
            new CountryVO(219, "Uganda", "UG", "256"),
            new CountryVO(220, "Ukraine", "UA", "380"),
            new CountryVO(221, "United Arab Emirates", "AE", "971"),
            new CountryVO(222, "United Kingdom", "GB", "44"),
            new CountryVO(223, "United States", "US", "1"),
            new CountryVO(224, "United States Minor Outlying Islands", "UM", "246"),
            new CountryVO(225, "Uruguay", "UY", "598"),
            new CountryVO(226, "Uzbekistan", "UZ", "998"),
            new CountryVO(227, "Vanuatu", "VU", "678"),
            new CountryVO(228, "Vatican City State new CountryVO(Holy See)", "VA", "379"),
            new CountryVO(229, "Venezuela", "VE", "58"),
            new CountryVO(230, "Vietnam", "VN", "84"),
            new CountryVO(231, "Virgin Islands new CountryVO(British)", "VG", "1-284"),
            new CountryVO(232, "Virgin Islands new CountryVO(U.S.)", "VI", "1-340"),
            new CountryVO(233, "Wallis and Futuna Islands", "WF", "681"),
            new CountryVO(234, "Western Sahara", "EH", "212"),
            new CountryVO(235, "Yemen", "YE", "967"),
            new CountryVO(236, "Serbia", "RS", "381"),
            new CountryVO(238, "Zambia", "ZM", "260"),
            new CountryVO(239, "Zimbabwe", "ZW", "263"),
            new CountryVO(240, "Aaland Islands", "AX", "358"),
            new CountryVO(241, "Palestine", "PS", "970"),
            new CountryVO(242, "Montenegro", "ME", "382"),
            new CountryVO(243, "Guernsey", "GG", "44-1481"),
            new CountryVO(244, "Isle of Man", "IM", "44-1624"),
            new CountryVO(245, "Jersey", "JE", "44-1534"),
            new CountryVO(247, "Curaçao", "CW", "599"),
            new CountryVO(248, "Ivory Coast", "CI", "225"),
            new CountryVO(249, "Kosovo", "XK", "383")
    };

    public static boolean isGPSEnabled(Context mContext) {
        final LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void alertDialog(Context mContext, final DialogInterface.OnClickListener mPositiveListener, final DialogInterface.OnClickListener mNegativeListener, final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.Material_Dialog);
        builder.setTitle("");
        builder.setMessage(message);
        builder.setPositiveButton("OK", mPositiveListener);
        if (mNegativeListener == null) {
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            builder.setNegativeButton("Cancel",mNegativeListener);
        }
        builder.show();
    }

    /**
     * @param mContext -App {@link Context}
     * @return -Device is connected to internet or not
     */
    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
