package generalassembly.yuliyakaleda.solution_code_thread_safe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView mImageView;
    private Button mChooseButton;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChooseButton = (Button) findViewById(R.id.choose_button);
        mImageView = (ImageView) findViewById(R.id.image);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mProgressBar.setMax(100);

        mImageView.setImageResource(R.drawable.placeholder);
        mChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == MainActivity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            ImageProcessingAsyncTask task = new ImageProcessingAsyncTask();
            task.execute(selectedImage);

            //TODO: Instantiate the async task and execute it
        }
    }

    // brings up the photo gallery/other resources to choose a picture
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //: Fill in the parameter types
    private class ImageProcessingAsyncTask extends AsyncTask<Uri,Integer,Bitmap> {

        //: Fill in the parameter type - look at the expected type for the parameter to openInputStream()
        @Override
        protected Bitmap doInBackground(Uri... params) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(params[0]));
                return invertImageColors(bitmap);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "Image uri is not received or recognized");
            }
            return null;
        }

        //: Fill in the parameter type - what type of data will be passed to this method when it's called from doInBackground()?
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressBar.setProgress(values[0]);
            //TODO: Update the progress bar
        }

        // Fill in the parameter type - what type of data will doInBackground() return, which the system then passes here as a parameter?

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mImageView.setImageBitmap(bitmap);
            mProgressBar.setVisibility(View.INVISIBLE);

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            // Complete this method
        }

        private Bitmap invertImageColors(Bitmap bitmap) {
            //You must use this mutable Bitmap in order to modify the pixels
            Bitmap mutableBitmap = bitmap.copy(bitmap.getConfig(), true);

            //Loop through each pixel, and invert the colors
            for (int i = 0; i < mutableBitmap.getWidth(); i++) {
                for (int j = 0; j < mutableBitmap.getHeight(); j++) {

                    int pixelColor = mutableBitmap.getPixel(i,j);
                    int A = 255-Color.alpha(pixelColor);
                    int R = 255-Color.red(pixelColor);
                    int G = 255-Color.green(pixelColor);
                    int B = 255-Color.blue(pixelColor);

                    mutableBitmap.setPixel(i,j, Color.argb(A,R,G,B));

                    //TODO: Get the Red, Green, and Blue values for the current pixel, and reverse them
                    //TODO: Set the current pixel's color to the new, reversed value
                }
                int progressVal = Math.round((long) (100 * (i / (1.0 * mutableBitmap.getWidth()))));
                publishProgress(progressVal);
                // Update the progress bar. progressVal is the current progress value out of 100
            }
            return mutableBitmap;
        }
    }
}

