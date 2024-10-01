package global.genesis;

import io.restassured.response.Response;

import java.util.UUID;

import static global.genesis.RequestResponseSpecifications.requestSpecification;
import static global.genesis.RequestResponseSpecifications.responseSpecification;
import static io.restassured.RestAssured.given;


public abstract class EventBase {
    private PojoBody pojoBody;
    private PojoDetails pojoDetails;
    private String endPoint;
    private String sourceRef;

    public EventBase() {
        this.pojoBody = new PojoBody();
        this.pojoDetails = new PojoDetails();
    }

    public PojoBody getPojoBody() {
        return pojoBody;
    }

    public void setPojoBody(PojoBody pojoBody) {
        this.pojoBody = pojoBody;
    }

    public PojoDetails getPojoDetails() {
        return pojoDetails;
    }

    public void setPojoDetails(PojoDetails pojoDetails) {
        this.pojoDetails = pojoDetails;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public abstract PojoDetails getBody();

    public Response post() {
        this.sourceRef = UUID.randomUUID().toString();
        try {
            return
                    given()
                            .spec(requestSpecification(this.getBody()))
                            .header("SOURCE-REF", sourceRef).
                    when()
                            .post(this.endPoint).
                    then()
                            .spec(responseSpecification())
                            .extract().response();
        } catch (Exception e) {
            throw e;
        }
    }

    public Response get() {
        if (this.sourceRef == null) {
            this.sourceRef = UUID.randomUUID().toString();
        }
        try {
            return
                    given()
                            .spec(requestSpecification(this.getBody()))
                            .header("SOURCE-REF", this.sourceRef).
                    when()
                            .get(this.endPoint).
                    then()
                            .spec(responseSpecification())
                            .extract().response();
        } catch (Exception e) {
            throw e;
        }
    }

    public Response put() {
        try {
            return
                    given()
                            .spec(requestSpecification(this.getBody()))
                            .header("SUBSCRIPTION-REF", sourceRef).
                    when()
                            .put(this.endPoint).
                    then()
                            .spec(responseSpecification())
                            .extract().response();
        } catch (Exception e) {
            throw e;
        }
    }


    public Response delete() {
        try {
            return
                    given()
                            .spec(requestSpecification(this.getBody()))
                            .header("SOURCE-REF", sourceRef).
                    when()
                            .delete(this.endPoint).
                    then()
                            .spec(responseSpecification())
                            .extract().response();
        } catch (Exception e) {
            throw e;
        }
    }
}