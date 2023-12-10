package edu.northeastern.finalproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.northeastern.finalproject.Adapter.DatesAdapter;
import edu.northeastern.finalproject.data.UserDailyRecord;

public class PhotoFragment extends Fragment {
    private Uri photoUri;
    private Button takePhotoBtn;
    private Button uploadPhotoBtn;
    private RecyclerView datesRecyclerView;
    private ActivityResultLauncher<Intent> takePhotoLauncher;
    private ActivityResultLauncher<String> pickPhotoLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;

    private DatesAdapter datesAdapter;


    public PhotoFragment() {
        // Required empty public constructor
    }

    public static PhotoFragment newInstance() {
        return new PhotoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue with opening camera.
                        startCameraIntent();
                    } else {
                        // Handle the case where the user denies the permission.
                    }
                }
        );
        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        uploadPhotoToFirebase(photoUri);
                        // The photoUri contains the path to the saved photo
                        // Use the photoUri for uploading to Firebase or other operations
                    }
                });

        pickPhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    // Handle the photo picked from the gallery
                    if (uri != null) {
                        // Use the uri for uploading to Firebase
                        uploadPhotoToFirebase(uri);
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        takePhotoBtn = view.findViewById(R.id.takePhotoBtn);
        uploadPhotoBtn = view.findViewById(R.id.uploadPhotoBtn);
        datesRecyclerView = view.findViewById(R.id.datesRecyclerView);


        setupButtons();
        setupRecyclerView();
        fetchUserPhotos();

        return view;
    }

    private void setupRecyclerView() {
        datesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        datesAdapter = new DatesAdapter(new ArrayList<>()); // should set up adapter right after recyclerView
        datesRecyclerView.setAdapter(datesAdapter);
    }

    private void setupButtons() {
        takePhotoBtn.setOnClickListener(v -> takePhoto());
        uploadPhotoBtn.setOnClickListener(v -> pickPhoto());
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCameraIntent();
        }
    }

    private void startCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create a File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("PhotoFragment", "Error occurred while creating the File", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(getContext(),
                        "edu.northeastern.finalproject.fileprovider", // Replace with your file provider authority
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePhotoLauncher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private void pickPhoto() {
        pickPhotoLauncher.launch("image/*");
    }


    private void uploadPhotoToFirebase(Uri photoUri) {
        String userId = getUserId();
        if (userId == null) {
            return;
        }
        StorageReference storageRef = FirebaseUtil.getStorage().getReference();
        StorageReference photoRef = storageRef.child("users/" + userId + "/photos/" + System.currentTimeMillis() + ".jpg");

        photoRef.putFile(photoUri).addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
            updatePhotoUrlInFirestore(downloadUri.toString(), userId);
        })).addOnFailureListener(e -> {
            // Handle failure
        });
    }
    private void updatePhotoUrlInFirestore(String photoUrl, String userId) {
        String currentDate = getCurrentDate();
        DocumentReference dailyRecordRef = FirebaseUtil.getFirestore()
                .collection("users")
                .document(userId)
                .collection("dailyRecords")
                .document(currentDate);

        // Get the current date as a java.util.Date object
        Date date = new Date();

        dailyRecordRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Daily record exists, update the photoUrls array
                    dailyRecordRef.update("photoUrls", FieldValue.arrayUnion(photoUrl))
                            .addOnSuccessListener(aVoid -> fetchUserPhotos()) // only call fetchUserPhoto() after success, or the function might be called before database updates, then you might face new photo not show (asyn)
                            .addOnFailureListener(e -> Log.e("PhotoFragment", "Error updating Firestore", e));
                } else {
                    // Daily record does not exist, create a new one with the photoUrl
                    List<String> photoUrls = new ArrayList<>();
                    photoUrls.add(photoUrl);
                    UserDailyRecord newRecord = new UserDailyRecord(userId, date, null, photoUrls, 0, 0.0, null);
                    dailyRecordRef.set(newRecord)
                            .addOnSuccessListener(aVoid -> fetchUserPhotos())
                            .addOnFailureListener(e -> Log.e("PhotoFragment", "Error creating Firestore document", e));
                }
            } else {
                Log.e("PhotoFragment", "Error getting daily record", task.getException());
            }
        });
    }

    private String getUserId() {
        if (FirebaseUtil.getAuth().getCurrentUser() != null) {
            return FirebaseUtil.getAuth().getCurrentUser().getUid();
        } else {
            // Handle the case where the user is not logged in
            return null;
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private void fetchUserPhotos() {
        String userId = getUserId();
        if (userId == null) {
            // Handle not logged in user
            return;
        }

        FirebaseUtil.getFirestore().collection("users").document(userId)
                .collection("dailyRecords")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<UserDailyRecord> records = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            UserDailyRecord record = documentSnapshot.toObject(UserDailyRecord.class);
                            if (record != null && record.getPhotoUrls() != null && !record.getPhotoUrls().isEmpty()) {
                                records.add(record);
                            }
                        }
                        updateRecyclerView(records);
                    } else {
                        updateRecyclerView(new ArrayList<>()); // Pass an empty list to clear the adapter
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });


    }


    private void updateRecyclerView(List<UserDailyRecord> records) {
//        for (UserDailyRecord record : records) {
//            Log.e("PhotoFragment", "Record: " + record.toString());
//        }
        if (records != null){
            datesAdapter.setRecords(records); // Update the adapter's data
            datesAdapter.notifyDataSetChanged(); // Notify the adapter of the dataset change
        }
    }

}

