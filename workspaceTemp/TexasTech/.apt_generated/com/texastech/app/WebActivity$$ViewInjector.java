// Generated code from Butter Knife. Do not modify!
package com.texastech.app;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class WebActivity$$ViewInjector<T extends com.texastech.app.WebActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 0, "field 'view'");
    target.view = finder.castView(view, 0, "field 'view'");
  }

  @Override public void reset(T target) {
    target.view = null;
  }
}
