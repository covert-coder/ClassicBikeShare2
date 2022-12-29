package com.example.ClassicBikeShare;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.List;
import java.util.Objects;

// this class receives output from the UsersTab class (a fragment) and thus must extend FragmentActivity
// rather than AppCompatActivity.
public class Users_Posts extends FragmentActivity {

    private LinearLayout mLinearLayout;
    // TODO check if this is needed


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_posts);

        mLinearLayout = findViewById(R.id.linearLayoutScroll); // assigning a linear layout for storage
        // of both the photo imageView and the description of the photo imageView

        // use the data sent from the UsersTab class
        Intent receivedIntentObject = getIntent(); //automatically gets any intent sent to this java class

        // store the data associated with the position clicked in the ListArray as "username" regardless of what it may be
        String receivedUsersName = receivedIntentObject.getStringExtra("username");

        // set the title of the UsersPosts page to match the user photos accessed
        setTitle(receivedUsersName + "'s pictures");

        // create a new parse query for a parse object of class photo
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Photo");

        // parse query where the username key we created, username, which intentionally matches the column name on
        // the parse server, is equal to whatever was retrieved from the arraylist field upon click on a field
        // i.e. "where username equals receivedUsersName" is the command being given to the parse server software
        parseQuery.whereEqualTo("username", receivedUsersName);

        // then sort the results by date/time submitted to the server
        parseQuery.orderByDescending("createdAt");

        // TODO create a progressBar to run while the photos download
        // Begin the transaction and access the SupportFragmentManager to do so
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container in the layout of this class with the new fragment
        fragmentTransaction.replace(R.id.alert_dialog, new CustomAlert());
        // add this transaction to the backstack for later recall
        fragmentTransaction.addToBackStack("photoOutput");
        // commit the transaction
        fragmentTransaction.commit();

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            // List<ParseObject> accesses the entire list of objects on the server for a given user
            // and, our query was restricted to only one user, the one clicked, so the objects are
            // for that user only
            @Override
            public void done(List<ParseObject> ThisUsersObjects, ParseException e) {

                // make sure text objects are valid from server. i.e, There is something there.
                if (ThisUsersObjects.size() > 0 && e == null) {

                    // for each ParseObject, that we will call "usersRecord" in the ThisUsersObjects ParseObject-List
                    // an object is retrieved from that users set of objects, one object at a time and stored
                    // in usersRecord
                    for (final ParseObject usersRecord : ThisUsersObjects) {

                        // create a textview that will store the description using code rather than xml
                        // once we parse it out from the overall succession of this users objects
                        final TextView imageDescription = new TextView(Users_Posts.this);

                        // we have a column called image_des (keyword) in the parse server. imageDescription is
                        // assigned the text-content by using that keyword to parse it from the usersRecord
                        // if it contains it on that loop.  Looping continues until all objects in that users set are accessed
                        // so eventually, "image_des" keyword is encountered and the description therein is pulled
                        // the retrieved "image_des" content is set as the text for our textView, imageDescription
                        imageDescription.setText(Objects.requireNonNull(usersRecord.get("image_des")).toString());

                        // picture is the column (keyword) on the parse server that contains photos
                        // the photo (object associated with "picture" keyword) will be pulled from usersRecord
                        // when encountered, and
                        // stored in a variable called postPicture
                        // the object retrieved using the "picture" keyword can be, and is, cast to a parse file
                        // called postPicture using: (ParseFile) as casting syntax. It has not yet been
                        // converted to a decoded bitmap
                        // the picture is thus associated with the description because their retrieval is
                        // done for one user's data set
                        ParseFile postPicture = (ParseFile) usersRecord.get("picture");

                        //looping stops when all objects in ThisUsersObjects have been accessed
                        // we now have content in postPicture (a file called pic.png) specific to this user
                        // and content in imageDescription (text/String) specific to this user

                        // now we retrieve the pic.png files as byte arrays (we are still in the for loop)
                        // and convert them to bitmaps
                        assert postPicture != null;
                        postPicture.getDataInBackground(new GetDataCallback() {
                            @Override
                            // done has parameters of byte array data, and the error, if any. If not then == null.
                            // get the data as an array of bytes called data
                            public void done(byte[] data, ParseException e) {

                                // is the retrieved data from the server sound?
                                if (data != null && e == null) {
                                    // byte array data is decoded then stored in bitmap  with: (no decoding,  length variable) as a Bitmap
                                    Bitmap retrievedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    // create an image view in the layout using code, called postImageView (instance of ImageView)
                                    ImageView postImageView = new ImageView(Users_Posts.this);
                                    // TODO check to see if this works
                                    // we create an instance of the deletePic icon with each loop of this for loop

                                    // below, we set all of the parameters for our ImageView that would ordinarily be in the layout file
                                    // here we create a ViewGroup with parameters that match the parent and wrap content (width and height);
                                    // that parent being the linear layout in the activity_users_posts layout)
                                    LinearLayout.LayoutParams imageView_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT); // the ViewGroup is called imageView_params
                                    // TODO create layout parameters for the FAB

                                    //  now margins are set on the viewgroup
                                    imageView_params.setMargins(5, 0, 5, 0);
                                    // now the instantiated imageView postImageView is given the same parameters as the view group

                                    postImageView.setLayoutParams(imageView_params);

                                    // the image view is set to the left margin
                                    postImageView.setScaleType(ImageView.ScaleType.FIT_START);
                                    // and the contents of the imageview are set to the bitmap retrieved from the parse server
                                    postImageView.setImageBitmap(retrievedBitmap);
                                    // image view is set to adjust its bounds to preserve the aspect ratio of it's drawable
                                    postImageView.setAdjustViewBounds(true);

                                    // parameters are still needed for the text view imageDescription
                                    LinearLayout.LayoutParams des_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                                    des_params.setMargins(5, 5, 5, 5);
                                    imageDescription.setLayoutParams(des_params);
                                    imageDescription.setGravity(Gravity.CENTER);
                                    imageDescription.setBackgroundColor(Color.BLUE);
                                    imageDescription.setTextColor(Color.WHITE);
                                    imageDescription.setTextSize(24f);

                                    // now add the three UI components to the layout
                                    mLinearLayout.addView(imageDescription);
                                    mLinearLayout.addView(postImageView);

                                    // stop the progress alert because the data has been accessed and loaded
                                    stopTheAlert();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(Users_Posts.this, "No photos are accessible " +
                            "via that entry", Toast.LENGTH_SHORT).show();
                    // stop the alert, because no data will be loaded
                    stopTheAlert();
                }
            }
        });
    }
    public void stopTheAlert() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }
}

