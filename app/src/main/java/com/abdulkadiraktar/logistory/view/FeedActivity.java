package com.abdulkadiraktar.logistory.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.abdulkadiraktar.logistory.R;
import com.abdulkadiraktar.logistory.adapter.PostAdapter;
import com.abdulkadiraktar.logistory.databinding.ActivityFeedBinding;
import com.abdulkadiraktar.logistory.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Post> postArrayList;
    private ActivityFeedBinding binding;
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        postArrayList = new ArrayList<>();

        auth = FirebaseAuth.getInstance(); // Firebase initialization edildi
        firebaseFirestore = FirebaseFirestore.getInstance();
        getData();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);
    }

    private void getData(){ //verileri çektiğimiz anasayfa için gerekli metod
        //whereequalto

        //post ekranı
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error != null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(),Toast.LENGTH_LONG).show(); //uyarı mesajı

                }
                if(value != null){

                    for(DocumentSnapshot snapshot : value.getDocuments()){

                        Map<String, Object> data = snapshot.getData(); //map oluiturma

                        //Casting yapma
                        String userEmail = (String) data.get("useremail");
                        String comment = (String) data.get("comment");
                        String downloadUrl = (String) data.get("downloadurl");

                        Post post = new Post(userEmail,comment,downloadUrl);
                        postArrayList.add(post);


                    }
                    postAdapter.notifyDataSetChanged(); //yapılan tüm değişiklikler post ekleme gibi anlık olarak kontrol edilir

                }


            }
        });
    }

    public void addPost(View view){ //ADD POST BUTONUNA TIKLANDIĞINDA
        //Upload activitesine gidilir
        Intent intentToUpload = new Intent(FeedActivity.this, UploadActivity.class);
        startActivity(intentToUpload);
    }



    public void signOutButton(View view){ //çıkış yap butonuna tıklandığında

        auth.signOut();

        //oluşturulan kullanıcı logout olur ve main aktivitesine dönülür
        Intent intentToMain = new Intent(FeedActivity.this, MainActivity.class);
        startActivity(intentToMain);
        finish();

    }
}