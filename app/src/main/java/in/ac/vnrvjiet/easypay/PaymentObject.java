package in.ac.vnrvjiet.easypay;

import java.util.ArrayList;

class PaymentObject {

    private String type;
    private String amount;
    public String date;
    public String key;

    public PaymentObject(String type, String amount, String date) {
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public PaymentObject() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
