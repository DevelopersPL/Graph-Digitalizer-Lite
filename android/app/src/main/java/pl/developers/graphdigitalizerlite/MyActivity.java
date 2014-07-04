package pl.developers.graphdigitalizerlite;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MyActivity extends Activity {

    private static final int TAKE_PICTURE_CODE = 100;
    private static final int SELECT_PICTURE = 1;
    private Bitmap cameraBitmap = null;
    private String selectedImagePath;
    private ImageView img;
    private boolean selectedImg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        ((Button)findViewById(R.id.btn_takepic)).setOnClickListener(btnClick);
        ((Button)findViewById(R.id.btn_loadpic)).setOnClickListener(btnClick);
        img = (ImageView)findViewById(R.id.graph_display);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO: Handling aborting taking picture
        if (TAKE_PICTURE_CODE == requestCode) {
            processCameraImage(data);
        }

        //TODO: Handling aborting selecting image
        if (SELECT_PICTURE == requestCode) {
            processLoadImage(data);
        }

    }

    private void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_CODE);
    }

    private void openSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.load_picture)), SELECT_PICTURE);

        /*
        TODO: Maybe better gallery intent
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PICTURE);
        */
    }

    private void processCameraImage(Intent intent) {
        setContentView(R.layout.detect);

        ((Button)findViewById(R.id.process_graph)).setOnClickListener(btnClick);
        ImageView imageView = (ImageView)findViewById(R.id.graph_display);

        //uncomment for using picture from camera
        cameraBitmap = (Bitmap)intent.getExtras().get("data");
        //cameraBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.friends);
        imageView.setImageBitmap(cameraBitmap);
    }

    private void processLoadImage(Intent intent) {
        setContentView(R.layout.detect);

        ((Button)findViewById(R.id.process_graph)).setOnClickListener(btnClick);
        ImageView imageView = (ImageView)findViewById(R.id.graph_display);

        Uri selectedImageUri = intent.getData();
        selectedImagePath = getPath(selectedImageUri);


        imageView.setImageURI(selectedImageUri);
        selectedImg = true;
    }

    private void detectGraph() {

        if (null != cameraBitmap) {
            int width = cameraBitmap.getWidth();
            int height = cameraBitmap.getHeight();

            // to do here
        }
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btn_takepic:
                    openCamera();
                    break;
                case R.id.btn_loadpic:
                    openSelector();
                    break;
                case R.id.process_graph:
                    detectGraph();
                    break;
            }
        }
    };

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
