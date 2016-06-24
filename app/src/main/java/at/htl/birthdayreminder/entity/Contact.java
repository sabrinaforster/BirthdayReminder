package at.htl.birthdayreminder.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sabrina on 16.05.2016.
 */
public class Contact implements Comparable<Contact>{
    private String name;
    private Date birthday;
    private Date age;

    //region Getter and Setter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    //endregion

    public Contact(String name, Date birthday) {
        this.name = name;
        setBirthday(birthday);
    }

    @Override
    public String toString() {
        return getName() + ": " + birthday.toString();
    }

    public String getBirthdayToString() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        return dateFormat.format(getBirthday());
    }

    @Override
    public int compareTo(Contact another) {
        Date now = new Date();
        Date date1 = new Date
                (now.getYear(), getBirthday().getMonth(), getBirthday().getDate());
        Date date2 = new Date
                (now.getYear(), another.getBirthday().getMonth(), another.getBirthday().getDate());
        return date1.compareTo(date2);
    }

    public int getAge(){
        Date now = new Date();
        int diff = (now.getYear()+1900) - (getBirthday().getYear()+1900);
        if (getBirthday().getMonth() > now.getMonth() ||
                getBirthday().getMonth() == now.getMonth() &&
                        getBirthday().getDate() > now.getDate()) {
            diff--;
        }
        diff++;
        return diff;
    }

    public String getAgeToString() {
        return Integer.toString(getAge());
    }
}
