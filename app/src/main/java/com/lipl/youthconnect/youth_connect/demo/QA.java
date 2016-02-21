package com.lipl.youthconnect.youth_connect.demo;

import android.app.Application;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.util.Log;
import com.lipl.youthconnect.youth_connect.pojo.Answer;
import com.lipl.youthconnect.youth_connect.pojo.Comment;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Android Luminous on 2/21/2016.
 */
public class QA {

    private static final String VIEW_NAME = "questionanswers";
    private static final String DOC_TYPE = "questionanswer";

    public static Query getQuery(Database database) {
        com.couchbase.lite.View view = database.getView(VIEW_NAME);
        if (view.getMap() == null) {
            Mapper mapper = new Mapper() {
                public void map(Map<String, Object> document, Emitter emitter) {
                    String type = (String)document.get("type");
                    if (DOC_TYPE.equals(type)) {
                        emitter.emit(document.get(DatabaseUtil.QA_TITLE), document);
                        emitter.emit(document.get(DatabaseUtil.QA_DESC), document);
                        emitter.emit(document.get(DatabaseUtil.QA_UPDATED_TIMESTAMP), document);
                        emitter.emit(document.get(DatabaseUtil.QA_ASKED_BY_USER_NAME), document);
                        emitter.emit(document.get(DatabaseUtil.QA_ASKED_BY_USER_ID), document);
                        emitter.emit(document.get(DatabaseUtil.QA_IS_ANSWERED), document);
                        emitter.emit(document.get(DatabaseUtil.QA_IS_PUBLISHED), document);
                        emitter.emit(document.get(DatabaseUtil.QA_IS_UPLOADED), document);
                        emitter.emit(document.get(DatabaseUtil.QA_ANSWER), document);
                        emitter.emit(document.get(DatabaseUtil.QA_COMMENT), document);
                    }
                }
            };
            view.setMap(mapper, "1");
        }

        Query query = view.createQuery();

        return query;
    }

    private String createDocument(Database database, String description,
                                  String title, String user_name,
                                  int is_answered, int is_published, int asked_by_user_id) {
        // Create a new document and add data
        Document document = database.createDocument();
        String documentId = document.getId();
        String currentTimestamp = System.currentTimeMillis()+"";

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(DatabaseUtil.QA_TITLE, title);
        map.put(DatabaseUtil.QA_DESC, description);
        map.put(DatabaseUtil.QA_UPDATED_TIMESTAMP, currentTimestamp);
        map.put(DatabaseUtil.QA_ASKED_BY_USER_NAME, user_name);
        map.put(DatabaseUtil.QA_ASKED_BY_USER_ID, asked_by_user_id);
        map.put(DatabaseUtil.QA_IS_ANSWERED, is_answered);
        map.put(DatabaseUtil.QA_IS_PUBLISHED, is_published);
        map.put(DatabaseUtil.QA_IS_UPLOADED, 0);
        map.put("type", DOC_TYPE);
        map.put(DatabaseUtil.QA_ANSWER, new ArrayList<Answer>());
        map.put(DatabaseUtil.QA_COMMENT, new ArrayList<Comment>());
        try {
            // Save the properties to the document
            document.putProperties(map);
            android.util.Log.i("QA", "Document created.");
        } catch (CouchbaseLiteException e) {
            android.util.Log.e("QA", "Error putting", e);
        }
        return documentId;
    }

    public static void updateCheckedStatus(Document task, boolean checked)
            throws CouchbaseLiteException {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.putAll(task.getProperties());
        if(checked) {
            properties.put(DatabaseUtil.QA_IS_UPLOADED, 1);
        } else{
            properties.put(DatabaseUtil.QA_IS_UPLOADED, 0);
        }
        task.putProperties(properties);
    }

    public static void deleteTask(Document qa) throws CouchbaseLiteException {
        qa.delete();
        Log.d("QA", "Deleted doc: %s", qa.getId());

    }
}