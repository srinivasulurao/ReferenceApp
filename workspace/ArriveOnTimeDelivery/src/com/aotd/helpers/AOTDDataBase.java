package com.aotd.helpers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aotd.model.DetailDeliveryModel;
import com.aotd.model.DispatchAllListModel;
import com.aotd.model.DispatchListModel;
import com.aotd.model.OfflineDataModel;

public class AOTDDataBase {

	DataBaseHelper mDatabase_helper;
	SQLiteDatabase mDatabase;

	public static final String TABLE_NAME = "OfflineDispatchTable";
	public static final String COLUMN_ORDERID = "order_id";
	public static final String COLUMN_DRIVERID = "driver_id";
	public static final String COLUMN_RDDATEFORMAT = "RDDateFormat";
	public static final String COLUMN_MIN = "min";
	public static final String COLUMN_TIMEZONE = "timezone";
	// public static final String COLUMN_ORDERCOLOR = "orderColor";
	// public static final String COLUMN_ORDERSTATUS = "orderStatus";
	public static final String COLUMN_ACCOUNTNAME = "accountName";
	public static final String COLUMN_REF = "ref";
	public static final String COLUMN_PUINSTRUCTION = "PUInstruction";
	public static final String COLUMN_DLINSTRUCTION = "DLInstruction";
	public static final String COLUMN_SERVICENAME = "ServiceName";
	public static final String COLUMN_REQUESTOR = "Requestor";
	public static final String COLUMN_PIECE = "Piece";
	public static final String COLUMN_WEIGHT = "Weight";
	public static final String COLUMN_DPDATE = "DPDate";
	public static final String COLUMN_DLDATE = "DLDate";
	public static final String COLUMN_PUDATE = "PUDate";
	public static final String COLUMN_RDDATE = "RDDate";
	public static final String COLUMN_HOUR = "hour";
	public static final String COLUMN_SIGNROUNDTRIP = "SignRoundTrip";
	public static final String COLUMN_SIGNDELIVERY = "SignDelivery";
	public static final String COLUMN_ISROUNDTRIP = "isRoundTrip";
	public static final String COLUMN_PCROUNDTRIP = "PCRoundTrip";
	public static final String COLUMN_SEQPU = "seqPU";

	public static final String COLUMN_COMPANYPU = "companyPU";
	public static final String COLUMN_ADDRESSPU = "addressPU";
	public static final String COLUMN_SUITPU = "suitPU";
	public static final String COLUMN_CITYPU = "cityPU";
	public static final String COLUMN_STATEPU = "statePU";
	public static final String COLUMN_ZIPPU = "zipPU";

	public static final String COLUMN_CELLPHONEPU = "cellPhonePU";
	public static final String COLUMN_HOMEPHONEPU = "homePhonePU";
	public static final String COLUMN_SEQDL = "seqDL";

	public static final String COLUMN_COMPANYDL = "companyDL";
	public static final String COLUMN_ADDRESSDL = "addressDL";
	public static final String COLUMN_SUITDL = "suitDL";
	public static final String COLUMN_CITYDL = "cityDL";
	public static final String COLUMN_STATEDL = "stateDL";
	public static final String COLUMN_ZIPDL = "zipDL";

	public static final String COLUMN_CELLPHONEDL = "cellPhoneDL";
	public static final String COLUMN_HOMEPHONEDL = "homePhoneDL";
	public static final String COLUMN_LASTNAME = "lastname";
	public static final String COLUMN_SIGNATURE = "signature";
	public static final String COLUMN_NOTES = "notes";
	public static final String COLUMN_WAITTIME = "waittime";
	public static final String COLUMN_BOXES = "boxes";
	public static final String COLUMN_VEHICLE = "vehicle";
	public static final String COLUMN_SIGNATUREDL = "signaturedl";
	// EmailModel mEmailDataBaseModel;

	// ArrayList<EmailModel> mTotal_mails = null;
	String mResult;

	public AOTDDataBase(Context context) {
		mDatabase_helper = DataBaseHelper.getDBAdapterInstance(context);
	}

	public DetailDeliveryModel getOrderDetails(String order_id) {
		String sql = "select * from OfflineDispatchTable where order_id = '"
				+ order_id + "'";
		Cursor cursor = null;
		DetailDeliveryModel dispatch = null;
		try {
			mDatabase = mDatabase_helper.getWritableDatabase();
			cursor = mDatabase.rawQuery(sql, null);
			if (cursor != null)
				cursor.moveToFirst();

			if (cursor != null) {

				String s = cursor.getString(0);
				dispatch = new DetailDeliveryModel();
				dispatch.setDLordersID(cursor.getString(0));
				// dispatch.setDriver_id(cursor.getString(1));
				dispatch.setRDDate(cursor.getString(2));
				// dispatch.setMin(cursor.getString(3));
				// dispatch.setTimeZone(cursor.getString(4));
				/*
				 * dispatch.setOrderColor(cursor.getString(5));
				 * dispatch.setOrderStatus(cursor.getString(6));
				 */
				dispatch.setAccountName(cursor.getString(5));
				// dispatch.setRev(cursor.getString(6));
				dispatch.setPUInstruction(cursor.getString(7));
				dispatch.setDLInstruction(cursor.getString(8));
				dispatch.setServiceName(cursor.getString(9));
				dispatch.setRequestor(cursor.getString(10));
				dispatch.setPiece(cursor.getString(11));
				dispatch.setWeight(cursor.getString(12));
				// dispatch.setDPDate(cursor.getString(13));
				// dispatch.setDLDate(cursor.getString(14));
				// dispatch.setPUDate(cursor.getString(15));
				// dispatch.setRDDate(cursor.getString(16));
				// dispatch.setHour(cursor.getString(17));
				// dispatch.setSignRoundTrip(cursor.getString(18));
				// dispatch.setSignDelivery(cursor.getString(19));
				dispatch.setRoundTrip(cursor.getString(20));
				// dispatch.setPcRoundtrip(cursor.getString(21));

				dispatch.setPUseq(cursor.getString(22));
				dispatch.setPUcompany(cursor.getString(23));
				dispatch.setPUaddress(cursor.getString(24));
				dispatch.setPUsuit(cursor.getString(25));
				dispatch.setPUcity(cursor.getString(26));
				dispatch.setPUstate(cursor.getString(27));
				dispatch.setPUzip(cursor.getString(28));
				dispatch.setPUcellPhone(cursor.getString(29));
				dispatch.setPUhomephone(cursor.getString(30));

				dispatch.setDLseq(cursor.getString(31));
				dispatch.setDLcompany(cursor.getString(31));
				dispatch.setDLaddress(cursor.getString(33));
				dispatch.setDLsuit(cursor.getString(34));
				dispatch.setDLcity(cursor.getString(35));
				dispatch.setDLstate(cursor.getString(36));
				dispatch.setDLzip(cursor.getString(37));
				dispatch.setDLcellPhone(cursor.getString(38));
				dispatch.setDLhomephone(cursor.getString(39));

				// dispatch.setLastname(cursor.getString(40));
				// dispatch.setBytes(cursor.getBlob(41));
				// dispatch.setNotes(cursor.getString(42));
				// dispatch.setWaittime(cursor.getString(43));
				// dispatch.setBoxes(cursor.getString(44));
				// dispatch.setVehicle(cursor.getString(45));

			}
			// cursor.close();

		} finally {
			if (cursor != null) {

				cursor.close();
			}
		}

		return dispatch;

	}

	public void updatePickup(String orderIds, String PUDate) {

		try {
			String sql = "UPDATE OfflineDispatchTable SET PUDate = '" + PUDate
					+ "', action = 'update' WHERE order_id = " + orderIds + "";
			Log.e("", sql);
			mDatabase = mDatabase_helper.getWritableDatabase();
			mDatabase.execSQL(sql);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			// if (mDatabase != null)
			// mDatabase.close();
		}

	}

	public ArrayList<DispatchListModel> retrieveOfflineDispathes(
			String driver_id) {
		ArrayList<DispatchListModel> dispatchList = new ArrayList<DispatchListModel>();
		Cursor cursor = null;
		try {

			mDatabase = mDatabase_helper.getWritableDatabase();
			cursor = mDatabase
					.rawQuery(
							"SELECT * FROM OfflineDispatchTable WHERE DPDate='0000-00-00 00:00:00' AND driver_id = '"
									+ driver_id + "'", null);

			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {

					DispatchListModel result = new DispatchListModel();
					result.setOrder_id(cursor.getString(cursor
							.getColumnIndex("order_id")));
					result.setRDDate(cursor.getString(cursor
							.getColumnIndex("RDDate")));
					dispatchList.add(result);

				} while (cursor.moveToNext());
			}
			// cursor.close();

		} finally {

			if (cursor != null) {

				cursor.close();
			}
		}

		return dispatchList;
	}

	public ArrayList<DispatchAllListModel> getOfflineOrders(String today,
			String forDate) {
		String sql = "";
		if (forDate.equalsIgnoreCase("present")) {

			sql = "select * from OfflineDispatchTable where DPDate !='0000-00-00 00:00:00' and (RDDate >= '"
					+ today
					+ " 00:00:00' and RDDate <= '"
					+ today
					+ " 23:59:59')";

		} else if (forDate.equalsIgnoreCase("past")) {

			sql = "select * from OfflineDispatchTable where DPDate !='0000-00-00 00:00:00' and (RDDate < '"
					+ today + " 00:00:00')";

		} else {

			sql = "select * from OfflineDispatchTable where DPDate !='0000-00-00 00:00:00' and (RDDate > '"
					+ today + " 23:59:59')";

		}
		ArrayList<DispatchAllListModel> dispatchList = new ArrayList<DispatchAllListModel>();
		Cursor cursor = null;
		try {

			mDatabase = mDatabase_helper.getWritableDatabase();
			cursor = mDatabase.rawQuery(sql, null);

			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {

					DispatchAllListModel result = new DispatchAllListModel();
					result.setOrder_id(cursor.getString(cursor
							.getColumnIndex("order_id")));
					result.setRDDate(cursor.getString(cursor
							.getColumnIndex("RDDate")));
					result.setMin(cursor.getString(cursor.getColumnIndex("min")));
					result.setTimezone(cursor.getString(cursor
							.getColumnIndex("timezone")));
					result.setAccountName(cursor.getString(cursor
							.getColumnIndex("accountName")));

					result.setCompany(cursor.getString(cursor
							.getColumnIndex("companyPU")));
					result.setAddress(cursor.getString(cursor
							.getColumnIndex("addressDL")));
					result.setCity(cursor.getString(cursor
							.getColumnIndex("cityPU")));
					result.setState(cursor.getString(cursor
							.getColumnIndex("statePU")));
					result.setZip(cursor.getString(cursor
							.getColumnIndex("zipPU")));

					result.setDlcompany(cursor.getString(cursor
							.getColumnIndex("companyDL")));
					result.setDladdress(cursor.getString(cursor
							.getColumnIndex("addressPU")));
					result.setDlcity(cursor.getString(cursor
							.getColumnIndex("cityDL")));
					result.setDlstate(cursor.getString(cursor
							.getColumnIndex("stateDL")));
					result.setDlzip(cursor.getString(cursor
							.getColumnIndex("zipDL")));

					result.setDLDate(cursor.getString(cursor
							.getColumnIndex("DLDate")));
					result.setPUDate(cursor.getString(cursor
							.getColumnIndex("PUDate")));
					result.setHour(cursor.getString(cursor
							.getColumnIndex("hour")));
					result.setSignRoundTrip(cursor.getString(cursor
							.getColumnIndex("SignRoundTrip")));
					result.setSignDelivery(cursor.getString(cursor
							.getColumnIndex("SignDelivery")));
					result.setIsRoundTrip(cursor.getString(cursor
							.getColumnIndex("isRoundTrip")));
					result.setPCRoundTrip(cursor.getString(cursor
							.getColumnIndex("PCRoundTrip")));
					result.setDPDate(cursor.getString(cursor
							.getColumnIndex("DPDate")));
					dispatchList.add(result);

				} while (cursor.moveToNext());
			}
			// cursor.close();

		} finally {
			if (cursor != null) {

				cursor.close();
			}
		}

		return dispatchList;
	}

	public void updateDispatchers(String orderIds, String DPDate,
			String action, String driverId) {

		try {
			String sql = "UPDATE OfflineDispatchTable SET DPDate = '" + DPDate
					+ "', action = 'update' WHERE order_id in (" + orderIds
					+ ") and driver_id = '" + driverId + "'";
			Log.e("", sql);
			mDatabase = mDatabase_helper.getWritableDatabase();
			mDatabase.execSQL(sql);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			// if (mDatabase != null)
			// mDatabase.close();
		}
	}

	public ArrayList<OfflineDataModel> getSyncabelData() {
		ArrayList<OfflineDataModel> dispatchList = new ArrayList<OfflineDataModel>();
		Cursor cursor = null;
		try {

			mDatabase = mDatabase_helper.getWritableDatabase();
			cursor = mDatabase
					.rawQuery(
							"SELECT * FROM OfflineDispatchTable WHERE action = 'update'",
							null);

			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {

					OfflineDataModel result = new OfflineDataModel();

					result.setOrder_id(cursor.getString(cursor
							.getColumnIndex("order_id")));
					result.setRDDate(cursor.getString(cursor
							.getColumnIndex("RDDate")));
					result.setDPDate(cursor.getString(cursor
							.getColumnIndex("DPDate")));
					result.setPUDate(cursor.getString(cursor
							.getColumnIndex("PUDate")));

					result.setDLDate(cursor.getString(cursor
							.getColumnIndex("DLDate")));
					result.setIsRoundtrip(cursor.getString(cursor
							.getColumnIndex("isRoundTrip")));
					result.setWaittime(cursor.getString(cursor
							.getColumnIndex("waittime")));
					result.setVehicle(cursor.getString(cursor
							.getColumnIndex("vehicle")));
					result.setBoxes(cursor.getString(cursor
							.getColumnIndex("boxes")));

					result.setNotes(cursor.getString(cursor
							.getColumnIndex("notes")));
					result.setLastname(cursor.getString(cursor
							.getColumnIndex("lastname")));

					result.setLastnamedl(cursor.getString(cursor
							.getColumnIndex("lastnamedl")));
					result.setSignDelivery(cursor.getString(cursor
							.getColumnIndex("SignDelivery")));
					result.setSignature(cursor.getBlob(cursor
							.getColumnIndex("signature")));
					result.setSignaturedl(cursor.getBlob(cursor
							.getColumnIndex("signaturedl")));

					result.setPcRoundtrip(cursor.getString(cursor
							.getColumnIndex("PCRoundTrip")));
					result.setSignRoundTrip(cursor.getString(cursor
							.getColumnIndex("SignRoundTrip")));

					dispatchList.add(result);

				} while (cursor.moveToNext());
			}
			// cursor.close();

		} finally {
			if (cursor != null) {

				cursor.close();
			}
		}

		return dispatchList;
	}

	public void updatedDone(String orderIds) {

		try {
			String sql = "UPDATE OfflineDispatchTable SET action = 'none' WHERE order_id in ("
					+ orderIds + ")";
			Log.e("", sql);
			mDatabase = mDatabase_helper.getWritableDatabase();
			mDatabase.execSQL(sql);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			// if (mDatabase != null)
			// mDatabase.close();
		}
	}

	public void addValues(OfflineDataModel dispatch) {
		SQLiteDatabase db = mDatabase_helper.getWritableDatabase();

		ContentValues values = new ContentValues();
		// Log.i("Order id Logging",dispatch.getOrder_id());
		// if(dispatch.getOrder_id() != null)
		values.put(COLUMN_ORDERID, dispatch.getOrder_id());
		// if(dispatch.getDriver_id() != null)
		values.put(COLUMN_DRIVERID, dispatch.getDriver_id());
		// if(dispatch.getRDDateFormat() != null)
		values.put(COLUMN_RDDATEFORMAT, dispatch.getRDDateFormat());
		// if(dispatch.getMin() != null)
		values.put(COLUMN_MIN, dispatch.getMin());
		// if(dispatch.getTimeZone() != null)
		values.put(COLUMN_TIMEZONE, dispatch.getTimeZone());
		// if(dispatch.getOrderColor() != null)
		// values.put(COLUMN_ORDERCOLOR, dispatch.getOrderColor());
		// if(dispatch.getOrderStatus() != null)
		// values.put(COLUMN_ORDERSTATUS, dispatch.getOrderStatus());
		// if(dispatch.getAccountName() != null)
		values.put(COLUMN_ACCOUNTNAME, dispatch.getAccountName());

		values.put(COLUMN_REF, dispatch.getRev());
		values.put(COLUMN_PUINSTRUCTION, dispatch.getPUInstruction());
		values.put(COLUMN_DLINSTRUCTION, dispatch.getDLInstruction());
		values.put(COLUMN_SERVICENAME, dispatch.getServiceName());
		values.put(COLUMN_REQUESTOR, dispatch.getRequestor());
		values.put(COLUMN_PIECE, dispatch.getPiece());
		values.put(COLUMN_WEIGHT, dispatch.getWeight());
		// if(dispatch.getDPDate() != null;
		values.put(COLUMN_DPDATE, dispatch.getDPDate());
		// if(dispatch.getDLDate() != null)
		values.put(COLUMN_DLDATE, dispatch.getDLDate());
		// if(dispatch.getPUDate() != null)
		values.put(COLUMN_PUDATE, dispatch.getPUDate());
		// if(dispatch.getRDDate() != null)
		values.put(COLUMN_RDDATE, dispatch.getRDDate());
		// if(dispatch.getHour() != null)
		values.put(COLUMN_HOUR, dispatch.getHour());
		// if(dispatch.getSignRoundTrip() != null)
		values.put(COLUMN_SIGNROUNDTRIP, dispatch.getSignRoundTrip());
		// if(dispatch.getSignDelivery() != null)
		values.put(COLUMN_SIGNDELIVERY, dispatch.getSignDelivery());
		// if(dispatch.getIsRoundtrip() != null)
		values.put(COLUMN_ISROUNDTRIP, dispatch.getIsRoundtrip());
		// if(dispatch.getPcRoundtrip() != null)
		values.put(COLUMN_PCROUNDTRIP, dispatch.getPcRoundtrip());
		// if(dispatch.getseqPU() != null)
		values.put(COLUMN_SEQPU, dispatch.getseqPU());
		// if(dispatch.getCompanyPU() != null)
		values.put(COLUMN_COMPANYPU, dispatch.getCompanyPU());
		// if(dispatch.getAddressPU() != null)
		values.put(COLUMN_ADDRESSPU, dispatch.getAddressPU());
		// if(dispatch.getSuitPU() != null)
		values.put(COLUMN_SUITPU, dispatch.getSuitPU());
		// if(dispatch.getCityPU() != null)
		values.put(COLUMN_CITYPU, dispatch.getCityPU());
		// if(dispatch.getStatePU() != null)
		values.put(COLUMN_STATEPU, dispatch.getStatePU());
		// if(dispatch.getZipPU() != null)
		values.put(COLUMN_ZIPPU, dispatch.getZipPU());
		// if(dispatch.getCellPhonePU() != null)
		values.put(COLUMN_CELLPHONEPU, dispatch.getCellPhonePU());
		// if(dispatch.getHomePhonePU() != null)
		values.put(COLUMN_HOMEPHONEPU, dispatch.getHomePhonePU());

		// if(dispatch.getseqDL() != null)
		values.put(COLUMN_SEQDL, dispatch.getseqDL());
		// if(dispatch.getCompanyDL() != null)
		values.put(COLUMN_COMPANYDL, dispatch.getCompanyDL());
		// if(dispatch.getAddressDL() != null)
		values.put(COLUMN_ADDRESSDL, dispatch.getAddressDL());
		// if(dispatch.getSuitDL() != null)
		values.put(COLUMN_SUITDL, dispatch.getSuitDL());
		// if(dispatch.getCityDL() != null)
		values.put(COLUMN_CITYDL, dispatch.getCityDL());
		// if(dispatch.getStateDL() != null)
		values.put(COLUMN_STATEDL, dispatch.getStateDL());
		// if(dispatch.getZipDL() != null)
		values.put(COLUMN_ZIPDL, dispatch.getZipDL());
		// if(dispatch.getCellPhoneDL() != null)
		values.put(COLUMN_CELLPHONEDL, dispatch.getCellPhoneDL());
		// if(dispatch.getHomePhoneDL() != null)
		values.put(COLUMN_HOMEPHONEDL, dispatch.getHomePhoneDL());
		/** Inserting Row **/
		db.insert(TABLE_NAME, null, values);
		db.close(); // Closing database connection
	}

	public void updateOrderDelivery(String order_id, Hashtable params,
			byte[] signature, String action) {
		SQLiteDatabase db = mDatabase_helper.getWritableDatabase();
		ContentValues values = new ContentValues();

		Enumeration keys = params.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) params.get(key);
			values.put(key, value);

		}
		values.put("action", action);
		values.put(COLUMN_SIGNATURE, signature);
		db.update(TABLE_NAME, values, COLUMN_ORDERID + "='" + order_id + "'",
				null);
	}

	public void updateOrderRoundTripPickup(String order_id, Hashtable params,
			String action) {
		SQLiteDatabase db = mDatabase_helper.getWritableDatabase();
		ContentValues values = new ContentValues();

		Enumeration keys = params.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) params.get(key);
			values.put(key, value);

		}
		values.put("action", action);
		db.update(TABLE_NAME, values, COLUMN_ORDERID + "='" + order_id + "'",
				null);

	}

	public void updateOrderRoundTripDelivery(String order_id, Hashtable params,
			byte[] signaturedl, String action) {
		SQLiteDatabase db = mDatabase_helper.getWritableDatabase();
		ContentValues values = new ContentValues();

		Enumeration keys = params.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) params.get(key);
			values.put(key, value);

		}
		values.put("action", action);
		values.put(COLUMN_SIGNATUREDL, signaturedl);
		db.update(TABLE_NAME, values, COLUMN_ORDERID + "='" + order_id + "'",
				null);

	}

	public void checkDatabaseDataTemp() {
		Cursor cursor = null;
		try {

			mDatabase = mDatabase_helper.getWritableDatabase();
			cursor = mDatabase.rawQuery("SELECT * FROM OfflineDispatchTable",
					null);

			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {

					Log.e("",
							"Offline data :: "
									+ "Ordere ID"
									+ cursor.getString(cursor
											.getColumnIndex("order_id"))
									+ " RDDate "
									+ cursor.getString(cursor
											.getColumnIndex("RDDate"))
									+ " DPDate "
									+ cursor.getString(cursor
											.getColumnIndex("DPDate"))
									+ " DLDate "
									+ cursor.getString(cursor
											.getColumnIndex("DLDate"))
									+ " PUDate "
									+ cursor.getString(cursor
											.getColumnIndex("PUDate")));

					Log.e("",
							"Offline data :: "
									+ " isRoundTrip "
									+ cursor.getString(cursor
											.getColumnIndex("isRoundTrip"))
									+ " PCRoundTrip "
									+ cursor.getString(cursor
											.getColumnIndex("PCRoundTrip"))
									+ " SignDelivery "
									+ cursor.getString(cursor
											.getColumnIndex("SignDelivery"))
									+ " SignRoundTrip "
									+ cursor.getString(cursor
											.getColumnIndex("SignRoundTrip")));

					Log.e("",
							"Offline data ::  Action "
									+ cursor.getString(cursor
											.getColumnIndex("action")));

				} while (cursor.moveToNext());
			}

		} finally {
			if (cursor != null) {

				cursor.close();
			}
		}

	}

	// deleting oreders

	public void deleteOredrs(String driverId, String orderIds) {

		try {
			String sql = "delete from OfflineDispatchTable WHERE DPDate = '0000-00-00 00:00:00'"
					+ " and driver_id = '"
					+ driverId
					+ "'"
					+ " and order_id not in (" + orderIds + ")";
			Log.e("", sql);
			System.out.println(sql);
			mDatabase = mDatabase_helper.getWritableDatabase();
			mDatabase.execSQL(sql);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
		}
	}

	public void deleteDPDateOredrs(String driverId) {

		try {
			String sql = "delete from OfflineDispatchTable WHERE DPDate = '0000-00-00 00:00:00' and driver_id = '"
					+ driverId + "'";
			Log.e("", sql);
			System.out.println(sql);
			mDatabase = mDatabase_helper.getWritableDatabase();
			mDatabase.execSQL(sql);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
		}
	}

}