import { UiSchema } from '@genesislcap/foundation-forms';

export const updateFormSchema: UiSchema = {
  "type": "VerticalLayout",
  "elements": [
    {
      "type": "Control",
      "label": "Isin",
      "scope": "#/properties/ISIN",
      "options": {}
    },
    {
      "type": "Control",
      "label": "Bond Price",
      "scope": "#/properties/BOND_PRICE",
      "options": {}
    },
    {
      "type": "Control",
      "label": "Max Quantity",
      "scope": "#/properties/MAX_QUANTITY",
      "options": {}
    },
    {
      "type": "Control",
      "label": "Price Date",
      "scope": "#/properties/PRICE_DATE",
      "options": {}
    },
    {
      "type": "Control",
      "label": "Price Datetime",
      "scope": "#/properties/PRICE_DATETIME",
      "options": {}
    },
    {
      "type": "Control",
      "label": "Indicative",
      "scope": "#/properties/INDICATIVE",
      "options": {}
    },
    {
      "type": "Control",
      "label": "Side",
      "scope": "#/properties/SIDE",
      "options": {}
    }
  ]
}
