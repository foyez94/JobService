package com.donbosco.jobservice.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.libraries.places.api.Places;
import com.google.gson.JsonObject;
import com.donbosco.jobservice.candidate.dashboard.Nokri_CandidateDashboardActivity;
import com.donbosco.jobservice.guest.dashboard.Nokri_GuestDashboardActivity;
import com.donbosco.jobservice.guest.settings.models.Nokri_SettingsModel;
import com.donbosco.jobservice.manager.Nokri_RequestHeaderManager;
import com.donbosco.jobservice.manager.Nokri_SharedPrefManager;
import com.donbosco.jobservice.manager.Nokri_ToastManager;
import com.donbosco.jobservice.network.Nokri_ServiceGenerator;
import com.donbosco.jobservice.rest.RestService;
import com.donbosco.jobservice.R;
import com.donbosco.jobservice.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.donbosco.jobservice.employeer.dashboard.Nokri_EmployeerDashboardActivity;
import com.donbosco.jobservice.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.donbosco.jobservice.guest.dashboard.models.Nokri_GuestDashboardModel;
import com.donbosco.jobservice.guest.home.models.Nokri_RateAppModel;
import com.donbosco.jobservice.guest.models.Nokri_MenuActiveJobsModel;
import com.donbosco.jobservice.guest.models.Nokri_MenuJobModel;
import com.donbosco.jobservice.guest.models.Nokri_MenuResumeReceivedModel;
import com.donbosco.jobservice.guest.models.Nokri_MenuSavedJobsModel;
import com.donbosco.jobservice.manager.Nokri_FontManager;
import com.donbosco.jobservice.manager.Nokri_GoogleAnalyticsManager;
import com.donbosco.jobservice.manager.Nokri_PopupManager;
import com.donbosco.jobservice.manager.models.Nokri_PopupModel;
import com.donbosco.jobservice.manager.models.Nokri_ProgressModel;
import com.donbosco.jobservice.manager.notification.FireBaseNotificationModel;
import com.donbosco.jobservice.utils.Nokri_Config;
import com.donbosco.jobservice.utils.Nokri_Globals;
import com.donbosco.jobservice.utils.Nokri_LanguageSupport;
import com.donbosco.jobservice.utils.Nokri_Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Nokri_SplashActivity extends AppCompatActivity implements Nokri_PopupManager.NoInternetInterface {
    private TextView loadingText;
    private static final int PERMISSION_CODE = 100;
    private ImageView logo;

    //  private LinearLayout adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nokri_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_SharedPrefManager.getAppColor(this)));
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(this, getResources().getString(R.string.google_places_API_key));
        }

        //updateViews("ku");
        logo = findViewById(R.id.img_logo);
        Picasso.with(this).load(R.drawable.logo).into(logo);

        Nokri_Utils.generateKeyhash(this);

        Nokri_Utils.turnSystemFontsOff(this);

        if (Build.VERSION.SDK_INT >= 23) {
            if(nokri_checkAndRequestPermission())
                nokri_performAcions();
        }
        else
            nokri_performAcions();

      //  PayUBaseActivity sadf;




    }


    private void nokri_performAcions(){

        if (!Nokri_SharedPrefManager.isAccountEmployeer(this) && !Nokri_SharedPrefManager.isAccountCandidate(this))
            Nokri_SharedPrefManager.saveAccountType("public", this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Nokri_GoogleAnalyticsManager.initialize(this);

        Nokri_GoogleAnalyticsManager.getInstance().get(Nokri_GoogleAnalyticsManager.Target.APP, Nokri_Config.GOOGLE_ANALYTICS_TRACKING_ID);


        loadingText = findViewById(R.id.loading_txt);
//        loadingText.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        new Nokri_FontManager().nokri_setMonesrratSemiBioldFont(loadingText, getAssets());
        nokri_saveAppSettings();
    }



    private void nokri_saveAppSettings() {


        FireBaseNotificationModel fireBaseNotificationModel = Nokri_SharedPrefManager.getFirebaseNotification(this);
        if(fireBaseNotificationModel!=null){

            fireBaseNotificationModel.setTitle("");
            Nokri_SharedPrefManager.saveFireBaseNotification(fireBaseNotificationModel,this);
        }


        //Nokri_DialogManager.showAlertDialog(this);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall;

        myCall = restService.getCandidateSettings(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {

                    try {


                        JSONObject jsonObject = new JSONObject(responseObject.body().string());

                        if (jsonObject.getBoolean("success")) {

                            JSONObject data = jsonObject.getJSONObject("data");





                            Nokri_SettingsModel settings = new Nokri_SettingsModel();

                            settings.setAboutEnabled(data.getJSONObject("about").getBoolean("about_section"));
                            settings.setAboutText(data.getJSONObject("about").getString("about_details"));
                            settings.setAboutTitle(data.getJSONObject("about").getString("about_title"));
                            settings.setVersionEnabled(data.getJSONObject("version").getBoolean("version_section"));
                            settings.setVersionText(data.getJSONObject("version").getString("version_txt"));
                            settings.setRatingEnabled(data.getJSONObject("rating").getBoolean("rating_section"));
                            settings.setRatingText(data.getJSONObject("rating").getString("rating_txt"));
                            settings.setShareEnabled(data.getJSONObject("share").getBoolean("share_section"));
                            settings.setShareText(data.getJSONObject("share").getString("popup_title"));
                            settings.setShareSubject(data.getJSONObject("share").getString("subject"));
                            settings.setShareUrl(data.getJSONObject("share").getString("url"));
                            settings.setPrvacyEnabled(data.getJSONObject("privacy").getBoolean("privacy_section"));
                            settings.setPrivacyText(data.getJSONObject("privacy").getString("privacy_title"));
                            settings.setPrivacyUrl(data.getJSONObject("privacy").getString("url"));

                            settings.setTermsEnabled(data.getJSONObject("terms_n_conditions").getBoolean("terms_section"));
                            settings.setTermsText(data.getJSONObject("terms_n_conditions").getString("terms_title"));
                            settings.setTermsUrl(data.getJSONObject("terms_n_conditions").getString("url"));
                            settings.setFaqEnabled(data.getJSONObject("faqs_section").getBoolean("faq"));
                             settings.setFeedbackEnabled(data.getJSONObject("feedback").getBoolean("is_show"));

                                if(data.getJSONObject("feedback").getBoolean("is_show")) {
                                    settings.setFeedbackTitle(data.getJSONObject("feedback").getString("title"));
                                    settings.setFeedbackSubtitle(data.getJSONObject("feedback").getString("subline"));
                                    settings.setFeedbackFormTitle(data.getJSONObject("feedback").getJSONObject("form").getString("title"));
                                    settings.setFeedbackFormEmail(data.getJSONObject("feedback").getJSONObject("form").getString("email"));
                                    settings.setFeedbackFormMessage(data.getJSONObject("feedback").getJSONObject("form").getString("message"));
                                    settings.setFormSubmit(data.getJSONObject("feedback").getJSONObject("form").getString("btn_submit"));
                                    settings.setFormCancel(data.getJSONObject("feedback").getJSONObject("form").getString("btn_cancel"));
                                    settings.setFromHeading(data.getJSONObject("feedback").getJSONObject("form").getString("header"));

                                }
                                  Nokri_SharedPrefManager.saveSettings(settings, Nokri_SplashActivity.this);







                            Nokri_Config.STRIPE_KEY = data.getJSONObject("extra").getString("stripe_Skey");
                            Nokri_Globals.NEXT_STEP = data.getJSONObject("extra").getString("nxt_step");
                            Nokri_Globals.LOGIN_FIRST = data.getJSONObject("extra").getString("is_login");
                            Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER =  data.getJSONObject("extra").getString("all_fields");
                            Nokri_Globals.NO_URL_FOUND =  data.getJSONObject("extra").getString("no_url");
                            Nokri_Globals.COMMENT_REQUIRED_TEXT =  data.getJSONObject("extra").getString("coment_first");
                            Nokri_Globals.REQUIRED_REPY_TEXT =  data.getJSONObject("extra").getString("rply_first");
                            Nokri_Globals.APP_NOT_FOUNT =  data.getJSONObject("extra").getString("no_app");
                            Nokri_Globals.EXIT_TEXT =  data.getJSONObject("extra").getString("exit");
                            Nokri_Globals.INVALID_EMAIL =  data.getJSONObject("extra").getString("valid_email");
                            Nokri_Globals.TERMS_AND_SERVICES =  data.getJSONObject("extra").getString("agree_term");
                            Nokri_Globals.ON_BACK_EXIT_TEXT =  data.getJSONObject("extra").getString("click_back");
                            Nokri_Globals.SELECT_VALID_SKILL =  data.getJSONObject("extra").getString("valid_skill");
                            Nokri_Globals.SELECT_SKILL =  data.getJSONObject("extra").getString("select_one");
                            Nokri_Globals.Continue_buy_Package =  data.getJSONObject("extra").getString("continue");
                            Nokri_Globals.APP_NOT_INSTALLED =  data.getJSONObject("extra").getString("not_instal");
                            Nokri_Globals.INVALID_URL =  data.getJSONObject("extra").getString("invalid_url");
                            Nokri_Globals.JOB_SEARCH_PLACEHOLER = data.getJSONObject("extra").getString("place_hldr");


                            JSONObject tabs = data.getJSONObject("tabs");
                            JSONObject employeerTabs = data.getJSONObject("emp_tabs");
                            JSONObject guestTabs = data.getJSONObject("guest_tabs");

                            Nokri_Globals.PLEASE_WAIT_TEXT = tabs.getString("loading");

                            Nokri_CandidateDashboardModel model = new Nokri_CandidateDashboardModel();
                            model.setDashboard(tabs.getString("dashboard"));
                            model.setEdit(tabs.getString("edit"));
                            model.setProfile(tabs.getString("profile"));
                            model.setResume(tabs.getString("resume"));
                            model.setApplied(tabs.getString("apllied"));
                            model.setSaved(tabs.getString("saved"));
                            model.setJobs(tabs.getString("jobs"));
                            model.setCompany(tabs.getString("company"));
                            model.setExplore(tabs.getString("search"));
                            model.setBlog(tabs.getString("blog"));
                            model.setLogout(tabs.getString("logout"));
                            model.setExit(tabs.getString("exit"));
                            model.setHome(tabs.getString("home"));
                            model.setFaq(employeerTabs.getString("faq"));

                            JSONObject candidateEditTabs = data.getJSONObject("cand_tabs");
                            //model.setSettings("set");
                            model.setSettings(data.getJSONObject("extra").getString("settings_txt"));
                            model.setTabPersonal(candidateEditTabs.getString("personal"));
                            model.setTabResume(candidateEditTabs.getString("resumes"));
                            model.setTabEducation(candidateEditTabs.getString("education"));
                            model.setTabProfessional(candidateEditTabs.getString("experience"));
                            model.setTabCertification(candidateEditTabs.getString("certification"));
                            model.setTabSkills(candidateEditTabs.getString("skills"));
                            model.setTabPortfolio(candidateEditTabs.getString("portfolio"));
                            model.setTabSocial(candidateEditTabs.getString("socail"));
                            model.setTabLocation(candidateEditTabs.getString("loca"));
                            model.setCandidateSearch(candidateEditTabs.getString("cand_search"));
                            model.setJobsForYou(candidateEditTabs.getString("job_for"));

                            Nokri_SharedPrefManager.saveCandidateSettings(model, Nokri_SplashActivity.this);

                            Nokri_EmployeerDashboardModel employeerDashboardModel = new Nokri_EmployeerDashboardModel();
                            employeerDashboardModel.setDashboard(employeerTabs.getString("dashboard"));
                            employeerDashboardModel.setProfile(employeerTabs.getString("profile"));
                            employeerDashboardModel.setTemplates(employeerTabs.getString("templates"));
                            employeerDashboardModel.setJobs(employeerTabs.getString("jobs"));
                            employeerDashboardModel.setFollower(employeerTabs.getString("followers"));
                            employeerDashboardModel.setPostJob(employeerTabs.getString("post_job"));
                            employeerDashboardModel.setPackageDetail(employeerTabs.getString("pkg_detail"));
                            employeerDashboardModel.setBuyPackage(employeerTabs.getString("buy_package"));
                            employeerDashboardModel.setBlog(employeerTabs.getString("blog"));
                            employeerDashboardModel.setLogout(employeerTabs.getString("logout"));
                            employeerDashboardModel.setExit(employeerTabs.getString("exit"));
                            employeerDashboardModel.setHome(employeerTabs.getString("home"));
                            employeerDashboardModel.setFaq(employeerTabs.getString("faq"));
                            employeerDashboardModel.setCandidateSearch(candidateEditTabs.getString("cand_search"));

                            JSONObject empTabs = data.getJSONObject("emp_edit_tabs");
                            employeerDashboardModel.setTabInfo(empTabs.getString("info"));
                            employeerDashboardModel.setTabspecialization(empTabs.getString("special"));
                            employeerDashboardModel.setTabSocail(empTabs.getString("social"));
                            employeerDashboardModel.setTabLocation(empTabs.getString("loc"));


                            JSONObject jobsTabs = data.getJSONObject("emp_jobs");
                            employeerDashboardModel.setJobActive(jobsTabs.getString("active"));
                            employeerDashboardModel.setJobInactive(jobsTabs.getString("inactive"));

                            JSONObject employeerPublicTabs = data.getJSONObject("compny_public_jobs");
                            employeerDashboardModel.setTabPublicOpen(employeerPublicTabs.getString("open"));
                            employeerDashboardModel.setTabPublicDetails(employeerPublicTabs.getString("details"));
                            //employeerDashboardModel.setSettings("set");
                            employeerDashboardModel.setSettings(data.getJSONObject("extra").getString("settings_txt"));
                            Nokri_SharedPrefManager.saveEmployeerSettings(employeerDashboardModel, Nokri_SplashActivity.this);

                            Nokri_GuestDashboardModel guestDashboardModel = new Nokri_GuestDashboardModel();
                            guestDashboardModel.setHome(guestTabs.getString("home"));
                            guestDashboardModel.setExplore(guestTabs.getString("explore"));
                            guestDashboardModel.setExit(guestTabs.getString("exit"));
                            guestDashboardModel.setSignin(guestTabs.getString("signin"));
                            guestDashboardModel.setSignup(guestTabs.getString("signup"));
                            guestDashboardModel.setBlog(guestTabs.getString("templates"));
                            guestDashboardModel.setGuestName(guestTabs.getString("guset"));
                            guestDashboardModel.setCandidateDp(guestTabs.getString("cand_dp"));
                            guestDashboardModel.setFaq(employeerTabs.getString("faq"));
                            //guestDashboardModel.setSettings("Set");
                            guestDashboardModel.setSettings(data.getJSONObject("extra").getString("settings_txt"));
                            JSONObject homeSceenTabs = data.getJSONObject("public_jobs");
                            guestDashboardModel.setTabLatest(homeSceenTabs.getString("latest"));
                            guestDashboardModel.setTabPrenium(homeSceenTabs.getString("premium"));
                            guestDashboardModel.setCandidateSearch(candidateEditTabs.getString("cand_search"));

                            Nokri_SharedPrefManager.saveGuestSettings(guestDashboardModel, Nokri_SplashActivity.this);
                            Nokri_PopupModel popupModel = new Nokri_PopupModel();

                            JSONObject genericDocumentionObject = data.getJSONObject("generic_txts");

                            popupModel.setCancelButton(genericDocumentionObject.getString("btn_cancel"));
                            popupModel.setConfirmButton(genericDocumentionObject.getString("btn_confirm"));
                            popupModel.setConfirmText(genericDocumentionObject.getString("confirm"));
                            popupModel.setSuccessText(genericDocumentionObject.getString("success"));

                            Nokri_Globals.EXIT_TEXT = genericDocumentionObject.getString("confirm");

                            Nokri_SharedPrefManager.savePopupSettings(popupModel,Nokri_SplashActivity.this);

                            JSONObject porgressDialogObject = data.getJSONObject("progress_txt");
                            Nokri_ProgressModel progressModel = new Nokri_ProgressModel();
                            progressModel.setTitle(porgressDialogObject.getString("title"));
                            progressModel.setFailMessage(porgressDialogObject.getString("msg_fail"));
                            progressModel.setSuccessMessage(porgressDialogObject.getString("msg_success"));
                            progressModel.setSuccessTitle(porgressDialogObject.getString("title_success"));
                            progressModel.setFailTitles(porgressDialogObject.getString("title_fail"));
                            progressModel.setButtonText(porgressDialogObject.getString("btn_ok"));
                             Nokri_SharedPrefManager.saveProgressSettings(progressModel,Nokri_SplashActivity.this);




                           JSONObject activeJobsMenuObject = data.getJSONObject("menu_active");
                            Nokri_MenuActiveJobsModel activeJobsModel = new Nokri_MenuActiveJobsModel();
                            activeJobsModel.setDelete(activeJobsMenuObject.getString("del"));
                            activeJobsModel.setEdit(activeJobsMenuObject.getString("edit"));
                            activeJobsModel.setResumeReceived(activeJobsMenuObject.getString("resume"));
                            activeJobsModel.setViewJob(activeJobsMenuObject.getString("view"));
                            Nokri_SharedPrefManager.saveActiveJobMenuSettings(activeJobsModel,Nokri_SplashActivity.this);

                            JSONObject jobsMenuObject = data.getJSONObject("menu_job");
                            Nokri_MenuJobModel jobModel = new Nokri_MenuJobModel();
                            jobModel.setViewCompanyProfile(jobsMenuObject.getString("company"));
                            jobModel.setViewJob(jobsMenuObject.getString("view"));
                            Nokri_SharedPrefManager.saveJobMenuSettings(jobModel,Nokri_SplashActivity.this);

                            JSONObject resumeReceivedMenuObject = data.getJSONObject("menu_resume");
                            Nokri_MenuResumeReceivedModel resumeReceivedModel = new Nokri_MenuResumeReceivedModel();
                            resumeReceivedModel.setDownload(resumeReceivedMenuObject.getString("download"));
                            resumeReceivedModel.setTakeAction(resumeReceivedMenuObject.getString("action"));
                            resumeReceivedModel.setViewProfile(resumeReceivedMenuObject.getString("profile"));
                            resumeReceivedModel.setLinkedin(resumeReceivedMenuObject.getString("linkedin"));
                            Nokri_SharedPrefManager.saveResumeReceivedbMenuSettings(resumeReceivedModel,Nokri_SplashActivity.this);

                            JSONObject savedJobsMenuObject = data.getJSONObject("menu_saved");
                            Nokri_MenuSavedJobsModel savedJobsModel = new Nokri_MenuSavedJobsModel();
                            savedJobsModel.setDeleteJob(savedJobsMenuObject.getString("delete"));
                            savedJobsModel.setViewJob(savedJobsMenuObject.getString("view"));
                            Nokri_SharedPrefManager.saveSavedJobsbMenuSettings(savedJobsModel,Nokri_SplashActivity.this);


                            Nokri_RateAppModel rateAppModel = new Nokri_RateAppModel();
                            rateAppModel.setCancelButton("Never");
                            rateAppModel.setConfirmButton("Maybe Later");
                            rateAppModel.setTitle("Rate Us?");
                            rateAppModel.setUrl("http://play.google.com/store/apps/details?id=" + getPackageName());
                            Nokri_SharedPrefManager.saveSaveRateAppModel(rateAppModel,Nokri_SplashActivity.this);
                //------------------------ Ads Settings -------------------------------------

                            JSONObject adObject = data.getJSONObject("ads");

                            if(adObject.getBoolean("show"))
                            {
                                Nokri_Globals.SHOW_AD = true;
                                if(adObject.getString("position").equals("top"))
                                {
                                    Nokri_Globals.SHOW_AD_TOP = true;
                                }
                                else
                                    Nokri_Globals.SHOW_AD_TOP = false;

                                if(adObject.getBoolean("is_show_banner"))
                                {
                                    Nokri_Globals.IS_BANNER_EBABLED = true;
                                    Nokri_Globals.AD_ID = adObject.getString("banner_id");
                                }

                                if(adObject.getBoolean("is_show_initial"))
                                {
                                    Nokri_Globals.IS_INTERTIAL_ENABLED = true;
                                    Nokri_Globals.INTERTIAL_ID = adObject.getString("ad_id");

                                    try{

                                        Nokri_Globals.AD_INITIAL_TIME = Long.parseLong(adObject.getString("time_initial"));
                                        Nokri_Globals.AD_DISPLAY_TIME = Long.parseLong(adObject.getString("time"));
                                    }
                                    catch (NumberFormatException e){


                                    }

                                }

                            }
                         //   Nokri_Config.APP_COLOR = "#000000";
                            Nokri_Config.APP_COLOR = data.getString("app_color");
                            Nokri_SharedPrefManager.saveAppColor(data.getString("app_color"),Nokri_SplashActivity.this);
                            //------------------------ Ads Settings -------------------------------------
                            Nokri_SharedPrefManager.saveHomeType(data.getString("home"),Nokri_SplashActivity.this);
                            Nokri_SharedPrefManager.showBlog(data.getString("isBlog"),Nokri_SplashActivity.this);
                            if(data.getString("is_rtl").equals("0"))
                            {
                                Nokri_Globals.IS_RTL_ENABLED = false;
                                Nokri_SharedPrefManager.saveLocal("en",Nokri_SplashActivity.this);
                                updateViews("en");

                                }
                            else  if(data.getString("is_rtl").equals("1")){
                                Nokri_Globals.IS_RTL_ENABLED = true;
                                Nokri_SharedPrefManager.saveLocal("ar",Nokri_SplashActivity.this);
                                updateViews("ar");

                            }

                            if (!Nokri_SharedPrefManager.isSocialLogin(Nokri_SplashActivity.this) && Nokri_SharedPrefManager.getEmail(Nokri_SplashActivity.this) != null && Nokri_SharedPrefManager.getPassword(Nokri_SplashActivity.this) != null) {
                                nokri_postSignin(Nokri_SharedPrefManager.getEmail(Nokri_SplashActivity.this), Nokri_SharedPrefManager.getPassword(Nokri_SplashActivity.this));


                            } else if (Nokri_SharedPrefManager.isSocialLogin(Nokri_SplashActivity.this) && Nokri_SharedPrefManager.getEmail(Nokri_SplashActivity.this) != null) {
                                nokri_postSocialSignin(Nokri_SharedPrefManager.getEmail(Nokri_SplashActivity.this), "social");
                            } else {
                                startActivity(new Intent(Nokri_SplashActivity.this, Nokri_GuestDashboardActivity.class));
                                finish();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Nokri_LanguageSupport.onAttach(base));
    }

    private void updateViews(String languageCode) {
        Nokri_LanguageSupport.setLocale(this, languageCode);
    }

    private void nokri_postSignin(String email, final String password) {

        //Nokri_DialogManager.showAlertDialog(this);
        JsonObject params = new JsonObject();
        params.addProperty("email", email);
        params.addProperty("pass", password);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, email, password, Nokri_SplashActivity.this);
        Call<ResponseBody> myCall = restService.postLogin(params, Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject respone = new JSONObject(responseObject.body().string());
                        Log.d("response", respone.toString());
                        if (respone.getBoolean("success")) {
                            JSONObject data = respone.getJSONObject("data");
                            Nokri_SharedPrefManager.saveEmail(data.getString("user_email"), Nokri_SplashActivity.this);
                            Nokri_SharedPrefManager.savePassword(password, Nokri_SplashActivity.this);
                            Nokri_SharedPrefManager.saveId(data.getString("id"), Nokri_SplashActivity.this);
                            Nokri_SharedPrefManager.saveName(data.getString("display_name"), Nokri_SplashActivity.this);
                            Nokri_SharedPrefManager.savePhone(data.getString("phone"), Nokri_SplashActivity.this);
                            if(Nokri_SharedPrefManager.getProfileImage(Nokri_SplashActivity.this)==null)
                            Nokri_SharedPrefManager.saveProfileImage(data.getString("profile_img"), Nokri_SplashActivity.this);
                            Log.d("profile_img", data.getString("profile_img"));
                            Nokri_SharedPrefManager.saveLoginType(null, Nokri_SplashActivity.this);
                            if (data.getString("user_type").equals("0")) {
                                Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_SplashActivity.this);
                                startActivity(new Intent(Nokri_SplashActivity.this, Nokri_CandidateDashboardActivity.class));
                                finish();
                            } else if (data.getString("user_type").equals("1")) {
                                Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SplashActivity.this);
                                startActivity(new Intent(Nokri_SplashActivity.this, Nokri_EmployeerDashboardActivity.class));

                                finish();
                            }

                        } else {
                            Log.d("response", responseObject.toString() + "error");

                                Nokri_ToastManager.showShortToast(Nokri_SplashActivity.this,respone.getString("message"));
                                Nokri_SharedPrefManager.invalidate(Nokri_SplashActivity.this);
                                startActivity(new Intent(Nokri_SplashActivity.this,Nokri_MainActivity.class));

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
           /*     Nokri_DialogManager.showError();
                Nokri_DialogManager.goBack(Nokri_SplashActivity.this);*/
            }
        });
    }


    private void nokri_postSocialSignin(String email, final String type) {
        Log.d("socail", email + type);
//        Nokri_DialogManager.showAlertDialog(this);
        JsonObject params = new JsonObject();
        params.addProperty("type", type);
        params.addProperty("email", email);

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, email, email, Nokri_SplashActivity.this);
//        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall = restService.postSocialLogin(params, Nokri_RequestHeaderManager.addSocialHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject respone = new JSONObject(responseObject.body().string());
                        Log.d("socail", respone.toString());
                        if (respone.getBoolean("success")) {
                            JSONObject data = respone.getJSONObject("data");


                            Nokri_SharedPrefManager.saveEmail(data.getString("user_email"), Nokri_SplashActivity.this);
                            Nokri_SharedPrefManager.saveLoginType(type, Nokri_SplashActivity.this);
                            Nokri_SharedPrefManager.savePassword("pass", Nokri_SplashActivity.this);

                            Nokri_SharedPrefManager.saveId(data.getString("id"), Nokri_SplashActivity.this);
                            Nokri_SharedPrefManager.saveName(data.getString("display_name"), Nokri_SplashActivity.this);
                            Nokri_SharedPrefManager.savePhone(data.getString("phone"), Nokri_SplashActivity.this);
                            if(Nokri_SharedPrefManager.getProfileImage(Nokri_SplashActivity.this)==null)
                            Nokri_SharedPrefManager.saveProfileImage(data.getString("profile_img"), Nokri_SplashActivity.this);


                            // Default Sigin in type for testing

//---------------------------------------------------------------------
                            if (Nokri_SharedPrefManager.isAccountCandidate(Nokri_SplashActivity.this)) {
                                // Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_SigninActivity.this);

                                startActivity(new Intent(Nokri_SplashActivity.this, Nokri_CandidateDashboardActivity.class));
                                finish();
                            } else if (Nokri_SharedPrefManager.isAccountEmployeer(Nokri_SplashActivity.this)) {
                                //Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SigninActivity.this);
                                startActivity(new Intent(Nokri_SplashActivity.this, Nokri_EmployeerDashboardActivity.class));
                                finish();
                            } else if (Nokri_SharedPrefManager.isAccountPublic(Nokri_SplashActivity.this)) {
                                //Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SigninActivity.this);
                                startActivity(new Intent(Nokri_SplashActivity.this, Nokri_GuestDashboardActivity.class));
                                finish();
                            } else
                                Nokri_ToastManager.showShortToast(Nokri_SplashActivity.this, Nokri_SharedPrefManager.getAccountType(Nokri_SplashActivity.this));


                        //    Nokri_DialogManager.hideAlertDialog();
                        } else {
                            Log.d("socail", responseObject.toString() + "error");
                            Nokri_ToastManager.showShortToast(Nokri_SplashActivity.this,respone.getString("message"));
                            Nokri_SharedPrefManager.invalidate(Nokri_SplashActivity.this);
                            startActivity(new Intent(Nokri_SplashActivity.this,Nokri_MainActivity.class));
                        }
                    } catch (IOException e) {
                        /*Nokri_DialogManager.showCustom(e.getMessage());
                        Nokri_DialogManager.hideAfterDelay();*/
//                        Nokri_DialogManager.hideAlertDialog();
                        e.printStackTrace();
                    } catch (JSONException e) {
                      /*  Nokri_DialogManager.showCustom(e.getMessage());
                        Nokri_DialogManager.hideAfterDelay();*/
                        //  Nokri_DialogManager.hideAlertDialog();
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
              /*  Nokri_DialogManager.showCustom(t.getMessage());
                Nokri_DialogManager.goBack(Nokri_SplashActivity.this);*/
            }
        });
    }

    @Override
    public void onButtonClick(DialogInterface dialog) {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onNoClick(DialogInterface dialog) {

    }

 /*   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }*/

    private boolean nokri_checkAndRequestPermission() {

        int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int accessCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> permissionsNeeded = new ArrayList<>();
        if (readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (accessCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (accessFineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), PERMISSION_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    nokri_performAcions();
                } else {
                    finish();
                }
                break;

        }


    }
}
