package com.food_on.app.ChefFoodPanel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.food_on.app.Chef;
import com.food_on.app.MainActivity;
import com.food_on.app.R;

import com.food_on.app.Chef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;
import java.util.UUID;

public class Chef_PostDish extends AppCompatActivity {


    ImageButton imageButton;
    Button post_dish;
    Spinner Dishes;
    EditText desc, qty, pri;
    String description, quantity, price, dishes;
    Uri imageuri;
    private Uri mCropimageuri;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference dataaa;
    FirebaseAuth FAuth;
    StorageReference ref;
    String ChefId;
    String RandomUId;
    String State, City, Sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef__post_dish);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Dishes = (Spinner) findViewById(R.id.dishes);
        desc =  findViewById(R.id.description);
        qty = findViewById(R.id.quantity);
        pri = findViewById(R.id.price);
        post_dish = (Button) findViewById(R.id.post);
        FAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getInstance().getReference("FoodSupplyDetails");




        try {
            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            dataaa = firebaseDatabase.getInstance().getReference("Chef").child(userid);
            dataaa.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Chef chefc = dataSnapshot.getValue(Chef.class);
                    if (chefc != null) {
                        State = chefc.getState();
                        City = chefc.getCity();
                        Sub = chefc.getSuburban();
                    }else{

                    }
                    imageButton = (ImageButton) findViewById(R.id.imageupload);
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSelectImageclick(v);
                        }
                    });


                    post_dish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dishes = Dishes.getSelectedItem().toString().trim();
                            description = desc.getText().toString().trim();
                            quantity = qty.getText().toString().trim();
                            price = pri.getText().toString().trim();

                            if (isValid()) {
                                uploadImage();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {

            Log.e("Error: ", e.getMessage());
        }
    }

    private boolean isValid() {
        desc.setError(null);
        qty.setError(null);
        pri.setError(null);

        boolean isValiDescription = false, isValidPrice = false, isvalidQuantity = false, isValid = false;
        if (TextUtils.isEmpty(description)) {
            desc.setError("Description is Required");
        } else {
            isValiDescription = true;
        }

        if (TextUtils.isEmpty(quantity)) {
            qty.setError("Quantity is Required");
        } else {
            isvalidQuantity = true;
        }

        if (TextUtils.isEmpty(price)) {
            pri.setError("Price is Required");
        } else {
            isValidPrice = true;
        }

        isValid = isValiDescription && isvalidQuantity && isValidPrice;

        return isValid;
    }


    private void uploadImage() {

        if (imageuri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(Chef_PostDish.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            RandomUId = UUID.randomUUID().toString();
            ref = storageReference.child(RandomUId);
            ChefId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FoodSupplyDetails info = new FoodSupplyDetails(dishes, quantity, price, description, String.valueOf(uri), RandomUId, ChefId);
                            FirebaseDatabase.getInstance().getReference("FoodSupplyDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(RandomUId)
                                    .setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Chef_PostDish.this, "Dish posted successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(Chef_PostDish.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    progressDialog.setCanceledOnTouchOutside(false);
                }
            });
        }

    }

    private void startCropImageActivity(Uri imageuri){
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }
    private void onSelectImageclick(View v){
        CropImage.startPickImageActivity(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mCropimageuri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCropImageActivity(mCropimageuri);
        } else {
            Toast.makeText(this, "Cancelling! Permission Not Granted", Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            imageuri = CropImage.getPickImageResultUri(this,data);
            if(CropImage.isReadExternalStoragePermissionsRequired(this,imageuri)){
                mCropimageuri = imageuri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }else{
                startCropImageActivity(imageuri);
            }
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                ((ImageButton) findViewById(R.id.imageupload)).setImageURI(result.getUri());
                Toast.makeText(this,"Cropped Successfully!",Toast.LENGTH_SHORT).show();
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toast.makeText(this,"Failed To Crop"+result.getError(),Toast.LENGTH_SHORT).show();

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}


