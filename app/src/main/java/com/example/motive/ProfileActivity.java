package com.example.motive;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.Objects;

public class  ProfileActivity extends AppCompatActivity {

    ImageView backImageView;
    Button resendCodeButton;
    String userId;
    TextView username, halls, bio, motives, degree, mainUserMotive;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ImageView profileImage;
    Button editProfileFields;
    StorageReference storageReference;

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started.");

        backImageView = findViewById(R.id.backImageView);
        fAuth = FirebaseAuth.getInstance();
        resendCodeButton = findViewById(R.id.resendCodeButton);

        username = findViewById(R.id.profileUsername);
        halls = findViewById(R.id.profileHalls);
        bio = findViewById(R.id.profileUserBio);
        mainUserMotive = findViewById(R.id.mainMotive);
        motives = findViewById(R.id.profileMotives);
        degree = findViewById(R.id.profileDegree);
        profileImage = findViewById(R.id.displayImageView);
        editProfileFields = findViewById(R.id.editprofileButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //Profile image reference for each user registered, seperate directory
        StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                username.setText(documentSnapshot.getString("username"));
                halls.setText(documentSnapshot.getString("halls"));
                bio.setText(documentSnapshot.getString("user bio"));
                degree.setText(documentSnapshot.getString("degree"));
                mainUserMotive.setText(documentSnapshot.getString("main motive"));
                motives.setText(documentSnapshot.getString("other motives"));
            }
        });

        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        final FirebaseUser user = fAuth.getCurrentUser();

        if (!user.isEmailVerified()) {
            resendCodeButton.setVisibility(View.VISIBLE);
            resendCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "Verification email has been sent.", Toast.LENGTH_SHORT).show();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Email not send " + e.getMessage());
                        }
                    });
                }
            });

        }

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editProfileFields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent edit;
                edit = new Intent(getBaseContext(),
                        UpdateProfileActivity.class);
                startActivity(edit);
            }
        });

    }
}
