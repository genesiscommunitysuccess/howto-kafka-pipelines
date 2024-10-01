/**
  * This file defines the entities (or tables) for the application.  
  * Entities aggregation a selection of the universe of fields defined in 
  * {app-name}-fields-dictionary.kts file into a business entity.  
  *
  * Note: indices defined here control the APIs available to the developer.
  * For example, if an entity requires lookup APIs by one or more of its attributes, 
  * be sure to define either a unique or non-unique index.

  * Full documentation on tables may be found here >> https://docs.genesis.global/docs/develop/server-capabilities/data-model/

 */

tables {
  table(name = "PRICE_RECEIVED", id = 11_000) {
    field("ISIN", STRING).notNull()
    field("PRICE_DATETIME", DATETIME).notNull()
    field("BOND_PRICE", DOUBLE).notNull()
    field("MAX_QUANTITY", INT).notNull()
    field("INDICATIVE", BOOLEAN).default(false)
    field("PRICE_DATE", DATE).notNull()
    field("SIDE", ENUM("SELL","BUY")).default("BUY")

    primaryKey("ISIN", "PRICE_DATETIME")
  }

  table(name = "PRICE_PUBLISHED", id = 11_001) {
    field("ISIN", STRING).notNull()
    field("PRICE_DATETIME", DATETIME).notNull()
    field("BOND_PRICE", DOUBLE).notNull()
    field("MAX_QUANTITY", INT).notNull()
    field("INDICATIVE", BOOLEAN).default(false)
    field("PRICE_DATE", DATE).notNull()
    field("SIDE", ENUM("SELL","BUY")).default("BUY")

    primaryKey("ISIN", "PRICE_DATETIME")
  }
}
