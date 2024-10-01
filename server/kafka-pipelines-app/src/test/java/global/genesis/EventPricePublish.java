package global.genesis;

import java.util.Map;

public class EventPricePublish extends EventBase {
    protected Map<String, String> priceDetails;

    public EventPricePublish(Map<String, String> priceDetails) {
        this.priceDetails = priceDetails;
        super.setEndPoint("EVENT_PRICE_PUBLISH");
    }

    @Override
    public PojoDetails getBody() {
        super.getPojoBody().setIsin(priceDetails.get("ISIN"));
        super.getPojoBody().setBondPrice(Double.valueOf(priceDetails.get("BOND_PRICE")));
        super.getPojoDetails().setPojoBody(super.getPojoBody());
        return super.getPojoDetails();
    }
}
