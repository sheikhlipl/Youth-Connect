package com.lipl.youthconnect.youth_connect.util;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.util.Log;

import java.io.IOException;

/**
 * Created by Android Luminous on 2/15/2016.
 */
public class DatabaseUtil {

    public static final String QA_TITLE = "title";
    public static final String QA_DESC = "description";
    public static final String QA_UPDATED_TIMESTAMP = "updated_timestamp";
    public static final String QA_ANSWER = "answer";
    public static final String QA_ASKED_BY_USER_NAME = "asked_by_user_name";
    public static final String QA_IS_ANSWERED = "is_answered";
    public static final String QA_IS_PUBLISHED = "is_published";

    public static final String ANSWER_CONTENT = "answer_content";
    public static final String ANSWER_BY_USER_NAME = "answer_by_user_name";
    public static final String ANSWER_TIMESTAMP = "answer_timestamp";


    public static Database getDatabaseInstance(Context context, String database_name) throws CouchbaseLiteException, IOException {
        Manager manager = null;
        Database database = null;
        if(manager == null){
            manager = getManagerInstance(context);
        }
        if ((database == null) & (manager != null)) {
            database = manager.getDatabase(database_name);
        }
        return database;
    }
    public static Manager getManagerInstance(Context context) throws IOException {
        Manager manager = null;
        if (manager == null) {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
        }
        return manager;
    }
    public static Document getDocumentFromDocumentId(Database database, String documentId){
        // retrieve the document from the database
        Document retrievedDocument = database.getDocument(documentId);
        return  retrievedDocument;
    }
}
