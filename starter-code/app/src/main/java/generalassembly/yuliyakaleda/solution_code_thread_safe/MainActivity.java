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
import android.util.Pair;
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

        mImageView.setImageResource(R.drawable.placeholder);
        mChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
//        findViewById(R.id.b_w_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == MainActivity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            AsyncTask<Uri, Integer, Bitmap> mMyTask = new ImageProcessingAsyncTask();
            mMyTask.execute(selectedImage);
            // Instantiate the async task and execute it
        }
    }

    // brings up the photo gallery/other resources to choose a picture
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //Fill in the parameter types
    private class ImageProcessingAsyncTask extends AsyncTask<Uri, Integer, Bitmap> {

        // Fill in the parameter type - look at the expected type for the parameter to openInputStream()
        @Override
        protected Bitmap doInBackground(Uri... params) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(params[0]));
                return sepia(bitmap);
//                return invertImageColors(bitmap);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "Image uri is not received or recognized");
            }
            return null;
        }

        //Fill in the parameter type - what type of data will be passed to this method when it's called from doInBackground()?
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            //Update the progress bar
            mProgressBar.setMax(values[0]);
            mProgressBar.setProgress(values[1]);
        }


        //Fill in the parameter type - what type of data will doInBackground() return, which the system then passes here as a parameter?
        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);
            mImageView.setImageBitmap(image);
            mProgressBar.setVisibility(View.INVISIBLE);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        private Bitmap invertImageColors(Bitmap bitmap) {
            //You must use this mutable Bitmap in order to modify the pixels
            Bitmap mutableBitmap = bitmap.copy(bitmap.getConfig(), true);

            //Loop through each pixel, and invert the colors
            for (int i = 0; i < mutableBitmap.getWidth(); i++) {
                for (int j = 0; j < mutableBitmap.getHeight(); j++) {
                    //Get Pixel
                    int color = mutableBitmap.getPixel(i, j);

                    //Get color values of pixels.
                    int redValue = Color.red(color);
                    int greenValue = Color.green(color);
                    int blueValue = Color.blue(color);

                    //Invert? the values.
                    redValue = 255 - redValue;
                    greenValue = 255 - greenValue;
                    blueValue = 255 - blueValue;

                    //Create new color, and set to pixel.
                    color = Color.argb(255, redValue, greenValue, blueValue);
                    mutableBitmap.setPixel(i, j, color);
                }

                //Changed the method so that it passes the max of the progressbar and the progress. Let the progressbar handle the math.
                int max = mutableBitmap.getWidth();

                publishProgress(max, i);
            }
            return mutableBitmap;
        }

        private Bitmap greyScale(Bitmap bitmap) {
            Bitmap mutableBitMap = bitmap.copy(bitmap.getConfig(), true);

            for (int i = 0; i < mutableBitMap.getWidth(); i++) {
                for (int j = 0; j < mutableBitMap.getHeight(); j++) {
                    int color = mutableBitMap.getPixel(i, j);

                    int redValue = Color.red(color);
                    int greenValue = Color.green(color);
                    int blueValue = Color.blue(color);

                    int maxVal = Math.max(redValue, greenValue);
                    maxVal = Math.max(maxVal,blueValue);

                    color =Color.argb(255, maxVal, maxVal,maxVal);

                    mutableBitMap.setPixel(i, j, color);
                }
                int max = mutableBitMap.getWidth();

                publishProgress(max, i);
            }
            return mutableBitMap;
        }

        private Bitmap bloodStained(Bitmap bitmap) {
            Bitmap mutableBitMap = bitmap.copy(bitmap.getConfig(), true);

            for (int i = 0; i < mutableBitMap.getWidth(); i++) {
                for (int j = 0; j < mutableBitMap.getHeight(); j++) {
                    int color = mutableBitMap.getPixel(i, j);

                    int redValue = Color.red(color);
                    int greenValue = Color.green(color)/5;
                    int blueValue = Color.blue(color)/5;


                    color =Color.argb(255, redValue, greenValue, blueValue);

                    mutableBitMap.setPixel(i, j, color);
                }
                int max = mutableBitMap.getWidth();

                publishProgress(max, i);
            }
            return mutableBitMap;
        }

        private Bitmap sepia(Bitmap bitmap) {
            Bitmap mutableBitMap = bitmap.copy(bitmap.getConfig(), true);

            for (int i = 0; i < mutableBitMap.getWidth(); i++) {
                for (int j = 0; j < mutableBitMap.getHeight(); j++) {
                    int color = mutableBitMap.getPixel(i, j);

                    int redValue = Color.red(color);
                    int greenValue = Color.green(color);
                    int blueValue = Color.blue(color);

                    //Get lowest grey value
                    int minValue = Math.min(redValue,greenValue);
                    minValue = Math.min(minValue,blueValue);

                    //Add a little brownish hue to it.
                    redValue = minValue+112;
                    greenValue= minValue+66;
                    blueValue=minValue+20;

                    int[] values = {redValue, greenValue,blueValue};

                    //Set any values that are above 255, to 255.
                    for(int x=0;x<3;x++){
                        if (values[x]>255){
                            values[x]=255;
                        }
                    }

                    color =Color.argb(255, values[0],values[1],values[2]);

                    mutableBitMap.setPixel(i, j, color);
                }
                int max = mutableBitMap.getWidth();

                publishProgress(max, i);
            }
            return mutableBitMap;
        }
    }


}



