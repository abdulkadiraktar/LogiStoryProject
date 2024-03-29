package com.abdulkadiraktar.logistory.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.abdulkadiraktar.logistory.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding; //İZİN AKTİVESİ
    private FirebaseAuth auth; //VERİTABANI AKTİVESİ
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); //binding kodları
        View view = binding.getRoot(); //binding oluşturma kodları
        setContentView(view);

        auth = FirebaseAuth.getInstance(); //Authentication oluşturma kodu

        FirebaseUser user = auth.getCurrentUser(); //online olan kullanıcı

        if (user != null){ //kullanıcı giriş yapmışsa uygulamadan çıktığında oturumun açık kalması için
            Intent intent = new Intent(MainActivity.this,FeedActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void signInClicked(View view){ //giriş yap butonuna tıklandığında


        String email= binding.emailText.getText().toString(); //e mail adresini Stringe dönüştürme ve tutmak için
        String password = binding.passwordText.getText().toString();//parolayı Stringe dönüştürmek ve tutmak için

        if(email.equals("") || password.equals("")){ //mail adresi veya parola girilmezse program çökmemesi için uyarı mesajı
            Toast.makeText(this,"E-mail ve parolanızı giriniz!",Toast.LENGTH_LONG).show();
        }
        else {

            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) { //EMAİL VEYA PAROLA KAYDINIDA BAŞARILI OLUNDUĞUNDA OLUŞAN METHOD

                    Intent intent = new Intent(MainActivity.this, FeedActivity.class);//üyelik oluştuğunda feed aktivitesine gidiliyor
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() { //KULLANICI KAYDINDA HATA OLUŞTUĞUNDA KULLANILACAK METHOD
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show(); //hata mesajı
                }}
            );
        }
    }
    public void signUpClicked(View view){ // yeni üyelik butonuna tıklandığında

        String email= binding.emailText.getText().toString(); //e mail adresini Stringe dönüştürme ve tutmak için
        String password = binding.passwordText.getText().toString();//parolayı Stringe dönüştürmek ve tutmak için

        if(email.equals("") || password.equals("")){ //mail adresi veya parola girilmezse program çökmemesi için uyarı mesajı
            Toast.makeText(this,"E-mail ve parolanızı giriniz!",Toast.LENGTH_LONG).show();
        }
        else  {

            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) { //EMAİL VEYA PAROLA KAYDINIDA BAŞARILI OLUNDUĞUNDA OLUŞAN METHOD

                    Intent intent = new Intent(MainActivity.this, FeedActivity.class);//üyelik oluştuğunda feed aktivitesine gidiliyor
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() { //KULLANICI KAYDINDA HATA OLUŞTUĞUNDA KULLANILACAK METHOD
                @Override
                public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show(); //hata mesajı
                }}
            );
        }


    }
}