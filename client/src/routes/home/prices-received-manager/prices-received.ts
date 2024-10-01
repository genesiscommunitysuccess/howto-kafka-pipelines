import { User } from '@genesislcap/foundation-user';
import { customElement, GenesisElement } from '@genesislcap/web-core';
import { PricesReceivedStyles as styles } from './prices-received.styles';
import { PricesReceivedTemplate as template } from './prices-received.template';

@customElement({
  name: 'home-prices-received-manager',
  template,
  styles,
})
export class HomePricesReceivedManager extends GenesisElement {
  @User user: User;
}
