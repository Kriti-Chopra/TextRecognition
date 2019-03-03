package kriti.somevalue.com.textrecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnSnap,btnDetect;
    TextView txtDetectedText;
    ImageView imgPicture;
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSnap=(Button) findViewById(R.id.button);
        btnDetect=(Button) findViewById(R.id.button2);
        txtDetectedText=(TextView) findViewById(R.id.textView);
        imgPicture=(ImageView) findViewById(R.id.imageView);

        btnSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectText();
            }
        });


    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imgPicture.setImageBitmap(imageBitmap);
        }
    }

    public void detectText(){
        FirebaseVisionImage image=FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer detector= FirebaseVision.getInstance().getOnDeviceTextRecognizer();


        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                // Task completed successfully
                                // ...
                                processTextBlock(firebaseVisionText);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        Toast.makeText(MainActivity.this, "failed to detect", Toast.LENGTH_SHORT).show();
                                    }
                                });
    }

    public void processTextBlock(FirebaseVisionText text){
        List<FirebaseVisionText.TextBlock> blocks = text.getTextBlocks();

        if (blocks.size() == 0) {

            Toast.makeText(MainActivity.this, "No Text :(", Toast.LENGTH_LONG).show();

            return;

        }

        for (FirebaseVisionText.TextBlock block : text.getTextBlocks()) {

            String txt = block.getText();

            txtDetectedText.setTextSize(24);

            txtDetectedText.setText(txt);

        }
    }
}
