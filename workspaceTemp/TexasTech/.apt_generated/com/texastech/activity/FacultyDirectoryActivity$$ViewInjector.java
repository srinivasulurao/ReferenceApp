// Generated code from Butter Knife. Do not modify!
package com.texastech.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class FacultyDirectoryActivity$$ViewInjector<T extends com.texastech.activity.FacultyDirectoryActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 0, "field 'etSearch'");
    target.etSearch = finder.castView(view, 0, "field 'etSearch'");
  }

  @Override public void reset(T target) {
    target.etSearch = null;
  }
}
