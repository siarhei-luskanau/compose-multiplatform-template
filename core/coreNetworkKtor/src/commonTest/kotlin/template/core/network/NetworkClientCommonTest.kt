package template.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.pluginOrNull
import kotlinx.coroutines.test.runTest
import org.koin.plugin.module.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

internal class NetworkClientCommonTest {
    @Test
    fun getReturnsResponseBody() =
        runTest {
            val koinApplication = koinApplication<TestKoinApplication>()
            val networkClient = koinApplication.koin.get<NetworkClient>()
            assertEquals(NetworkResult.Success("get response"), networkClient.get("https://example.com"))
            koinApplication.close()
        }

    @Test
    fun postReturnsResponseBody() =
        runTest {
            val koinApplication = koinApplication<TestKoinApplication>()
            val networkClient = koinApplication.koin.get<NetworkClient>()
            assertEquals(
                NetworkResult.Success("post response"),
                networkClient.post("https://example.com", """{"key":"value"}"""),
            )
            koinApplication.close()
        }

    @Test
    fun getReturnsFailureWhenRequestThrows() =
        runTest {
            val httpClient =
                HttpClient(
                    MockEngine { throw IllegalStateException("network down") },
                )
            val networkClient = NetworkClientKtor(httpClient)
            val result = networkClient.get("https://example.com")
            assertIs<NetworkResult.Failure<String>>(result)
            httpClient.close()
        }

    @Test
    fun postReturnsFailureWhenRequestThrows() =
        runTest {
            val httpClient =
                HttpClient(
                    MockEngine { throw IllegalStateException("network down") },
                )
            val networkClient = NetworkClientKtor(httpClient)
            val result = networkClient.post("https://example.com", """{"key":"value"}""")
            assertIs<NetworkResult.Failure<String>>(result)
            httpClient.close()
        }

    @Test
    fun httpClientInstallsContentNegotiation() {
        val httpClient = CoreNetworkKtorModule().httpClient()
        assertNotNull(httpClient.pluginOrNull(ContentNegotiation))
        httpClient.close()
    }
}
