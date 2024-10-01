import { html, whenElse, repeat } from '@genesislcap/web-core';
import { getViewUpdateRightComponent } from '../../../utils';
import type { HomePricesPublishedManager } from './prices-published';
import { createFormSchema } from './prices-published.create.form.schema';
import { updateFormSchema } from './prices-published.update.form.schema';
import { columnDefs } from './prices-published.column.defs';


export const PricesPublishedTemplate = html<HomePricesPublishedManager>`
    ${whenElse(
        (x) => getViewUpdateRightComponent(x.user, ''),
        html`
            <entity-management
                design-system-prefix="rapid"
                header-case-type="capitalCase"
                enable-row-flashing
                enable-cell-flashing
                resourceName="PRICE_PUBLISHED"
                createEvent="${(x) => getViewUpdateRightComponent(x.user, '', 'EVENT_PRICE_PUBLISH')}"
                :createFormUiSchema=${() => createFormSchema }
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
