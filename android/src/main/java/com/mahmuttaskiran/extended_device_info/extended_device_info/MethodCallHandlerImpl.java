// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.mahmuttaskiran.extended_device_info.extended_device_info;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * The implementation of {@link MethodChannel.MethodCallHandler} for the plugin. Responsible for
 * receiving method calls from method channel.
 */
class MethodCallHandlerImpl implements MethodChannel.MethodCallHandler {

  private ContentResolver contentResolver;
  private Context context;

  /** Substitute for missing values. */
  private static final String[] EMPTY_STRING_LIST = new String[] {};

  /** Constructs DeviceInfo. The {@code contentResolver} must not be null. */
  MethodCallHandlerImpl(ContentResolver contentResolver, Context context) {
    this.contentResolver = contentResolver;
    this.context = context;
  }

  @Override
  public void onMethodCall(MethodCall call, MethodChannel.Result result) {
    if (call.method.equals("getAndroidDeviceInfo")) {
      Map<String, Object> build = new HashMap<>();
      putDhcInfoTo(getDhcInfo(), build);
      build.put("uniquePsuedoId", getUniquePsuedoID());
      build.put("board", Build.BOARD);
      build.put("bootloader", Build.BOOTLOADER);
      build.put("brand", Build.BRAND);
      build.put("device", Build.DEVICE);
      build.put("display", Build.DISPLAY);
      build.put("fingerprint", Build.FINGERPRINT);
      build.put("hardware", Build.HARDWARE);
      build.put("host", Build.HOST);
      build.put("id", Build.ID);
      build.put("manufacturer", Build.MANUFACTURER);
      build.put("model", Build.MODEL);
      build.put("product", Build.PRODUCT);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        build.put("supported32BitAbis", Arrays.asList(Build.SUPPORTED_32_BIT_ABIS));
        build.put("supported64BitAbis", Arrays.asList(Build.SUPPORTED_64_BIT_ABIS));
        build.put("supportedAbis", Arrays.asList(Build.SUPPORTED_ABIS));
      } else {
        build.put("supported32BitAbis", Arrays.asList(EMPTY_STRING_LIST));
        build.put("supported64BitAbis", Arrays.asList(EMPTY_STRING_LIST));
        build.put("supportedAbis", Arrays.asList(EMPTY_STRING_LIST));
      }
      build.put("tags", Build.TAGS);
      build.put("type", Build.TYPE);
      build.put("isPhysicalDevice", !isEmulator());
      build.put("androidId", getAndroidId());

      Map<String, Object> version = new HashMap<>();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        version.put("baseOS", Build.VERSION.BASE_OS);
        version.put("previewSdkInt", Build.VERSION.PREVIEW_SDK_INT);
        version.put("securityPatch", Build.VERSION.SECURITY_PATCH);
      }
      version.put("codename", Build.VERSION.CODENAME);
      version.put("incremental", Build.VERSION.INCREMENTAL);
      version.put("release", Build.VERSION.RELEASE);
      version.put("sdkInt", Build.VERSION.SDK_INT);
      build.put("version", version);

      result.success(build);
    } else {
      result.notImplemented();
    }
  }


 private static String getUniquePsuedoID() {
  // If all else fails, if the user does have lower than API 9 (lower
  // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
  // returns 'null', then simply the ID returned will be solely based
  // off their Android device information. This is where the collisions
  // can happen.
  // Thanks http://www.pocketmagic.net/?p=1662!
  // Try not to use DISPLAY, HOST or ID - these items could change.
  // If there are collisions, there will be overlapping data
  String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

  // Thanks to @Roman SL!
  // https://stackoverflow.com/a/4789483/950427
  // Only devices with API >= 9 have android.os.Build.SERIAL
  // http://developer.android.com/reference/android/os/Build.html#SERIAL
  // If a user upgrades software or roots their device, there will be a duplicate entry
  String serial = null;
  try {
   serial = android.os.Build.class.getField("SERIAL").get(null).toString();

   // Go ahead and return the serial for api => 9
   return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
  } catch (Exception exception) {
   // String needs to be initialized
   serial = "serial"; // some value
  }

  // Thanks @Joe!
  // https://stackoverflow.com/a/2853253/950427
  // Finally, combine the values we have found by using the UUID class to create a unique identifier
  return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
 }

  /**
   * Returns the Android hardware device ID that is unique between the device + user and app
   * signing. This key will change if the app is uninstalled or its data is cleared. Device factory
   * reset will also result in a value change.
   *
   * @return The android ID
   */
  @SuppressLint("HardwareIds")
  private String getAndroidId() {
    return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
  }

 private void putDhcInfoTo(DhcpInfo info, Map<String, Object> map) {
  if (info != null) {
   map.put("dns1", intToIp(info.dns1));
   map.put("dns2", intToIp(info.dns2));
   map.put("gateway", intToIp(info.gateway));
   map.put("ipAddress", intToIp(info.ipAddress));
   map.put("subnet", intToIp(info.gateway));
   map.put("serverIp", intToIp(info.serverAddress));
  }
 }

 private DhcpInfo getDhcInfo() {
  WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
  return wifiManager.getDhcpInfo();
 }

  /**
   * A simple emulator-detection based on the flutter tools detection logic and a couple of legacy
   * detection systems
   */
  private boolean isEmulator() {
    return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        || Build.FINGERPRINT.startsWith("generic")
        || Build.FINGERPRINT.startsWith("unknown")
        || Build.HARDWARE.contains("goldfish")
        || Build.HARDWARE.contains("ranchu")
        || Build.MODEL.contains("google_sdk")
        || Build.MODEL.contains("Emulator")
        || Build.MODEL.contains("Android SDK built for x86")
        || Build.MANUFACTURER.contains("Genymotion")
        || Build.PRODUCT.contains("sdk_google")
        || Build.PRODUCT.contains("google_sdk")
        || Build.PRODUCT.contains("sdk")
        || Build.PRODUCT.contains("sdk_x86")
        || Build.PRODUCT.contains("vbox86p")
        || Build.PRODUCT.contains("emulator")
        || Build.PRODUCT.contains("simulator");
  }

 @SuppressLint("DefaultLocale")
 public String intToIp(int ip) {
  return String.format("%d.%d.%d.%d",
   (ip & 0xff),
   (ip >> 8 & 0xff),
   (ip >> 16 & 0xff),
   (ip >> 24 & 0xff));
 }
}