package foodzu.com.models;

public class FilterCategory {

	String id, categoryName, selectCount;

	public FilterCategory(String id, String categoryName, String selectCount) {

		this.id = id;
		this.categoryName = categoryName;
		this.selectCount = selectCount;

	}
	
	public FilterCategory(){
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getSelectCount() {
		return selectCount;
	}

	public void setSelectCount(String selectCount) {
		this.selectCount = selectCount;
	}

}
