package com.youflik.youflik.models;

public class MyPojo {
	private Timefeeds[] timefeeds;

    private String error;

    private String status;

    public Timefeeds[] getTimefeeds ()
    {
        return timefeeds;
    }

    public void setTimefeeds (Timefeeds[] timefeeds)
    {
        this.timefeeds = timefeeds;
    }

    public String getError ()
    {
        return error;
    }

    public void setError (String error)
    {
        this.error = error;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }
}
