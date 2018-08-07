package com.botevplovdiv.foodmatch;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.Toast;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // keys for reading data from SharedPreferences
    public static final String FISH_SWITCH = "fishSwitch";
    public static final String MEAT_SWITCH = "meatSwitch";
    public static final String VEGETARIAN_SWITCH = "vegSwitch";
    public static final String PIZZA_SWITCH = "pizzaSwitch";
    public static final String PASTA_SWITCH = "pastaSwitch";
    public static final String RESTAURANTS_SWITCH = "restaurantSwitch";
    public static final String TAKEAWAY_SWITCH = "takeAwaySwitch";

    //keys for the venue categories
    public static final String TAKEAWAY = "TAKE AWAY";
    public static final String RESTAURANT = "RESTAURANT";

    //key for the device density
    public static final int DENSITY_LIMIT = 250;

    // keys for the food categories
    public static final String FISH = "FISH";
    public static final String MEAT = "MEAT";
    public static final String VEGETARIAN = "VEGETARIAN";
    public static final String PIZZA = "PIZZA";
    public static final String PASTA = "PASTA";

    //database reference
    private FirebaseDatabase mDataBase;
    private DatabaseReference databaseReference;

    private MyAppAdapter myAppAdapter;
    private List<FoodDish> dishList;
    private List<FoodDish> refineList;
    private List<FoodDish> dumpedList;
    private boolean[] booleans;
    private String[] booleansTags;
    private List<String> searchCriteria;
    private SwipeFlingAdapterView flingContainer;
    boolean fishSwitch;
    boolean meatSwitch;
    boolean vegetarianSwitch;
    boolean pizzaSwitch;
    boolean pastaSwitch;
    boolean restaurantSwitch;
    boolean takeAwaySwitch;
    boolean preferenceChanged = true;
    public final static String TAG = "FoodMatch";
    public static List<FoodDish> likedProductsList;
    private ProgressDialog progress;
    private List<String> mKeys;
    private List<FoodDish> testDishesList;
    private boolean listIsEmpty;

    private GridLayout bottomButtonLayout;

    public HashMap<Long,Venue> venueList;
    public HashMap<Long,Venue> testVenueList;


    MenuItem settingsItem;
    MenuItem viewSavedItems;




    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i(TAG,"Entered onCreate() method in MainActivity");

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        //instantiating the database and getting reference
        mDataBase = FirebaseDatabase.getInstance();
        databaseReference = mDataBase.getReference();



        // set default values in the app's SharedPreferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // register listener for SharedPreferences changes
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(
                preferencesChangeListener);

        fishSwitch = sharedPreferences.getBoolean(FISH_SWITCH,false);
        meatSwitch =  sharedPreferences.getBoolean(MEAT_SWITCH,false);
        vegetarianSwitch =  sharedPreferences.getBoolean(VEGETARIAN_SWITCH,false);
        pizzaSwitch =  sharedPreferences.getBoolean(PIZZA_SWITCH,false);
        pastaSwitch =  sharedPreferences.getBoolean(PASTA_SWITCH,false);
        restaurantSwitch =  sharedPreferences.getBoolean(RESTAURANTS_SWITCH,false);
        takeAwaySwitch =  sharedPreferences.getBoolean(TAKEAWAY_SWITCH,false);


        booleansTags = new String[] {FISH,MEAT,VEGETARIAN,PIZZA,PASTA,RESTAURANT,TAKEAWAY};

        dishList = new ArrayList<>();
        searchCriteria = new ArrayList<>();
        refineList = new ArrayList<>();
        likedProductsList = new ArrayList<>();
        dumpedList = new ArrayList<>();
        venueList = new HashMap<>();
        testVenueList = new HashMap<>();
        mKeys = new ArrayList<>();
        testDishesList = new ArrayList<>();
        listIsEmpty=false;

        progress = new ProgressDialog(this);
        progress.show();


        createSearchCriteria(searchCriteria);

        //lock the device to portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Create the fling cards adapter and bind it to the container
        myAppAdapter = new MyAppAdapter(this, refineList);
        flingContainer.setAdapter(myAppAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter()
            {

            }

            @Override
            public void onLeftCardExit(Object dataObject)
            {
                FoodDish current = refineList.get(0);
                dumpedList.add(current);
                refineList.remove(0);
                myAppAdapter.notifyDataSetChanged();
                if (refineList.isEmpty()){
                    if (checkSwitches()){
                        showRestartDialog();
                    }
                }

            }

            @Override
            public void onRightCardExit(Object dataObject)
            {
                FoodDish current = refineList.get(0);
                Venue currentVenue = venueList.get(current.getVenueId());
                Bundle extras = new Bundle();
                extras.putParcelable("current",current);
                extras.putParcelable("currentVenue",currentVenue);
                Intent intent = new Intent(MainActivity.this,LikedProduct.class);
                intent.putExtras(extras);
                startActivityForResult(intent,0);
                refineList.remove(0);
                if(refineList.isEmpty()){
                    listIsEmpty=true;
                }
                myAppAdapter.notifyDataSetChanged();

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter)
            {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }

        });

        loadFromDatabase();


        // Listener for touching events
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                FoodDish current = refineList.get(0);
                Venue currentVenue = venueList.get(current.getVenueId());

                Bundle extras = new Bundle();
                extras.putParcelable("current",current);
                extras.putParcelable("currentVenue",currentVenue);
                Intent intent = new Intent(MainActivity.this,InfoActivity.class).putExtras(extras);
                startActivity(intent);
                myAppAdapter.notifyDataSetChanged();
            }
        });



        //floating buttons for remote flipping the cards

        FloatingActionButton undoFAB =
                (FloatingActionButton) findViewById(R.id.undoButton);
        undoFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dumpedList.isEmpty()){
                    FoodDish undoDish = dumpedList.get(dumpedList.size()-1); //gets the last item in the list
                    dumpedList.remove(dumpedList.size()-1);
                    refineList.add(0,undoDish);
                    flingContainer.removeAllViewsInLayout();
                    myAppAdapter.notifyDataSetChanged();

                }


            }
        });

        FloatingActionButton yesFAB =
                (FloatingActionButton) findViewById(R.id.acceptButton);
        yesFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!refineList.isEmpty())
                    flingContainer.getTopCardListener().selectRight();
//                FoodDish current = refineList.get(0);
//                Venue currentVenue = venueList.get(current.getVenueId());
//                databaseReference.child("MobileApp").child("Dishes").child(current.getTitle()).setValue(current);
//                databaseReference.child("MobileApp").child("Venues").child(currentVenue.getName()).setValue(currentVenue);

            }
        });

        FloatingActionButton noFAB =
                (FloatingActionButton) findViewById(R.id.rejectButton);
        noFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!refineList.isEmpty())
                    flingContainer.getTopCardListener().selectLeft();

            }
        });

        FloatingActionButton infoFAB =
                (FloatingActionButton) findViewById(R.id.infoButton);
        infoFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodDish current = refineList.get(0);
                Venue currentVenue = venueList.get(current.getVenueId());
                Bundle extras = new Bundle();
                extras.putParcelable("current",current);
                extras.putParcelable("currentVenue",currentVenue);
                Intent intent = new Intent(MainActivity.this,InfoActivity.class).putExtras(extras);
                startActivity(intent);
            }
        });

        FloatingActionButton favFAB =
                (FloatingActionButton) findViewById(R.id.favButton);
        favFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("likedProducts",(ArrayList<? extends Parcelable>) likedProductsList);
                Intent intent = new Intent(MainActivity.this,SavedProducts.class);
                intent.putExtras(bundle);
                intent.putExtra("venueList",venueList);
                startActivity(intent);
            }
        });

        bottomButtonLayout = (GridLayout) findViewById(R.id.buttonLayout);

        int displayMetrics = getResources().getDisplayMetrics().densityDpi;
        String msg = "The density of your screen is " + String.valueOf(displayMetrics) + "dpi";


        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        if (displayMetrics < DENSITY_LIMIT){
            bottomButtonLayout.setVisibility(View.GONE);
        }



    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"Entered onStart() method in MainActivity");


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,"Entered onRestart() method in MainActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"Entered onResume() method in MainActivity");
        if (listIsEmpty){
            showRestartDialog();
            listIsEmpty=false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"Entered onPause() method in MainActivity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"Entered onStop() method in MainActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"Entered onDestroy() method in MainActivity");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK)
        {
            String receivedSimularsTag = data.getStringExtra("simularTag");
            refineListWithSimilar(receivedSimularsTag);
        }
    }

    public void refineListWithSimilar(String similarCategory){
        List<FoodDish> tmp = new ArrayList<>();
        if (this.refineList.size() >0){
            for (FoodDish dish : this.refineList){
                if (dish.getCategory().toUpperCase().equals(similarCategory.toUpperCase())){
                    tmp.add(0,dish);
                }else{
                    tmp.add(dish);
                }
            }
            this.refineList.clear();
            this.refineList.addAll(tmp);

            flingContainer.removeAllViewsInLayout();
            myAppAdapter.notifyDataSetChanged();
        }


    }

    private void createSearchCriteria(List<String> searchCriteria){
        booleans = new boolean[]{fishSwitch,meatSwitch,vegetarianSwitch,pizzaSwitch,pastaSwitch,restaurantSwitch,takeAwaySwitch};
        if (searchCriteria.size() >0){
            searchCriteria.clear();
        }

        for (int i = 0;i < booleansTags.length;i++){
            boolean temp = booleans[i];
            if (temp){
                searchCriteria.add(booleansTags[i]);
            }
        }
    }

    private void refineDishesList() {
        if (this.refineList.size() > 0)
        {
            this.refineList.clear();
        }
        for (FoodDish dish : this.dishList){
            if (this.searchCriteria.contains(dish.getCategory().toUpperCase()) && this.searchCriteria.contains(dish.getVenueType().toUpperCase())){
                this.refineList.add(dish);
            }
        }

    }

    private boolean checkSwitches(){
        boolean check = true;
        if ((!fishSwitch && !meatSwitch && !vegetarianSwitch && !pizzaSwitch && !pastaSwitch) || (!restaurantSwitch && !takeAwaySwitch)){
            check = false;
        }
        return check;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        settingsItem = menu.findItem(R.id.settingsItem);
        viewSavedItems = menu.findItem(R.id.viewItems);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item ==settingsItem){
            Intent preferencesIntent = new Intent(this, Settings.class);
            startActivity(preferencesIntent);
        }

        if (item == viewSavedItems){
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("likedProducts",(ArrayList<? extends Parcelable>) likedProductsList);

            Intent intent = new Intent(MainActivity.this,SavedProducts.class);
            intent.putExtras(bundle);
            intent.putExtra("venueList",venueList);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadFromDatabase(){
        databaseReference.child("Venues").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Venue current = dataSnapshot.getValue(Venue.class);
                String key = dataSnapshot.getKey();
                mKeys.add(key);
                venueList.put(current.getVenueId(),current);
                //progress.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String test;
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("Dishes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FoodDish current = dataSnapshot.getValue(FoodDish.class);
                String key = dataSnapshot.getKey();
                mKeys.add(key);
                dishList.add(current);
                refineDishesList();
                Collections.shuffle(refineList);
                flingContainer.removeAllViewsInLayout();
                myAppAdapter.notifyDataSetChanged();
                progress.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void refreshContent(){
        createSearchCriteria(searchCriteria);
        refineDishesList();
        flingContainer.removeAllViewsInLayout();
        myAppAdapter.notifyDataSetChanged();
        preferenceChanged = true;
    }

    public void showRestartDialog(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Restart list?")
                .setMessage("Do you want to restart the list?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        createSearchCriteria(searchCriteria);
                        refineDishesList();
                        myAppAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }




    private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {
                    switch (key){
                        case FISH_SWITCH:
                            fishSwitch = sharedPreferences.getBoolean(FISH_SWITCH,false);
                            refreshContent();
                            break;
                        case MEAT_SWITCH:
                            meatSwitch = sharedPreferences.getBoolean(MEAT_SWITCH,false);
                            refreshContent();
                            break;
                        case VEGETARIAN_SWITCH:
                            vegetarianSwitch = sharedPreferences.getBoolean(VEGETARIAN_SWITCH,false);
                            refreshContent();
                            break;
                        case PIZZA_SWITCH:
                            pizzaSwitch = sharedPreferences.getBoolean(PIZZA_SWITCH,false);
                            refreshContent();
                            break;
                        case PASTA_SWITCH:
                            pastaSwitch = sharedPreferences.getBoolean(PASTA_SWITCH,false);
                            refreshContent();
                            break;
                        case RESTAURANTS_SWITCH:
                            restaurantSwitch = sharedPreferences.getBoolean(RESTAURANTS_SWITCH,false);
                            refreshContent();
                            break;
                        case TAKEAWAY_SWITCH:
                            takeAwaySwitch = sharedPreferences.getBoolean(TAKEAWAY_SWITCH,false);
                            refreshContent();
                            break;

                    } //end of switch statement
        }
    };


    //=======================BACK UP METHODS==========================

//    public void loadDishes(){
//        dishList.add(new FoodDish("Shrimp dish","https://cdn.pixabay.com/photo/2015/04/10/00/41/food-715539_960_720.jpg",getString(R.string.test),"2.14", FISH,RESTAURANT,1));
//        dishList.add(new FoodDish("Vegetarian Pizza","https://fthmb.tqn.com/6MLlS1ubNVh6vgoFZoNf0kQTB0I=/960x0/filters:no_upscale()/about/vegetarian-pizza-550205657-58765ce43df78c17b61b6bfb.jpg","Test description.This a very tasty dish and you should definitely order it.I don`t know what are you waiting for", "3.87", PIZZA,TAKEAWAY,1));
//        dishList.add(new FoodDish("Pasta","http://assets.kraftfoods.com/recipe_images/opendeploy/92161_640x428.jpg",getString(R.string.test), "4.87",PASTA,RESTAURANT,1));
//        dishList.add(new FoodDish("Chicken wings","http://cf.yellowblissroad.com/wp-content/uploads/2015/02/Baked-Chicken-Wings.jpg",getString(R.string.test),"1.23",MEAT,TAKEAWAY,1));
//        dishList.add(new FoodDish("Fish","http://cdn.skim.gs/image/upload/v1456339187/msi/grilled-catfish_izglgf.jpg",getString(R.string.test), "5.87", FISH,TAKEAWAY,1));
//        dishList.add(new FoodDish("Spaghetti","http://food.fnr.sndimg.com/content/dam/images/food/fullset/2009/6/12/2/FO1D41_23785_s4x3.jpg.rend.hgtvcom.616.462.jpeg",getString(R.string.test), "9.23", PASTA,RESTAURANT,1));
//        dishList.add(new FoodDish("Ceasar salad","http://entomofarms.com/wp-content/uploads/2016/04/EF-caesar-salad-cricket-powder.jpg",getString(R.string.test), "3.45", VEGETARIAN,RESTAURANT,2));
//        dishList.add(new FoodDish("Steak sandwich","http://s3.amazonaws.com/finecooking.s3.tauntonclud.com/app/uploads/2017/04/18125307/051120015-01-steak-sandwich-recipe-main.jpg",getString(R.string.test), "3.45",MEAT,TAKEAWAY,2));
//        dishList.add(new FoodDish("Lasagna","http://www.weightlossresources.co.uk/img/recipes/vegetarian-dishes.jpg",getString(R.string.test), "11.45",VEGETARIAN,RESTAURANT,2));
//        dishList.add(new FoodDish("Sea Food Risotto","http://www.dvo.com/recipe_pages/healthy/Lemony_Seafood_Risotto.jpg",getString(R.string.test), "8.45",VEGETARIAN,TAKEAWAY,2));
//        dishList.add(new FoodDish("Marinated Hake","https://www.jean-georges.com/content/slides/20120710-jgr-137-750x500.jpg",getString(R.string.test), "23.45",FISH,RESTAURANT,2));
//        dishList.add(new FoodDish("Salmon and Asparagus in Hollandaise Sauce","http://outdoorchannel.com/content/articles/mhm-salmon-asparagus-M.jpg",getString(R.string.test), "21.75",FISH,RESTAURANT,3));
//        dishList.add(new FoodDish("Fried Calamari","http://media.olivegarden.com/en_us/images/product/h-classic-calamari-dpv.jpg",getString(R.string.test), "11.99",FISH,TAKEAWAY,3));
//        dishList.add(new FoodDish("Fish and Chips","http://www.bizzielizzies.co.uk/wp-content/uploads/2015/03/slide-fish-chips-2015.jpg",getString(R.string.test), "14.99",FISH,TAKEAWAY,3));
//        dishList.add(new FoodDish("Pulled Pork Sandwich","http://demandware.edgesuite.net/aaxm_prd/on/demandware.static/-/Library-Sites-TraegerSharedLibrary/default/dwafca29a9/images/recipes/DW_Recipe_traeger_pulled_pork_Mobile.jpg",getString(R.string.test), "12.99",MEAT,TAKEAWAY,3));
//        dishList.add(new FoodDish("Beef Fillet Steak","http://3.bp.blogspot.com/-QZhm5iiI718/Tm-xp3Z93QI/AAAAAAAAABQ/WV7gC15nb2Q/s1600/Grilled-Beef-Fillet.jpg",getString(R.string.test), "27.89",MEAT,RESTAURANT,3));
//        dishList.add(new FoodDish("Dusck Breast with Apple-Pomegranate Sauce","http://images.scrippsnetworks.com/up/tp/Scripps_-_Food_Category_Prod/241/39/0149681_630x355.jpg",getString(R.string.test), "23.99",MEAT,RESTAURANT,4));
//        dishList.add(new FoodDish("Venison with Butternut Squash,Parmesan and truffle","http://gbc-cdn-public-media.azureedge.net/img24760.700x466.jpg",getString(R.string.test), "26.79",MEAT,RESTAURANT,4));
//        dishList.add(new FoodDish("Doner Kebab","http://www.passionforfoodtakeaway.com/images/hi/responsive/kebabs/about-image.jpg",getString(R.string.test), "10.79",MEAT,TAKEAWAY,4));
//        dishList.add(new FoodDish("Double Cheeseburger","https://7b30cfc4a1b0ada1cdb2-893624c4eafa7bb58a8641a0ccf4ceea.ssl.cf1.rackcdn.com/double-cheeseburger_thumb-m.jpg?mod=636239954109000000",getString(R.string.test), "14.79",MEAT,TAKEAWAY,4));
//        dishList.add(new FoodDish("Pepperoni Pizza 12'","http://i.dailymail.co.uk/i/pix/2015/05/01/15/2834F1E700000578-3064318-image-a-34_1430491334730.jpg",getString(R.string.test), "12.79",PIZZA,TAKEAWAY,4));
//        dishList.add(new FoodDish("Hawaiian Pizza 12'","http://www.delmonteeurope.com/media/europe/recipes/recipes%20(new)/delmonte-hawaiian-pizza-list.png",getString(R.string.test), "11.79",PIZZA,TAKEAWAY,4));
//
//    }
//
//    public void loadVenues(){
//        Venue venue1 = new Venue("Dante Pizza","Unit 7, Superquinn Shopping Centre, Blanchardstown, Dublin 15","00353857797778","https://media-cdn.tripadvisor.com/media/photo-s/05/26/a4/30/dante-pizza.jpg",TAKEAWAY,1);
//        Venue venue2 = new Venue("The Great Wood","Westend Retail Park, Blanchardstown, Dublin 15","00353851805508","http://www.menupages.ie/images/550x344/9157_menupages_the_great_wood_2.jpg",RESTAURANT,2);
//        Venue venue3 = new Venue("TGI Friday`s","15 The Mall, Blanchardstown, Dublin 15, D15 E0V1","00353018225990","https://media-cdn.tripadvisor.com/media/photo-s/05/26/ac/f0/tgi-fridays.jpg",RESTAURANT,3);
//        Venue venue4 = new Venue("Camile Thai Phibsborough","143B Phibsborough Rd, Phibsborough, Dublin 7","00353018500111","http://grublin.ie/wp-content/uploads/2016/01/Camille_exterior.jpg",RESTAURANT,4);
//        venueList.put(venue1.getVenueId(),venue1);
//        venueList.put(venue2.getVenueId(),venue2);
//        venueList.put(venue3.getVenueId(),venue3);
//        venueList.put(venue4.getVenueId(),venue4);
//    }
//
    //===============================================END==========================================================
}


