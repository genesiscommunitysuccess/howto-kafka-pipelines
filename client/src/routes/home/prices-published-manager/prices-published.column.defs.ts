import { ColDef } from '@ag-grid-community/core';
import { getNumberFormatter, getDateFormatter } from '@genesislcap/foundation-utils';

export const columnDefs: ColDef[] = [
  {
    field: "ISIN",
  },
  {
    field: "BOND_PRICE",
    valueFormatter: getNumberFormatter("0,0.00", null),
  }
]
