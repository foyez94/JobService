package com.donbosco.jobservice.candidate.dashboard.fragments;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.youtube.player.YouTubePlayer;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.donbosco.jobservice.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.donbosco.jobservice.employeer.jobs.adapters.Nokri_DescriptionRecyclerViewAdapter;
import com.donbosco.jobservice.employeer.jobs.models.Nokri_DescriptionModel;
import com.donbosco.jobservice.manager.Nokri_DialogManager;
import com.donbosco.jobservice.manager.Nokri_SharedPrefManager;
import com.donbosco.jobservice.rest.RestService;
import com.donbosco.jobservice.R;
import com.donbosco.jobservice.candidate.edit.fragments.Nokri_CandidateEditProfileFragment;
import com.donbosco.jobservice.manager.Nokri_FontManager;
import com.donbosco.jobservice.manager.Nokri_GoogleAnalyticsManager;
import com.donbosco.jobservice.manager.Nokri_PopupManager;
import com.donbosco.jobservice.manager.Nokri_RequestHeaderManager;
import com.donbosco.jobservice.network.Nokri_ServiceGenerator;
import com.donbosco.jobservice.utils.Nokri_Config;
import com.donbosco.jobservice.utils.Nokri_Globals;
import com.donbosco.jobservice.utils.Nokri_Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_CandidateDashboardFragment extends Fragment implements OnMapReadyCallback ,View.OnClickListener{
    private RecyclerView recyclerView1;
    private Nokri_DescriptionRecyclerViewAdapter adapter1;

    private List<Nokri_DescriptionModel> modelList;
    private Nokri_FontManager fontManager;

    private MapView map;
    private TextView nameTextView,addressTextView;
    private TextView yourDashboardTextView,aboutMeTextView,aboutMeDataTextView,locationAndMapTextView;
    private CircularImageView profileImage;
    private String facebook,twitter,linkedin,googlePlus;
    private static double LATITUDE,LONGITUDE;
    private ImageView facebookImageViwe,twitterImageView,linkedinImageView,googleplusImageView;
    private Nokri_DialogManager dialogManager;
    Nokri_PopupManager popupManager;
    private YouTubePlayer YPlayer;
    FrameLayout linearLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    public static Boolean checkLoading = false;
    public Nokri_CandidateDashboardFragment() {
        // Required empty public constructor
    }


    private void nokri_initialize(){

        fontManager = new Nokri_FontManager();
        recyclerView1 = getView().findViewById(R.id.recyclerview);
        recyclerView1.setNestedScrollingEnabled(false);
        profileImage =  getView().findViewById(R.id.img_profile);
        linearLayout=getView().findViewById(R.id.background_circle);

        Drawable mDrawable = getActivity().getResources().getDrawable(R.drawable.saa);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.MULTIPLY));

        nameTextView =  getView().findViewById(R.id.txt_name);
        addressTextView =  getView().findViewById(R.id.txt_address);
        yourDashboardTextView =  getView().findViewById(R.id.txt_your_dashboard);
        aboutMeTextView =  getView().findViewById(R.id.txt_about_me);
        aboutMeDataTextView =  getView().findViewById(R.id.txt_about_me_data);
        locationAndMapTextView =  getView().findViewById(R.id.txt_location_and_map);



        facebookImageViwe = getView().findViewById(R.id.img_facebook);
        twitterImageView = getView().findViewById(R.id.img_twitter);
        linkedinImageView = getView().findViewById(R.id.img_linkedin);
        googleplusImageView = getView().findViewById(R.id.img_gooogle_plus);

        facebookImageViwe.setOnClickListener(this);
        twitterImageView.setOnClickListener(this);
        linkedinImageView.setOnClickListener(this);
        googleplusImageView.setOnClickListener(this);
        modelList = new ArrayList<>();

        Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getDashboard());
    }

    private void nokri_setupFonts(){



        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(addressTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(aboutMeDataTextView,getActivity().getAssets());

        fontManager.nokri_setMonesrratSemiBioldFont(yourDashboardTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(aboutMeTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(locationAndMapTextView,getActivity().getAssets());
     }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeRefreshLayout = getView().findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {

            checkLoading = true;
            recreate_nokri_getCandidateDasboard();

        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nokri_initialize();




        nokri_setupFonts();

        nokri_getCandidateDasboard();

    }
private void recreate_nokri_getCandidateDasboard(){

    Nokri_CandidateDashboardFragment nokri_candidateDashboardFragment=new Nokri_CandidateDashboardFragment();
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.replace(R.id.fragment_placeholder, nokri_candidateDashboardFragment);
    transaction.commit();
}
    private void nokri_getCandidateDasboard(){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());
        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
             myCall = restService.getCandidateDashboard(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
             myCall = restService.getCandidateDashboard(Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){

                    try {
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        String aboutEmptyText = "";
                        if(jsonObject.getBoolean("success")){

                            Nokri_Globals.EDIT_MESSAGE  = jsonObject.getString("message");
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONObject profile = data.getJSONObject("profile");
                            JSONObject againData = profile.getJSONObject("data");
                            JSONObject socialObject = data.getJSONObject("social_icons");

                            facebook = socialObject.getString("facebook");
                            twitter = socialObject.getString("twitter");
                            linkedin = socialObject.getString("linkedin");
                            googlePlus = socialObject.getString("google_plus");


                            if(facebook.trim().isEmpty())
                                    facebookImageViwe.setVisibility(View.GONE);
                            if(twitter.trim().isEmpty())
                                twitterImageView.setVisibility(View.GONE);
                            if(linkedin.trim().isEmpty())
                                linkedinImageView.setVisibility(View.GONE);
                            if(googlePlus.trim().isEmpty())
                                googleplusImageView.setVisibility(View.GONE);

                            JSONArray extraArray = againData.getJSONArray("extra");
                            for(int i=0;i<extraArray.length();i++){

                                if(extraArray.getJSONObject(i).getString("field_type_name").equals("cand_about")){
                                        aboutEmptyText = extraArray.getJSONObject(i).getString("value");
                                }
                            }

                                JSONArray jsonArray = againData.getJSONArray("info");



                            if(!TextUtils.isEmpty(data.getString("cand_dp")))
                            Picasso.with(getContext()).load(data.getString("cand_dp")).into(profileImage);
                            Nokri_SharedPrefManager.saveProfileImage(data.getString("cand_dp"),getContext());
                           NavigationView navigationView =  getActivity().findViewById(R.id.nav_view);
                            View headerView = navigationView.getHeaderView(0); // 0-index header
                           ImageView profileImageView = headerView.findViewById(R.id.img_profile);
                            if(!TextUtils.isEmpty(data.getString("cand_dp")))
                           Picasso.with(getContext()).load(data.getString("cand_dp")).into(profileImageView);

                            Nokri_SharedPrefManager.saveCoverImage(data.getString("cand_cover"),getContext());
                            for(int i = 0; i<jsonArray.length();i++){
                                JSONObject jsonData = jsonArray.getJSONObject(i);
                                if(jsonData.getString("field_type_name").equals("cand_name"))
                                {     //  toolbarInterface.setToolbarText(jsonData.getString("value"));
                                    nameTextView.setText(jsonData.getString("value"));
                                    continue;
                                }


                                if(jsonData.getString("field_type_name").equals("cand_adress")){
                                    addressTextView.setText(jsonData.getString("value"));
                                    Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                    model.setTitle(jsonData.getString("key"));
                                    model.setDescription(jsonData.getString("value"));
                                    modelList.add(model);
                                    continue;
                                }
                                if(jsonData.getString("field_type_name").equals("about_me"))
                                {   aboutMeTextView.setText(jsonData.getString("key"));
                                    if(!jsonData.getString("value").trim().isEmpty())
                                    aboutMeDataTextView.setText(Nokri_Utils.stripHtml(jsonData.getString("value")));
                                    else
                                        aboutMeDataTextView.setText(aboutEmptyText);
                                    Nokri_SharedPrefManager.saveAbout(jsonData.getString("value"),getContext());
                                    continue;
                                }
                                if(jsonData.getString("field_type_name").equals("loc")){
                                    locationAndMapTextView.setText(jsonData.getString("key"));
                                    continue;
                                }
                                if(jsonData.getString("field_type_name").equals("set_profile")){

                                   continue;
                                    }
                                if(jsonData.getString("field_type_name").equals("cand_lat")){

                                    try {
                                        LATITUDE = Double.parseDouble(jsonData.getString("value"));
                                        continue;
                                    }
                                    catch (NumberFormatException e){
                                        LATITUDE = 0;
                                        continue;
                                    }
                                }
                                if(jsonData.getString("field_type_name").equals("cand_long")){
                                    try {
                                        LONGITUDE = Double.parseDouble(jsonData.getString("value"));
                                        continue;
                                    }
                                    catch (NumberFormatException e){
                                        LONGITUDE = 0;
                                        continue;
                                    }
                                }
                                if(jsonData.getString("field_type_name").equals("your_dashbord")){
                                    yourDashboardTextView.setText(jsonData.getString("key"));
                                    continue;
                                }
                                if(jsonData.getString("key").equals("dp")||jsonData.getString("key").equals("cover"))
                                    continue;
                                Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                model.setTitle(jsonData.getString("key"));
                                model.setDescription(jsonData.getString("value"));
                                if(jsonData.getString("field_type_name").equals("cand_dob"))
                                    Nokri_SharedPrefManager.saveDateOfBirth(jsonData.getString("value"),getContext());
                                if(jsonData.getString("field_type_name").equals("last_esdu"))
                                    Nokri_SharedPrefManager.saveLastEducation(jsonData.getString("value"),getContext());
                                if(jsonData.getString("field_type_name").equals("cand_hand"))
                                    Nokri_SharedPrefManager.saveHeadline(jsonData.getString("value"),getContext());



                                modelList.add(model);

                            }
                            Log.v("array",jsonArray.toString());

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    adapter1 = new Nokri_DescriptionRecyclerViewAdapter(modelList, getContext(), 0, new Nokri_DescriptionRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Nokri_DescriptionModel item) {
                            android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                            RelativeLayout placeholder = getActivity().findViewById(R.id.fragment_placeholder);
                            fragmentTransaction.replace(R.id.fragment_placeholder,new Nokri_CandidateEditProfileFragment()).commit();
                        }
                    });

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView1.setLayoutManager(layoutManager);

                    recyclerView1.setItemAnimator(new DefaultItemAnimator());
                    recyclerView1.setAdapter(adapter1);
                    dialogManager.hideAlertDialog();
                    map.getMapAsync(Nokri_CandidateDashboardFragment.this);}
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.hideAlertDialog();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nokri_candidate_dashboard, container, false);
        map =view.findViewById(R.id.map_fragment);
        map.onCreate(savedInstanceState);

        map.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
       // map.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
      /*  LatLng location = new LatLng(LATITUDE, LONGITUDE);
        map.addMarker(new MarkerOptions().position(location));
        map.setMinZoomPreference(Nokri_Config.MAP_CAM_MIN_ZOOM);
        map.setMaxZoomPreference(Nokri_Config.MAP_CAM_MAX_ZOOM);
        map.moveCamera(CameraUpdateFactory.newLatLng(location));*/
        LatLng location = new LatLng(LATITUDE, LONGITUDE);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.addMarker(new MarkerOptions().position(location));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location).zoom(Nokri_Config.MAP_CAM_MIN_ZOOM).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);
    }



    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_facebook:
                Nokri_Utils.opeInBrowser(getContext(),facebook);
                break;
            case R.id.img_twitter:
                Nokri_Utils.opeInBrowser(getContext(),twitter);
                break;
            case R.id.img_linkedin:
                Nokri_Utils.opeInBrowser(getContext(),linkedin);
                break;
            case R.id.img_gooogle_plus:
                Nokri_Utils.opeInBrowser(getContext(),googlePlus);
                break;

        }
    }







}
