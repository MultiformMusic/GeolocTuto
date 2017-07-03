/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.stetho.common.android;

import android.content.res.Resources;
import android.view.View;



public interface FragmentAccessor<FRAGMENT, FRAGMENT_MANAGER> {
  int NO_ID = 0;


  FRAGMENT_MANAGER getFragmentManager(FRAGMENT fragment);

  Resources getResources(FRAGMENT fragment);

  int getId(FRAGMENT fragment);


  String getTag(FRAGMENT fragment);


  View getView(FRAGMENT fragment);


  FRAGMENT_MANAGER getChildFragmentManager(FRAGMENT fragment);
}
