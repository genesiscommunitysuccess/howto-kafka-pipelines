package global.genesis.cucumber

import global.genesis.Authentication.SESSION_AUTH_TOKEN
import global.genesis.Authentication.LOGIN_SCHEMA_PATH
import global.genesis.CompareJSON.compareJSONFiles
import global.genesis.EventLoginAuth
import global.genesis.EventPricePublish
import global.genesis.ReqRep
import global.genesis.WriteReadFile.writeJSON
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.module.jsv.JsonSchemaValidator
import io.restassured.response.Response
import kotlin.test.assertNotNull

class KafkaTest {
    private var username: String = ""
    private var response: Response? = null
    private var endpoint: String = ""

    @Given("user connect with username {string} and password {string}")
    fun `user connect with username and password`(username: String, password: String) {
        this.username = username
        val eventLoginAuth = EventLoginAuth(username, password)
        val response: Response = eventLoginAuth.post()
        SESSION_AUTH_TOKEN  = response.jsonPath().get("SESSION_AUTH_TOKEN")
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath(LOGIN_SCHEMA_PATH))
    }

    @And("user sends the details to {string}")
    fun `user sends details to event`(event: String, dataTable: DataTable) {
        val details = dataTable.asMaps()
        details.forEach {
            if(it["EVENT"].equals(event)) {
                val eventPricePublish = EventPricePublish(it)
                response = eventPricePublish.post()
            }
        }
    }

    @When("user sends request to {string} with {string}")
    fun `user sends request with`(endpoint: String, query: String) {
        this.endpoint = endpoint
        val reqRep = ReqRep(endpoint, query)
        response = reqRep.get()
    }

    @Then("user compares to {string}")
    fun `user compares result to expected`(result: String) {
        val jsonPathMap = response?.jsonPath()?.get<Map<String, Object>>()
        assertNotNull(jsonPathMap)
        val filePath = generateJSONFilePath()
        writeJSON(filePath, jsonPathMap)
        compareJSONFiles(result, filePath, "", "")
    }

    private fun generateJSONFilePath(): String = "src/test/resources/result/$username/actual/$endpoint.json"

}