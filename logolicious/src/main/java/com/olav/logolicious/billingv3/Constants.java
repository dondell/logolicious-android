/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.olav.logolicious.billingv3;

public class Constants {
    // Use the fake local server data or real remote server.
    public static boolean USE_FAKE_SERVER = false;

    public static final String BASIC_SKU = "basic_subscription";
    public static final String COM_OLAV_LOGOLICIOUS_SUBSCRIPTION = "com.olav.logolicious.subscription";
    public static final String ADDYOURLOGOAPP_2022 = "addyourlogoapp2022";
    public static final String PLAY_STORE_SUBSCRIPTION_URL
            = "https://play.google.com/store/account/subscriptions";
    public static final String PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL
            = "https://play.google.com/store/account/subscriptions?sku=%s&package=%s";
    public static final String KEY_PURCHASE_CODE = "KEY_PURCHASE_CODE";
}
