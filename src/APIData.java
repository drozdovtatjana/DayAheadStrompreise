import java.util.Date;

public class APIData {

    private Date start_timestamp;
    private Date end_timestamp;
    private double marketprice;
    private String unit;


    public Date getStart_timestamp() {
        return start_timestamp;
    }

    public void setStart_timestamp(Date start_timestamp) {
        this.start_timestamp = start_timestamp;
    }

    public Date getEnd_timestamp() {
        return end_timestamp;
    }

    public void setEnd_timestamp(Date end_timestamp) {
        this.end_timestamp = end_timestamp;
    }

    public double getMarketprice() {
        return marketprice;
    }

    public void setMarketprice(double marketprice) {
        this.marketprice = marketprice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}

