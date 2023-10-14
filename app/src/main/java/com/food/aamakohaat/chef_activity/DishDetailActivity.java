package com.food.aamakohaat.chef_activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.food.aamakohaat.R;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class DishDetailActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 1001;

    Button post_dish;
    Spinner Dishes;
    EditText desc, qty, pri;
    String description, quantity, price, dishes;
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
    ImageButton imageUploadButton; // Add ImageButton variable

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);

        TextInputLayout descriptionTextInputLayout = findViewById(R.id.description);
        TextInputLayout quantityTextInputLayout = findViewById(R.id.quantity);
        TextInputLayout priceTextInputLayout = findViewById(R.id.price);

        final String[] description = {descriptionTextInputLayout.getEditText().getText().toString().trim()};
        final String[] quantity = {quantityTextInputLayout.getEditText().getText().toString().trim()};
        final String[] price = {priceTextInputLayout.getEditText().getText().toString().trim()};

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Dishes = findViewById(R.id.dishes);

        post_dish = findViewById(R.id.post);
        imageUploadButton = findViewById(R.id.imageupload); // Initialize the ImageButton

        FAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("FoodSupplyDetails");

        try {
            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            dataaa = firebaseDatabase.getInstance().getReference("Chef").child(userid);

            dataaa.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    post_dish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dishes = Dishes.getSelectedItem().toString().trim();
                            description[0] = desc.getText().toString().trim();
                            quantity[0] = qty.getText().toString().trim();
                            price[0] = pri.getText().toString().trim();

                            if (isValid()) {
                                openImagePicker();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            Log.e("Errrrrr: ", e.getMessage());
        }

        // Set an OnClickListener for the ImageButton
        imageUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });
    }

    private boolean isValid() {
        boolean isValiDescription = false, isValidPrice = false, isvalidQuantity = false, isvalid = false;

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

        isvalid = (isValiDescription && isvalidQuantity && isValidPrice);

        return isvalid;
    }

    private void uploadImage(Uri imageuri) {
        if (imageuri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
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
                            firebaseDatabase.getInstance().getReference("FoodSupplyDetails")
                                    .child(State).child(City).child(Sub)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(RandomUId)
                                    .setValue(info)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            Toast.makeText(DishDetailActivity.this, "Dish posted successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(DishDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void openImagePicker() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Create a camera intent to capture a photo
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoUri = getOutputMediaFileUri(); // You need to implement this method
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        // Create a chooser intent to allow the user to select from camera or gallery
        Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
    }


    private Uri getOutputMediaFileUri() {
        File mediaFile;
        try {
            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "YourAppDirectoryName");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }

            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            return Uri.fromFile(mediaFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openCamera() {
        // Check if the app has permission to access the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Create an intent to open the camera
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Check if there is a camera app available to handle the intent
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                // Start the camera activity
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            } else {
                // Handle the case where no camera app is available
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Request camera permission from the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    // Handle the result of the camera activity

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Handle the captured image from the camera
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                if (imageBitmap != null) {
                    // Update the ImageButton with the captured image
                    imageUploadButton.setImageBitmap(imageBitmap);
                }
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Handle the selected image from the gallery
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                // Update the ImageButton with the selected image
                imageUploadButton.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
