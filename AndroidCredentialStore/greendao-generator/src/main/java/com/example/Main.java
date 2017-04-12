package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Main {
    public static void main(String[] args){
        Schema schema = new Schema(0, "com.example.camilla.androidcredentialstore.activities.db");

        Entity credential = schema.addEntity("Credential");
        credential.addIdProperty();
        credential.addStringProperty("website");
        credential.addStringProperty("username");
        credential.addStringProperty("password");

       // DaoGenerator dg = new DaoGenerator();

        //dg.generateAll(schema, "./");
    }
}
