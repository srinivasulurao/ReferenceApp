package com.youflik.youflik.models;

import java.util.ArrayList;

public class SettingsNotificationsModel {

	String  mSettingsId, mCreatedBy, mDisableEmailNoti,
			mWhoCanAddThingsToTimeLine, mWhoCanSendMeAMessage,
			mOnlySecurityEmailNoti, mOnlyMessageEmailNoti,
			mOnlyFriendRequestEmailNoti, mPostOnMyWallEmailNoti,
			mPostShareEmailNoti, mRequestApprovalEmailNoti,
			mYouflikUpdatesEmailNoti, mWeeklyActivityLogEmailNoti,
			mPromotionsOffersEmailNoti;
	
	public String getSettingsId() {
		return mSettingsId;
	}

	public void setSettingsId(String mSettingsId) {
		this.mSettingsId = mSettingsId;
	}

	ArrayList<SettingsNotificationsItemModel> mSettingsNotificationsItems;

	public String getDisableEmailNoti() {
		return mDisableEmailNoti;
	}

	public void setDisableEmailNoti(String mDisableEmailNoti) {
		this.mDisableEmailNoti = mDisableEmailNoti;
	}

	public String getWhoCanAddThingsToTimeLine() {
		return mWhoCanAddThingsToTimeLine;
	}

	public void setWhoCanAddThingsToTimeLine(String mWhoCanAddThingsToTimeLine) {
		this.mWhoCanAddThingsToTimeLine = mWhoCanAddThingsToTimeLine;
	}

	public String getWhoCanSendMeAMessage() {
		return mWhoCanSendMeAMessage;
	}

	public void setWhoCanSendMeAMessage(String mWhoCanSendMeAMessage) {
		this.mWhoCanSendMeAMessage = mWhoCanSendMeAMessage;
	}

	public String getOnlySecurityEmailNoti() {
		return mOnlySecurityEmailNoti;
	}

	public void setOnlySecurityEmailNoti(String mOnlySecurityEmailNoti) {
		this.mOnlySecurityEmailNoti = mOnlySecurityEmailNoti;
	}

	public String getOnlyMessageEmailNoti() {
		return mOnlyMessageEmailNoti;
	}

	public void setOnlyMessageEmailNoti(String mOnlyMessageEmailNoti) {
		this.mOnlyMessageEmailNoti = mOnlyMessageEmailNoti;
	}

	public String getOnlyFriendRequestEmailNoti() {
		return mOnlyFriendRequestEmailNoti;
	}

	public void setOnlyFriendRequestEmailNoti(String mOnlyFriendRequestEmailNoti) {
		this.mOnlyFriendRequestEmailNoti = mOnlyFriendRequestEmailNoti;
	}

	public String getPostOnMyWallEmailNoti() {
		return mPostOnMyWallEmailNoti;
	}

	public void setPostOnMyWallEmailNoti(String mPostOnMyWallEmailNoti) {
		this.mPostOnMyWallEmailNoti = mPostOnMyWallEmailNoti;
	}

	public String getPostShareEmailNoti() {
		return mPostShareEmailNoti;
	}

	public void setPostShareEmailNoti(String mPostShareEmailNoti) {
		this.mPostShareEmailNoti = mPostShareEmailNoti;
	}

	public String getRequestApprovalEmailNoti() {
		return mRequestApprovalEmailNoti;
	}

	public void setRequestApprovalEmailNoti(String mRequestApprovalEmailNoti) {
		this.mRequestApprovalEmailNoti = mRequestApprovalEmailNoti;
	}

	public String getYouflikUpdatesEmailNoti() {
		return mYouflikUpdatesEmailNoti;
	}

	public void setYouflikUpdatesEmailNoti(String mYouflikUpdatesEmailNoti) {
		this.mYouflikUpdatesEmailNoti = mYouflikUpdatesEmailNoti;
	}

	public String getWeeklyActivityLogEmailNoti() {
		return mWeeklyActivityLogEmailNoti;
	}

	public void setWeeklyActivityLogEmailNoti(String mWeeklyActivityLogEmailNoti) {
		this.mWeeklyActivityLogEmailNoti = mWeeklyActivityLogEmailNoti;
	}

	public String getPromotionsOffersEmailNoti() {
		return mPromotionsOffersEmailNoti;
	}

	public void setPromotionsOffersEmailNoti(String mPromotionsOffersEmailNoti) {
		this.mPromotionsOffersEmailNoti = mPromotionsOffersEmailNoti;
	}

	public void setSettingsNotificationsItems(
			ArrayList<SettingsNotificationsItemModel> items) {
		this.mSettingsNotificationsItems = items;
	}

	public ArrayList<SettingsNotificationsItemModel> getSettingsNotificationsItems() {
		return this.mSettingsNotificationsItems;
	}

	public String getCreatedBy() {
		return mCreatedBy;
	}

	public void setCreatedBy(String mCreatedBy) {
		this.mCreatedBy = mCreatedBy;
	}

}
