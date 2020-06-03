package com.donbosco.jobservice.candidate.edit.fragments;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.donbosco.jobservice.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.donbosco.jobservice.R;
import com.donbosco.jobservice.custom.Nokri_ViewPagerAdapter;
import com.donbosco.jobservice.custom.TabLayoutNoAutoScroll;
import com.donbosco.jobservice.manager.Nokri_FontManager;
import com.donbosco.jobservice.manager.Nokri_GoogleAnalyticsManager;
import com.donbosco.jobservice.manager.Nokri_SharedPrefManager;
import com.donbosco.jobservice.utils.Nokri_Config;
import com.donbosco.jobservice.utils.Nokri_Globals;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_CandidateEditProfileFragment extends Fragment implements TabLayout.OnTabSelectedListener ,View.OnClickListener{
    private ViewPager viewPager;
    private TabLayoutNoAutoScroll tabLayout;
    private final String tabTitles[] = new String[9];
    private TextView nextStepTextView,nextStepTextViewData;
    private Nokri_FontManager fontManager;
    private View overlay;
    private TextView totalStepsTextView;
    private ImageButton nextArrow;
    private Nokri_CandidateDashboardModel candidateDashboardModel;
    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    public Nokri_CandidateEditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

         candidateDashboardModel = Nokri_SharedPrefManager.getCandidateSettings(getContext());
        if(Nokri_Globals.IS_RTL_ENABLED)
        {
            tabTitles[8] = candidateDashboardModel.getTabPersonal();
            tabTitles[7] = candidateDashboardModel.getTabResume();
            tabTitles[6] = candidateDashboardModel.getTabEducation();
            tabTitles[5] = candidateDashboardModel.getTabProfessional();
            tabTitles[4] = candidateDashboardModel.getTabCertification();
            tabTitles[3] = candidateDashboardModel.getTabSkills();
            tabTitles[2] = candidateDashboardModel.getTabPortfolio();
            tabTitles[1] = candidateDashboardModel.getTabSocial();
            tabTitles[0] = candidateDashboardModel.getTabLocation();
        }
        else {
            tabTitles[0] = candidateDashboardModel.getTabPersonal();
            tabTitles[1] = candidateDashboardModel.getTabResume();
            tabTitles[2] = candidateDashboardModel.getTabEducation();
            tabTitles[3] = candidateDashboardModel.getTabProfessional();
            tabTitles[4] = candidateDashboardModel.getTabCertification();
            tabTitles[5] = candidateDashboardModel.getTabSkills();
            tabTitles[6] = candidateDashboardModel.getTabPortfolio();
            tabTitles[7] = candidateDashboardModel.getTabSocial();
            tabTitles[8] = candidateDashboardModel.getTabLocation();
        }
        nokri_initialize();
        nokri_setupViewPager();
        nextStepTextViewData.setText("01");
        if(Nokri_Globals.IS_RTL_ENABLED){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }}
        tabLayout.setSmoothScrollingEnabled(true);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(this);
        nokri_setFonts();



    }
    private void nokri_initialize() {
        fontManager = new Nokri_FontManager();

        viewPager = getView().findViewById(R.id.viewpager);
        tabLayout = getView().findViewById(R.id.tabs);
        nextStepTextView = getView().findViewById(R.id.txt_next_step);
        nextStepTextView.setText(Nokri_Globals.NEXT_STEP);
        nextStepTextViewData = getView().findViewById(R.id.txt_next_step_data);
        overlay = getView().findViewById(R.id.ovelay);
        totalStepsTextView = getView().findViewById(R.id.txt_total_steps);

        nextArrow = getView().findViewById(R.id.txt_next_arrow);
        overlay.setOnClickListener(this);
        Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());
        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getEdit());
        getView().findViewById(R.id.bottom_container).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Nokri_Config.APP_COLOR));
    }


    private void nokri_setupViewPager(){
        Nokri_ViewPagerAdapter pagerAdapter = new Nokri_ViewPagerAdapter(getChildFragmentManager());


        if(Nokri_Globals.IS_RTL_ENABLED){
            pagerAdapter.addFragment(new Nokri_LocationAndAddressFragment(), candidateDashboardModel.getTabLocation());
            pagerAdapter.addFragment(new Nokri_SocialLinksFragment(), candidateDashboardModel.getTabSocial());
            pagerAdapter.addFragment(new Nokri_AddPortfolioFragment(), candidateDashboardModel.getTabPortfolio());
            pagerAdapter.addFragment(new Nokri_AddSkillsFragment(), candidateDashboardModel.getTabSkills());
            pagerAdapter.addFragment(new Nokri_CertificationsDetailsFragment(), candidateDashboardModel.getTabCertification());
            pagerAdapter.addFragment(new Nokri_ProfessionalsDetailsFragment(), candidateDashboardModel.getTabProfessional());
            pagerAdapter.addFragment(new Nokri_EducationalDetailsFragment(), candidateDashboardModel.getTabEducation());
            pagerAdapter.addFragment(new Nokri_AddResumeFragment(), candidateDashboardModel.getTabResume());
            pagerAdapter.addFragment(new Nokri_PersonalInfoFragment(), candidateDashboardModel.getTabPersonal());








        }


else {
            pagerAdapter.addFragment(new Nokri_PersonalInfoFragment(), candidateDashboardModel.getTabPersonal());
            pagerAdapter.addFragment(new Nokri_AddResumeFragment(), candidateDashboardModel.getTabResume());
            pagerAdapter.addFragment(new Nokri_EducationalDetailsFragment(), candidateDashboardModel.getTabEducation());
            pagerAdapter.addFragment(new Nokri_ProfessionalsDetailsFragment(), candidateDashboardModel.getTabProfessional());
            pagerAdapter.addFragment(new Nokri_CertificationsDetailsFragment(), candidateDashboardModel.getTabCertification());
            pagerAdapter.addFragment(new Nokri_AddSkillsFragment(), candidateDashboardModel.getTabSkills());
            pagerAdapter.addFragment(new Nokri_AddPortfolioFragment(), candidateDashboardModel.getTabPortfolio());
            pagerAdapter.addFragment(new Nokri_SocialLinksFragment(), candidateDashboardModel.getTabSocial());
            pagerAdapter.addFragment(new Nokri_LocationAndAddressFragment(), candidateDashboardModel.getTabLocation());
        }
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(9);
        totalStepsTextView.setText("/0"+viewPager.getAdapter().getCount());
    }
    private void nokri_setFonts() {


        fontManager.nokri_setMonesrratSemiBioldFont(nextStepTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(nextStepTextViewData, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(totalStepsTextView, getActivity().getAssets());
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            //noinspection ConstantConditions
            RelativeLayout relativeLayout = (RelativeLayout)
                    LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, tabLayout, false);
            TextView tv= relativeLayout.findViewById(R.id.tab_title);
            relativeLayout.findViewById(R.id.divider).setVisibility(View.GONE);
            if(i==0) {
                tv.setText(tabTitles[0]);
                tv.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
            }
            else
            if(i>0) {
                tv.setText(tabTitles[i]);

            }
            fontManager.nokri_setOpenSenseFontTextView(tv,getActivity().getAssets());
            tabLayout.getTabAt(i).setCustomView(relativeLayout);

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_candidate_edit_profile, container, false);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        View view =  tab.getCustomView();
        TextView custom = view.findViewById(R.id.tab_title);
        custom.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        int currentItem = tab.getPosition()+1;
        String text = "0"+Integer.toString(currentItem);

        nextStepTextViewData.setText(text);
        totalStepsTextView.setText("/0"+viewPager.getAdapter().getCount());
        if(viewPager.getCurrentItem()+1 == viewPager.getAdapter().getCount())
            nextArrow.setVisibility(View.INVISIBLE);
        else
            nextArrow.setVisibility(View.VISIBLE);

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        View view =  tab.getCustomView();
        TextView custom = view.findViewById(R.id.tab_title);
        custom.setTextColor(getResources().getColor(R.color.black));


    }


    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onClick(View view) {
        int currentItem = viewPager.getCurrentItem()+1;
        totalStepsTextView.setText("/0"+viewPager.getAdapter().getCount());
        String text = "0"+Integer.toString(currentItem);
        nextStepTextViewData.setText(text);
        viewPager.setCurrentItem(viewPager.getCurrentItem()+1);

        if(viewPager.getCurrentItem()+1 == viewPager.getAdapter().getCount())
            nextArrow.setVisibility(View.INVISIBLE);
        else
            nextArrow.setVisibility(View.VISIBLE);

    }
}
