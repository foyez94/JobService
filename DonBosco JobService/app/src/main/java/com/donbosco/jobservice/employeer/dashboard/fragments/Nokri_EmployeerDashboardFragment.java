package com.donbosco.jobservice.employeer.dashboard.fragments;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.donbosco.jobservice.employeer.jobs.adapters.Nokri_DescriptionRecyclerViewAdapter;
import com.donbosco.jobservice.employeer.jobs.models.Nokri_DescriptionModel;
import com.donbosco.jobservice.manager.Nokri_DialogManager;
import com.donbosco.jobservice.manager.Nokri_RequestHeaderManager;
import com.donbosco.jobservice.manager.Nokri_SharedPrefManager;
import com.donbosco.jobservice.manager.Nokri_ToastManager;
import com.donbosco.jobservice.network.Nokri_ServiceGenerator;
import com.donbosco.jobservice.rest.RestService;
import com.donbosco.jobservice.R;
import com.donbosco.jobservice.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.donbosco.jobservice.employeer.edit.fragments.Nokri_CompanyEditProfileFragment;
import com.donbosco.jobservice.manager.Nokri_FontManager;
import com.donbosco.jobservice.manager.Nokri_GoogleAnalyticsManager;
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

import mabbas007.tagsedittext.TagsEditText;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_EmployeerDashboardFragment extends Fragment implements OnMapReadyCallback ,View.OnClickListener{
    private RecyclerView recyclerView1;
    private Nokri_DescriptionRecyclerViewAdapter adapter1;

    private List<Nokri_DescriptionModel> modelList;
    private Nokri_FontManager fontManager;

    private MapView map;
    private TextView nameTextView,addressTextView;
    private TextView yourDashboardTextView,aboutMeTextView,aboutMeDataTextView,locationAndMapTextView,skillsTextView;
    private CircularImageView profileImage;

    private static double LATITUDE,LONGITUDE;

    private TagsEditText tagsEditText;
    private String facebook,twitter,linkedin,googleplus;
    private ImageView facebookImageButton,twitterImageButton,linkedinImageButton,googlePlusImageButton;
    private TextView noSkillsTextView;
    private Nokri_DialogManager dialogManager;
    SwipeRefreshLayout swipeRefreshLayout;
    public static Boolean checkLoading = false;
    public Nokri_EmployeerDashboardFragment() {
        // Required empty public constructor
    }


    private void nokri_initialize(){

        fontManager = new Nokri_FontManager();
        recyclerView1 = getView().findViewById(R.id.recyclerview);
        profileImage =  getView().findViewById(R.id.img_profile);



        Drawable mDrawable = getActivity().getResources().getDrawable(R.drawable.saa);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.MULTIPLY));


        nameTextView =  getView().findViewById(R.id.txt_name);
        addressTextView =  getView().findViewById(R.id.txt_address);
        yourDashboardTextView =  getView().findViewById(R.id.txt_your_dashboard);
        aboutMeTextView =  getView().findViewById(R.id.txt_about_me);
        aboutMeDataTextView =  getView().findViewById(R.id.txt_about_me_data);
        locationAndMapTextView =  getView().findViewById(R.id.txt_location_and_map);
        skillsTextView = getView().findViewById(R.id.txt_skills);
        noSkillsTextView = getView().findViewById(R.id.txt_no_skills);


        facebookImageButton = getView().findViewById(R.id.img_btn_facebook);
        twitterImageButton = getView().findViewById(R.id.img_btn_twitter);
        linkedinImageButton = getView().findViewById(R.id.img_btn_linkedin);
        googlePlusImageButton = getView().findViewById(R.id.img_btn_goole_plus);


        facebookImageButton.setOnClickListener(this);
        twitterImageButton.setOnClickListener(this);
        linkedinImageButton.setOnClickListener(this);
        googlePlusImageButton.setOnClickListener(this);

        modelList = new ArrayList<>();

        tagsEditText = getView().findViewById(R.id.skills_container);
        tagsEditText.setEnabled(false);

        recyclerView1.setNestedScrollingEnabled(false);
    }

    private void setSkills(JSONArray jsonArray,String skillsEmpty){
        List<String>tagsList = new ArrayList<>();
        if(jsonArray.length()<=0)
        {  String []tag = {skillsEmpty};
            tagsEditText.setTags(tag);
            tagsEditText.setVisibility(View.GONE);
            noSkillsTextView.setVisibility(View.VISIBLE);
            noSkillsTextView.setText(skillsEmpty);
        }
        for(int i = 0;i<jsonArray.length();i++)
        {
            try {
                JSONObject tagObject = jsonArray.getJSONObject(i);
                tagsList.add(tagObject.getString("value"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String tags [] = new String[tagsList.size()];
        tagsEditText.setTags(tagsList.toArray(tags));
    }

    private void nokri_setupFonts(){





        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(addressTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(noSkillsTextView,getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontTextView(aboutMeDataTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(yourDashboardTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(aboutMeTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(locationAndMapTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(skillsTextView,getActivity().getAssets());
     }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setupFonts();
        getEmployeerDashboard();
        Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getDashboard());
        swipeRefreshLayout = getView().findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {

            checkLoading = true;
            recreate_nokri_getEmployeerDashboard();

        });
    }
private void recreate_nokri_getEmployeerDashboard(){

    Nokri_EmployeerDashboardFragment nokri_employeerDashboardFragment= new Nokri_EmployeerDashboardFragment();
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.replace(R.id.fragment_placeholder, nokri_employeerDashboardFragment);
    transaction.commit();
}
    private void getEmployeerDashboard(){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());


        Call<ResponseBody> myCall;

        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getEmployeerDashboard(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getEmployeerDashboard(Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){

                    try {
                        String defaultAboutText = null,skillsEmpty = null;
                    //    Log.d("errrrrrrrr",responseObject.body().string());
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());

                        if(jsonObject.getBoolean("success")){

                            Nokri_Globals.EDIT_MESSAGE  = jsonObject.getString("message");
                            JSONObject data = jsonObject.getJSONObject("data");

                            JSONObject social = data.getJSONObject("social");
                            facebook = social.getString("facebook");
                            twitter = social.getString("twitter");
                            linkedin = social.getString("linkedin");
                            googleplus = social.getString("google_plus");
                            if(facebook.trim().isEmpty())
                                facebookImageButton.setVisibility(View.GONE);
                            if(twitter.trim().isEmpty())
                                twitterImageButton.setVisibility(View.GONE);
                            if(linkedin.trim().isEmpty())
                                linkedinImageButton.setVisibility(View.GONE);
                            if(googleplus.trim().isEmpty())
                                googlePlusImageButton.setVisibility(View.GONE);

                            JSONArray extrasArray = data.getJSONArray("extra");
                            for(int i=0;i<extrasArray.length();i++){
                                JSONObject extra = extrasArray.getJSONObject(i);
                                if(extra.getString("field_type_name").equals("emp_about")){
                                    defaultAboutText = extra.getString("value");
                                }
                                    else if(extra.getString("field_type_name").equals("emp_not_skills")) {
                                    skillsEmpty = extra.getString("value");
                                    }
                                else if(extra.getString("field_type_name").equals("emp_skills")) {
                                    skillsTextView.setText(extra.getString("value"));

                                }

                                }

                            JSONArray skillsArray = data.getJSONArray("skills");
                            setSkills(skillsArray, skillsEmpty);

                            JSONArray jsonArray = data.getJSONArray("info");

                            JSONObject dpJsonObject = data.getJSONObject("profile_img");
                            JSONObject coverJsonObject = data.getJSONObject("cvr_img");



                            Log.v("backgroudn",coverJsonObject.getString("img"));
                            if(!TextUtils.isEmpty(dpJsonObject.getString("img")))
                            Picasso.with(getContext()).load(dpJsonObject.getString("img")).into(profileImage);
                            Nokri_SharedPrefManager.saveProfileImage(dpJsonObject.getString("img"),getContext());
                            NavigationView navigationView =  getActivity().findViewById(R.id.nav_view);
                            View headerView = navigationView.getHeaderView(0);
                           CircularImageView dashboardImage = headerView.findViewById(R.id.img_profile);
                            if(!TextUtils.isEmpty(Nokri_SharedPrefManager.getProfileImage(getContext())))
                           Picasso.with(getContext()).load(Nokri_SharedPrefManager.getProfileImage(getContext())).fit().centerCrop().into(dashboardImage);
                            //Picasso.with(getContext()).load(coverJsonObject.getString("img")).fit().centerCrop().into(headerImageView);
                            Nokri_SharedPrefManager.saveProfileImage(dpJsonObject.getString("img"),getContext());
                            Nokri_SharedPrefManager.saveCoverImage(coverJsonObject.getString("img"),getContext());
                            for(int i = 0; i<jsonArray.length();i++){
                                JSONObject jsonData = jsonArray.getJSONObject(i);
                                if(jsonData.getString("field_type_name").equals("emp_name"))
                                {
                                    nameTextView.setText(jsonData.getString("value"));
                                    continue;
                                }


                                if(jsonData.getString("field_type_name").equals("emp_adress")){
                                    addressTextView.setText(jsonData.getString("value"));
                                    Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                    model.setTitle(jsonData.getString("key"));
                                    model.setDescription(jsonData.getString("value"));
                                    modelList.add(model);
                                    continue;
                                }
                                if(jsonData.getString("field_type_name").equals("about_me"))
                                {       aboutMeTextView.setText(jsonData.getString("key"));
                                String aboutMeText = Nokri_Utils.stripHtml(jsonData.getString("value")).toString();
                                if(aboutMeText.isEmpty() || aboutMeText == null)
                                    aboutMeDataTextView.setText(defaultAboutText);
                                else
                                aboutMeDataTextView.setText(aboutMeText);
                                    Nokri_SharedPrefManager.saveAbout(jsonData.getString("value"),getContext());
                                    continue;
                                }
                                if(jsonData.getString("field_type_name").equals("loc")){
                                    locationAndMapTextView.setText(jsonData.getString("key"));
                                    continue;
                                }
                                if(jsonData.getString("field_type_name").equals("emp_lat")){

                                    try {
                                        LATITUDE = Double.parseDouble(jsonData.getString("value"));
                                        continue;
                                    }
                                    catch (NumberFormatException e){
                                        LATITUDE = 0;
                                        continue;
                                    }
                                }
                                if(jsonData.getString("field_type_name").equals("emp_long")){
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
                                if(jsonData.getString("field_type_name").equals("emp_est"))
                                    Nokri_SharedPrefManager.saveEstablishedSince(jsonData.getString("value"),getContext());
                                if(jsonData.getString("field_type_name").equals("emp_nos"))
                                    Nokri_SharedPrefManager.saveNumberOfEmployees(jsonData.getString("value"),getContext());
                                if(jsonData.getString("field_type_name").equals("emp_rgstr"))
                                    Nokri_SharedPrefManager.saveMemberSince(jsonData.getString("value"),getContext());
                                if(jsonData.getString("field_type_name").equals("emp_head"))
                                    Nokri_SharedPrefManager.saveHeadline(jsonData.getString("value"),getContext());



                                modelList.add(model);
                                dialogManager.hideAlertDialog();
                            }
                            Log.v("array",jsonArray.toString());
                        }

                    } catch (JSONException e) {
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showShortToast(getContext(),e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showShortToast(getContext(),e.getMessage());
                    }
                    adapter1 = new Nokri_DescriptionRecyclerViewAdapter(modelList, getContext(), 0, new Nokri_DescriptionRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Nokri_DescriptionModel item) {
                            android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                            RelativeLayout placeholder = getActivity().findViewById(R.id.fragment_placeholder);
                            fragmentTransaction.replace(R.id.fragment_placeholder,new Nokri_CompanyEditProfileFragment()).commit();
                        }
                    });

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView1.setLayoutManager(layoutManager);

                    recyclerView1.setItemAnimator(new DefaultItemAnimator());
                    recyclerView1.setAdapter(adapter1);
//                    Nokri_DialogManager.hideAlertDialog();
                    map.getMapAsync(Nokri_EmployeerDashboardFragment.this);}
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.hideAlertDialog();
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage()); }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nokri_employeer_dashboard, container, false);
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
     /*   LatLng location = new LatLng(LATITUDE, LONGITUDE);
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

        case R.id.img_btn_facebook:
            Nokri_Utils.opeInBrowser(getContext(),facebook);
            break;
        case R.id.img_btn_twitter:
            Nokri_Utils.opeInBrowser(getContext(),twitter);
            break;
        case R.id.img_btn_linkedin:
            Nokri_Utils.opeInBrowser(getContext(),linkedin);
            break;
        case R.id.img_btn_goole_plus:
            Nokri_Utils.opeInBrowser(getContext(),googleplus);
            break;

    }
    }


}
