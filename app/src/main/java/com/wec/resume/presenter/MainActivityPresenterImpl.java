package com.wec.resume.presenter;


import android.os.Bundle;
import android.support.annotation.Nullable;

import com.wec.resume.model.Bio;
import com.wec.resume.model.Social;
import com.wec.resume.model.Social.Type;
import com.wec.resume.model.usecase.FetchBioUsecase;
import com.wec.resume.model.usecase.UpdateResumeUsecase;
import com.wec.resume.view.MainActivityView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.webkit.URLUtil.isValidUrl;

public class MainActivityPresenterImpl extends AbstractPresenter<MainActivityView> implements MainActivityPresenter {

    private static final String TITLE_PLACEHOLDER = "%s %s";
    private final UpdateResumeUsecase updateResumeUsecase;
    private final FetchBioUsecase fetchBioUsecase;
    private Disposable updateResumeDisposable;
    private Disposable fetchBioDisposable;
    private boolean socialButtonSelected = false;
    private Bio bio;

    public MainActivityPresenterImpl(FetchBioUsecase fetchBioUsecase, UpdateResumeUsecase updateResumeUsecase) {
        this.fetchBioUsecase = fetchBioUsecase;
        this.updateResumeUsecase = updateResumeUsecase;
    }

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);

        if (isViewBounded()) {
            getView().showSplashScreen();
        }

        updateResume();
    }

    private void updateResume() {
        if (updateResumeDisposable != null && !updateResumeDisposable.isDisposed()) {
            updateResumeDisposable.dispose();
        }

        updateResumeDisposable = updateResumeUsecase
                .execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resume -> {
                    if (isViewBounded()) {
                        getView().hideSplashScreen();
                    }
                    handleFetchBio();
                }, throwable -> {
                    if (isViewBounded()) {
                        getView().showCouldNoteLoadDataErrorMessage();
                    }
                });
    }

    private void handleFetchBio() {
        if (fetchBioDisposable != null && !fetchBioDisposable.isDisposed()) {
            fetchBioDisposable.dispose();
        }

        fetchBioDisposable = fetchBioUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bio -> {
                    setBio(bio);
                    if (isViewBounded()) {
                        final MainActivityView view = getView();
                        view.setTitle(String.format(TITLE_PLACEHOLDER, bio.getName(), bio.getSurname()));
                        final String avatar = bio.getAvatar();

                        if (isValidUrl(avatar)) {
                            view.setAvatar(avatar);
                        }

                        final Social[] socials = bio.getSocials();

                        if (socials != null && socials.length > 0) {
                            view.showAndEnableSocialButtons();
                            for (final Social social : socials) {
                                view.enableButtonByType(social.getType());
                            }
                        }
                    }
                });
    }

    @Override
    public void socialsButtonClicked() {
        toggleSocialButtonState();
    }

    private void toggleSocialButtonState() {
        socialButtonSelected = !socialButtonSelected;
        if (isViewBounded()) {
            final MainActivityView view = getView();
            view.setSocialButtonToSelected(socialButtonSelected);
            for (int i = 0; i < bio.getSocials().length; i++) {
                view.animateButton(bio.getSocials()[i].getType(), i, socialButtonSelected);
            }
        }
    }

    @Override
    public void onButtonClicked(Type type) {
        if (!isViewBounded()) {
            return;
        }
        final Social[] socials = bio.getSocials();
        for (final Social social : socials) {
            if (social.getType() == type) {
                getView().navigateToURL(social.getUrl());
                return;
            }
        }
    }

    public void setBio(Bio bio) {
        this.bio = bio;
    }
}
