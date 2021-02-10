package com.example.expovitadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddNewGameActivity extends AppCompatActivity {

    private ImageButton setGameImage, replaceGameImage;
    private ImageView setImageHolder;
    private EditText setGameName, setGameEan;
    private TextView eanHolder, linkHolder;
    private CheckBox setIsGameNew, setIsGamePreorder;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Button addNewGameConfirmed;
    private Spinner choosePlatformSpinner;

    private static int RESULT_LOAD_IMAGE;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_game);

        setGameName = findViewById(R.id.addGameNameText);
        setGameEan = findViewById(R.id.addGameEanText);
        setIsGameNew = findViewById(R.id.addIsNewCheck);
        setIsGamePreorder = findViewById(R.id.addIsPreorderCheck);
        setGameImage = findViewById(R.id.setGameImage);
        setImageHolder = findViewById(R.id.addNewGameImageHolder);
        replaceGameImage = findViewById(R.id.replaceGameImage);
        addNewGameConfirmed = findViewById(R.id.addNewGameButton);
        choosePlatformSpinner = findViewById(R.id.addNewGamePlatformSpinner);
        eanHolder = findViewById(R.id.eanHolder);
        linkHolder = findViewById(R.id.linkHolder);

        setIsGamePreorder.setChecked(false);
        setIsGameNew.setChecked(false);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, getResources().getStringArray(R.array.add_platform));
        choosePlatformSpinner.setAdapter(adapter);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("games");

        addNewGameConfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setGameName.getText().toString().isEmpty() || setGameEan.getText().toString().isEmpty()){
                    Toast.makeText(AddNewGameActivity.this, "Toate campurile trebuiesc completate.", Toast.LENGTH_SHORT).show();
                }
                if (setIsGamePreorder.isChecked() && setGameEan.getText().toString().contains("http")){
                    uploadFile();
                    finish();
                }else{
                    if (setIsGamePreorder.isChecked() && !setGameEan.getText().toString().contains("http")){
                        Toast.makeText(AddNewGameActivity.this, "Link-ul este invalid.", Toast.LENGTH_SHORT).show();
                    }else{
                        if (!setIsGamePreorder.isChecked() && setGameEan.getText().toString().length() != 13) {
                            Toast.makeText(AddNewGameActivity.this, "Lungimea codului ean trebuie sa fie 13.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!Character.toString(setGameEan.getText().charAt(12)).matches(String.valueOf(checkSum(setGameEan.getText().toString())))) {
                                Toast.makeText(AddNewGameActivity.this, "Codul ean este invalid.", Toast.LENGTH_SHORT).show();
                            } else {
                                uploadFile();
                                finish();
                            }
                        }
                    }
                }
                Log.i("EAN_LENGTH", "Lungimea codului ean este = " + setGameEan.getText().toString().length());
                //int check_sum = checkSum(setGameEan.getText().toString());
                //8864015670588
            }
        });

        setIsGamePreorder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    eanHolder.setVisibility(View.GONE);
                    linkHolder.setVisibility(View.VISIBLE);
                    setGameEan.setInputType(InputType.TYPE_CLASS_TEXT);
                    setEditTextMaxLength(setGameEan, Integer.MAX_VALUE);
                }else{
                    eanHolder.setVisibility(View.VISIBLE);
                    linkHolder.setVisibility(View.GONE);
                    setEditTextMaxLength(setGameEan, 13);
                    setGameEan.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }
        });
    }

    //Game Image Functionality
    public void loadImageFromGallery(View view){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData()!= null){
                selectedImage = data.getData();
                Picasso.get().load(selectedImage).fit().centerInside().into(setImageHolder);
                setGameImage.setVisibility(View.GONE);
                replaceGameImage.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Nu a fost selectată nicio imagine.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Toast.makeText(this, "Eroare la selectarea imaginii.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile(){
        if (selectedImage != null){
            //final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(selectedImage));
            String selectedImageName = setGameName.getText().toString().replaceAll("\\s+|[^a-zA-Z0-9]", "");
            final StorageReference fileReference = storageReference.child(selectedImageName + "." + getFileExtension(selectedImage));
            fileReference.putFile(selectedImage).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        String platform_chosen = null;
                        switch(choosePlatformSpinner.getSelectedItem().toString()){
                            case "Playstation 4":
                                platform_chosen = "ps4";
                                break;
                            case "Xbox One":
                                platform_chosen = "x1";
                                break;
                            case "Nintendo Switch":
                                platform_chosen = "ns";
                                break;
                            case "Nintendo 3DS":
                                platform_chosen = "3ds";
                                break;
                            case "PC":
                                platform_chosen = "pc";
                                break;
                            case "Playstation 3":
                                platform_chosen = "ps3";
                                break;
                            case "Xbox 360":
                                platform_chosen = "x360";
                                break;
                        }
                        Uri downloadUri = task.getResult();
                        String key = setGameName.getText().toString().replaceAll("\\s+|[^a-zA-Z0-9]", "");
                        Game game = new Game(setGameName.getText().toString().trim(), setGameEan.getText().toString().trim(), downloadUri.toString(), platform_chosen, Boolean.valueOf(setIsGamePreorder.isChecked()), Boolean.valueOf(setIsGameNew.isChecked()));
                        //String uploadId = databaseReference.push().getKey();
                        //databaseReference.child(uploadId).setValue(game);
                        String uploadId = databaseReference.child(key).getKey();
                        databaseReference.child(uploadId).setValue(game);
                        databaseReference.child("new").removeValue();
                        if (!game.getNew()){
                            databaseReference.child(uploadId).child("isNew").removeValue();
                        }
                        if (!game.getPreorder()){
                            databaseReference.child(uploadId).child("preorder").removeValue();
                        }
                        /*if (setGameName.getText().toString().isEmpty() || setGameEan.getText().toString().isEmpty()){
                            Toast.makeText(AddNewGameActivity.this, "Toate campurile trebuiesc completate.", Toast.LENGTH_SHORT).show();
                        }*/
                    }
                }
            });
        } else {
            Toast.makeText(this, "Eroare la încarcarea jocului în baza de date.", Toast.LENGTH_SHORT).show();
        }
    }

    public int checkSum(String code){
        int val=0;
        for(int i=0;i<code.length();i++){
            //val+=((int)Integer.parseInt(code.charAt(i)+""))*((i%2==0)?1:3);
            int value1 = ((int) Integer.parseInt(code.charAt(0)+""));
            int value2 = ((int) Integer.parseInt(code.charAt(1)+""))*3;
            int value3 = ((int) Integer.parseInt(code.charAt(2)+""));
            int value4 = ((int) Integer.parseInt(code.charAt(3)+""))*3;
            int value5 = ((int) Integer.parseInt(code.charAt(4)+""));
            int value6 = ((int) Integer.parseInt(code.charAt(5)+""))*3;
            int value7 = ((int) Integer.parseInt(code.charAt(6)+""));
            int value8 = ((int) Integer.parseInt(code.charAt(7)+""))*3;
            int value9 = ((int) Integer.parseInt(code.charAt(8)+""));
            int value10 = ((int) Integer.parseInt(code.charAt(9)+""))*3;
            int value11 = ((int) Integer.parseInt(code.charAt(10)+""));
            int value12 = ((int) Integer.parseInt(code.charAt(11)+""))*3;
            val = value1+value2+value3+value4+value5+value6+value7+value8+value9+value10+value11+value12;
        }
        int nearest_ten = ((val+5)/10)*10;
        if (val % 10 == 0){
            int checksum_digit = 0;
            return checksum_digit;
        }else{
            int checksum_digit = nearest_ten - val;
            if (checksum_digit < 0){
                checksum_digit = 10 + checksum_digit;
            }
            return checksum_digit;
        }
        //int checksum_digit = 10 - (val % 10);
        //if (checksum_digit == 10) checksum_digit = 0;
    }

    public void setEditTextMaxLength(final EditText editText,int length){
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(length);
        editText.setFilters(filterArray);
    }

    //TODO Something with the edit button
    //TODO DONE - Check Ean Form
    //TODO DONE - Delete Edit Button
    //TODO Delete image from Storage on Game deleted
    //TODO DONE - Set Image Name to Game Name with no spaces
    //TODO DONE - Spinner Background color not white
    //TODO Check with real database
    //TODO Why does the key "new" still get created in the database?
    //TODO DONE - If preorder=true and the code is an ean, the generated QR is the ean - should be a link.
    //TODO DONE - Generate better name of images and database keys
}
