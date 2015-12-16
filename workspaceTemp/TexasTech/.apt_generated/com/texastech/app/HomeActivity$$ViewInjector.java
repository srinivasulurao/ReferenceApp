// Generated code from Butter Knife. Do not modify!
package com.texastech.app;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class HomeActivity$$ViewInjector<T extends com.texastech.app.HomeActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 0, "field 'mPane'");
    target.mPane = finder.castView(view, 0, "field 'mPane'");
  }

  @Override public void reset(T target) {
    target.mPane = null;
  }
}
