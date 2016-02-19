package com.lipl.youthconnect.youth_connect.util;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Android Luminous on 2/15/2016.
 */
public class DatabaseUtil {

    public static final String QA_TITLE = "title";
    public static final String QA_DESC = "description";
    public static final String QA_UPDATED_TIMESTAMP = "updated_timestamp";
    public static final String QA_ANSWER = "answer";
    public static final String QA_COMMENT = "comment";
    public static final String QA_ASKED_BY_USER_NAME = "asked_by_user_name";
    public static final String QA_ASKED_BY_USER_ID = "asked_by_user_id";
    public static final String QA_IS_ANSWERED = "is_answered";
    public static final String QA_IS_PUBLISHED = "is_published";
    public static final String QA_IS_UPLOADED = "is_uploaded";

    public static final String DOC_TITLE = "doc_title";
    public static final String DOC_PURPOSE = "doc_purpose";
    public static final String DOC_FILES = "files";
    public static final String DOC_CREATED = "created";
    public static final String DOC_IS_PUBLISHED = "is_published";
    public static final String DOC_CREATED_BY_USER_NAME = "created_by_user_name";
    public static final String DOC_CREATED_BY_USER_ID = "created_by_user_id";
    public static final String DOC_ASSIGNED_TO_USER_IDS = "doc_assigned_to_user_ids";
    public static final String DOC_IS_UPLOADED = "is_uploaded";

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

    public static void startReplications(Context context, Replication.ChangeListener changeListener, String TAG) throws CouchbaseLiteException, IOException {
        Replication pull = DatabaseUtil.getDatabaseInstance(context, Constants.YOUTH_CONNECT_DATABASE)
                .createPullReplication(createSyncURL(false));
        Replication push = DatabaseUtil.getDatabaseInstance(context, Constants.YOUTH_CONNECT_DATABASE)
                .createPushReplication(createSyncURL(false));
        pull.setContinuous(true);
        push.setContinuous(true);
        pull.start();
        push.start();

        pull.addChangeListener(changeListener);
        push.addChangeListener(changeListener);

        com.couchbase.lite.util.Log.i(TAG, "startReplications()", "Replication start");
    }

    private static URL createSyncURL(boolean isEncrypted){
        URL syncURL = null;
        String host = "http://192.168.1.107";
        String port = "4984";
        String dbName = Constants.YOUTH_CONNECT_DATABASE;
        try {
            syncURL = new URL(host + ":" + port + "/" + dbName);
        } catch (MalformedURLException me) {
            me.printStackTrace();
        }
        return syncURL;
    }
}
