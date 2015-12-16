package com.fivestarchicken.lms.database;

import android.content.ContentValues;

public class Table {

	public static class User {
		public static final String CREATE_TABLE = "CREATE TABLE User (id INTEGER PRIMARY KEY, name TEXT, email TEXT, phone TEXT, role TEXT, user_id TEXT,profile_image TEXT,star_level TEXT,manager_id TEXT,passward TEXT,category_id TEXT)";

		public static final String TABLE_NAME = "User";

		public static final String NAME = "name", EMAIL = "email",
				PROFILEIMAGE = "profile_image", STARLEVEL = "star_level",
				CATEGORYID = "category_id", PHONE = "phone", ROLE = "role",
				USERID = "user_id", MANAGERID = "manager_id",
				PASSWORD = "passward";

		public static final String DROP_TABLE = "drop table if exists User";

		public static ContentValues createValues(String name, String email,
				String phone, String role, String userId, String profileImage,
				String starLevel, String managerId, String passward,
				String categoryId) {
			ContentValues values = new ContentValues();
			values.put(NAME, name);
			values.put(EMAIL, email);
			values.put(PHONE, phone);
			values.put(ROLE, role);
			values.put(USERID, userId);
			values.put(PROFILEIMAGE, profileImage);
			values.put(STARLEVEL, starLevel);
			values.put(MANAGERID, managerId);
			values.put(PASSWORD, passward);
			values.put(CATEGORYID, categoryId);
			return values;
		}

		public static ContentValues createValues(String starLevel) {

			ContentValues values = new ContentValues();
			values.put(STARLEVEL, starLevel);
			return values;
		}

	}

	/*
	 * public static class Exam { public static final String CREATE_TABLE =
	 * "CREATE TABLE Exam (id INTEGER PRIMARY KEY, server_id TEXT, category_name TEXT, title TEXT,module_count TEXT,level TEXT)"
	 * ;
	 * 
	 * 
	 * public static final String TABLE_NAME = "Exam";
	 * 
	 * public static final String SERVERID = "server_id",
	 * CATEGORYNAME="category_name",MODULECOUNT="module_count",
	 * TITLE="title",LEVEL="level";
	 * 
	 * 
	 * 
	 * public static final String DROP_TABLE = "drop table if exists Exam";
	 * 
	 * public static ContentValues createValues(String serverId, String
	 * categoryName,String title,String moduleCount,String level ) {
	 * ContentValues values = new ContentValues(); values.put(SERVERID,
	 * serverId); values.put(CATEGORYNAME, categoryName); values.put(TITLE,
	 * title); values.put(MODULECOUNT, moduleCount); values.put(LEVEL, level);
	 * return values; }
	 * 
	 * }
	 */

	public static class ExamModule {
		public static final String CREATE_TABLE = "CREATE TABLE ExamModule (id INTEGER PRIMARY KEY, module_id TEXT, module_name TEXT, total_question TEXT, passing_score TEXT, duration TEXT,description TEXT,star_level TEXT,category_id Text,language_type Text)";

		public static final String TABLE_NAME = "ExamModule";

		public static final String MODULEID = "module_id",
				MODULENAME = "module_name", TOTALQUESTION = "total_question",
				PASSINGSCORE = "passing_score", DURATION = "duration",
				DESCRIPTION = "description", STAR_LEVEL = "star_level",
				CATEGORYID = "category_id",LANGUAGETYPE = "language_type";

		public static final String DROP_TABLE = "drop table if exists ExamModule";

		public static ContentValues createValues(String moduleName,
				String totalQuestion, String passingScore, String duration,
				String moduleId, String description, String starLevel,
				String categoryId,String languageType) {
			ContentValues values = new ContentValues();

			values.put(MODULEID, moduleId);
			values.put(MODULENAME, moduleName);
			values.put(TOTALQUESTION, totalQuestion);
			values.put(PASSINGSCORE, passingScore);
			values.put(DURATION, duration);
			values.put(DESCRIPTION, description);
			values.put(STAR_LEVEL, starLevel);
			values.put(CATEGORYID, categoryId);
			values.put(LANGUAGETYPE, languageType);

			return values;
		}

	}

	public static class Question {
		public static final String CREATE_TABLE = "CREATE TABLE Question (id INTEGER PRIMARY KEY, module_id TEXT, question_id TEXT, question_text TEXT, answare_count TEXT,language_type Text)";

		public static final String TABLE_NAME = "Question";

		public static final String MODULEID = "module_id",
				QUESTIONID = "question_id", QUESTIONTEXT = "question_text",
				ANSWARECOUNT = "answare_count",LANGUAGETYPE = "language_type";;

		public static final String DROP_TABLE = "drop table if exists Question";

		public static ContentValues createValues(String moduleId,
				String questionId, String questionText, String answareCount,String languageType) {
			ContentValues values = new ContentValues();
			values.put(MODULEID, moduleId);
			values.put(QUESTIONID, questionId);
			values.put(QUESTIONTEXT, questionText);
			values.put(ANSWARECOUNT, answareCount);
			values.put(LANGUAGETYPE, languageType);
			return values;
		}
	}

	public static class Answer {
		public static final String CREATE_TABLE = "CREATE TABLE Answer (id INTEGER PRIMARY KEY, answer_id TEXT, question_id TEXT, answer_text TEXT,language_type Text, module_id TEXT)";

		public static final String TABLE_NAME = "Answer";

		public static final String ANSWERID = "answer_id",LANGUAGETYPE = "language_type",
				QUESTIONID = "question_id", ANSWERTEXT = "answer_text",MODULEID="module_id";

		public static final String DROP_TABLE = "drop table if exists Answer";

		public static ContentValues createValues(String answerId,
				String questionId, String answerText,String languageType,String moduleId) {
			ContentValues values = new ContentValues();
			values.put(ANSWERID, answerId);
			values.put(QUESTIONID, questionId);
			values.put(ANSWERTEXT, answerText);
			values.put(MODULEID, moduleId);
			values.put(LANGUAGETYPE, languageType);
			return values;
		}

	}

	public static class Result {
		public static final String CREATE_TABLE = "CREATE TABLE Result (id INTEGER PRIMARY KEY, result_id TEXT,exam_name TEXT,exam_id TEXT,module_id TEXT,module_name TEXT, exam_date TEXT, percentage TEXT,status TEXT,selected_answare TEXT,user_id TEXT,star_level TEXT,language_type Text)";

		public static final String TABLE_NAME = "Result";

		public static final String RESULTID = "result_id",
				MODULEID = "module_id", STARLEVEL = "star_level",
				PERCENTAGE = "percentage", STATUS = "status",
				EXAMDATE = "exam_date", MODULENAME = "module_name",
				SELECTEDANSWARE = "selected_answare", USERID = "user_id",
				LANGUAGETYPE = "language_type";
		
		public static final String DROP_TABLE = "drop table if exists Result";

		public static ContentValues createValues(String resultId,
				String moduleId, String examDate, String percentage,
				String status, String moduleName, String selectedAnsware,
				String userId, String starLevel,String languageType) {
			ContentValues values = new ContentValues();
			values.put(RESULTID, resultId);
			values.put(EXAMDATE, examDate);
			values.put(MODULEID, moduleId);
			values.put(PERCENTAGE, percentage);
			values.put(STATUS, status);
			values.put(MODULENAME, moduleName);
			values.put(SELECTEDANSWARE, selectedAnsware);
			values.put(STARLEVEL, starLevel);
			values.put(USERID, userId);
			values.put(LANGUAGETYPE, languageType);
			return values;
		}

	}

	public static class SyncStatus {
		public static final String CREATE_TABLE = "CREATE TABLE SyncStatus (id INTEGER PRIMARY KEY, module_name TEXT,last_sync_time TEXT)";

		public static final String TABLE_NAME = "SyncStatus";

		public static final String MODULENAME = "module_name",
				LASTSYNCTIME = "last_sync_time";

		public static final String DROP_TABLE = "drop table if exists SyncStatus";

		public static ContentValues createValues(String lastSyncTime,
				String moduleName) {
			ContentValues values = new ContentValues();

			values.put(MODULENAME, moduleName);
			values.put(LASTSYNCTIME, lastSyncTime);

			return values;
		}

	}

	public static class ModuleDetail {
		public static final String CREATE_TABLE = "CREATE TABLE ModuleDetail (id INTEGER PRIMARY KEY, module_id TEXT,type TEXT,detail TEXT,orderId TEXT,language_type Text)";

		public static final String TABLE_NAME = "ModuleDetail";

		public static final String MODULEID = "module_id", TYPE = "type",
				DETAIL = "detail", ORDERID = "orderId",LANGUAGETYPE = "language_type";;

		public static final String DROP_TABLE = "drop table if exists ModuleDetail";

		public static ContentValues createValues(String moduleId, String type,
				String detail, String order,String languageType) {
			ContentValues values = new ContentValues();

			values.put(MODULEID, moduleId);
			values.put(TYPE, type);
			values.put(DETAIL, detail);
			values.put(ORDERID, order);
            values.put(LANGUAGETYPE, languageType);

			return values;
		}
	}

	public static class Blog {
		public static final String CREATE_TABLE = "CREATE TABLE Blog (id INTEGER PRIMARY KEY, blogId TEXT,blogTitle TEXT,sync_time DATETIME,language_type Text)";

		public static final String TABLE_NAME = "Blog";

		public static final String BLOGID = "blogId", BLOGTITLE = "blogTitle",
				SYNCTIME = "sync_time",LANGUAGETYPE = "language_type";;

		public static final String DROP_TABLE = "drop table if exists Blog";

		public static ContentValues createValues(String blogId,
				String blogTitle, String syncTime,String languageType) {
			ContentValues values = new ContentValues();

			values.put(BLOGID, blogId);
			values.put(BLOGTITLE, blogTitle);
			values.put(SYNCTIME, syncTime);
			values.put(LANGUAGETYPE, languageType);
			return values;
		}
	}

	public static class BlogDetail {
		public static final String CREATE_TABLE = "CREATE TABLE BlogDetail (id INTEGER PRIMARY KEY, blog_id TEXT,type TEXT,detail TEXT,orderId TEXT,language_type Text)";

		public static final String TABLE_NAME = "BlogDetail";

		public static final String BLOGID = "blog_id", TYPE = "type",
				DETAIL = "detail", ORDERID = "orderId",LANGUAGETYPE = "language_type";;

		public static final String DROP_TABLE = "drop table if exists BlogDetail";

		public static ContentValues createValues(String blogId, String type,
				String detail, String order,String languageType) {
			ContentValues values = new ContentValues();

			values.put(BLOGID, blogId);
			values.put(TYPE, type);
			values.put(DETAIL, detail);
			values.put(ORDERID, order);
			values.put(LANGUAGETYPE, languageType);

			return values;
		}
	}

	public static class Configure {
		public static final String CREATE_TABLE = "CREATE TABLE Configure (id INTEGER PRIMARY KEY, config_title TEXT,config_value TEXT)";

		public static final String TABLE_NAME = "Configure";

		public static final String CONFIGURE_TITLE = "config_title",
				CONFIGURE_VALUE = "config_value";

		public static final String DROP_TABLE = "drop table if exists Configure";

		public static ContentValues createValues(String configTitle,
				String configValue) {
			ContentValues values = new ContentValues();

			values.put(CONFIGURE_TITLE, configTitle);
			values.put(CONFIGURE_VALUE, configValue);

			return values;
		}
	}

	public static class Certificate {
		public static final String CREATE_TABLE = "CREATE TABLE Certificate (id INTEGER PRIMARY KEY, star_value TEXT,user_id TEXT,file_name TEXT,passed_date TEXT)";

		public static final String TABLE_NAME = "Certificate";

		public static final String STAR_VALUE = "star_value",FILE_NAME="file_name",
				                   USER_ID = "user_id",PASSED_DATE="passed_date";

		public static final String DROP_TABLE = "drop table if exists Certificate";

		public static ContentValues createValues(String starValue,String userId,String fileName,String passDate) {
			ContentValues values = new ContentValues();

			values.put(STAR_VALUE, starValue);
			values.put(USER_ID, userId);
			values.put(FILE_NAME, fileName);
			values.put(PASSED_DATE, passDate);

			return values;
		}

	}

}
