Feature: Kafka

  Scenario Outline: Login with admin and send price to kafka
    Given user connect with username "admin" and password "genesis"
    And user sends the details of type PricePublished to "<event>"
      | ISIN          | PRICE        | EVENT               |
      | my_isin       | 1            | EVENT_PRICE_PUBLISH |
    When user sends request to "<reqrep>" with "<query>"
    Then user compares to "<result>"
    Examples:
      | event               | reqrep             | query  | result                                                      |
      | EVENT_PRICE_PUBLISH | REQ_PRICE_RECEIVED | ISIN=* | src/test/resources/results/REQ_PRICE_RECEIVED_expected.json |
