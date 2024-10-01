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
    }
  ]
}
