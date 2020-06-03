package com.donbosco.jobservice.candidate.profile.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.donbosco.jobservice.activities.Nokri_ImagePreview;
import com.donbosco.jobservice.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.donbosco.jobservice.candidate.profile.adapter.Nokri_SkillsAdapter;
import com.donbosco.jobservice.candidate.profile.model.Nokri_ProgressModel;
import com.donbosco.jobservice.employeer.jobs.adapters.Nokri_DescriptionRecyclerViewAdapter;
import com.donbosco.jobservice.employeer.jobs.models.Nokri_DescriptionModel;
import com.donbosco.jobservice.manager.Nokri_RequestHeaderManager;
import com.donbosco.jobservice.manager.Nokri_SharedPrefManager;
import com.donbosco.jobservice.network.Nokri_ServiceGenerator;
import com.donbosco.jobservice.rest.RestService;
import com.donbosco.jobservice.R;
import com.donbosco.jobservice.candidate.edit.fragments.Nokri_CandidateEditProfileFragment;
import com.donbosco.jobservice.candidate.profile.adapter.Nokri_MyProfileAdapter;
import com.donbosco.jobservice.candidate.profile.adapter.Nokri_PortfolioAdapter;
import com.donbosco.jobservice.candidate.profile.model.Nokri_MyProfileModel;
import com.donbosco.jobservice.candidate.profile.model.Nokri_PortfolioModel;
import com.donbosco.jobservice.manager.Nokri_DialogManager;
import com.donbosco.jobservice.manager.Nokri_FontManager;
import com.donbosco.jobservice.manager.Nokri_GoogleAnalyticsManager;
import com.donbosco.jobservice.manager.Nokri_ToastManager;
import com.donbosco.jobservice.utils.Nokri_Globals;
import com.donbosco.jobservice.utils.Nokri_Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_MyProfileFragment extends Fragment  implements View.OnClickListener,Nokri_DescriptionRecyclerViewAdapter.OnItemClickListener {
    private RecyclerView recyclerView1,recyclerView2,recyclerView3,certificationRecyclerview,portfolioRecyclerview;

    private List<Nokri_DescriptionModel> modelList;
    private List<Nokri_MyProfileModel>modelList2,modelList3,modelList4;
    private TextView aboutMeTextView,aboutMeDataTextView,educationalInfoTextView,workExperienceTextView,nameTextView,jobTextView,skillsTextiew,certificationTextView,portfolioTextView;
    private Nokri_FontManager fontManager;
    private CircularImageView profileImageView;
    private ImageView facebookImageView,twitterImageView,googlePlusImageView,linkedinImageView;
    private List<Nokri_PortfolioModel>potfolionModelList;
    private TextView portfolioGoneTextView;

    private String facebook,twitter,linkedin,googlePlus;
    private TextView noSkillsTextView;
    private Nokri_DialogManager dialogManager;
    private TextView youtubeTextView,youtubeGoneTextView;
    private YouTubePlayer YPlayer;
    private RecyclerView skillsRecyclerView;
    private List<Nokri_ProgressModel>skillsModelList;
    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setUpFonts();
        Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getProfile());
        nokri_getProfile();
        nokri_getEducation();
        nokri_getProfession();
        nokri_getCertification();
        nokri_getPortfolio();
    }

    public Nokri_MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_my_profile, container, false);
    }

    private  void nokri_setupDescriptionRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView1.setLayoutManager(layoutManager);

        recyclerView1.setItemAnimator(new DefaultItemAnimator());
        recyclerView1.setAdapter(new Nokri_DescriptionRecyclerViewAdapter(modelList,getContext(),0,this));

    }
    private void nokri_initialize(){

        fontManager = new Nokri_FontManager();

        recyclerView1 = getView().findViewById(R.id.recyclerview1);
        recyclerView2 = getView().findViewById(R.id.recyclerview_educational_info);
        recyclerView3 = getView().findViewById(R.id.recyclerview_work_experience);
        skillsRecyclerView = getView().findViewById(R.id.skills_recyclerview);
        certificationRecyclerview = getView().findViewById(R.id.recyclerview_certification);
        portfolioRecyclerview = getView().findViewById(R.id.recyclerview_portfolio);

        recyclerView1.setNestedScrollingEnabled(false);
        recyclerView2.setNestedScrollingEnabled(false);
        recyclerView3.setNestedScrollingEnabled(false);
        skillsRecyclerView.setNestedScrollingEnabled(false);
        certificationRecyclerview.setNestedScrollingEnabled(false);
        portfolioRecyclerview.setNestedScrollingEnabled(false);


        aboutMeTextView = getView().findViewById(R.id.txt_about_me);
        aboutMeDataTextView = getView().findViewById(R.id.txt_about_me_data);
        nameTextView = getView().findViewById(R.id.txt_name);
        jobTextView = getView().findViewById(R.id.txt_job);
        skillsTextiew = getView().findViewById(R.id.txt_skills);
        educationalInfoTextView = getView().findViewById(R.id.txt_educational_info);
        workExperienceTextView = getView().findViewById(R.id.txt_work_experence);
        portfolioTextView = getView().findViewById(R.id.txt_portfolio);
        portfolioGoneTextView = getView().findViewById(R.id.txt_no_porfolio);
        youtubeTextView = getView().findViewById(R.id.txt_youttube);
        youtubeGoneTextView = getView().findViewById(R.id.txt_no_youtube);

        noSkillsTextView = getView().findViewById(R.id.txt_no_skills);


        profileImageView = getView().findViewById(R.id.img_logo);
        certificationTextView = getView().findViewById(R.id.txt_certificaton);

        facebookImageView = getView().findViewById(R.id.img_facebook);
        twitterImageView = getView().findViewById(R.id.img_twitter);
        googlePlusImageView = getView().findViewById(R.id.img_gooogle_plus);
        linkedinImageView = getView().findViewById(R.id.img_linkedin);




        modelList = new ArrayList<>();
        modelList2 = new ArrayList<>();
        modelList3 = new ArrayList<>();
        modelList4 = new ArrayList<>();
        skillsModelList = new ArrayList<>();
        potfolionModelList = new ArrayList<>();
        facebookImageView.setOnClickListener(this);
        twitterImageView.setOnClickListener(this);
        googlePlusImageView.setOnClickListener(this);
        linkedinImageView.setOnClickListener(this);
    }
    private void nokri_getProfile(){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidateProfile(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidateProfile( Nokri_RequestHeaderManager.addHeaders());
        }
      //  Call<ResponseBody> myCall = service.getCandidateProfile(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){

                    try {
                        String aboutEmpty= null,skillsEmpty = null;
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        Nokri_Globals.EDIT_MESSAGE = jsonObject.getString("message");
                        JSONObject data = jsonObject.getJSONObject("data");

                        JSONArray extrasArray = data.getJSONArray("extra");
                        for (int i =0;i<extrasArray.length();i++){
                            JSONObject extra = extrasArray.getJSONObject(i);
                            if(extra.getString("field_type_name").equals("cand_about"))
                                    aboutEmpty = extra.getString("value");
                                if(extra.getString("field_type_name").equals("cand_skills"))
                                    skillsEmpty = extra.getString("value");

                        }


                        if(!TextUtils.isEmpty(data.getString("profile_img")))
                        Picasso.with(getContext()).load(data.getString("profile_img")).fit().centerCrop().into(profileImageView);
                        JSONArray skillsArray = data.getJSONArray("skills");

                        if(skillsArray.length()>0) {

                            for (int i = 0; i < skillsArray.length(); i++) {
                                JSONObject obj = skillsArray.getJSONObject(i);
                                Nokri_ProgressModel model = new Nokri_ProgressModel();
                                model.setTitle(obj.getString("name"));

                                try {
                                    model.setProgress(Integer.parseInt(obj.getString("Percent value")));
                                } catch (NumberFormatException ex) {
                                    model.setProgress(0);
                                } finally {
                                    skillsModelList.add(model);
                                }

                            }
                            nokri_setupSkillsRecyclerView();
                        }
                        else
                        {
                            noSkillsTextView.setVisibility(View.VISIBLE);
                            noSkillsTextView.setText(skillsEmpty);
                        }


                        JSONObject socialObject = data.getJSONObject("social");
                         facebook = socialObject.getString("facebook");
                         twitter = socialObject.getString("twitter");
                         linkedin = socialObject.getString("linkedin");
                         googlePlus = socialObject.getString("google_plus");
                        if(facebook.trim().isEmpty())
                            facebookImageView.setVisibility(View.GONE);
                        if(twitter.trim().isEmpty())
                            twitterImageView.setVisibility(View.GONE);
                        if(linkedin.trim().isEmpty())
                            linkedinImageView.setVisibility(View.GONE);
                        if(googlePlus.trim().isEmpty())
                            googlePlusImageView.setVisibility(View.GONE);
                        JSONArray infoArray = data.getJSONArray("info");
                        for(int i=0;i<infoArray.length();i++)
                        {       JSONObject response = infoArray.getJSONObject(i);
                            if(response.getString("field_type_name").equals("cand_name"))
                                nameTextView.setText(response.getString("value").trim());
                            else
                            if(response.getString("field_type_name").equals("cand_hand"))
                                jobTextView.setText(response.getString("value").trim());
                            else
                            if(response.getString("field_type_name").equals("about_me"))
                            { aboutMeTextView.setText(response.getString("key").trim());
                                String text = Nokri_Utils.stripHtml(response.getString("value")).toString();
                                if(text.isEmpty())
                                    aboutMeDataTextView.setText(aboutEmpty.trim());
                                else
                                aboutMeDataTextView.setText(Nokri_Utils.stripHtml(response.getString("value").trim()));}
                            else if(!response.getString("field_type_name").equals("your_dashbord")&&!response.getString("field_type_name").equals("loc")&&!response.getString("field_type_name").equals("cand_long")&&!response.getString("field_type_name").equals("cand_lat") && !response.getString("field_type_name").equals("set_profile"))
                            {
                                Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                model.setTitle(response.getString("key"));
                                model.setDescription(response.getString("value"));
                                modelList.add(model);
                            }
                        }
                        nokri_setupDescriptionRecyclerView();
                        //  Nokri_DialogManager.hideAlertDialog();
                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    }

                }
                else {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });

    }
    private void nokri_getEducation(){
        //Nokri_DialogManager.showAlertDialog(this);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidateEducation(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidateEducation( Nokri_RequestHeaderManager.addHeaders());
        }
       // Call<ResponseBody> myCall = service.getCandidateEducation(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        boolean isEmpty = false;
                        //    Log.v("response",responseObject.body().string());
                        String notAdded = null;

                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        JSONArray dataArray = dataObject.getJSONArray("education");
                        JSONArray extrasArray = dataObject.getJSONArray("extras");


                        for(int i = 0;i<extrasArray.length();i++){
                            JSONObject object = extrasArray.getJSONObject(i);
                            if(object.getString("field_type_name").equals("section_name"))
                                educationalInfoTextView.setText(object.getString("value").trim());

                            if(object.getString("field_type_name").equals("not_added"))
                                notAdded = object.getString("value");
                        }

                        for(int i=0;i<dataArray.length();i++)
                        {String degreeStart = null,degreeEnd = null;
                            JSONArray responseArray = dataArray.getJSONArray(i);
                            Nokri_MyProfileModel model = new Nokri_MyProfileModel();
                            for(int j=0;j<responseArray.length();j++)
                            {
                                JSONObject response = responseArray.getJSONObject(j);

                                if(response.getString("field_type_name").equals("degree_name"))
                                    model.setDegreeTitle(response.getString("value"));
                                else if(response.getString("field_type_name").equals("degree_start"))
                                    degreeStart = response.getString("value");
                                else if(response.getString("field_type_name").equals("degree_end"))
                                    degreeEnd =response.getString("value");
                                else if(response.getString("field_type_name").equals("degree_institute"))
                                    model.setTitleText(response.getString("value"));
                                else if(response.getString("field_type_name").equals("degree_percent")){
                                    model.setDegreePercentage(response.getString("value"));
                                    model.setPercentageTitle(response.getString("key"));

                                }
                                else if(response.getString("field_type_name").equals("degree_grade")) {
                                    model.setDegreeGrade(response.getString("value"));
                                    model.setGradeTitle(response.getString("key"));

                                }



                                else if(response.getString("field_type_name").equals("degree_detail"))
                                    model.setParagraphText(Nokri_Utils.stripHtml(response.getString("value")).toString());

                                if(j+1==responseArray.length())
                                {
                                    if(degreeStart.trim().isEmpty() || degreeEnd.trim().isEmpty()) {
                                        model.setDateText(notAdded);
                                        isEmpty = true;
                                    }
                                    else
                                    model.setDateText(degreeStart+" to "+degreeEnd);
                                    // Log.v("response",model.toString());
                                    modelList2.add(model);

                                }
                            }

                        }
                        nokri_setupEducationRecyclerview(isEmpty);
                        //     Nokri_DialogManager.hideAlertDialog();
                    }
                    catch (JSONException e) {
                        //  Nokri_DialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {

                        //  Nokri_DialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    }
                }
                else {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }
    private void nokri_getProfession(){
        //Nokri_DialogManager.showAlertDialog(this);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidateProfession(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidateProfession( Nokri_RequestHeaderManager.addHeaders());
        }
       // Call<ResponseBody> myCall = service.getCandidateProfession(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {String notAdded = null;
                        //    Log.v("response",responseObject.body().string());
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        JSONArray extras = dataObject.getJSONArray("extras") ;
                        for(int i =0;i<extras.length();i++)
                        {
                            JSONObject object = extras.getJSONObject(i);
                            if(object.getString("field_type_name").equals("section_name"))
                                        workExperienceTextView.setText(object.getString("value").trim());
                            if(object.getString("field_type_name").equals("not_added"))
                                notAdded = object.getString("value");
                        }
                        JSONArray dataArray = dataObject.getJSONArray("profession");
                        for(int i=0;i<dataArray.length();i++)
                        {String degreeStart = null,degreeEnd = null;
                            JSONArray responseArray = dataArray.getJSONArray(i);
                            Nokri_MyProfileModel model = new Nokri_MyProfileModel();
                            for(int j=0;j<responseArray.length();j++)
                            {
                                JSONObject response = responseArray.getJSONObject(j);

                                if(response.getString("field_type_name").equals("project_organization"))
                                    model.setDegreeTitle(response.getString("value"));
                                else if(response.getString("field_type_name").equals("project_start"))
                                    degreeStart = response.getString("value");
                                else if(response.getString("field_type_name").equals("project_end"))
                                    degreeEnd =response.getString("value");
                                else if(response.getString("field_type_name").equals("project_role"))
                                    model.setTitleText(response.getString("value"));
                                else if(response.getString("field_type_name").equals("project_desc"))
                                    model.setParagraphText(Nokri_Utils.stripHtml(response.getString("value")).toString());

                                if(j+1==responseArray.length())
                                {
                                    if(degreeStart.isEmpty() || degreeEnd.isEmpty())
                                        model.setDateText(notAdded);
                                    else
                                        model.setDateText(degreeStart+" to "+degreeEnd);
                                    modelList3.add(model);

                                }
                            }

                        }
                        nokri_setupProfesstionRecyclerview();
                        //    Nokri_DialogManager.hideAlertDialog();
                    }
                    catch (JSONException e) {
                        //  Nokri_DialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {

                        //  Nokri_DialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    }
                }
                else {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }

    private void nokri_getCertification(){
        //Nokri_DialogManager.showAlertDialog(this);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidateCerification(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidateCerification( Nokri_RequestHeaderManager.addHeaders());
        }
      //  Call<ResponseBody> myCall = service.getCandidateCerification(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {String notAdded = null;
                        //    Log.v("response",responseObject.body().string());
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        JSONArray extras = dataObject.getJSONArray("extras");
                        for(int i=0;i<extras.length();i++)
                        {
                            JSONObject object = extras.getJSONObject(i);
                            if(object.getString("field_type_name").equals("section_name"))
                                certificationTextView.setText(object.getString("value").trim());

                            if(object.getString("field_type_name").equals("not_added"))
                                notAdded = object.getString("value");
                        }


                        JSONArray dataArray = dataObject.getJSONArray("certification");
                        for(int i=0;i<dataArray.length();i++)
                        {String degreeStart = null,degreeEnd = null;
                            JSONArray responseArray = dataArray.getJSONArray(i);
                            Nokri_MyProfileModel model = new Nokri_MyProfileModel();
                            for(int j=0;j<responseArray.length();j++)
                            {
                                JSONObject response = responseArray.getJSONObject(j);

                                if(response.getString("field_type_name").equals("certification_name"))
                                    model.setDegreeTitle(response.getString("value"));
                                else if(response.getString("field_type_name").equals("certification_start"))
                                    degreeStart = response.getString("value");
                                else if(response.getString("field_type_name").equals("certification_end"))
                                    degreeEnd =response.getString("value");
                                else if(response.getString("field_type_name").equals("certification_institute"))
                                    model.setTitleText(response.getString("value"));
                                else if(response.getString("field_type_name").equals("certification_desc"))
                                    model.setParagraphText(Nokri_Utils.stripHtml(response.getString("value")).toString());

                                if(j+1==responseArray.length())
                                {
                                    if(degreeStart.isEmpty() || degreeEnd.isEmpty())
                                        model.setDateText(notAdded);
                                    else
                                        model.setDateText(degreeStart+" to "+degreeEnd);
                                    modelList4.add(model);

                                }
                            }

                        }
                        nokri_setupCertificationRecyclerview();
                    }
                    catch (JSONException e) {
                        //  Nokri_DialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {

                        //  Nokri_DialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    }
                }
                else {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }

    private void nokri_getPortfolio(){
        //Nokri_DialogManager.showAlertDialog(this);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidatePortfolio(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidatePortfolio( Nokri_RequestHeaderManager.addHeaders());
        }
     //   Call<ResponseBody> myCall = service.getCandidatePortfolio(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        //    Log.v("response",responseObject.body().string());
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        JSONObject dataObject = jsonObject.getJSONObject("data");

                        JSONArray dataArray = dataObject.getJSONArray("img");
                        JSONArray extras = dataObject.getJSONArray("extra");
                        String youtubeGone = "";
                        for(int i =0;i<extras.length();i++){
                            JSONObject object = extras.getJSONObject(i);
                            if(object.getString("field_type_name").equals("section_label"))
                            {   portfolioTextView.setText(object.getString("value").trim());

                            }
                            if(object.getString("field_type_name").equals("not_added"))
                            {   portfolioGoneTextView.setText(object.getString("value").trim());

                            }

                            if(object.getString("field_type_name").equals("video_url"))
                            {   youtubeTextView.setText(object.getString("key").trim());
                                if(!object.getBoolean("is_required")){

                                    youtubeGoneTextView.setVisibility(View.VISIBLE);
                                }
                                else
                                {

                                    nokri_initYoutube(object.getString("value"));
                                }
                            }

                            if(object.getString("field_type_name").equals("no_video_url"))
                            {

                                    youtubeGoneTextView.setText(object.getString("value").trim());

                            }

                        }
                        if(dataArray.length()<=0)
                        {
                            portfolioGoneTextView.setVisibility(View.VISIBLE);
                            dialogManager.hideAlertDialog();
                            return;
                        }
                        else
                            portfolioGoneTextView.setVisibility(View.GONE);

                        for(int i=0;i<dataArray.length();i++)
                        {
                            JSONObject object = dataArray.getJSONObject(i);
                            Nokri_PortfolioModel model = new Nokri_PortfolioModel();
                            model.setUrl(object.getString("value"));
                            potfolionModelList.add(model);
                        }
                        nokri_setupPortfolioRecyclerview();
                        dialogManager.hideAlertDialog(); }
                    catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {

                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    }
                }
                else {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }

    private void nokri_setUpFonts(){
        fontManager.nokri_setMonesrratSemiBioldFont(aboutMeTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(skillsTextiew,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(educationalInfoTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(workExperienceTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(certificationTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(portfolioTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(youtubeTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(portfolioGoneTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(noSkillsTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(youtubeGoneTextView,getActivity().getAssets());
    }


    private void nokri_setupEducationRecyclerview(boolean isEmpty){
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        recyclerView2.setLayoutManager(layoutManager2);

        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        if(!isEmpty)
        recyclerView2.setAdapter(new Nokri_MyProfileAdapter(modelList2,getContext(),true));
        else
            recyclerView2.setAdapter(new Nokri_MyProfileAdapter(modelList2,getContext()));
    }
    private void nokri_setupProfesstionRecyclerview(){
        RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(getContext());
        recyclerView3.setLayoutManager(layoutManager3);
        recyclerView3.setItemAnimator(new DefaultItemAnimator());
        recyclerView3.setAdapter(new Nokri_MyProfileAdapter(modelList3,getContext()));
    }
    private void nokri_setupPortfolioRecyclerview(){
        portfolioRecyclerview.setLayoutManager(new GridLayoutManager(getContext(),4));
        portfolioRecyclerview.setItemAnimator(new DefaultItemAnimator());


        portfolioRecyclerview.setAdapter(new Nokri_PortfolioAdapter(potfolionModelList, getContext(), new Nokri_PortfolioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_PortfolioModel item, int position) {
                Intent intent = new Intent(getActivity(), Nokri_ImagePreview.class);
              //  Nokri_ImagePreview.INDEX = position;
                intent.putStringArrayListExtra(Nokri_ImagePreview.EXTRA_NAME, nokri_getImagesList(potfolionModelList,position));


                startActivity(intent);
            }
        }));
    }
    private ArrayList<String> nokri_getImagesList(List<Nokri_PortfolioModel> models, int position){
        ArrayList<String>images = new ArrayList<>();
        for(int i =0;i<models.size();i++) {
            images.add(models.get(i).getUrl());

        }
        Collections.swap(images, 0, position);
        return images;
    }
    private void nokri_setupCertificationRecyclerview(){
        RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(getContext());
        certificationRecyclerview.setLayoutManager(layoutManager3);

        certificationRecyclerview.setItemAnimator(new DefaultItemAnimator());
        certificationRecyclerview.setAdapter(new Nokri_MyProfileAdapter(modelList4,getContext()));
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
            case R.id.img_gooogle_plus:
                Nokri_Utils.opeInBrowser(getContext(),googlePlus);
                break;
            case R.id.img_linkedin:
                Nokri_Utils.opeInBrowser(getContext(),linkedin);
                break;
        }
    }

    @Override
    public void onItemClick(Nokri_DescriptionModel item) {
        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_placeholder,new Nokri_CandidateEditProfileFragment()).commit();
    }

    private void nokri_initYoutube(final String url){



        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();
        youTubePlayerFragment.initialize(getResources().getString(R.string.google_api_credentials_for_youtube), new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    YPlayer = youTubePlayer;
                    YPlayer.setFullscreen(false);
                    YPlayer.setShowFullscreenButton(false);
                    YPlayer.cueVideo(url);

                    //YPlayer.play();

                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {


            }
        });




    }


    private void nokri_setupSkillsRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        skillsRecyclerView.setLayoutManager(layoutManager);

        skillsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        skillsRecyclerView.setAdapter(new Nokri_SkillsAdapter(skillsModelList,getContext()));

    }


}
