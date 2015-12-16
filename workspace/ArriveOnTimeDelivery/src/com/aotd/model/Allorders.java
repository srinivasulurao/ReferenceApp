package com.aotd.model;

import java.util.ArrayList;

public class Allorders {
	
	public static ArrayList<DispatchAllListModel> presentOrders;
	public static ArrayList<DispatchAllListModel> pastOrders;
	public static ArrayList<DispatchListModel> dispatchOrders;
	public static ArrayList<OfflineDataModel> dispatchOrders_;

	public static ArrayList<OfflineDataModel> getDispatchOrders_() {
		return dispatchOrders_;
	}

	public static void setDispatchOrders_(
			ArrayList<OfflineDataModel> dispatchOrders_) {
		Allorders.dispatchOrders_ = dispatchOrders_;
	}

	public static ArrayList<DispatchListModel> getDispatchOrders() {
		return dispatchOrders;
	}

	public static void setDispatchOrders(ArrayList<DispatchListModel> dispatchOrders) {
		Allorders.dispatchOrders = dispatchOrders;
	}

	public static ArrayList<DispatchAllListModel> getPastOrders() {
		return pastOrders;
	}

	public static void setPastOrders(ArrayList<DispatchAllListModel> pastOrders) {
		Allorders.pastOrders = pastOrders;
	}

	public static ArrayList<DispatchAllListModel> getPresentOrders() {
		return presentOrders;
	}

	public static void setPresentOrders(
			ArrayList<DispatchAllListModel> presentOrders) {
		Allorders.presentOrders = presentOrders;
	}

}
