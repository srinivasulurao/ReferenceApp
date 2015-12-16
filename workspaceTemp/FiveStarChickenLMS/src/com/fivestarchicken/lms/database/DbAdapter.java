package com.fivestarchicken.lms.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fivestarchicken.lms.model.Answer;
import com.fivestarchicken.lms.model.Blog;
import com.fivestarchicken.lms.model.BlogDetail;
import com.fivestarchicken.lms.model.Certificate;
import com.fivestarchicken.lms.model.Configure;
import com.fivestarchicken.lms.model.Exam;
import com.fivestarchicken.lms.model.ExamModule;
import com.fivestarchicken.lms.model.ModuleDescription;
import com.fivestarchicken.lms.model.Questions;
import com.fivestarchicken.lms.model.Result;
import com.fivestarchicken.lms.model.User;
import com.fivestarchicken.lms.utils.Commons;

public class DbAdapter {

	private static Context context;
	private static SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	public DbAdapter(Context context) {
		DbAdapter.context = context;
		dbHelper = new DatabaseHelper(DbAdapter.context);
		DbAdapter.database = dbHelper.getWritableDatabase();

	}

	public void close() {
		dbHelper.close();
	}

	/*
	 * public boolean insertExam(Exam exam) {
	 * 
	 * return database.insert( Table.Exam.TABLE_NAME, null,
	 * Table.Exam.createValues(exam.getExamId(), exam.getCategoryName(),
	 * exam.getTitle(), exam.getModuleCount(), exam.getLevel())) > 0; }
	 */

	public boolean insertExamModule(ExamModule examModule) {

		return database.insert(Table.ExamModule.TABLE_NAME, null,
				Table.ExamModule.createValues(examModule.getModuleName(),
						examModule.getTotalQuestions(),
						examModule.getPassingScore(), examModule.getDuration(),
						examModule.getModuleId(), examModule.getDescription(),
						examModule.getStarLevel(), examModule.getCategoryId(),examModule.getLanguageType())) > 0;
	}

	public boolean insertQuestion(Questions question) {

		return database.insert(Table.Question.TABLE_NAME, null, Table.Question
				.createValues(question.getModuleId(), question.getQuestionId(),
						question.getQuestionText(), question.getAnswerCount(),question.getLanguageType())) > 0;
	}

	public boolean insertUser(User user) {

		return database.insert(Table.User.TABLE_NAME, null, Table.User
				.createValues(user.getUserName(), user.getEmail(),
						user.getPhone(), user.getRole(), user.getUserId(),
						user.getProfileImage(), user.getStarRate(),
						user.getManagerId(), user.getPassward(),user.getCategoryId())) > 0;
	}

	public boolean insertAnswer(Answer answer) {

		return database.insert(
				Table.Answer.TABLE_NAME,
				null,
				Table.Answer.createValues(answer.getAnswerId(),
						answer.getQuestionId(), answer.getAnswerText(),answer.getLanguageType(),answer.getModuleId())) > 0;
	}

	public boolean insertConfigure(Configure configure) {

		return database.insert(Table.Configure.TABLE_NAME, null,
				Table.Configure.createValues(configure.getConfigureTitle(),
						configure.getConfigureValue())) > 0;
	}

	public Long insertResult(Result result) {

		return database.insert(
				Table.Result.TABLE_NAME,
				null,
				Table.Result.createValues(result.getResultId(),

				result.getModuleId(), result.getExamDate(),
						result.getPercentage(), result.getStatus(),
						result.getModuleName(), result.getAnswerSelection(),
						result.getUserId(), result.getStarLevel(),result.getLanguageType()));
	}

	public boolean insertSyncStatus(String moduleName, String syncTime) {

		return database.insert(Table.SyncStatus.TABLE_NAME, null,
				Table.SyncStatus.createValues(syncTime, moduleName)) > 0;
	}

	public boolean insertModuleDetail(ModuleDescription moduleDescription) {

		return database.insert(Table.ModuleDetail.TABLE_NAME, null,
				Table.ModuleDetail.createValues(
						moduleDescription.getModuleId(),
						moduleDescription.getType(),
						moduleDescription.getDetail(),
						moduleDescription.getOrder(),moduleDescription.getLanguageType())) > 0;
	}

	public boolean insertBlog(Blog blog) {

		return database.insert(Table.Blog.TABLE_NAME, null,
				Table.Blog.createValues(blog.getBlogId(), blog.getBlogTitle(), blog.getSyncDate(),blog.getLanguageType())) > 0;
	}

	public boolean insertBlogDetail(BlogDetail blogDetail) {

		return database.insert(Table.BlogDetail.TABLE_NAME, null,
				Table.BlogDetail.createValues(blogDetail.getBlogId(),
						blogDetail.getType(), blogDetail.getDetail(),
						blogDetail.getOrder(),blogDetail.getLanguageType())) > 0;
	}
	
	public boolean insertCertificate(Certificate certificate) {

		return database.insert(Table.Certificate.TABLE_NAME, null,
				Table.Certificate.createValues(certificate.getStarValue(),
						certificate.getUserId(), certificate.getFileName(),
						certificate.getPassDate())) > 0;
	}

	public boolean updateSyncStatus(String moduleName, String syncTime) {

		return database.update(Table.SyncStatus.TABLE_NAME,

		Table.SyncStatus.createValues(syncTime, moduleName),
				Table.SyncStatus.MODULENAME + " = ?",
				new String[] { Commons.resultModule }) > 0;

	}

	public boolean updateConfigure(Configure configure, String cofigureTitle) {

		return database.update(Table.Configure.TABLE_NAME, Table.Configure
				.createValues(configure.getConfigureTitle(),
						configure.getConfigureValue()),
				Table.Configure.CONFIGURE_TITLE + " = ?",
				new String[] { cofigureTitle }) > 0;

	}

	public boolean updateUser(User user) {

		return database.update(Table.User.TABLE_NAME, Table.User.createValues(
				user.getUserName(), user.getEmail(), user.getPhone(),
				user.getRole(), user.getUserId(), user.getProfileImage(),
				user.getStarRate(), user.getManagerId(), user.getPassward(),user.getCategoryId()),
				Table.User.USERID + " = ?", new String[] { user.getUserId() }) > 0;
	}

	public int updateResult(Result result) {

		return database.update(
				Table.Result.TABLE_NAME,
				Table.Result.createValues(result.getResultId(),

				result.getModuleId(), result.getExamDate(),
						result.getPercentage(), result.getStatus(),
						result.getModuleName(), result.getAnswerSelection(),result.getUserId(),result.getStarLevel(),result.getLanguageType()),
				"id = ?", new String[] { result.getId() });
	}

	public User getUserDetail(String userId) {
		User user = null;

		Cursor mCursor = database.query(true, Table.User.TABLE_NAME,
				new String[] { Table.User.NAME, Table.User.EMAIL,
						Table.User.PHONE, Table.User.ROLE, Table.User.USERID,
						Table.User.PROFILEIMAGE, Table.User.STARLEVEL },
				Table.User.USERID + "='" + userId + "'", null, null, null,
				null, null);

		if (null != mCursor) {

			if (mCursor.moveToFirst()) {
				do {
					user = new User();
					user.setUserName(mCursor.getString(mCursor
							.getColumnIndex(Table.User.NAME)));
					user.setEmail(mCursor.getString(mCursor
							.getColumnIndex(Table.User.EMAIL)));
					user.setPhone(mCursor.getString(mCursor
							.getColumnIndex(Table.User.PHONE)));
					user.setRole(mCursor.getString(mCursor
							.getColumnIndex(Table.User.ROLE)));
					user.setUserId(mCursor.getString(mCursor
							.getColumnIndex(Table.User.USERID)));
					user.setProfileImage(mCursor.getString(mCursor
							.getColumnIndex(Table.User.PROFILEIMAGE)));
					user.setStarRate(mCursor.getString(mCursor
							.getColumnIndex(Table.User.STARLEVEL)));

				}

				while (mCursor.moveToNext());
			}
		}

		return user;
	}

	public void saveSyncTime(String moduleName, String syncTime) {

		Cursor mCursor = database.query(true, Table.SyncStatus.TABLE_NAME,
				new String[] { Table.SyncStatus.MODULENAME,
						Table.SyncStatus.LASTSYNCTIME, "id" },
				Table.SyncStatus.MODULENAME + "='" + moduleName + "'", null,
				null, null, null, null);

		if (mCursor.getCount() > 0) {

			updateSyncStatus(moduleName, syncTime);

		} else {

			insertSyncStatus(moduleName, syncTime);
		}

	}
	
	public Boolean updateStar(String starValue, String userId) {
		try {

			return database.update(Table.User.TABLE_NAME,Table.User.createValues(starValue), Table.User.USERID + " = ?",
					new String[] { userId }) > 0;

		} catch (Exception e) {

			return false;

		}
		

	}
	
	public Boolean clearAllData() {
		try {
			
			database.delete(Table.ExamModule.TABLE_NAME, null, null);
			database.delete(Table.Question.TABLE_NAME, null, null);
			database.delete(Table.Answer.TABLE_NAME, null, null);
			database.delete(Table.Result.TABLE_NAME, null, null);
			database.delete(Table.SyncStatus.TABLE_NAME, null, null);
			database.delete(Table.ModuleDetail.TABLE_NAME, null, null);
			database.delete(Table.Blog.TABLE_NAME, null, null);
			database.delete(Table.BlogDetail.TABLE_NAME, null, null);
			database.delete(Table.Configure.TABLE_NAME, null, null);
			database.delete(Table.Certificate.TABLE_NAME, null, null);
			
		} catch (Exception e) {

			return false;

		}
		return true;
	}
	public Boolean clearUserData() {
		try {
		database.delete(Table.User.TABLE_NAME, null, null);
		} catch (Exception e) {

			return false;

		}
		return true;
	}
		
	

	public String checkPendingResult(Result result) {
		String resultId;

		String query = "SELECT id  from " + Table.Result.TABLE_NAME + " where "
				+ Table.Result.MODULEID + "=" + result.getModuleId() + " and "
				+ Table.Result.STATUS + " ='" + Commons.statusProcessing + "'";

		Cursor mCursor = database.rawQuery(query, null);

		if (mCursor.getCount() > 0) {

			if (null != mCursor) {
				if (mCursor.moveToFirst()) {

					do {

						result.setId(mCursor.getString(mCursor
								.getColumnIndex("id")));

					} while (mCursor.moveToNext());
				}
			}
			mCursor.close();

			resultId = String.valueOf(updateResult(result));
		} else {

			resultId = insertResult(result).toString();

		}

		return resultId;
	}

	public ExamModule getModuleDetail(String moduleId,String languageType) {
		ExamModule examModule = null;

		Cursor mCursor = database.query(true, Table.ExamModule.TABLE_NAME,
				new String[] { Table.ExamModule.MODULENAME,
						Table.ExamModule.DURATION,
						Table.ExamModule.PASSINGSCORE,
						Table.ExamModule.TOTALQUESTION,
						Table.ExamModule.DESCRIPTION },
				Table.ExamModule.MODULEID + "='" + moduleId + "' and "+ Table.ExamModule.LANGUAGETYPE + "='" + languageType + "'", null, null,
				null, null, null);

		if (null != mCursor) {

			if (mCursor.moveToFirst()) {
				do {
					examModule = new ExamModule();
					examModule.setModuleName(mCursor.getString(mCursor
							.getColumnIndex(Table.ExamModule.MODULENAME)));
					examModule.setPassingScore((mCursor.getString(mCursor
							.getColumnIndex(Table.ExamModule.PASSINGSCORE))));
					examModule.setDuration(mCursor.getString(mCursor
							.getColumnIndex(Table.ExamModule.PASSINGSCORE)));
					examModule.setTotalQuestions(mCursor.getString(mCursor
							.getColumnIndex(Table.ExamModule.TOTALQUESTION)));
					examModule.setDuration(mCursor.getString(mCursor
							.getColumnIndex(Table.ExamModule.DURATION)));
					examModule.setDescription(mCursor.getString(mCursor
							.getColumnIndex(Table.ExamModule.DESCRIPTION)));

				}

				while (mCursor.moveToNext());
			}
		}

		return examModule;
	}

	public void saveResult(List<Result> resultList) {

		for (Result result : resultList) {

			insertResult(result);

		}

	}

	public Result getResultDetail(String resultId) {
		Result result = null;

		Cursor mCursor = database.query(true, Table.Result.TABLE_NAME,
				new String[] { Table.Result.EXAMDATE, Table.Result.PERCENTAGE,
						Table.Result.STATUS, Table.Result.MODULEID }, "id ='"
						+ resultId + "'", null, null, null, null, null);

		if (null != mCursor) {

			if (mCursor.moveToFirst()) {
				do {
					result = new Result();

					result.setModuleId((mCursor.getString(mCursor
							.getColumnIndex(Table.Result.MODULEID))));
					result.setPercentage(mCursor.getString(mCursor
							.getColumnIndex(Table.Result.PERCENTAGE)));

					result.setExamDate(mCursor.getString(mCursor
							.getColumnIndex(Table.Result.EXAMDATE)));
					result.setStatus(mCursor.getString(mCursor
							.getColumnIndex(Table.Result.STATUS)));

				}

				while (mCursor.moveToNext());
			}
		}

		return result;
	}

	public Boolean isAllExamClear() {

		Boolean result = true;

		Cursor mCursor = database.query(true, Table.ExamModule.TABLE_NAME,
				new String[] { Table.ExamModule.MODULENAME, }, null, null,
				null, null, null, null);

		if (mCursor.getCount() > 0) {

			result = false;
		}

		return result;
	}
	
	public Boolean isNewBlog(String fromDate,String todate) {

		Boolean result = false;
		
		String query = "SELECT id  from " + Table.Blog.TABLE_NAME + " where "
				+ Table.Blog.SYNCTIME + " >= '" + fromDate + "' and "+ Table.Blog.SYNCTIME + " <= '" + todate + "'";

		Cursor mCursor = database.rawQuery(query, null);

		

		if (mCursor.getCount() > 0) {

			result = true;
		}

		return result;
	}

	public Boolean isPendingSyncResult() {

		Boolean result = false;

		Cursor mCursor = database.query(true, Table.Result.TABLE_NAME,
				new String[] { Table.Result.RESULTID, }, Table.Result.STATUS
						+ "='" + Commons.statusProcessing + "'", null, null,
				null, null, null);

		if (mCursor.getCount() > 0) {

			result = true;
		}

		return result;

	}
	
	
	public Boolean isModulePassed(String moduleId,String userId) {

		Boolean result = false;

		Cursor mCursor = database.query(true, Table.Result.TABLE_NAME,
				new String[] { Table.Result.RESULTID, }, Table.Result.STATUS
						+ "='" + Commons.statusPass + "'and "
						+ Table.Result.MODULEID + " ='" + moduleId + "'and "
						+ Table.Result.USERID + " ='" + userId + "'", null,
				null, null, null, null);

		if (mCursor.getCount() > 0) {

			result = true;
		}

		return result;

	}
	
	public String getLastFailTime(String moduleId,String userId) {

		String lastFailTime = null;

		String query = "SELECT id," + Table.Result.EXAMDATE + "  from "
				+ Table.Result.TABLE_NAME + " where " + Table.Result.MODULEID
				+ "='" + moduleId + "' and " + Table.Result.STATUS + " ='"
				+ Commons.statusFail + "' and " + Table.Result.USERID + "='"
				+ userId + "' ORDER BY id DESC LIMIT 1";

		Cursor mCursor = database.rawQuery(query, null);

		if (mCursor.getCount() > 0) {

			if (null != mCursor) {
				if (mCursor.moveToFirst()) {
					do {

						lastFailTime = mCursor.getString(mCursor
								.getColumnIndex(Table.Result.EXAMDATE));

					} while (mCursor.moveToNext());
				}
			}
			mCursor.close();
		} else {

			lastFailTime = "0000";

		}

		return lastFailTime;

	}
	
	public String getLevelStartTime(String starLevel,String userId) {

		String lastTime = null;

		String query = "SELECT id," + Table.Result.EXAMDATE + "  from "
				+ Table.Result.TABLE_NAME + " where " + Table.Result. STARLEVEL
				+ "='" + starLevel + "' and "+ Table.Result.USERID + "='"
				+ userId + "' ORDER BY id ASC LIMIT 1";

		Cursor mCursor = database.rawQuery(query, null);

		if (mCursor.getCount() > 0) {

			if (null != mCursor) {
				if (mCursor.moveToFirst()) {
					do {

						lastTime = mCursor.getString(mCursor
								.getColumnIndex(Table.Result.EXAMDATE));

					} while (mCursor.moveToNext());
				}
			}
			mCursor.close();
		} else {

			lastTime = "0000";

		}

		return lastTime;

	}
	
	public ArrayList<Certificate> getCertificateList(String userId) {
		ArrayList<Certificate> certificateList = new ArrayList<Certificate>();
		Certificate certificate;

		Cursor mCursor = database.query(true, Table.Certificate.TABLE_NAME,
				new String[] { Table.Certificate.STAR_VALUE, Table.Certificate.USER_ID,Table.Certificate.FILE_NAME, "id" },
				Table.Certificate.USER_ID+"="+userId, null, null, null, null, null);

		if (null != mCursor) {
			if (mCursor.moveToFirst()) {
				do {
					certificate = new Certificate();
					certificate.setStarValue(mCursor.getString(mCursor
							.getColumnIndex(Table.Certificate.STAR_VALUE)));
					certificate.setUserId(mCursor.getString(mCursor
							.getColumnIndex( Table.Certificate.USER_ID)));
					certificate.setFileName(mCursor.getString(mCursor
							.getColumnIndex(Table.Certificate.FILE_NAME)));
					certificateList.add(certificate);

				} while (mCursor.moveToNext());
			}
		}
		mCursor.close();

		return certificateList;
	}

	
	public ArrayList<Blog> getBlogList(String languageType) {
		ArrayList<Blog> blogList = new ArrayList<Blog>();
		Blog blog;
		List<String> blogIdList = new ArrayList<String>();
		
		
		Cursor mCursorId = database.query(true, Table.Blog.TABLE_NAME,
				new String[] { Table.Blog.BLOGID }, Table.Blog.LANGUAGETYPE + "='"
						+ Commons.DEFAULT_LANGUAGE_TYPE + "'", null, null,
				null, null, null);
		if (mCursorId.getCount() > 0) {

			if (null != mCursorId) {
				if (mCursorId.moveToFirst()) {
					do {
						blogIdList.add(mCursorId.getString(mCursorId
								.getColumnIndex(Table.Blog.BLOGID)));

					}

					while (mCursorId.moveToNext());
				}
			}

			for (String blogId : blogIdList) {
		
		

				Cursor mCursor = database.query(true, Table.Blog.TABLE_NAME,
						new String[] { Table.Blog.BLOGID, Table.Blog.BLOGTITLE,
								Table.Blog.SYNCTIME,Table.Blog.LANGUAGETYPE, "id" },
						Table.Blog.LANGUAGETYPE + "='" + languageType
								+ "' and " + Table.Blog.BLOGID + "='" + blogId
								+ "'", null, null, null, null, null);
				if (mCursor.getCount() > 0) {
					if (null != mCursor) {
						if (mCursor.moveToFirst()) {
							do {
								blog = new Blog();
								blog.setBlogId(mCursor.getString(mCursor
										.getColumnIndex(Table.Blog.BLOGID)));
								blog.setBlogTitle(mCursor.getString(mCursor
										.getColumnIndex(Table.Blog.BLOGTITLE)));
								blog.setSyncDate(mCursor.getString(mCursor
										.getColumnIndex(Table.Blog.SYNCTIME)));
								blog.setLanguageType(mCursor.getString(mCursor
										.getColumnIndex(Table.Blog.LANGUAGETYPE)));
								blogList.add(blog);

							} while (mCursor.moveToNext());
						}
					}
					mCursor.close();

				}else{
				
					Cursor nCursor = database.query(true, Table.Blog.TABLE_NAME,
							new String[] { Table.Blog.BLOGID, Table.Blog.BLOGTITLE,
									Table.Blog.SYNCTIME,Table.Blog.LANGUAGETYPE, "id" },
							Table.Blog.LANGUAGETYPE + "='" + Commons.DEFAULT_LANGUAGE_TYPE
									+ "' and " + Table.Blog.BLOGID + "='" + blogId
									+ "'", null, null, null, null, null);
				
						if (null != nCursor) {
							if (nCursor.moveToFirst()) {
								do {
									blog = new Blog();
									blog.setBlogId(nCursor.getString(nCursor
											.getColumnIndex(Table.Blog.BLOGID)));
									blog.setBlogTitle(nCursor.getString(nCursor
											.getColumnIndex(Table.Blog.BLOGTITLE)));
									blog.setSyncDate(nCursor.getString(nCursor
											.getColumnIndex(Table.Blog.SYNCTIME)));
									blog.setLanguageType(nCursor.getString(nCursor
											.getColumnIndex(Table.Blog.LANGUAGETYPE)));
									blogList.add(blog);

								} while (nCursor.moveToNext());
							}
						}
						nCursor.close();
				
				
				
			}
			}
		}

		return blogList;
	}
	
	
	public ArrayList<BlogDetail> getBlogDetailList(String blogId,String languageType) {

		ArrayList<BlogDetail> blogDetailList = new ArrayList<BlogDetail>();
		BlogDetail blogDetail;

		Cursor mCursor = database.query(true, Table.BlogDetail.TABLE_NAME,
				new String[] { Table.BlogDetail.BLOGID,
						Table.BlogDetail.DETAIL, Table.BlogDetail.TYPE,
						Table.BlogDetail.ORDERID, "id" },
				Table.BlogDetail.BLOGID + "='" + blogId + "' and "+  Table.BlogDetail.LANGUAGETYPE + "='"
						+ languageType + "'", null, null,
				null, null, null);

		if (null != mCursor) {
			if (mCursor.moveToFirst()) {
				do {

					blogDetail = new BlogDetail();
					blogDetail.setBlogId(mCursor.getString(mCursor
							.getColumnIndex(Table.BlogDetail.BLOGID)));
					blogDetail.setDetail(mCursor.getString(mCursor
							.getColumnIndex(Table.BlogDetail.DETAIL)));
					blogDetail.setOrder(mCursor.getString(mCursor
							.getColumnIndex(Table.BlogDetail.ORDERID)));
					blogDetail.setType(mCursor.getString(mCursor
							.getColumnIndex(Table.BlogDetail.TYPE)));
					blogDetailList.add(blogDetail);

				} while (mCursor.moveToNext());
			}
		}
		mCursor.close();

		return blogDetailList;
	}

	public ArrayList<ModuleDescription> getFileList() {

		ArrayList<ModuleDescription> fileList = new ArrayList<ModuleDescription>();

		ModuleDescription moduleDescription;

		Cursor mCursor = database.query(true, Table.ModuleDetail.TABLE_NAME,
				new String[] { Table.ModuleDetail.MODULEID,
						Table.ModuleDetail.DETAIL, Table.ModuleDetail.TYPE,
						Table.ModuleDetail.ORDERID, "id" },
				Table.ModuleDetail.TYPE + "='" + Commons.TYPE_IMAGE + "' or "
						+ Table.ModuleDetail.TYPE + "='" + Commons.TYPE_VIDEO
						+ "' or "+ Table.BlogDetail.TYPE + "='" + Commons.TYPE_PDF
						+ "'", null, null, null, null, null);

		if (null != mCursor) {
			if (mCursor.moveToFirst()) {
				do {

					moduleDescription = new ModuleDescription();
					moduleDescription.setModuleId(mCursor.getString(mCursor
							.getColumnIndex(Table.ModuleDetail.MODULEID)));
					moduleDescription.setDetail(mCursor.getString(mCursor
							.getColumnIndex(Table.ModuleDetail.DETAIL)));
					moduleDescription.setOrder(mCursor.getString(mCursor
							.getColumnIndex(Table.ModuleDetail.ORDERID)));
					moduleDescription.setType(mCursor.getString(mCursor
							.getColumnIndex(Table.ModuleDetail.TYPE)));
					fileList.add(moduleDescription);

				} while (mCursor.moveToNext());
			}
		}
		mCursor.close();

		return fileList;
	}
	
	public ArrayList<ModuleDescription> getBlogFileList() {

		ArrayList<ModuleDescription> fileList = new ArrayList<ModuleDescription>();

		ModuleDescription moduleDescription;

		Cursor mCursor = database.query(true, Table.BlogDetail.TABLE_NAME,
				new String[] { Table.BlogDetail.BLOGID,
						Table.BlogDetail.DETAIL, Table.BlogDetail.TYPE,
						Table.BlogDetail.ORDERID, "id" },
				Table.BlogDetail.TYPE + "='" + Commons.TYPE_IMAGE + "' or "
						+ Table.BlogDetail.TYPE + "='" + Commons.TYPE_VIDEO
						+ "' or "+ Table.BlogDetail.TYPE + "='" + Commons.TYPE_PDF
						+ "'", null, null, null, null, null);

		if (null != mCursor) {
			if (mCursor.moveToFirst()) {
				do {

					moduleDescription = new ModuleDescription();
					moduleDescription.setModuleId(mCursor.getString(mCursor
							.getColumnIndex(Table.BlogDetail.BLOGID)));
					moduleDescription.setDetail(mCursor.getString(mCursor
							.getColumnIndex(Table.BlogDetail.DETAIL)));
					moduleDescription.setOrder(mCursor.getString(mCursor
							.getColumnIndex(Table.BlogDetail.ORDERID)));
					moduleDescription.setType(mCursor.getString(mCursor
							.getColumnIndex(Table.BlogDetail.TYPE)));
					fileList.add(moduleDescription);

				} while (mCursor.moveToNext());
			}
		}
		mCursor.close();

		return fileList;
	}

	public ArrayList<Result> getPendingResultList() {

		ArrayList<Result> resultList = new ArrayList<Result>();
		Result result;

		Cursor mCursor = database.query(true, Table.Result.TABLE_NAME,
				new String[] { Table.Result.PERCENTAGE, Table.Result.STATUS,
						Table.Result.MODULENAME, "id" }, Table.Result.STATUS
						+ "='" + Commons.statusProcessing + "'", null, null,
				null, null, null);

		if (null != mCursor) {
			if (mCursor.moveToFirst()) {
				do {
					result = new Result();
					result.setResultId(mCursor.getString(mCursor
							.getColumnIndex("id")));
					result.setPercentage(mCursor.getString(mCursor
							.getColumnIndex(Table.Result.PERCENTAGE)));
					result.setStatus(mCursor.getString(mCursor
							.getColumnIndex(Table.Result.STATUS)));
					result.setModuleName(mCursor.getString(mCursor
							.getColumnIndex(Table.Result.MODULENAME)));

					resultList.add(result);

				} while (mCursor.moveToNext());
			}
		}
		mCursor.close();

		return resultList;

	}

	public ArrayList<ModuleDescription> getModuleDescription(String moduleId,String languageType) {

		ArrayList<ModuleDescription> moduleDescriptionList = new ArrayList<ModuleDescription>();
		ModuleDescription moduleDescription;

		Cursor mCursor = database.query(true, Table.ModuleDetail.TABLE_NAME,
				new String[] { Table.ModuleDetail.MODULEID,
						Table.ModuleDetail.DETAIL, Table.ModuleDetail.TYPE,
						Table.ModuleDetail.ORDERID, "id" },
				Table.ModuleDetail.MODULEID + "='" + moduleId + "' and "+  Table.ModuleDetail.LANGUAGETYPE + "='"
						+ languageType + "'", null,
				null, null, null, null);

		if (null != mCursor) {
			if (mCursor.moveToFirst()) {
				do {

					moduleDescription = new ModuleDescription();
					moduleDescription.setModuleId(mCursor.getString(mCursor
							.getColumnIndex(Table.ModuleDetail.MODULEID)));
					moduleDescription.setDetail(mCursor.getString(mCursor
							.getColumnIndex(Table.ModuleDetail.DETAIL)));
					moduleDescription.setOrder(mCursor.getString(mCursor
							.getColumnIndex(Table.ModuleDetail.ORDERID)));
					moduleDescription.setType(mCursor.getString(mCursor
							.getColumnIndex(Table.ModuleDetail.TYPE)));
					moduleDescriptionList.add(moduleDescription);

				} while (mCursor.moveToNext());
			}
		}
		mCursor.close();

		return moduleDescriptionList;
	}

	
	public List<ExamModule> getExamModule(String starLevel, String languageType) {
		List<ExamModule> examModuleList = new ArrayList<ExamModule>();
		List<String> examModuleIdList = new ArrayList<String>();
		ExamModule examModule;
		Cursor mCursorId = database.query(true, Table.ExamModule.TABLE_NAME,
				new String[] { Table.ExamModule.MODULEID,
						Table.ExamModule.STAR_LEVEL },
				Table.ExamModule.STAR_LEVEL + "='" + starLevel + "' and "
						+ Table.ExamModule.LANGUAGETYPE + "='"
						+ Commons.DEFAULT_LANGUAGE_TYPE + "'", null, null,
				null, null, null);
		if (mCursorId.getCount() > 0) {

			if (null != mCursorId) {
				if (mCursorId.moveToFirst()) {
					do {
						examModuleIdList.add(mCursorId.getString(mCursorId
								.getColumnIndex(Table.ExamModule.MODULEID)));

					}

					while (mCursorId.moveToNext());
				}
			}

			for (String moduleId : examModuleIdList) {

				Cursor mCursor = database.query(true,
						Table.ExamModule.TABLE_NAME, new String[] {
								Table.ExamModule.MODULENAME,
								Table.ExamModule.PASSINGSCORE,
								Table.ExamModule.TOTALQUESTION,
								Table.ExamModule.DURATION,
								Table.ExamModule.MODULEID,
								Table.ExamModule.STAR_LEVEL,
								Table.ExamModule.LANGUAGETYPE},
						Table.ExamModule.STAR_LEVEL + "='" + starLevel
								+ "' and " + Table.ExamModule.MODULEID + "='"
								+ moduleId + "' and "
								+ Table.ExamModule.LANGUAGETYPE + "='"
								+ languageType + "'", null, null, null, null,
						null);

				if (mCursor.getCount() > 0) {

					if (null != mCursor) {
						if (mCursor.moveToFirst()) {
							do {
								examModule = new ExamModule();
								examModule
										.setModuleName(mCursor.getString(mCursor
												.getColumnIndex(Table.ExamModule.MODULENAME)));
								examModule
										.setPassingScore(mCursor.getString(mCursor
												.getColumnIndex(Table.ExamModule.PASSINGSCORE)));
								examModule
										.setModuleId(mCursor.getString(mCursor
												.getColumnIndex(Table.ExamModule.MODULEID)));
								examModule
										.setTotalQuestions(mCursor.getString(mCursor
												.getColumnIndex(Table.ExamModule.TOTALQUESTION)));
								examModule
										.setDuration(mCursor.getString(mCursor
												.getColumnIndex(Table.ExamModule.DURATION)));
								
								examModule
								.setStarLevel(mCursor.getString(mCursor
										.getColumnIndex(Table.ExamModule.STAR_LEVEL)));
								
								examModule
								.setLanguageType(mCursor.getString(mCursor
										.getColumnIndex(Table.ExamModule.LANGUAGETYPE)));

								examModuleList.add(examModule);

							}

							while (mCursor.moveToNext());
						}
					}
				} else {

					Cursor nCursor = database.query(true,
							Table.ExamModule.TABLE_NAME, new String[] {
									Table.ExamModule.MODULENAME,
									Table.ExamModule.PASSINGSCORE,
									Table.ExamModule.TOTALQUESTION,
									Table.ExamModule.DURATION,
									Table.ExamModule.MODULEID,
									Table.ExamModule.STAR_LEVEL,
									Table.ExamModule.LANGUAGETYPE},
							Table.ExamModule.STAR_LEVEL + "='" + starLevel
									+ "' and " + Table.ExamModule.MODULEID
									+ "='" + moduleId + "' and "
									+ Table.ExamModule.LANGUAGETYPE + "='"
									+ Commons.DEFAULT_LANGUAGE_TYPE + "'",
							null, null, null, null, null);

					if (null != nCursor) {
						if (nCursor.moveToFirst()) {
							do {
								examModule = new ExamModule();
								examModule
										.setModuleName(nCursor.getString(nCursor
												.getColumnIndex(Table.ExamModule.MODULENAME)));
								examModule
										.setPassingScore(nCursor.getString(nCursor
												.getColumnIndex(Table.ExamModule.PASSINGSCORE)));
								examModule
										.setModuleId(nCursor.getString(nCursor
												.getColumnIndex(Table.ExamModule.MODULEID)));
								examModule
										.setTotalQuestions(nCursor.getString(nCursor
												.getColumnIndex(Table.ExamModule.TOTALQUESTION)));
								examModule
										.setDuration(nCursor.getString(nCursor
												.getColumnIndex(Table.ExamModule.DURATION)));
								examModule
								.setLanguageType(nCursor.getString(nCursor
										.getColumnIndex(Table.ExamModule.LANGUAGETYPE)));
								examModule
								.setStarLevel(mCursor.getString(mCursor
										.getColumnIndex(Table.ExamModule.STAR_LEVEL)));

								examModuleList.add(examModule);

							}

							while (nCursor.moveToNext());
						}

					}
				}
			}

		}

		return examModuleList;
	}

	public void saveUser(List<User> userList) {

		for (User user : userList) {

			insertUser(user);

		}

	}
	
	
	public void saveBlog(List<Blog> blogList) {
		try {
			for (Blog blog : blogList) {

				for (BlogDetail blogDetail : blog.getBlogDetailList()) {

					insertBlogDetail(blogDetail);

				}
				insertBlog(blog);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	
	public void saveCertificate(List<Certificate> certificateList){
		
		try {
			for(Certificate ctf:certificateList){
				
				insertCertificate(ctf);
			}
			
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		
	}
	
	
	public void saveConfig(List<Configure> configureList) {

		for (Configure configure : configureList) {

			Cursor mCursor = database.query(
					true,
					Table.Configure.TABLE_NAME,
					new String[] { Table.Configure.CONFIGURE_TITLE,
							Table.Configure.CONFIGURE_VALUE, "id" },
					Table.Configure.CONFIGURE_TITLE + "='"
							+ configure.getConfigureTitle() + "'", null, null,
					null, null, null);

			if (mCursor.getCount() > 0) {
				
				updateConfigure(configure, configure.getConfigureTitle());

			}else{
				
				insertConfigure(configure);
			}

		}
	}

	public void saveExam(List<ExamModule> examModuleList) {
		try {

			for (ExamModule examModule : examModuleList) {

				for (ModuleDescription mod : examModule.getDescriptionList()) {

					insertModuleDetail(mod);

				}

				for (Questions questions : examModule.getQuestionsList()) {

					for (Answer answer : questions.getAnswareList()) {

						insertAnswer(answer);

					}
					insertQuestion(questions);
				}
				insertExamModule(examModule);

			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void saveInterviewExam(ExamModule examModule) {
		try {

			for (Questions questions : examModule.getQuestionsList()) {

				for (Answer answer : questions.getAnswareList()) {

					insertAnswer(answer);

				}
				insertQuestion(questions);
			}
			insertExamModule(examModule);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public ArrayList<User> getEmployeeList() {

		ArrayList<User> employeeList = new ArrayList<User>();

		User user;

		Cursor mCursor = database.query(true, Table.User.TABLE_NAME,
				new String[] { Table.User.NAME, Table.User.EMAIL,
						Table.User.PHONE, Table.User.ROLE, Table.User.USERID,
						Table.User.PROFILEIMAGE, Table.User.STARLEVEL,
						Table.User.PASSWORD }, null, null, null, null, null,
				null);

		if (null != mCursor) {

			if (mCursor.moveToFirst()) {
				do {
					user = new User();
					user.setUserName(mCursor.getString(mCursor
							.getColumnIndex(Table.User.NAME)));
					user.setEmail(mCursor.getString(mCursor
							.getColumnIndex(Table.User.EMAIL)));
					user.setPhone(mCursor.getString(mCursor
							.getColumnIndex(Table.User.PHONE)));
					user.setRole(mCursor.getString(mCursor
							.getColumnIndex(Table.User.ROLE)));
					user.setUserId(mCursor.getString(mCursor
							.getColumnIndex(Table.User.USERID)));
					user.setProfileImage(mCursor.getString(mCursor
							.getColumnIndex(Table.User.PROFILEIMAGE)));
					user.setStarRate(mCursor.getString(mCursor
							.getColumnIndex(Table.User.STARLEVEL)));
					user.setPassward(mCursor.getString(mCursor
							.getColumnIndex(Table.User.PASSWORD)));

					employeeList.add(user);

				}

				while (mCursor.moveToNext());
			}
		}

		return employeeList;
	}

	public ArrayList<Result> getResult(String userId,String languageType) {

		ArrayList<Result> resultList = new ArrayList<Result>();
		Result result;
		
		String query = "SELECT result." + Table.Result.EXAMDATE + ",result."+Table.Result.PERCENTAGE+",result."+Table.Result.MODULEID+",result."+ Table.Result.STATUS+",module."+ 
						Table.Result.MODULENAME+","+  "result.id  from "
				+ Table.Result.TABLE_NAME + " result LEFT JOIN "+Table.ExamModule.TABLE_NAME+" module ON result.module_id=module.module_id and module.language_type='" + languageType + "'"
						+ "where  result.user_id='"+userId+"'";

		Cursor mCursor = database.rawQuery(query, null);

		/*Cursor mCursor = database.query(true, Table.Result.TABLE_NAME,
				new String[] { Table.Result.PERCENTAGE, Table.Result.STATUS,
						Table.Result.MODULENAME, "id" }, Table.Result.USERID+"='"+userId+"'", null, null,
				null, null, null);*/
		if (mCursor.getCount() > 0) {
		if (null != mCursor) {
			if (mCursor.moveToFirst()) {
				do {
					result = new Result();
					result.setResultId(mCursor.getString(mCursor
							.getColumnIndex("result.id")));
					result.setPercentage(mCursor.getString(mCursor
							.getColumnIndex(Table.Result.PERCENTAGE)));
					result.setStatus(mCursor.getString(mCursor
							.getColumnIndex(Table.Result.STATUS)));
					result.setModuleName(mCursor.getString(mCursor
							.getColumnIndex(Table.Result.MODULENAME)));
					result.setModuleId(mCursor.getString(mCursor
							.getColumnIndex(Table.Result.MODULEID)));
					
					if(result.getModuleName()==null){
						
							Cursor nCursor = database
									.query(true,
											Table.ExamModule.TABLE_NAME,
											new String[] { Table.ExamModule.MODULENAME, },
											Table.ExamModule.MODULEID
													+ "='"
													+ result.getModuleId()
													+ "' and "
													+ Table.ExamModule.LANGUAGETYPE
													+ "='"
													+ Commons.DEFAULT_LANGUAGE_TYPE
													+ "'", null, null, null,
											null, null);
							
							if (null != nCursor) {
								if (nCursor.moveToFirst()) {
									do {
										
										result.setModuleName(nCursor.getString(nCursor
												.getColumnIndex(Table.ExamModule.MODULENAME)));
										
									} while (nCursor.moveToNext());
							}
					}
							nCursor.close();
						
						
						
					}

					resultList.add(result);

				} while (mCursor.moveToNext());
			}
		}
		}
		mCursor.close();

		return resultList;

	}
	public String getConfigureValue(String configureTitle) {
		String configureValue=null;
		Cursor mCursor = database.query(true, Table.Configure.TABLE_NAME,
				new String[] { Table.Configure.CONFIGURE_TITLE,
						Table.Configure.CONFIGURE_VALUE, "id" },
						Table.Configure.CONFIGURE_TITLE + "='" + configureTitle + "'", null,
				null, null, null, null);

		

			if (null != mCursor) {
				if (mCursor.moveToFirst()) {
					do {

						configureValue = mCursor.getString(mCursor
								.getColumnIndex(Table.Configure.CONFIGURE_VALUE));

					} while (mCursor.moveToNext());
				}
			}
		
		
		
		return configureValue;
	}
	

	public String getLastSynctime(String moduleName) {

		String lastSyncTime = null;

		Cursor mCursor = database.query(true, Table.SyncStatus.TABLE_NAME,
				new String[] { Table.SyncStatus.MODULENAME,
						Table.SyncStatus.LASTSYNCTIME, "id" },
				Table.SyncStatus.MODULENAME + "='" + moduleName + "'", null,
				null, null, null, null);

		if (mCursor.getCount() > 0) {

			if (null != mCursor) {
				if (mCursor.moveToFirst()) {
					do {

						lastSyncTime = mCursor.getString(mCursor
								.getColumnIndex(Table.SyncStatus.LASTSYNCTIME));

					} while (mCursor.moveToNext());
				}
			}
			mCursor.close();
		} else {

			lastSyncTime = "0000";

		}

		return lastSyncTime;

	}

	public ArrayList<Questions> getQuestionList(String moduleId,String languageType) {

		ArrayList<Questions> quotationList = new ArrayList<Questions>();
		ArrayList<Answer> answareList;
		Questions questions;
		Answer answer;

		Cursor mCursor = database.query(true, Table.Question.TABLE_NAME,
				new String[] { Table.Question.QUESTIONID,
						Table.Question.QUESTIONTEXT,
						Table.Question.ANSWARECOUNT, Table.Question.MODULEID,
						"id" },
				Table.Question.MODULEID + "='" + moduleId + "' and "+Table.Question.LANGUAGETYPE + "='" + languageType + "'", null, null,
				null, null, null);

		if (null != mCursor) {
			if (mCursor.moveToFirst()) {
				do {

					questions = new Questions();
					// itemCategory=new ItemCategory();
					questions.setQuestionId(mCursor.getString(mCursor
							.getColumnIndex(Table.Question.QUESTIONID)));
					questions.setQuestionText(mCursor.getString(mCursor
							.getColumnIndex(Table.Question.QUESTIONTEXT)));

					Cursor nCursor = database.query(
							true,
							Table.Answer.TABLE_NAME,
							new String[] { Table.Answer.ANSWERID,
									Table.Answer.ANSWERTEXT,
									Table.Answer.QUESTIONID, "id" },
							Table.Answer.QUESTIONID + "='"
									+ questions.getQuestionId() + "' and "+Table.Answer.MODULEID + "='" + moduleId + "' and "+Table.Answer.LANGUAGETYPE + "='" + languageType + "'", null,
							null, null, null, null);
					answareList = new ArrayList<Answer>();

					if (null != nCursor) {
						if (nCursor.moveToFirst()) {
							do {

								answer = new Answer();
								answer.setAnswerId(nCursor.getString(nCursor
										.getColumnIndex(Table.Answer.ANSWERID)));
								answer.setAnswerText(nCursor.getString(nCursor
										.getColumnIndex(Table.Answer.ANSWERTEXT)));
								answer.setQuestionId(nCursor.getString(nCursor
										.getColumnIndex(Table.Answer.QUESTIONID)));
								answareList.add(answer);

							}

							while (nCursor.moveToNext());
						}
					}
					nCursor.close();

					questions.setAnswareList(answareList);
					quotationList.add(questions);

				} while (mCursor.moveToNext());
			}
		}
		mCursor.close();
		return quotationList;

	}

}
