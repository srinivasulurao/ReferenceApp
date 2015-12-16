// Generated code from Butter Knife. Do not modify!
package com.texastech.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ToDoActivity$$ViewInjector<T extends com.texastech.activity.ToDoActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 0, "field 'tvClear'");
    target.tvClear = finder.castView(view, 0, "field 'tvClear'");
  }

  @Override public void reset(T target) {
    target.tvClear = null;
  }
}
