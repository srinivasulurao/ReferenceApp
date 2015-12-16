package foodzu.com.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import foodzu.com.R;

public class AppRater {
	private final static String APP_PNAME = "foodzu.com";

	private final static int DAYS_UNTIL_PROMPT = 7;
	private final static int LAUNCHES_UNTIL_PROMPT = 0;

	public static void app_launched(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
		if (prefs.getBoolean("dontshowagain", false)) {
			return;
		}

		SharedPreferences.Editor editor = prefs.edit();

		// Increment launch counter
		long launch_count = prefs.getLong("launch_count", 0) + 1;
		editor.putLong("launch_count", launch_count);

		// Get date of first launch
		Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong("date_firstlaunch", date_firstLaunch);
		}

		if (launch_count > 500) {
			editor.putBoolean("dontshowagain", true);
			return;
		}

		// Wait at least n days before opening
		if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
			if (System.currentTimeMillis() >= date_firstLaunch
					+ (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
				showRateDialog(mContext, editor);
			}
		}

		editor.commit();
	}

	public static void showRateDialog(final Context mContext,
			final SharedPreferences.Editor editor) {
		ImageView rate, later, nothanks;

		final Dialog dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.WHITE));
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.dialog_rater);

		rate = (ImageView) dialog.findViewById(R.id.like);
		later = (ImageView) dialog.findViewById(R.id.later);
		nothanks = (ImageView) dialog.findViewById(R.id.nothanks);

		rate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("market://details?id=" + APP_PNAME)));
				dialog.dismiss();
			}
		});
		later.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		nothanks.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (editor != null) {

					Long postpone = System.currentTimeMillis();
					editor.putLong("date_firstlaunch", postpone);
					editor.commit();
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}
}
