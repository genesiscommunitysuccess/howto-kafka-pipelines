import { html, whenElse, repeat } from '@genesislcap/web-core';
import { getViewUpdateRightComponent } from '../../../utils';
import type { HomePricesReceivedManager } from './prices-received';
import { createFormSchema } from './prices-received.create.form.schema';
import { updateFormSchema } from './prices-received.update.form.schema';
import { columnDefs } from './prices-received.column.defs';


export const PricesReceivedTemplate = html<HomePricesReceivedManager>`
    ${whenElse(
        (x) => getViewUpdateRightComponent(x.user, ''),
        html`
            <entity-management
                design-system-prefix="rapid"
                header-case-type="capitalCase"
                enable-row-flashing
                enable-cell-flashing
                resourceName="PRICE_RECEIVED"
                :datasourceConfig=${() => ({pollingInterval: 5000 })}
                :columns=${() => columnDefs }
                modal-position="centre"
                size-columns-to-fit
            ></entity-management>
        `,
        html`
          <not-permitted-component></not-permitted-component>
        `,
    )}`;
