package com.kickstarter.viewmodels

import com.kickstarter.KSRobolectricTestCase
import com.kickstarter.R
import com.kickstarter.libs.Environment
import com.kickstarter.libs.featureflag.FlagKey
import com.kickstarter.libs.utils.extensions.addToDisposable
import com.kickstarter.mock.MockFeatureFlagClient
import com.kickstarter.mock.factories.UserFactory
import com.kickstarter.models.User
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Test

class LoggedInViewHolderViewModelTest : KSRobolectricTestCase() {
    private lateinit var vm: LoggedInViewHolderViewModel.ViewModel
    private val activityCount = TestSubscriber<Int>()
    private val activityCountTextColor = TestSubscriber<Int>()
    private val avatarUrl = TestSubscriber<String>()
    private val dashboardRowIsGone = TestSubscriber<Boolean>()
    private val name = TestSubscriber<String>()
    private val unreadMessagesCount = TestSubscriber<Int>()
    private val user = TestSubscriber<User>()
    private val pledgedProjectsIsVisible = TestSubscriber<Boolean>()
    private val disposables = CompositeDisposable()

    fun setUpEnvironment(environment: Environment) {
        this.vm = LoggedInViewHolderViewModel.ViewModel(environment)
        this.vm.outputs.activityCount().subscribe { this.activityCount.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.activityCountTextColor().subscribe { this.activityCountTextColor.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.avatarUrl().subscribe { this.avatarUrl.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.dashboardRowIsGone().subscribe { this.dashboardRowIsGone.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.name().subscribe { this.name.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.unreadMessagesCount().subscribe { this.unreadMessagesCount.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.user().subscribe { this.user.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.pledgedProjectsIsVisible().subscribe { this.pledgedProjectsIsVisible.onNext(it) }.addToDisposable(disposables)
    }

    @Test
    fun testActivityCount_whenUserHasUnseenActivityAndErroredBackings() {
        setUpEnvironment(environment())

        val user = UserFactory.user()
            .toBuilder()
            .erroredBackingsCount(3)
            .unseenActivityCount(2)
            .build()
        this.vm.inputs.configureWith(user)

        this.activityCount.assertValue(5)
    }

    @Test
    fun testActivityCount_whenUserHasUnseenActivityAndNoErroredBackings() {
        setUpEnvironment(environment())

        val user = UserFactory.user()
            .toBuilder()
            .unseenActivityCount(2)
            .build()
        this.vm.inputs.configureWith(user)

        this.activityCount.assertValue(2)
    }

    @Test
    fun testActivityCount_whenUserHasNoUnseenActivityAndErroredBackings() {
        setUpEnvironment(environment())

        val user = UserFactory.user()
            .toBuilder()
            .erroredBackingsCount(3)
            .build()
        this.vm.inputs.configureWith(user)

        this.activityCount.assertValue(3)
    }

    @Test
    fun testActivityCount_whenUserHasNoUnseenActivityAndNoErroredBackings() {
        setUpEnvironment(environment())

        this.vm.inputs.configureWith(UserFactory.user())

        this.activityCount.assertValue(0)
    }

    @Test
    fun testActivityCountTextColor_whenUserHasErroredBackings() {
        setUpEnvironment(environment())

        val user = UserFactory.user()
            .toBuilder()
            .erroredBackingsCount(3)
            .build()
        this.vm.inputs.configureWith(user)

        this.activityCountTextColor.assertValue(R.color.kds_alert)
    }

    @Test
    fun testActivityCountTextColor_whenUserNoErroredBackings() {
        setUpEnvironment(environment())

        this.vm.inputs.configureWith(UserFactory.user())

        this.activityCountTextColor.assertValue(R.color.text_primary)
    }

    @Test
    fun testAvatarUrl() {
        setUpEnvironment(environment())

        this.vm.inputs.configureWith(UserFactory.user())

        this.avatarUrl.assertValueCount(1)
    }

    @Test
    fun testDashboardRowIsGone() {
        setUpEnvironment(environment())

        this.vm.inputs.configureWith(UserFactory.user())

        this.dashboardRowIsGone.assertValue(true)

        this.vm.inputs.configureWith(UserFactory.collaborator())

        this.dashboardRowIsGone.assertValues(true, false)
    }

    @Test
    fun testName() {
        setUpEnvironment(environment())

        this.vm.inputs.configureWith(UserFactory.user().toBuilder().name("Klay Thompson").build())

        this.name.assertValue("Klay Thompson")
    }

    @Test
    fun testUnreadMessagesCount() {
        setUpEnvironment(environment())

        this.vm.inputs.configureWith(UserFactory.user().toBuilder().unreadMessagesCount(5).build())

        this.unreadMessagesCount.assertValue(5)
    }

    @Test
    fun testUser() {
        setUpEnvironment(environment())

        val user = UserFactory.user()
        this.vm.inputs.configureWith(user)

        this.user.assertValue(user)
    }

    @Test
    fun `when pledged projects feature is flag off, should emit false`() {
        val mockFeatureFlagClient: MockFeatureFlagClient =
            object : MockFeatureFlagClient() {
                override fun getBoolean(FlagKey: FlagKey): Boolean {
                    return false
                }
            }

        setUpEnvironment(environment().toBuilder().featureFlagClient(mockFeatureFlagClient).build())
        val user = UserFactory.user()

        this.pledgedProjectsIsVisible.assertValue(false)
    }

    @Test
    fun `when pledged projects feature is flag on, should emit true`() {
        val mockFeatureFlagClient: MockFeatureFlagClient =
            object : MockFeatureFlagClient() {
                override fun getBoolean(FlagKey: FlagKey): Boolean {
                    return true
                }
            }

        setUpEnvironment(environment().toBuilder().featureFlagClient(mockFeatureFlagClient).build())
        val user = UserFactory.user()

        this.pledgedProjectsIsVisible.assertValue(true)
    }

    @After
    fun clear() {
        disposables.clear()
    }
}
