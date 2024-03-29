package com.abdulkadiraktar.logistory.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.abdulkadiraktar.logistory.databinding.ActivityUploadBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
    Uri imageData; //görsel url
    ActivityResultLauncher<Intent> activityResultLauncher; //izin için gerekli launcher
    ActivityResultLauncher<String> permissionLauncher; //izin isteme launcherı
    Bitmap selectedImage;
    private FirebaseStorage firebaseStorage; //data aktiveleri
    private FirebaseAuth auth; //data aktiveleri

    private FirebaseFirestore firebaseFirestore; //data aktiveleri
    private ActivityUploadBinding binding; //string, imageview gibi yapıların aktiveleri
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view); //METOD BAŞLANGIÇ KODLARI

        registerLauncher(); //METOD ÇAĞIRIMI

        firebaseStorage = FirebaseStorage.getInstance(); // veritabanı fotoğraf dosyaları
        auth = FirebaseAuth.getInstance(); //KAYIT OL , GİRİŞ BUTONU AKTİVELERİ İÇİN GEREKLİ
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
    }


    public void uploadButton(View view){ //Paylaş butonuna tıklandığında

        if(imageData != null){ //kullanıcı galeriden fotoğraf seçtimi diye kontrol ediyoruz

            //universal unique id
            UUID uuid = UUID.randomUUID(); //fotoğraf dosyalarının isimlerini farklı farklı olması için
            String imageName ="images/" + uuid + ".jpg"; //VERİTABANINDAN ÇEKİLEN İDLERİ ÇEKME

            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //url indir
                StorageReference newReference = firebaseStorage.getReference(imageName);
                newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl= uri.toString();
                        String comment = binding.commentText.getText().toString();
                        FirebaseUser user = auth.getCurrentUser();
                        String email = user.getEmail();

                        HashMap<String, Object> postData = new HashMap<>();
                        postData.put("useremail",email);
                        postData.put("downloadurl",downloadUrl);
                        postData.put("comment",comment);
                        postData.put("date", FieldValue.serverTimestamp());

                        firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Intent intent = new Intent(UploadActivity.this,FeedActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void selectImage(View view){

        //KULLANICIDAN GALERİ VERİLERİ İÇİN İZİN İSTEME (PERMİSSİON)

        //İZİN YOKSA izin iste açıklama yap
if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)) {
            Snackbar.make(view, "Galeriye erişmek için izin gerekiyor.", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //izin iste açıklama yapıldı
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }).show();
        }
        else
        {
            //izin iste açıklamadan sonra
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        }
    }
    else{//izin verilmiş galeriye git
        Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //GALERİYE GİDİP MEDİA TUTMA
        activityResultLauncher.launch(intentToGallery);
    }
}


else {
    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar.make(view, "Galeriye erişmek için izin gerekiyor.", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //izin iste açıklama yapıldı
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }).show();
        }
        else
        {
            //izin iste açıklamadan sonra
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }
    else{//izin verilmiş galeriye git
        Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //GALERİYE GİDİP MEDİA TUTMA
        activityResultLauncher.launch(intentToGallery);
    }
}


}

    private void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) { //almak istediğimiz verinin kontrolü
            if (result.getResultCode() == RESULT_OK){ //HER ŞEY TAMAM İSE
                Intent intentFromResult = result.getData();
                if(intentFromResult != null){
                    imageData = intentFromResult.getData();
                    binding.imageView.setImageURI(imageData); // image uri oluşturma

                }
            }
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
                }
                else {
                    Toast.makeText(UploadActivity.this,"İzin gerekiyor",Toast.LENGTH_LONG).show();


                }
            }
        });

    }



}