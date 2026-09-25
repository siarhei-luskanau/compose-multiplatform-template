package template.ui.main

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import template.core.common.DispatcherSet
import template.core.database.DatabaseRecord
import template.core.database.DatabaseRepository
import template.core.pref.PrefService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
internal class MainViewModelCommonTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakePrefService(
        initialKey: String?,
    ) : PrefService {
        val keyFlow = MutableStateFlow(initialKey)

        override suspend fun cleanStorage() = Unit

        override fun getUserPreferenceContent(): Flow<String?> = keyFlow

        override fun getKey(): Flow<String?> = keyFlow

        override suspend fun setKey(key: String?) {
            keyFlow.value = key
        }
    }

    private class FakeDatabaseRepository(
        initialRecords: List<DatabaseRecord>,
    ) : DatabaseRepository {
        val recordsFlow = MutableStateFlow(initialRecords)

        override fun getAll(): Flow<List<DatabaseRecord>> = recordsFlow

        override suspend fun save(record: DatabaseRecord) {
            recordsFlow.value = recordsFlow.value + record
        }

        override suspend fun delete(id: String) {
            recordsFlow.value = recordsFlow.value.filterNot { it.id == id }
        }
    }

    private class FakeDispatcherSet(
        private val dispatcher: CoroutineDispatcher,
    ) : DispatcherSet {
        override fun defaultDispatcher() = dispatcher

        override fun ioDispatcher() = dispatcher

        override fun mainDispatcher() = dispatcher
    }

    private class FakeMainNavigationCallback : MainNavigationCallback {
        var goBackCallCount = 0
            private set

        override fun goBack() {
            goBackCallCount++
        }
    }

    private fun createViewModel(
        initArg: String = "arg",
        initialPref: String? = "prefValue",
        initialRecords: List<DatabaseRecord> = listOf(DatabaseRecord(id = "1", tag = "tag")),
    ): Triple<MainViewModel, FakePrefService, FakeDatabaseRepository> {
        val prefService = FakePrefService(initialPref)
        val databaseRepository = FakeDatabaseRepository(initialRecords)
        val viewModel =
            MainViewModel(
                initArg = initArg,
                navigationCallback = FakeMainNavigationCallback(),
                dispatcherSet = FakeDispatcherSet(testDispatcher),
                prefService = prefService,
                databaseRepository = databaseRepository,
            )
        return Triple(viewModel, prefService, databaseRepository)
    }

    @Test
    fun viewState_shouldBeLoading_initially() =
        runTest(testDispatcher) {
            val (viewModel, _, _) = createViewModel()
            assertEquals(MainViewState.Loading, viewModel.viewState.value)
        }

    @Test
    fun viewState_shouldBeSuccess_whenCollected() =
        runTest(testDispatcher) {
            val (viewModel, _, _) =
                createViewModel(
                    initArg = "myArg",
                    initialPref = "myPref",
                    initialRecords = listOf(DatabaseRecord("1", "a")),
                )
            backgroundScope.launch { viewModel.viewState.collect {} }
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(MainViewState.Success(data = "initArg=myArg pref=myPref records=1"), viewModel.viewState.value)
        }

    @Test
    fun viewState_shouldUpdate_whenPrefAndRecordsFlowsEmit() =
        runTest(testDispatcher) {
            val (viewModel, prefService, databaseRepository) =
                createViewModel(initArg = "myArg", initialPref = "prefA", initialRecords = emptyList())
            backgroundScope.launch { viewModel.viewState.collect {} }
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(MainViewState.Success(data = "initArg=myArg pref=prefA records=0"), viewModel.viewState.value)

            prefService.keyFlow.value = "prefB"
            databaseRepository.recordsFlow.value = listOf(DatabaseRecord("1", "a"), DatabaseRecord("2", "b"))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(MainViewState.Success(data = "initArg=myArg pref=prefB records=2"), viewModel.viewState.value)
        }

    @Test
    fun onEvent_shouldInvokeGoBack_whenNavigateBackReceived() =
        runTest(testDispatcher) {
            val prefService = FakePrefService("pref")
            val databaseRepository = FakeDatabaseRepository(emptyList())
            val navigationCallback = FakeMainNavigationCallback()
            val viewModel =
                MainViewModel(
                    initArg = "arg",
                    navigationCallback = navigationCallback,
                    dispatcherSet = FakeDispatcherSet(testDispatcher),
                    prefService = prefService,
                    databaseRepository = databaseRepository,
                )

            viewModel.onEvent(MainViewEvent.NavigateBack)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(1, navigationCallback.goBackCallCount)
        }
}
