package com.example.seg2105;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UserView {
    private String username;
    public String role;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseAuth mAuth =  FirebaseAuth.getInstance();;
    public String id;

    public UserView(String username, String role, String id){
        this.username = username;
        this.role = role;
        this.id = id;
    }

    public String getUsername(){
        return this.username;
    }
    public String getRole(){return this.role;}

    public static UserView createUser(String username, String user_email, String user_password, String user_role, customCallback... cb){
        DocumentReference finalUser_role = null;
        switch(user_role){
            case "instructor":
                finalUser_role = db.document("roles/3xQrDfZhc7Kdjr9TTueY");
                break;
            case "member":
                finalUser_role = db.document("/roles/KhXfzrrVCK2dJtSoQeWX");
                break;
        }
        DocumentReference finalUser_role1 = finalUser_role;
        final String[] user_id = new String[1];
        mAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    System.out.println("Created user successfully");
                    user_id[0] = task.getResult().getUser().getUid();
                    Map<String, Object> data1 = new HashMap<>();
                    data1.put("user_id", user_id[0]);
                    data1.put("username", username);
                    data1.put("role", finalUser_role1);
                    db.collection("users").add(data1);
                    if(cb[0] != null){
                        cb[0].onSuccess(task);
                    }

                } else {
                    if(cb[0] != null) {
                        cb[0].onError(task.getException());
                    }
                }
            }
        });

        return new UserView(username, user_role, user_id[0]);

    }

    public static interface GetUserInterface{
        void onSuccess(UserView user);
    }

    public static void getUser(String user_id, GetUserInterface method) throws ExecutionException, InterruptedException, InvocationTargetException, IllegalAccessException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("calling");
                try {
                    System.out.println("loading users");
                    ArrayList<UserView> users = new ArrayList<UserView>();

                    QuerySnapshot task = Tasks.await(db.collection("users").whereEqualTo("user_id", user_id).get());


                    DocumentSnapshot document = task.getDocuments().get(0);
                    DocumentSnapshot role_task = Tasks.await(document.getDocumentReference("role").get());
                    String user_name = document.get("username").toString();
                    String user_role = role_task.get("name").toString();
                    UserView temp_user = new UserView(user_name, user_role, user_id);
                    Object[] parameters = new Object[1];
                    parameters[0] = temp_user;
                    method.onSuccess(temp_user);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
