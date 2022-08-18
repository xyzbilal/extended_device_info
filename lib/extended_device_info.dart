import 'dart:async';

import 'package:flutter/services.dart';

class DeviceInfoPlugin {
  DeviceInfoPlugin();
  static const MethodChannel channel =
      MethodChannel('plugins.flutter.io/device_info');

  AndroidDeviceInfo? _cachedAndroidDeviceInfo;

  Future<AndroidDeviceInfo>? get androidInfo async =>  
    _cachedAndroidDeviceInfo ??= AndroidDeviceInfo._fromMap(await channel.invokeMapMethod<String, dynamic>('getAndroidDeviceInfo')??{});
  
  }
      

class AndroidDeviceInfo {
  AndroidDeviceInfo._({
    this.version,
    this.board,
    this.bootloader,
    this.brand,
    this.device,
    this.display,
    this.fingerprint,
    this.hardware,
    this.host,
    this.id,
    this.manufacturer,
    this.model,
    this.product,
    List<String> supported32BitAbis=const[],
    List<String> supported64BitAbis=const[],
    List<String> supportedAbis=const[],
    this.tags,
    this.type,
    this.isPhysicalDevice=false,
    this.androidId,
    this.dns1,
    this.dns2,
    this.gateway,
    this.ipAddress,
    this.serverIp,
    this.subnet,
    this.uniquePsuedoId,
  })  : supported32BitAbis = List<String>.unmodifiable(supported32BitAbis),
        supported64BitAbis = List<String>.unmodifiable(supported64BitAbis),
        supportedAbis = List<String>.unmodifiable(supportedAbis);

  final String? uniquePsuedoId;
  final String? dns1;
  final String? dns2;
  final String? gateway;
  final String? ipAddress;
  final String? subnet;
  final String? serverIp;

  /// Android operating system version values derived from `android.os.Build.VERSION`.
  final AndroidBuildVersion? version;

  /// The name of the underlying board, like "goldfish".
  final String? board;

  /// The system bootloader version number.
  final String? bootloader;

  /// The consumer-visible brand with which the product/hardware will be associated, if any.
  final String? brand;

  /// The name of the industrial design.
  final String? device;

  /// A build ID string meant for displaying to the user.
  final String? display;

  /// A string that uniquely identifies this build.
  final String? fingerprint;

  /// The name of the hardware (from the kernel command line or /proc).
  final String? hardware;

  /// Hostname.
  final String? host;

  /// Either a changelist number, or a label like "M4-rc20".
  final String? id;

  /// The manufacturer of the product/hardware.
  final String? manufacturer;

  /// The end-user-visible name for the end product.
  final String? model;

  /// The name of the overall product.
  final String? product;

  /// An ordered list of 32 bit ABIs supported by this device.
  final List<String> supported32BitAbis;

  /// An ordered list of 64 bit ABIs supported by this device.
  final List<String> supported64BitAbis;

  /// An ordered list of ABIs supported by this device.
  final List<String> supportedAbis;

  /// Comma-separated tags describing the build, like "unsigned,debug".
  final String? tags;

  /// The type of build, like "user" or "eng".
  final String? type;

  /// `false` if the application is running in an emulator, `true` otherwise.
  final bool isPhysicalDevice;

  /// The Android hardware device ID that is unique between the device + user and app signing.
  final String? androidId;

  /// Deserializes from the message received from [_kChannel].
  static AndroidDeviceInfo _fromMap(Map<String, dynamic> map) {
    return AndroidDeviceInfo._(
      version:
          AndroidBuildVersion._fromMap(map['version']?.cast<String, dynamic>()),
      board: map['board'],
      bootloader: map['bootloader'],
      brand: map['brand'],
      device: map['device'],
      display: map['display'],
      fingerprint: map['fingerprint'],
      hardware: map['hardware'],
      host: map['host'],
      id: map['id'],
      manufacturer: map['manufacturer'],
      model: map['model'],
      product: map['product'],
      supported32BitAbis: _fromList(map['supported32BitAbis']),
      supported64BitAbis: _fromList(map['supported64BitAbis']),
      supportedAbis: _fromList(map['supportedAbis']),
      tags: map['tags'],
      type: map['type'],
      isPhysicalDevice: map['isPhysicalDevice'],
      androidId: map['androidId'],
      dns1: map['dns1'],
      dns2: map['dns2'],
      gateway: map['gateway'],
      ipAddress: map['ipAddress'],
      subnet: map['subnet'],
      serverIp: map['serverIp'],
      uniquePsuedoId: map['uniquePsuedoId'],
    );
  }

  @override
  String toString() {
    return 'AndroidInfo(dns1: $dns1, dns2: $dns2, gateway: $gateway, ' +
        'uniquePsuedoId: $uniquePsuedoId, subnet: $subnet,)';
  }

  /// Deserializes message as List<String>
  static List<String> _fromList(dynamic message) {
    final List<dynamic> list = message;
    return List<String>.from(list);
  }
}

class AndroidBuildVersion {
  AndroidBuildVersion._({
    this.baseOS,
    this.codename,
    this.incremental,
    this.previewSdkInt,
    this.release,
    this.sdkInt,
    this.securityPatch,
  });

  /// The base OS build the product is based on.
  final String? baseOS;

  /// The current development codename, or the string "REL" if this is a release build.
  final String ?codename;

  /// The internal value used by the underlying source control to represent this build.
  final String? incremental;

  /// The developer preview revision of a prerelease SDK.
  final int? previewSdkInt;

  /// The user-visible version string.
  final String? release;

  /// The user-visible SDK version of the framework.
  ///
  /// Possible values are defined in: https://developer.android.com/reference/android/os/Build.VERSION_CODES.html
  final int? sdkInt;

  /// The user-visible security patch level.
  final String? securityPatch;

  /// Deserializes from the map message received from [_kChannel].
  static AndroidBuildVersion _fromMap(Map<String, dynamic> map) {
    return AndroidBuildVersion._(
      baseOS: map['baseOS'],
      codename: map['codename'],
      incremental: map['incremental'],
      previewSdkInt: map['previewSdkInt'],
      release: map['release'],
      sdkInt: map['sdkInt'],
      securityPatch: map['securityPatch'],
    );
  }
}
