package global.genesis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PojoBody {
    public String getCriteriaMatch() {
        return criteriaMatch;
    }

    public void setCriteriaMatch(String criteriaMatch) {
        this.criteriaMatch = criteriaMatch;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public Double getBondPrice() {
        return bondPrice;
    }

    public void setBondPrice(Double bondPrice) {
        this.bondPrice = bondPrice;
    }

    @JsonProperty("CRITERIA_MATCH")
    private String criteriaMatch;
    @JsonProperty("USER_NAME")
    private String userName;
    @JsonProperty("PASSWORD")
    private String password;
    @JsonProperty("ISIN")
    private String isin;
    @JsonProperty("BOND_PRICE")
    private Double bondPrice;
}
