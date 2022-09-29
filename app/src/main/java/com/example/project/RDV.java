package com.example.project;

import android.os.Parcel;
import android.os.Parcelable;

public class RDV implements Parcelable {

    private int identifier;
    private String description;

    private String date;
    private String time;
    private String contact;

    private String address;
    private String phoneNumber;
    private boolean isDone;

    public RDV(Parcel pParcel)
    {
        this.identifier = pParcel.readInt();
        this.description = pParcel.readString();
        this.date = pParcel.readString();
        this.time = pParcel.readString();
        this.contact = pParcel.readString();
        this.address = pParcel.readString();
        this.phoneNumber = pParcel.readString();
        this.isDone = pParcel.readBoolean();
    }

    public RDV(int pId,String pDescription, String pDate, String pTime, String pContact, String pAddress, String pPhoneNumber)
    {
        this.identifier = pId;
        this.description = pDescription;
        this.date = pDate;
        this.time = pTime;
        this.contact = pContact;
        this.address = pAddress;
        this.phoneNumber = pPhoneNumber;
        this.isDone = false;
    }

    public RDV(String pDescription, String pDate, String pTime, String pContact)
    {
        this.description = pDescription;
        this.date = pDate;
        this.time = pTime;
        this.contact = pContact;
        this.address = "no address";
        this.phoneNumber = "no phone number";
        this.isDone = false;
    }

    public RDV(String pDescription, String pDate, String pTime, String pContact, String pAddress, String pPhoneNumber)
    {
        this.description = pDescription;
        this.date = pDate;
        this.time = pTime;
        this.contact = pContact;
        this.address = pAddress;
        this.phoneNumber = pPhoneNumber;
        this.isDone = false;
    }

    public int getIdentifier()
    {
        return this.identifier;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getDate()
    {
        return this.date;
    }

    public String getTime(){
        return  this.time;
    }

    public String getContact()
    {
        return this.contact;
    }

    public String getAddress(){ return this.address;}

    public String getPhoneNumber(){ return this.phoneNumber;}

    public boolean getIsDone()
    {
        return this.isDone;
    }

    public void setIdentifier(int pIdentifier)
    {
        this.identifier = pIdentifier;
    }

    public void setDescription(String pDescription)
    {
        this.description = pDescription;
    }

    public void setDate(String pDate)
    {
        this.date = pDate;
    }

    public void setTime(String pTime)
    {
        this.time = pTime;
    }

    public void setContact(String pContact)
    {
        this.contact = pContact;
    }

    public void setAddress(String pAddress){this.address = pAddress;}

    public void setPhoneNumber(String pPhoneNumber){this.phoneNumber = pPhoneNumber;}

    public void setIsDone(boolean pIsDone)
    {
        this.isDone = pIsDone;
    }


    @Override
    public int describeContents() {
        return hashCode();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(identifier);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(contact);
        dest.writeString(address);
        dest.writeString(phoneNumber);
        dest.writeBoolean(isDone);
    }

    public static final Parcelable.Creator<RDV> CREATOR = new Parcelable.Creator<RDV>(){
        @Override
        public RDV createFromParcel(Parcel parcel) {
            return new RDV(parcel);
        }
        @Override
        public RDV[] newArray(int size) {
            return new RDV[size];
        }
    };

}
