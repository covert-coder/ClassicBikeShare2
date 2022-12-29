package com.example.ClassicBikeShare;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.net.Uri;
import android.widget.Toast;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SocialMediaActivity extends AppCompatActivity implements ProfileTab_Fragment.OnFragmentInteractionListener,
        UsersTab.OnFragmentInteractionListener, SharePictureTab_Fragment.OnFragmentInteractionListener {

    private Image mImgSharePhoto;
    private ParseObject parseObject;
    private ByteArrayOutputStream byteArrayOutputStream;
    private Bitmap mBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        setTitle("Classic Bike Share"); // sets the title of the action bar
        FloatingActionButton floatingActionButton = findViewById(R.id.fab); // assign and instantiate a
                // circular floating action button (message icon) named fab

        // when the button is pushed, the snackbar appears at the screen bottom
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "For a quick-pic press the camera icon!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        configureTabLayout();  // calls the function below
    }

    private void configureTabLayout() {
        TabLayout tabLayout = findViewById(R.id.tab_layout); // the id for this layout was added to the
        //xml file activity_social_media.xml
        // it is a child of the AppBarLayout in the same file and appears as a view in the app bar
        // add tabs to tabLayout
        tabLayout.addTab(tabLayout.newTab().setText("My Classic Bike Share Profile"));
        tabLayout.addTab(tabLayout.newTab().setText("Other Club  Users"));
        tabLayout.addTab(tabLayout.newTab().setText("Classic Bike Picture Share"));
        // the ViewPager handles scrolling/sliding of pages, is a member of the View superclass
        // a pager adapter is assigned to this View class that is responsible for handling the fragments (tabs)
        // pager is the name assigned to the ViewPager in the activity_social_media xml layout
        final ViewPager viewPager = findViewById(R.id.pager); // instantiated and assigned the "viewPager"

        // the statement below, creates a new instance of the class tabAdapter (tabAdapter.java)
        // tabAdaptor.java extends the class FragmentPagerAdapter. This tab adapter is thus a PagerAdapter
        // accessing the tabAdapter class and the methods therein.
        final PagerAdapter adapter = new tabAdapter(getSupportFragmentManager(), tabLayout.getTabCount()); //

        // the pager adapter, of tabAdapter class, is set to our viewPager object in the xml file
        viewPager.setAdapter(adapter);

        // tabLayout, an object of class TabLayout contains our three tabs in a horizontal array
        // adding an OnPageChangeListener to the viewPager with the contents of tabLayout
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        // the onTabSelectedListener is now added to that "three-tab" tabLayout for tab specificity
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // position is determined by where the user taps on the tab array on the action bar
                // this is built-in with TabLayout and is not the same "position" as the variable in tabAdapter.java
                // that value is passed to tabAdapter.java, regardless, for use as "position" in that class by
                // "setCurrentItem" and by virtue of the fact that viewPager is of tabAdapter class
                viewPager.setCurrentItem(tab.getPosition());

                // change the title of the page in the app bar as the tabs are selected, to match content
                // getPageTitle is a method in tabAdapter and "adapter" is of tabAdapter class so the method can be accessed via
                // adapter.  The tab position, intentionally, has the same value as position in tabAdapter so it can
                // be substituted in that method to arrive at the desired fragment (tab) heading
                String title;
                title = (Objects.requireNonNull(adapter.getPageTitle(tab.getPosition()))).toString();
                setTitle(title);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void onFragmentInteraction(Uri uri) {
    }
    // this method is called from the onOptionsItemSelected method and accesses storage on the users device
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 6000) {
            // if the user responded, and if that response was give permission then capture image

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                captureImage(); // call the captureImage method below
            }
        }
    }
    // this requires an intent.  Method is called from onOptionsItemsSelected
    public void captureImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // start the process of accessing the media
        startActivityForResult(intent, 4000); // we create this code here
        // code moves from here to the onActivityResult method, below

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4000 && resultCode == RESULT_OK && data != null) {
            // now we compress and add parse codes to upload the image to the server
        try{

            Uri capturedImage = data.getData();
            // captured image is assigned to mBitmap as a bitmap
            mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),capturedImage);

            Toast.makeText(SocialMediaActivity.this, "Your image is being uploaded to the server.  " + "\n"+
                    "Please wait for a message showing it has completed", Toast.LENGTH_SHORT).show();

           // redirect to Async Task to divert non-UI coding from main thread
            AsyncTask CompressandSend = new CompressandSend().execute();

            // return from Async thread


        }catch(Exception e){
            e.printStackTrace();
            }
        }
    }
    private class CompressandSend extends AsyncTask <String, Void, ParseObject> {
        @Override
        protected ParseObject doInBackground(String... strings) {
            // create a new instance of byteArrayOutputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // compression takes (format, quality, bytearrayoutputstream object) as parameters
            // this process appears to compress a png file and rewrite to storage using the same file name, or it could be stored in memory (not sure which)
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            // convert the byteArrayOutputStream (compressed?) to a Byte Array and store it in bytes
            byte[] bytes = byteArrayOutputStream.toByteArray();
            ParseFile parseFile = new ParseFile("pic.png", bytes); // create a new instance of ParseFile containing the compressed byte array
            parseObject = new ParseObject("Photo"); // new instance of ParseObject, creates class called Photo
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM:dd:yyyy", Locale.CANADA); // new instance of SimpleDateFormat for use with pic
            parseObject.put("picture", parseFile); // parseFile contains the compressed byte array (the image). It is put in the "picture" category of parse
            parseObject.put("image_des", "my pic, "+ dateFormat.format(new Date()));// the date instance is added along with the image description from the edtText
            parseObject.put("username", ParseUser.getCurrentUser().getUsername()); // include the username with the parse puts

            return parseObject; // this return is passed to the onPostExecute method below and a return is expected by the method here
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(ParseObject parseObject) { // parseObject was passed from doInBackground, above
            super.onPostExecute(parseObject);


            parseObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        Toast.makeText(SocialMediaActivity.this, "Your image has been posted to the server",Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(SocialMediaActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            //Toast.makeText(SocialMediaActivity.this, "Your picture was saved to the server!!", Toast.LENGTH_LONG).show();
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
    // method to inflate the menu in the app bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }
    // method called when a menu item is selected from the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // if the camera icon is clicked and permission has not yet been granted for reading external
        // storage then, we request permission to do just that
        // if permission is already granted, we simply capture the image from the users storage (else)
        if (item.getItemId() == R.id.post_image_item) {

            // if the SDK is newer and permission will consequently be needed, and permission hasn't been granted already
            if (android.os.Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                // the request code below is generated by us and is unique within this app
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 6000);

            }
            // from here the code moves to the onRequestPermissionsResult method which then directs to the

            captureImage();

        } else if (item.getItemId() == R.id.logout_user_item){

            Intent intent = new Intent(SocialMediaActivity.this, ClassicBikeShareSignup.class);
            startActivity(intent);
            ParseUser.getCurrentUser().logOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}




