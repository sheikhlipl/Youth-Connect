package com.lipl.youthconnect.youth_connect.util;

import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import com.couchbase.lite.Document;
import com.lipl.youthconnect.youth_connect.pojo.Answer;
import com.lipl.youthconnect.youth_connect.pojo.AssignedToUSer;
import com.lipl.youthconnect.youth_connect.pojo.Comment;
import com.lipl.youthconnect.youth_connect.pojo.Doc;
import com.lipl.youthconnect.youth_connect.pojo.FileToUpload;
import com.lipl.youthconnect.youth_connect.pojo.Question;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Android Luminous on 2/18/2016.
 */
public class DocUtil {

    public static Doc getDocFromDocument(Document document){

        try {

            String doc_id = document.getId();

            String title = (String) document.getProperty(DatabaseUtil.DOC_TITLE);
            String docPurpose = (String) document.getProperty(DatabaseUtil.DOC_PURPOSE);
            String updated_time_stamp = (String) document.getProperty(DatabaseUtil.DOC_CREATED);

            Long timeStamp = Long.parseLong(updated_time_stamp);
            String postDate = Util.getDateAndTimeFromTimeStamp(timeStamp);
            String user_name = (String) document.getProperty(DatabaseUtil.DOC_CREATED_BY_USER_NAME);
            int created_by_user_id = (Integer) document.getProperty(DatabaseUtil.DOC_CREATED_BY_USER_ID);
            int is_uploaded = (Integer) document.getProperty(DatabaseUtil.DOC_IS_UPLOADED);

            ArrayList<String> fileList = (ArrayList<String>) document.getProperty(DatabaseUtil.DOC_FILES);
            ArrayList<LinkedHashMap<String, Object>> assigned_user_ids = (ArrayList<LinkedHashMap<String, Object>>) document.getProperty(DatabaseUtil.DOC_ASSIGNED_TO_USER_IDS);

            ArrayList<FileToUpload> files = new ArrayList<FileToUpload>();
            if(fileList != null && fileList.size() > 0){
                for(int i = 0; i <fileList.size(); i++){
                    String file_name =  fileList.get(i);
                    if (file_name != null){

                        String host = "http://192.168.1.107";
                        String port = "4984";
                        String dbName = Constants.YOUTH_CONNECT_DATABASE;
                        /*
                        * url to download file :
                        * http://192.168.1.107:4984/youth_connect/{doc_id}/{file_name}
                        * */
                        String download_link = "http://192.168.1.107:4984/youth_connect/" + doc_id + "/" + file_name;
                        FileToUpload fileToUpload = new FileToUpload();
                        fileToUpload.setDownload_link_url(download_link);
                        fileToUpload.setFile_name(file_name);
                        files.add(fileToUpload);
                    }
                }
            }

            ArrayList<AssignedToUSer> ausers = new ArrayList<AssignedToUSer>();
            if(assigned_user_ids != null && assigned_user_ids.size() > 0){
                for(int i = 0; i <assigned_user_ids.size(); i++){
                    LinkedHashMap<String, Object> users = (LinkedHashMap<String, Object>) (assigned_user_ids.get(i));
                    if((users.get("user_name") != null)
                            && (users.get("user_id") != null)){
                        AssignedToUSer answer = new AssignedToUSer();
                        answer.setUser_name((String)users.get("user_name"));
                        answer.setUser_id((Integer)users.get("user_id"));
                        ausers.add(answer);
                    }
                }
            }

            Doc doc = new Doc();
            doc.setDoc_id(doc_id);
            doc.setDoc_title(title);
            doc.setDoc_purpose(docPurpose);
            doc.setCreated_by_user_name(user_name);
            doc.setCreated_by_user_id(created_by_user_id);
            doc.setIs_uploaded(is_uploaded);
            doc.setFileToUploads(files);
            doc.setDoc_assigned_to_user_ids(ausers);

            return doc;
        } catch(Exception exception){
            Log.e("QAUtil", "getQAFromDocument()", exception);
        }
        return null;
    }
}
