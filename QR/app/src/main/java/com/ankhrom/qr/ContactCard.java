package com.ankhrom.qr;


import android.content.Intent;
import android.provider.ContactsContract;

import java.io.BufferedReader;
import java.io.StringReader;

public class ContactCard {

    /**
     * BEGIN:VCARD
     * VERSION:3.0
     * N:sur;name
     * FN:name sur
     * ORG:comp
     * TITLE:job
     * ADR:;;str;cit;stat;zip;count
     * TEL;WORK;VOICE:pho
     * TEL;CELL:mobi
     * TEL;FAX:fax
     * EMAIL;WORK;INTERNET:ema
     * URL:web
     * BDAY:ahoj jak se mas
     * END:VCARD
     */

    public String name;
    public String title;
    public String company;
    public String street;
    public String city;
    public String zip;
    public String country;
    public String state;
    public String email;
    public String phone;
    public String tel;
    public String fax;
    public String url;
    public String note;

    public static ContactCard parseVCard(String vcard) {

        ContactCard card = new ContactCard();

        try {

            BufferedReader br = new BufferedReader(new StringReader(vcard));

            String line;
            while ((line = br.readLine()) != null) {

                int index = line.indexOf(":") + 1;
                String key = line.substring(0, index);
                String data = line.substring(index);

                if (line.startsWith("FN")) {
                    card.name = data;
                } else if (key.startsWith("ORG")) {
                    card.company = data;
                } else if (key.startsWith("TITLE")) {
                    card.title = data;
                } else if (key.startsWith("ADR")) {
                    String[] values = data.split(";");

                    card.street = values[2];
                    card.city = values[3];
                    card.state = values[4];
                    card.zip = values[5];
                    card.country = values[6];
                } else if (key.startsWith("TEL")) {
                    String[] type = key.split(";");
                    switch (type[1]) {
                        case "WORK":
                            card.tel = data;
                            break;
                        case "CELL":
                            card.phone = data;
                            break;
                        case "FAX":
                            card.fax = data;
                            break;
                    }
                } else if (key.startsWith("EMAIL")) {
                    card.email = data;
                } else if (key.startsWith("URL")) {
                    card.url = data;
                } else if (key.startsWith("BDAY")) {
                    card.note = data;
                }
            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return card;
    }

    public Intent toIntent() {

        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.COMPANY, company);
        intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, title);
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, tel);
        intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        intent.putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE, fax);
        intent.putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK);
        intent.putExtra(ContactsContract.Intents.Insert.NOTES, url + "\n" + note);

        return intent;
    }
}
