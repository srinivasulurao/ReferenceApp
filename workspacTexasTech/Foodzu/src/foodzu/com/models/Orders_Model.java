package foodzu.com.models;

import java.util.ArrayList;

public class Orders_Model {

	String Order_ID, Shipping_ID, Transaction_ID, Order_Date,
			Order_Total_Amount;
	

	public ArrayList<Ordered_Products> Products=new ArrayList<Ordered_Products>();

	public ArrayList<Ordered_Products> getProducts_data() {
		return Products;
	}

	public void setProducts_data(ArrayList<Ordered_Products> Products) {
		this.Products = Products;
	}
	
	public String getOrder_ID() {
		return Order_ID;
	}

	public void setOrder_ID(String Order_ID) {
		this.Order_ID = Order_ID;
	}

	public String getShipping_ID() {
		return Shipping_ID;
	}

	public void setShipping_ID(String Shipping_ID) {
		this.Shipping_ID = Shipping_ID;
	}

	public String getTransaction_ID() {
		return Transaction_ID;
	}

	public void setTransaction_ID(String Transaction_ID) {
		this.Transaction_ID = Transaction_ID;
	}

	public String getOrder_Date() {
		return Order_Date;
	}

	public void setOrder_Date(String Order_Date) {
		this.Order_Date = Order_Date;
	}

	public String getOrder_Total_Amount() {
		return Order_Total_Amount;
	}

	public void setOrder_Total_Amount(String Order_Total_Amount) {
		this.Order_Total_Amount = Order_Total_Amount;
	}

}
