package com.saketme.bunkometer;

class Classes {

    //private variables
    private int id, bunked_classes, limit;
    private String subject, bunk_date;

    //empty constructor
    public Classes(){}

    //constructor for reading values
    public Classes(String subject, int bunked_classes, int limit){
        this.subject = subject;
        this.bunked_classes = bunked_classes;
        this.limit = limit;
    }

    //Now setters and getters
    public int getId(){
        return this.id;
    }

    public int getBunkedClasses(){
        return bunked_classes;
    }

    public String getSubject(){
        return subject;
    }

    public int getLimit(){
        return limit;
    }

    public String getLastBunkDate() {
        return bunk_date;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setBunkedClasses(int bunked_classes){
        this.bunked_classes = bunked_classes;
    }

    public void setSubject(String classes){
        this.subject = classes;
    }

    public void setLimit(int limit){
        this.limit = limit;
    }

    public void setLastBunkDate(String bunk_date) {
        this.bunk_date = bunk_date;
    }
}
