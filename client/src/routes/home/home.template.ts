import { isDev } from '@genesislcap/foundation-utils';
import { html } from '@genesislcap/web-core';
import type { Home } from './home';
import { HomePricesReceivedManager } from './prices-received-manager';
import { HomePricesPublishedManager } from './prices-published-manager';

HomePricesReceivedManager;
HomePricesPublishedManager;

export const HomeTemplate = html<Home>`
  <rapid-layout auto-save-key="${() => (isDev() ? null : 'HOME_1724172938212')}">
     <rapid-layout-region type="vertical">
         <rapid-layout-item title="Prices Published">
           <home-prices-published-manager></home-prices-published-manager>
         </rapid-layout-item>
         <rapid-layout-item title="Prices Received">
             <home-prices-received-manager></home-prices-received-manager>
         </rapid-layout-item>
     </rapid-layout-region>
  </rapid-layout>
`;
