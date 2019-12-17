package com.mahmuttaskiran.extended_device_info.extended_device_info;

import android.content.ContentResolver;

import androidx.annotation.NonNull;
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
   binding.getApplicationContext().getContentResolver());
 }

 private void setupMethodChannel(BinaryMessenger messenger, ContentResolver contentResolver) {
  channel = new MethodChannel(messenger, "plugins.flutter.io/device_info");
  final MethodCallHandlerImpl handler = new MethodCallHandlerImpl(contentResolver);
  channel.setMethodCallHandler(handler);
 }

  public static void registerWith(Registrar registrar) {
   ExtendedDeviceInfoPlugin plugin = new ExtendedDeviceInfoPlugin();
   plugin.setupMethodChannel(registrar.messenger(), registrar.context().getContentResolver());
  }


 private void tearDownChannel() {
  channel.setMethodCallHandler(null);
  channel = null;
 }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
   tearDownChannel();
  }
}
