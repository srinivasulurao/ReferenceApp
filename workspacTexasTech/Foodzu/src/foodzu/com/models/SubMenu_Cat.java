package foodzu.com.models;

import java.util.ArrayList;

public class SubMenu_Cat {
	String submenu_id, submenu_name, submenu_image, submenu_haschild;

	public ArrayList<Child_Cat> childmenu = new ArrayList<Child_Cat>();

	public ArrayList<Child_Cat> getchildmenu_list() {
		return childmenu;
	}

	public void setchildmenu_list(ArrayList<Child_Cat> childmenu) {
		this.childmenu = childmenu;
	}

	public String getsubmenu_id() {
		return submenu_id;
	}

	public void setsubmenu_id(String submenu_id) {
		this.submenu_id = submenu_id;
	}

	public String getsubmenu_name() {
		return submenu_name;
	}

	public void setsubmenu_name(String submenu_name) {
		this.submenu_name = submenu_name;
	}
	
	public String getsubmenu_haschild() {
		return submenu_haschild;
	}

	public void setsubmenu_haschild(String submenu_haschild) {
		this.submenu_haschild = submenu_haschild;
	}

}
