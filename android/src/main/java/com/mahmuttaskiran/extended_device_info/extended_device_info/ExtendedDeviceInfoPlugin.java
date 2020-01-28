package com.mahmuttaskiran.extended_device_info.extended_device_info;

import android.content.ContentResolver;
import android.content.Context;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** ExtendedDeviceInfoPlugin */
public class ExtendedDeviceInfoPlugin implements FlutterPlugin {
 MethodChannel channel;
 @Override
 public void onAttachedToEngine(FlutterPlugin.FlutterPluginBinding binding) {
  setupMethodChannel(
   binding.getFlutterEngine().getDartExecutor(),
   binding.getApplicationContext());
 }

 private void setupMethodChannel(BinaryMessenger messenger, Context context) {
  ContentResolver contentResolver = context.getContentResolver();
  channel = new MethodChannel(messenger, "plugins.flutter.io/device_info");
  final MethodCallHandlerImpl handler = new MethodCallHandlerImpl(contentResolver, context);
  channel.setMethodCallHandler(handler);
 }

  public static void registerWith(Registrar registrar) {
   ExtendedDeviceInfoPlugin plugin = new ExtendedDeviceInfoPlugin();
   plugin.setupMethodChannel(registrar.messenger(), registrar.context());
  }


 private void tearDownChannel() {
  channel.setMethodCallHandler(null);
  channel = null;
 }


  @Override
  public void onDetachedFromEngine(FlutterPluginBinding binding) {
   tearDownChannel();
  }
}
