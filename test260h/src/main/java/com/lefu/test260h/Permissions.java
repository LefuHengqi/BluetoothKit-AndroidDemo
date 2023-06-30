package com.lefu.test260h;
//
// Created by L on 2020/4/29.
// Since: 1.1.13
// Copyright (c) 2020 lefu.cc . All rights reserved.
//


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

/**
 * @author L
 * @date 2020/4/29
 */
public class Permissions {
    /**
     * @param object     {@link FragmentActivity}
     * @param permission {@link android.Manifest.permission}
     */
    public static void request(FragmentActivity object, String[] permission, Apply<Boolean> apply) {
        if (Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
            RxPermissions permissions = new RxPermissions(object);
            request(permissions, permission, apply);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    RxPermissions permissions = new RxPermissions(object);
                    request(permissions, permission, apply);
                }
            });
        }

    }

    /**
     * @param object     {@link Fragment}
     * @param permission {@link android.Manifest.permission}
     * @return ObservableTransformer
     */
    public static void request(Fragment object, String[] permission, Apply<Boolean> apply) {
        if (Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
            RxPermissions permissions = new RxPermissions(object);
            request(permissions, permission, apply);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    RxPermissions permissions = new RxPermissions(object);
                    request(permissions, permission, apply);
                }
            });
        }
    }

    @SuppressLint("CheckResult")
    private static void request(RxPermissions permissions, String[] permission, Apply<Boolean> apply) {
        permissions.request(permission)
            .subscribe(apply::apply);
    }

    /**
     * for RxViews and {@link Observable#compose(ObservableTransformer)}
     *
     * @param object     {@link FragmentActivity}
     * @param permission {@link android.Manifest.permission}
     * @return ObservableTransformer
     */
    public static <O> ObservableTransformer<O, Boolean> compose(FragmentActivity object, String... permission) {
        RxPermissions permissions = new RxPermissions(object);
        return permissions.ensure(permission);
    }

    /**
     * for RxViews and {@link Observable#compose(ObservableTransformer)}
     *
     * @param object     {@link Fragment}
     * @param permission {@link android.Manifest.permission}
     * @return ObservableTransformer
     */
    public static <O> ObservableTransformer<O, Boolean> compose(Fragment object, String... permission) {
        RxPermissions permissions = new RxPermissions(object);
        return permissions.ensure(permission);
    }

    public interface Apply<T> {
        void apply(T t) throws Exception;
    }
}
