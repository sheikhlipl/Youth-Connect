package com.lipl.youthconnect.youth_connect.util;

import android.content.Context;
import android.os.AsyncTask;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.util.Log;
import com.lipl.youthconnect.youth_connect.database.DBHelper;
import com.lipl.youthconnect.youth_connect.pojo.NodalUser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Android Luminous on 2/15/2016.
 */
public class DatabaseUtil {

    private static final String TAG = "DatabaseUtil";

    public static final String TYPE = "type";
    public static final String TYPE_QA = "questionandanswer";
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

    public static final String DISTRICTS = "districts";
    public static final String NODAL_OFFICERS = "users";

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

    public static void startReplications(final Context context, final Replication.ChangeListener changeListener, String TAG) throws CouchbaseLiteException, IOException {

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
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

                    com.couchbase.lite.util.Log.i("DatabaseUtil", "startReplications()", "Replication start");
                } catch(CouchbaseLiteException exception){
                    Log.e("DatabaseUtil", "startReplication()", exception);
                } catch(IOException exception){
                    Log.e("DatabaseUtil", "startReplication()", exception);
                } catch(Exception exception){
                    Log.e("DatabaseUtil", "startReplication()", exception);
                }

                return null;
            }
        }.execute();
    }

    public static final String syncURL = "http://192.168.1.107" + ":" + "4984" + "/" + Constants.YOUTH_CONNECT_DATABASE;

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

    public static void deleteDoc(Database database, String documentId){
        Document document = database.getDocument(documentId);
        try {
            document.delete();
        } catch (CouchbaseLiteException e) {
            com.couchbase.lite.util.Log.e("DocUtil", "Error putting", e);
        } catch(Exception exception){
            android.util.Log.e("DocUtil", "updateDocument()", exception);
        }
    }

    public static List<String> getAllDocumentIds(Context context){

        List<String> docIds = new ArrayList<String>();
        try {
            Database database = DatabaseUtil.getDatabaseInstance(context, Constants.YOUTH_CONNECT_DATABASE);
            Query query = database.createAllDocumentsQuery();
            query.setAllDocsMode(Query.AllDocsMode.BY_SEQUENCE);
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                docIds.add(row.getDocumentId());
            }
        } catch(CouchbaseLiteException exception){
            android.util.Log.e(TAG, "Error", exception);
        } catch (IOException exception){
            com.couchbase.lite.util.Log.e(TAG, "onDeleteClick()", exception);
        }

        return docIds;
    }

    public static List<String> getAllQADocumentIds(Context context){

        final List<String> docIds = new ArrayList<String>();
        try {
            Database database = DatabaseUtil.getDatabaseInstance(context, Constants.YOUTH_CONNECT_DATABASE);

            View productView = database.getView("products");
            productView.setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    if(TYPE_QA.equals(document.get(TYPE))){
                        emitter.emit(document.get(QA_UPDATED_TIMESTAMP), document.get(QA_TITLE));
                    }
                }
            }, "1");

            Query query = productView.createQuery();
            query.setMapOnly(true);
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                String productName = (String) row.getValue();
                docIds.add(productName);
                Log.w("MYAPP", "Product named %s", productName);
            }
        } catch(CouchbaseLiteException exception){
            android.util.Log.e(TAG, "Error", exception);
        } catch (IOException exception){
            com.couchbase.lite.util.Log.e(TAG, "onDeleteClick()", exception);
        }

        return docIds;
    }

    public static void setDashboardCountInfo(Context context){
        int numberOfNodalOfficers = 0;
        try {
            DBHelper dbHelper = new DBHelper(context);
            List<NodalUser> nodalOfficerUsers = dbHelper.getAllNodalUsers();
            dbHelper.close();

            if (nodalOfficerUsers != null
                    && nodalOfficerUsers.size() > 0) {
                numberOfNodalOfficers = nodalOfficerUsers.size();
                context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit()
                        .putInt(Constants.SP_KEY_COUNT_NODAL_OFFICERS, numberOfNodalOfficers).commit();
            } else{
                context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit()
                        .putInt(Constants.SP_KEY_COUNT_NODAL_OFFICERS, 0).commit();
            }
        } catch(Exception exception){
            android.util.Log.e("DashboardFragment", "error", exception);
        }

        int numberOfPendingQuestions = 0;
        try {
            if (QAUtil.getPendingQuestionAndAnswerList(context) != null
                    && QAUtil.getPendingQuestionAndAnswerList(context).size() > 0) {
                numberOfPendingQuestions = QAUtil.getPendingQuestionAndAnswerList(context).size();
                context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit()
                        .putInt(Constants.SP_KEY_COUNT_PENDING_QUESTIONS, numberOfPendingQuestions).commit();
            } else{
                context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit()
                        .putInt(Constants.SP_KEY_COUNT_PENDING_QUESTIONS, 0).commit();
            }
        } catch(CouchbaseLiteException exception){
            android.util.Log.e("DashboardFragment", "fetchData()", exception);
        } catch(IOException exception){
            android.util.Log.e("DashboardFragment", "fetchData()", exception);
        } catch(Exception exception){
            android.util.Log.e("DashboardFragment", "fetchData()", exception);
        }

        int numberOfAnsweredQuestion = 0;
        try {
            if (QAUtil.getAnsweredQuestionAndAnswerList(context) != null
                    && QAUtil.getAnsweredQuestionAndAnswerList(context).size() > 0) {
                numberOfAnsweredQuestion = QAUtil.getAnsweredQuestionAndAnswerList(context).size();
                context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit()
                        .putInt(Constants.SP_KEY_COUNT_QUESTIONS_ANSWERED, numberOfAnsweredQuestion).commit();
            } else{
                context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit()
                        .putInt(Constants.SP_KEY_COUNT_QUESTIONS_ANSWERED, 0).commit();
            }
        } catch(CouchbaseLiteException exception){
            android.util.Log.e("DashboardFragment", "fetchData()", exception);
        } catch(IOException exception){
            android.util.Log.e("DashboardFragment", "fetchData()", exception);
        } catch(Exception exception){
            android.util.Log.e("DashboardFragment", "fetchData()", exception);
        }

        int numberOfPublishedDoc = 0;
        try {
            if (DocUtil.getPublishedDocList(context) != null
                    && DocUtil.getPublishedDocList(context).size() > 0) {
                numberOfPublishedDoc = DocUtil.getPublishedDocList(context).size();
                context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit()
                        .putInt(Constants.SP_KEY_COUNT_SHOWCASE_EVENTS, numberOfPublishedDoc).commit();
            } else {
                context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit()
                        .putInt(Constants.SP_KEY_COUNT_SHOWCASE_EVENTS, 0).commit();
            }
        } catch(CouchbaseLiteException exception){
            android.util.Log.e("DashboardFragment", "fetchData()", exception);
        } catch(IOException exception){
            android.util.Log.e("DashboardFragment", "fetchData()", exception);
        } catch(Exception exception){
            android.util.Log.e("DashboardFragment", "fetchData()", exception);
        }

        int docCount = 0;
        try {
            if (DocUtil.getAllDocList(context) != null
                    && DocUtil.getAllDocList(context).size() > 0) {
                docCount = DocUtil.getAllDocList(context).size();
                context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit()
                        .putInt(Constants.SP_KEY_COUNT_DOCUMENT, docCount).commit();
            } else{
                context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit()
                        .putInt(Constants.SP_KEY_COUNT_DOCUMENT, 0).commit();
            }
        } catch (CouchbaseLiteException exception){
            android.util.Log.e("DashboardFragment", "fetchData()", exception);
        } catch(IOException exception){
            android.util.Log.e("DashboardFragment", "fetchData()", exception);
        } catch (Exception exception){
            android.util.Log.e("DashboardFragment", "fetchData()", exception);
        }
    }
}
