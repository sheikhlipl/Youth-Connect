package com.lipl.youthconnect.youth_connect.util;

import android.content.Context;
import android.os.Parcel;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.lipl.youthconnect.youth_connect.pojo.Answer;
import com.lipl.youthconnect.youth_connect.pojo.Comment;
import com.lipl.youthconnect.youth_connect.pojo.District;
import com.lipl.youthconnect.youth_connect.pojo.Doc;
import com.lipl.youthconnect.youth_connect.pojo.NodalUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Android Luminous on 2/20/2016.
 */
public class MasterDataUtil {
    private static final String TAG = "MasterDataUtil";

    public static String createDocument(Database database, List<District> districts,
                                  List<NodalUser> nodalUsers) {
        // Create a new document and add data
        Document document = database.createDocument();
        String documentId = document.getId();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(DatabaseUtil.DISTRICTS, districts);
        map.put(DatabaseUtil.NODAL_OFFICERS, nodalUsers);
        try {
            // Save the properties to the document
            document.putProperties(map);
            Log.i(TAG, "Document created.");
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
        return documentId;
    }

    public static List<District> getDistrictList(Context context) throws CouchbaseLiteException,
            IOException, Exception {
        List<District> districts = new ArrayList<District>();

        Database database = DatabaseUtil.getDatabaseInstance(context, Constants.YOUTH_CONNECT_DATABASE);
        List<String> allDocIds = DatabaseUtil.getAllDocumentIds(context);
        for(String doc_id : allDocIds){
            Document document = DatabaseUtil.getDocumentFromDocumentId(database, doc_id);
            ArrayList<LinkedHashMap<String, Object>> district_list =
                    (ArrayList<LinkedHashMap<String, Object>>) document.getProperty(DatabaseUtil.DISTRICTS);

            if(district_list != null && district_list.size() > 0){
                for(int i = 0; i <district_list.size(); i++){
                    LinkedHashMap<String, Object> answers = (LinkedHashMap<String, Object>) (district_list.get(i));
                    if((answers.get("m_district") != null)
                            && (answers.get("modifiedDate") != null)) {
                        District answer = new District(Parcel.obtain());
                        answer.setM_district_id((Integer) answers.get("m_district_id"));
                        answer.setM_district((String) answers.get("m_district"));
                        answer.setModifiedDate((String) answers.get("modifiedDate"));
                        districts.add(answer);
                    }
                }
            }
        }
        return districts;
    }

    public static List<NodalUser> getNodalUsersList(Context context) throws CouchbaseLiteException,
            IOException, Exception {
        List<NodalUser> nodalUsers = new ArrayList<NodalUser>();

        Database database = DatabaseUtil.getDatabaseInstance(context, Constants.YOUTH_CONNECT_DATABASE);
        List<String> allDocIds = DatabaseUtil.getAllDocumentIds(context);
        for(String doc_id : allDocIds){
            Document document = DatabaseUtil.getDocumentFromDocumentId(database, doc_id);
            ArrayList<LinkedHashMap<String, Object>> nodal_user_list =
                    (ArrayList<LinkedHashMap<String, Object>>) document.getProperty(DatabaseUtil.NODAL_OFFICERS);

            if(nodal_user_list != null && nodal_user_list.size() > 0){
                for(int i = 0; i <nodal_user_list.size(); i++){
                    LinkedHashMap<String, Object> answers = (LinkedHashMap<String, Object>) (nodal_user_list.get(i));
                    if((answers.get("full_name") != null)
                            && (answers.get("m_district_id") != null)) {
                        NodalUser nodalUser = new NodalUser(Parcel.obtain());
                        nodalUser.setUser_id((Integer) answers.get("user_id"));
                        nodalUser.setFull_name((String) answers.get("full_name"));
                        nodalUser.setM_district_id((String) answers.get("m_district_id"));
                        nodalUsers.add(nodalUser);
                    }
                }
            }
        }
        return nodalUsers;
    }
}
