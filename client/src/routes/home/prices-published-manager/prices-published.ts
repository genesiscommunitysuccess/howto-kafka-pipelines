import { User } from '@genesislcap/foundation-user';
import { customElement, GenesisElement } from '@genesislcap/web-core';
import { PricesPublishedStyles as styles } from './prices-published.styles';
import { PricesPublishedTemplate as template } from './prices-published.template';

@customElement({
  name: 'home-prices-published-manager',
  template,
  styles,
})
export class HomePricesPublishedManager extends GenesisElement {
  @User user: User;
}
