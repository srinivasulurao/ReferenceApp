package foodzu.com.models;

import java.util.ArrayList;

public class Data_Models {

	public static ArrayList<Products> cart_data;
	public static ArrayList<Products> home_data;
	public static ArrayList<Country_Model> countries;
	
	public static Orders_Model Product;

//	public static ArrayList<Ordered_Products> Products;
//	
//	public ArrayList<Ordered_Products> getProducts_data() {
//		return Products;
//	}
//
//	public void setProducts_data(ArrayList<Ordered_Products> Products) {
//		Orders_Model.Products = Products;
//	}
	
	public ArrayList<Products> gethome_data() {
		return home_data;
	}

	public void sethome_data(ArrayList<Products> home_data) {
		Data_Models.home_data = home_data;
	}
	
	public ArrayList<Products> getcartdata() {
		return cart_data;
	}

	public void setcartdata(ArrayList<Products> cartdata) {
		Data_Models.cart_data = cartdata;
	}
	
	public ArrayList<Country_Model> getcountrydata() {
		return countries;
	}

	public void setcountrydata(ArrayList<Country_Model> countrydata) {
		Data_Models.countries = countrydata;
	}

	public Orders_Model getProduct_data() {
		return Product;
	}

	public void setProduct_data(Orders_Model Product) {
		Data_Models.Product = Product;
	}

}
